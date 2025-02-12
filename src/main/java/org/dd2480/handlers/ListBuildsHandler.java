package org.dd2480.handlers;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dd2480.builder.BuildResult;
import org.dd2480.builder.Builder;

import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Handles requests for listing all builds in the system.
 * 
 * This handler retrieves all build results from the builder, sorts them by
 * completion time (most recent first), and renders them using the
 * `listBuilds.ftl` template.
 */
public class ListBuildsHandler implements Handler {

    private Builder builder;

    public ListBuildsHandler(Builder builder) {
        this.builder = builder;
    }

    @Override
    public void handle(Context ctx) {
        List<BuildResult> builds = builder.listAllBuilds();

        if (builds == null) {
            ctx.status(404); // Builds not found
        } else {
            var map = new HashMap<String, Object>();

            map.put("count", builds.size());

            // Sorts the builds so that the most recent build comes first
            builds.sort((b1, b2) -> b2.endTime.compareTo(b1.endTime));

            var buildMaps= new ArrayList<Map<String,Object>>();
            for (var build : builds) {
                buildMaps.add(getBuildMap(build));
            }
            map.put("builds", buildMaps);

            ctx.render("/templates/listBuilds.ftl", Map.of("model", map));
        }
    }

    private Map<String, Object> getBuildMap(BuildResult build) {
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

        // Format time to look presentable before putting it in the map
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime zonedDateTime = build.endTime.atZone(ZoneId.systemDefault());
        String formattedDateTime = zonedDateTime.format(formatter);
        map.put("date", formattedDateTime);

        return map;
    }
}