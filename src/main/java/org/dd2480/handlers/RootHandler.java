package org.dd2480.handlers;

import org.jetbrains.annotations.NotNull;

import io.javalin.http.Context;

/**
 * Root path handler
 * 
 * Returns 200 OK with "Hello, world!" as the response body for GET requests to
 * "/"
 * 
 * Can be used as a simple health check
 */
public class RootHandler implements io.javalin.http.Handler {

    @Override
    public void handle(@NotNull Context context) throws Exception {
        context.status(200);
        context.result("Hello, world!");
    }

}
