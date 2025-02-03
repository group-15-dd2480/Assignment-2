package org.dd2480;

import org.dd2480.handlers.RootHandler;

import io.javalin.Javalin;

/**
 * Container class for the http server.
 */
public class App {

    public final Javalin app;
    private final String bindIp;
    private final int port;

    public App(String bindIp, int port) {
        this.bindIp = bindIp;
        this.port = port;
        this.app = Javalin.create();
        this.buildRoutes();
    }

    /**
     * Add all routes for the application here.
     */
    private void buildRoutes() {
        this.app.get("/", new RootHandler());
    }

    /**
     * Start the http server and block.
     */
    public void start() {
        this.app.start(this.bindIp, this.port);
    }

}
