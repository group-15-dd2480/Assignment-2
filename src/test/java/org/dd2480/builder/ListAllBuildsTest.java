package org.dd2480.builder;

import org.dd2480.Commit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListAllBuildsTest {
    private static final String DIRECTORY = "buildResults";
    private static Builder builder;

    @BeforeAll
    static void setup() {
        builder = new Builder();
    }

    @BeforeEach
    void cleanUpBefore() {
        deleteDirectory(new File(DIRECTORY)); // Ensure the directory is empty before each test
    }

    @AfterEach
    void cleanUpAfter() {
        deleteDirectory(new File(DIRECTORY)); // Clean up the directory after each test
    }

    @Test
    void shouldReturnEmptyList_IfNoFiles() {
        List<BuildResult> results = builder.listAllBuilds();
        assertNotNull(results, "List should not be null");
        assertTrue(results.isEmpty(), "Should return an empty list when no files exist");
    }
    @Test
    void shouldReturnList_IfFilesExist() throws IOException, ClassNotFoundException {
        new File(DIRECTORY).mkdirs();
        // Dummy Commit
        Commit commit = new Commit("owner", "repo","123abc" ,"hi","branch");
        BuildResult buildResult = new BuildResult(commit, BuildStatus.SUCCESS, Collections.singletonList("Log entry"), Instant.now(), Instant.now());
        File file = new File(DIRECTORY, "testBuild.dat");

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(buildResult);
        }

        List<BuildResult> results = builder.listAllBuilds();
        assertEquals(1, results.size(), "Should return a list with one item");
    }
    @Test
    void shouldSkipInvalidFiles() throws IOException {
        new File(DIRECTORY).mkdirs();
        File invalidFile = new File(DIRECTORY, "invalidFile.dat");

        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write("corrupted data");
        }

        List<BuildResult> results = builder.listAllBuilds();
        assertTrue(results.isEmpty(), "Should skip invalid files and return an empty list");
    }

    /** Helper Method to Delete Directory */
    private void deleteDirectory(File dir) {
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
            dir.delete();
        }
    }
}