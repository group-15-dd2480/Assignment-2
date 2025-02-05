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

}
