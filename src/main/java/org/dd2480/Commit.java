package org.dd2480;

/**
 * Encapsulates all data required to indentify a specific commit.
 */
public class Commit {

    public final String repositoryOwner;
    public final String repositoryName;
    public final String hash;
    public final String message;
    public final String branch;

    public Commit(String repositoryOwner, String repositoryName, String commitHash, String commitMessage, String branch) {
        this.repositoryOwner = repositoryOwner;
        this.repositoryName = repositoryName;
        this.hash = commitHash;
        this.message = commitMessage;
        this.branch = branch;
    }
}
