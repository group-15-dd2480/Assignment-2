package org.dd2480.builder;

import org.dd2480.Commit;

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
     * Fetch project files from a remote repository, for a specific branch and
     * commit.
     *
     * Save locally in a temp folder and return the absolute path to that
     * folder.
     *
     * Name the folder something appropriate and unique, like the commit hash.
     */
    public static String fetchProjectFiles(Commit commit) {
        return "Project files fetched";
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
