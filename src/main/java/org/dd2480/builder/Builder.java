package org.dd2480.builder;

import org.dd2480.Commit;

public class Builder {

    /**
     * Build a project from the repository on a specific commit.
     *
     * Save the build result.
     */
    public static void buildProject(Commit commit) {

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
