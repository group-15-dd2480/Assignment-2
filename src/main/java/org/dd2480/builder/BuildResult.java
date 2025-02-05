package org.dd2480.builder;

import java.time.Instant;
import java.util.List;

/**
 * Encapsulates the result of a single build.
 */
public class BuildResult {

    public String repositoryOwner;
    public String repositoryName;
    public String branch;
    public String commitHash;

    public BuildStatus status;
    public List<String> logs;

    public Instant startTime;
    public Instant endTime;

    public BuildResult(String repositoryOwner, String repositoryName, String branch, String commitHash, BuildStatus status, List<String> logs, Instant startTime, Instant endTime) {
        this.repositoryOwner = repositoryOwner;
        this.repositoryName = repositoryName;
        this.branch = branch;
        this.commitHash = commitHash;
        this.status = status;
        this.logs = logs;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public BuildResult(){
        this(null,null,null,null,null,null,null,null);
    }
}
