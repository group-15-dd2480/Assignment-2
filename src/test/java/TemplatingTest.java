import java.util.Map;

import org.dd2480.App;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

public class TemplatingTest {

    @Test
    public void rendered_page_contains_checksum() {
        String checksum = "1234567890";

        Javalin app = new App("localhost", 9876).app;
        JavalinTest.test(app, (server, client) -> {
            app.get("/rendertest", ctx -> {
                ctx.render("/templates/test.ftl", Map.of("checksum", checksum));
            });
            var response = client.get("/rendertest");
            assertEquals(200, response.code(), "Response code is not 200");
            assertTrue(response.body().string().contains(checksum), "Checksum not found in rendered page");
        });
    }

}
