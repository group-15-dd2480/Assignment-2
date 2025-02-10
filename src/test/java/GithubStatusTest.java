import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.dd2480.Commit;
import org.dd2480.GithubStatus;
import org.dd2480.builder.BuildStatus;

public class GithubStatusTest {

    @Test
    public void shouldThrowRunTimeException_GivenInvalidSha() throws IOException {
        String owner = "Markuswessen";
        String repo = "Assignment-2";
        String commitSha = "69e1535c";
        BuildStatus state = BuildStatus.SUCCESS; // Or FAILURE, ERROR, or PENDING
        String description = "Build passed!";
        String targetUrl = "https://example.com/build-details";

        Commit commit = new Commit(owner, repo, commitSha, "Commit message", "main");
        // Call the method to update the commit status
        assertThrows(RuntimeException.class, () -> {
            GithubStatus.setCommitStatus(commit, state, description, targetUrl);
        });
    }

    @Test
    public void shouldThrowException_WhenTokenIsMissing() {
        Commit commit = new Commit("Markuswessen", "Assignment-2", "validsha", "Commit message", "main");

        // Temporarily remove the token
        System.clearProperty("GITHUB_TOKEN");

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            GithubStatus.setCommitStatus(commit, BuildStatus.SUCCESS, "Build passed!", "https://example.com");
        });

        assertEquals("GitHub token is missing. Set the GITHUB_TOKEN environment variable.", exception.getMessage());
    }

    @Test
    public void shouldSendCorrectStatusRequest() throws Exception {
        // Mock HTTP connection
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(201);

        // Mock commit details
        Commit commit = new Commit("Markuswessen", "Assignment-2", "validsha", "Commit message", "main");

        // Call method
        assertDoesNotThrow(() -> {
            GithubStatus.setCommitStatus(commit, BuildStatus.SUCCESS, "Build passed!", "https://example.com");
        });

        // Verify it did not fail
        verify(mockConnection, never()).getErrorStream();
    }

}