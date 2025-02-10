package org.dd2480.builder;

import org.dd2480.Commit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SaveResultTest {
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
    void testSaveResultCreatesFile() {
        // Dummy Commit
        Commit commit = new Commit("owner", "repo","123abc" ,"hi","branch");
        BuildResult result = new BuildResult(commit, BuildStatus.SUCCESS, Collections.singletonList("Log entry"), Instant.now(), Instant.now());
        builder.saveResult(result);

        File savedFile = new File(DIRECTORY, "123abc.dat");
        assertTrue(savedFile.exists(), "Build result file should be created in the correct directory.");
    }

    @Test
    void testSaveResultPlacesFile_CorrectDirectory() {
        // Dummy Commit
        Commit commit = new Commit("owner", "repo","commit456" ,"hi","branch");
        BuildResult result = new BuildResult(commit, BuildStatus.SUCCESS, Collections.singletonList("Log entry"), Instant.now(), Instant.now());
        builder.saveResult(result);

        File directory = new File(DIRECTORY);
        assertTrue(directory.exists(), "The buildResults directory should be created.");
        assertTrue(directory.isDirectory(), "buildResults should be a directory.");

        File savedFile = new File(directory, "commit456.dat");
        assertTrue(savedFile.exists(), "The file should be inside the buildResults directory.");
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