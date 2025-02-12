package org.dd2480.builder;

import org.dd2480.Commit;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * Encapsulates the result of a single build.
 */
public class BuildResult implements Serializable {

    public String repositoryOwner;
    public String repositoryName;
    public String branch;
    public String commitHash;

    public BuildStatus status;
    public List<String> logs;

    public Instant startTime;
    public Instant endTime;

    /**
     * Constructor for a BuildResult object.
     * 
     * @param commit
     * @param status
     * @param logs
     * @param startTime
     * @param endTime
     */

    public BuildResult(Commit commit, BuildStatus status, List<String> logs, Instant startTime, Instant endTime) {
        this.repositoryOwner = commit.repositoryOwner;
        this.repositoryName = commit.repositoryName;
        this.branch = commit.branch;
        this.commitHash = commit.hash;
        this.status = status;
        this.logs = logs;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public BuildResult() {

    }
}
