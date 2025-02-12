package org.dd2480;

import org.dd2480.builder.Builder;
import org.dd2480.handlers.BuildInfoHandler;
import org.dd2480.handlers.ListBuildsHandler;
import org.dd2480.handlers.RootHandler;
import org.dd2480.handlers.WebhookHandler;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinFreemarker;

/**
 * Container class for the http server.
 */
public class App {

    public final Javalin app;
    private final String bindIp;
    private final int port;

    public Builder builder;

    /**
     * Creates a new App instance with default settings.
     *
     * @param bindIp The IP address to bind the server to.
     * @param port   The port number to bind the server to.
     */
    public App(String bindIp, int port) {
        this(bindIp, port, new Builder());
    }

    /**
     * Create a new instance of the application.
     * 
     * @param bindIp  The ip address to bind the server to.
     * @param port    The port number to bind the server to.
     * @param builder A builder object for managing build-related routes.
     */
    public App(String bindIp, int port, Builder builder) {
        this.bindIp = bindIp;
        this.port = port;
        this.app = Javalin.create(config -> {
            // Configures static file hosting, any files in the static directory will be
            // served under /static/{filename}
            config.staticFiles.add(staticConfig -> {
                // The directory to serve files from
                staticConfig.directory = "/static";
                // The path to serve files from
                staticConfig.hostedPath = "/static";
            });

            // Configures server-side-rendering using the freemarker templating engine
            // Any templates in src/main/resources/templates can be used for rendering
            config.fileRenderer(new JavalinFreemarker());
        });
        this.builder = builder;
        this.buildRoutes();
    }

    private void buildRoutes() {
        this.app.get("/", new RootHandler());
        this.app.post("/webhook", new WebhookHandler());
        this.app.get("/builds", new ListBuildsHandler(this.builder));
        this.app.get("/builds/{commitHash}", new BuildInfoHandler(this.builder));
    }

    /**
     * Starts the HTTP server and blocks execution until stopped.
     */
    public void start() {
        this.app.start(this.bindIp, this.port);
    }

}
