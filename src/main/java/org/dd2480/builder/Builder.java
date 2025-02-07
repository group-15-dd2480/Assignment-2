package org.dd2480.builder;

import java.io.File;
import java.nio.file.Paths;

import org.dd2480.Commit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Builder {

    /**
     * Build a project from the repository on a specific commit.
     *
     * Save the build result.
     */
    public static void buildProject(Commit commit) {
        Instant startTime = Instant.now();

        // First fetch project files
        String projectPath = fetchProjectFiles(commit);
        if (projectPath == null) {
            System.out.println("Failed to fetch the project files for commit: " + commit.hash);
            BuildResult result = new BuildResult(commit, BuildStatus.ERROR, List.of("Failed to fetch project files"), startTime, Instant.now());
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
    public static boolean runCommand(String command, String workingDir, List<String> output) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
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
     * @param commit A commit object
     * @return The absolute path for the project files, or an empty string if
     *         something went wrong
     */
    public static String fetchProjectFiles(Commit commit) {

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
     */
    public static void saveResult(BuildResult result) {

    }

    /**
     * Get the build result for a specific commit.
     */
    public static BuildResult getResult(String commitHash) {
        return new BuildResult();
    }

}
