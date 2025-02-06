package org.dd2480.builder;

import java.io.File;
import java.nio.file.Paths;

import org.dd2480.Commit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

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
     * commit. Files are put in a temp folder and named after the commit hash.
     *
     * @param commit A commit object
     * @return The absolute path for the project files, or an empty string if
     *         something went wrong
     */
    public static String fetchProjectFiles(Commit commit) {

        String repoUrl = "https://github.com/" + commit.repositoryOwner + "/" + commit.repositoryName + ".git";

        // Absolute path to project file location
        String filePath = Paths.get("temp", commit.hash).toAbsolutePath().toString();

        try {
            // Clone the repo
            Git git = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(new File(filePath))
                    .call();

            // Checkout specific branch and commit
            git.checkout().setName(commit.branch).call();
            git.checkout().setName(commit.hash).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
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
