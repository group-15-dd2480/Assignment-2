import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.dd2480.Commit;
import org.dd2480.GithubStatus;
import org.dd2480.builder.BuildStatus;

public class GithubStatusTest {

    @Test
    public void shouldThrowRunTimeException_GivenInvalidSha() throws IOException {
        String owner = "Name";
        String repo = "Assignment-2";
        String commitSha = "69e1535c";
        BuildStatus state = BuildStatus.SUCCESS; // Or FAILURE, ERROR, or PENDING
        String description = "Build passed!";
        String targetUrl = "https://example.com/build-details";

        Commit commit = new Commit(owner, repo, commitSha, "Commit message", "main");
        GithubStatus status = new GithubStatus();

        // Call the method to update the commit status
        assertThrows(RuntimeException.class, () -> {
            status.setCommitStatus(commit, state, description, targetUrl);
        });
    }

    @Test
    public void shouldThrowException_WhenTokenIsMissing() {
        Commit commit = new Commit("Name", "Assignment-2", "validsha", "Commit message", "main");
        GithubStatus status = new GithubStatus();
        // Temporarily remove the token
        status.token = null;

        assertThrows(IllegalStateException.class, () -> {
            status.setCommitStatus(commit, BuildStatus.SUCCESS, "Build passed!", "https://example.com");
        });

    }

}