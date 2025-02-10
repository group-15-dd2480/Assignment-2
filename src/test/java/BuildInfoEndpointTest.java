import org.dd2480.App;
import org.dd2480.Commit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.dd2480.builder.BuildResult;
import org.dd2480.builder.BuildStatus;
import org.dd2480.builder.Builder;
import org.dd2480.handlers.BuildInfoHandler;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

public class BuildInfoEndpointTest {

    @Test
    public void return404_whenRouteContainsIncorrectHash() {
        Javalin app = new App("localhost", 9876).app;
        JavalinTest.test(app, (server, client) -> {
            // Wrong hash should not give anything
            assertEquals(404, client.get("/builds/nonExistentHash").code());
        });
    }

    @Test
    public void returnBody_whenRouteContainsCorrectHash() {
        App app = new App("localhost", 9876);

        // BuildInfoHandler will call this function and expect a somewhat correct
        // BuildResult object
        Builder builder = mock(Builder.class);
        when(builder.getResult("existentHash"))
                .thenReturn(new BuildResult(new Commit("Owner", "Name", "existentHash", "Message", "main"),
                        BuildStatus.SUCCESS, new ArrayList<String>(), Instant.now(), Instant.now()));

        app.builder = builder;

        JavalinTest.test(app.app, (server, client) -> {
            var response = client.get("/builds/existentHash");

            // Check for correct response code
            assertEquals(200, response.code(), "Incorrect response code");

            // Check that the content of the file contains BuildResult specific information
            assertEquals(true, response.body().string().contains("existentHash"),
                    "BuildResult specific information could not be found");
        });
    }
}
