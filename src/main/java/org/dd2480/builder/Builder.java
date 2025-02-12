package org.dd2480.builder;

import java.io.*;
import java.nio.file.Paths;

import org.dd2480.Commit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles the build process for a given project repository.
 */
public class Builder {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Builder.class);

    /**
     * Build and tests a project from the repository on a specific commit.
     * 
     * @param commit The commit object representing the version of the project to
     *               build.
     *
     */
    public void buildProject(Commit commit) {
        Instant startTime = Instant.now();

        // First fetch project files
        String projectPath = fetchProjectFiles(commit);
        if (projectPath == null) {
            log.info("Failed to fetch the project files for commit: " + commit.hash);
            BuildResult result = new BuildResult(commit, BuildStatus.ERROR, List.of("Failed to fetch project files"),
                    startTime, Instant.now());
            saveResult(result);
            return;
        }

        // Run maven test and package
        List<String> testOutput = new ArrayList<>();
        List<String> packageOutput = new ArrayList<>();

        boolean testsSuccessful = runCommand("mvn test", projectPath, testOutput);
        boolean buildSuccessful = runCommand("mvn package", projectPath, packageOutput);

        boolean success = testsSuccessful && buildSuccessful;

        // Determine final build status
        org.dd2480.builder.BuildStatus status;
        if (success)
            status = BuildStatus.SUCCESS;
        else
            status = BuildStatus.FAILURE;
        List<String> allLogs = new ArrayList<>();
        allLogs.addAll(testOutput);
        allLogs.addAll(packageOutput);

        Instant endTime = Instant.now();
        // Save the result
        BuildResult result = new BuildResult(commit, status, allLogs, startTime, endTime);
        saveResult(result);
    }

    /**
     * Executes a shell command within a specified working directory.
     *
     * @param command    The shell command to execute.
     * @param workingDir The directory where the command should be run.
     * @param output     A list to store command output (stdout and stderr).
     * @return {@code true} if the command executed successfully, {@code false}
     *         otherwise
     */
    public boolean runCommand(String command, String workingDir, List<String> output) {
        try {
            ProcessBuilder pb = new ProcessBuilder();

            // Fix OS dependencies
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                pb.command("cmd.exe", "/c", command);
            } else {
                pb.command("sh", "-c", command);
            }

            pb.directory(new File(workingDir));
            pb.redirectErrorStream(true);

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }

            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            output.add("Error executing command: " + command);
            output.add(e.getMessage());
            return false;
        }
    }

    /**
     * Fetch project files from a remote repository for a specific branch and
     * commit. Files are put in a temporary folder and named after the commit hash.
     *
     * @param commit The commit object containing repository details.
     * @return The absolute path for the project files, or an empty string if
     *         something went wrong.
     */
    public String fetchProjectFiles(Commit commit) {

        String repoUrl = "https://github.com/" + commit.repositoryOwner + "/" + commit.repositoryName + ".git";

        // Absolute path to project file location
        String filePath = Paths.get("temp", commit.hash).toAbsolutePath().toString();

        /*
         * Clones the directory and switches to correct branch and commit.
         * 
         * Is a try-with-resource block that automatically runs git.close() whenever the
         * block finishes, even if an exception happens. This is important, otherwise
         * it might cause issues when deleting the repo files.
         */
        try (Git git = Git
                .cloneRepository()
                .setURI(repoUrl)
                .setDirectory(new File(filePath))
                .call()) {
            // Checkout specific branch and commit
            git.checkout().setName(commit.branch).call();
            git.checkout().setName(commit.hash).call();
        } catch (Exception e) { // Incorrect commit info or other issue
            return "";
        }

        return filePath;
    }

    /**
     * Save the build result so that it can be accessed at a later date.
     * 
     * @param result The BuildResult object from buildProject
     *
     */
    public void saveResult(BuildResult result) {
        File directory = new File("buildResults");
        // Create directory if it doesn't exist
        if (!directory.exists()) {
            directory.mkdir();
        }
        File file = new File(directory, result.commitHash + ".dat");
        try (FileOutputStream stream = new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(stream)) {
            out.writeObject(result);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            log.warn("Failed to create file for build result: " + e.getMessage());
        } catch (IOException e) {
            log.error("Failed to save build result: " + e.getMessage(), e);
        }
    }

    /**
     * Get the build result for a specific commit.
     * 
     * @param commitHash a commit hash
     *
     * @return the result of the build
     */
    public BuildResult getResult(String commitHash) {
        try {
            File directory = new File("buildResults");
            if (!directory.exists()) {
                return null;
            }
            File file = new File(directory, commitHash + ".dat");
            FileInputStream stream = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(stream);
            BuildResult result = (BuildResult) in.readObject();
            in.close();
            return result;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a list of all build results.
     * 
     * @return A list of all build results.
     */
    public List<BuildResult> listAllBuilds() {
        List<BuildResult> results = new ArrayList<>();
        File directory = new File("buildResults");
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".dat"));
        if (files == null)
            return results;
        // Deserialize each file
        for (File file : files) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                BuildResult result = (BuildResult) in.readObject();
                results.add(result);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Failed to read build result from " + file.getName() + ": " + e.getMessage());
            }
        }
        return results;
    }
}
