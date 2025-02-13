import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.dd2480.App;
import org.dd2480.Commit;
import org.junit.jupiter.api.Test;
import org.dd2480.builder.BuildResult;
import org.dd2480.builder.BuildStatus;
import org.dd2480.builder.Builder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.javalin.testtools.JavalinTest;

class ListBuildsEndpointTest {

    @Test
    void returnCorrectBody_whenNoBuilds() {

        // ListBuildsHandler will call this function and get this mock object
        Builder builder = mock(Builder.class);
        when(builder.listAllBuilds())
                .thenReturn(new ArrayList<BuildResult>());

        App app = new App("localhost", 9876, builder);

        JavalinTest.test(app.app, (server, client) -> {
            var response = client.get("/builds");

            assertEquals(200, response.code(), "Incorrect response code");

            // Check that the content of the file contains specific information
            assertEquals(true, response.body().string().contains("Builds available: 0"), "Incorrect build count");
        });
    }

    @Test
    void returnCorrectBody_whenOneBuild() {

        Builder builder = mock(Builder.class);

        // Mock object
        BuildResult build = new BuildResult(new Commit("Owner", "RepoName", "ABC123", "", "main"),
                BuildStatus.SUCCESS, new ArrayList<String>(), Instant.now(), Instant.now());
        List<BuildResult> builds = new ArrayList<BuildResult>();
        builds.add(build);

        // ListBuildsHandler will call this function and get mock object
        when(builder.listAllBuilds()).thenReturn(builds);

        App app = new App("localhost", 9876, builder);

        JavalinTest.test(app.app, (server, client) -> {
            var response = client.get("/builds");

            assertEquals(200, response.code(), "Incorrect response code");

            String body = response.body().string();

            // Check that the content of the file contains specific information
            assertEquals(true, body.contains("ABC123"), "Commit info not present on page");
            assertEquals(true, body.contains("Builds available: 1"), "Incorrect build count");
        });
    }

    @Test
    void returnCorrectBody_whenSeveralBuilds() {

        Builder builder = mock(Builder.class);

        // Mock object
        BuildResult build1 = new BuildResult(new Commit("Owner", "RepoName", "ABC123", "", "main"),
                BuildStatus.SUCCESS, new ArrayList<String>(), Instant.now(), Instant.now());
        BuildResult build2 = new BuildResult(new Commit("Owner", "RepoName", "XYZ789", "", "older-branch"),
                BuildStatus.FAILURE, new ArrayList<String>(), Instant.now().minus(Duration.ofDays(1)),
                Instant.now().minus(Duration.ofDays(1)));
        List<BuildResult> builds = new ArrayList<BuildResult>();
        builds.add(build1);
        builds.add(build2);

        // ListBuildsHandler will call this function and get mock object
        when(builder.listAllBuilds()).thenReturn(builds);

        App app = new App("localhost", 9876, builder);

        JavalinTest.test(app.app, (server, client) -> {
            var response = client.get("/builds");

            assertEquals(200, response.code(), "Incorrect response code");

            String body = response.body().string();

            // Check that the content of the file contains specific information
            assertEquals(true, body.contains("ABC123"), "Commit info not present on page");
            assertEquals(true, body.contains("XYZ789"), "Commit info not present on page");
            assertEquals(true, body.contains("Builds available: 2"), "Incorrect build count");
        });
    }
}
