import org.dd2480.App;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

public class StaticHostTest {

    @Test
    public void get_missing_returns_404() {
        Javalin app = new App("localhost", 9876).app;
        JavalinTest.test(app, (server, client) -> {
            // Empty directory should return 404
            assertEquals(404, client.get("/static").code());
            // Missing file should return 404
            assertEquals(404, client.get("/static/missing_file.txt").code());
        });
    }

    @Test
    public void get_test_file_return_body() {
        Javalin app = new App("localhost", 9876).app;
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/static/test.txt");
            assertEquals(200, response.code());
            assertEquals("Hello, static!", response.body().string());
        });
    }

}
