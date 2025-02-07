package org.dd2480;

import org.dd2480.handlers.RootHandler;
import org.dd2480.handlers.WebhookHandler;

import io.javalin.Javalin;

/**
 * Container class for the http server.
 */
public class App {

    public final Javalin app;
    private final String bindIp;
    private final int port;

    /**
     * Create a new instance of the application.
     * 
     * @param bindIp The ip to bind the server to.
     * @param port   The port to bind the server to.
     */
    public App(String bindIp, int port) {
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
        });
        this.buildRoutes();
    }

    /**
     * Add all routes for the application here.
     */
    private void buildRoutes() {
        this.app.get("/", new RootHandler());
        this.app.post("/webhook", new WebhookHandler());
    }

    /**
     * Start the http server and block.
     */
    public void start() {
        this.app.start(this.bindIp, this.port);
    }

}
