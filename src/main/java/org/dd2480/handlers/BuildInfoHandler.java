package org.dd2480.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.dd2480.builder.BuildResult;
import org.dd2480.builder.Builder;

import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Handler for list builds page.
 */
public class BuildInfoHandler implements Handler {

    @Override
    public void handle(Context ctx) {
        String commitHash = ctx.pathParam("commitHash");
        Builder builder = new Builder();
        BuildResult build = builder.getResult(commitHash);
        
        if (build == null) {
            ctx.status(404); // Build not found
        } else {
            var map = new HashMap<String, Object>();

            // Fills in variables present in /templates/buildInfo.ftl
            map.put("repository", build.repositoryOwner + "/" + build.repositoryName);
            map.put("hash", build.commitHash);
            map.put("status", build.status.toString());
            switch (build.status) {
                case SUCCESS -> map.put("statusStyle", "status-success");
                case FAILURE -> map.put("statusStyle", "status-failure");
                case PENDING -> map.put("statusStyle", "status-pending");
                case ERROR -> map.put("statusStyle", "status-error");
            }
            map.put("branch", build.branch);
            map.put("date", build.endTime.toString());
            map.put("logs", build.logs);

            ctx.render("/templates/buildInfo.ftl", Map.of("build", map));
        }
    }
}