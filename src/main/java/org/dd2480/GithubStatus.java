package org.dd2480;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.dd2480.builder.BuildStatus;

public class GithubStatus {
    /**
     * Updates the status of a commit using the GitHub Status API.
     *
     * @param commit      Commit object containing relevant information.
     * @param status      The state of the status (success, failure, error, or
     *                    pending).
     * @param description A short description of the status.
     * @param targetUrl   A link for more details.
     * @throws IOException           If an error occurs during the API request.
     * @throws IllegalStateException If the GITHUB_TOKEN environment variable is
     *                               missing.
     */
    public static void setCommitStatus(Commit commit, BuildStatus status, String description, String targetUrl)
            throws IOException {
        // Get the GitHub token from the environment
        String token = System.getenv("GITHUB_TOKEN");
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("GitHub token is missing. Set the GITHUB_TOKEN environment variable.");
        }

        // Create and open the connection
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/statuses/%s", commit.repositoryOwner,
                commit.repositoryName, commit.hash);
        URI uri = URI.create(apiUrl);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set appropriate headers
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "token " + token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
        connection.setDoOutput(true);

        // Create the status to be sent
        String commitStatus = String.format(
                "{ \"state\": \"%s\", \"description\": \"%s\", \"target_url\": \"%s\" }",
                status.name().toLowerCase(), // Convert Enum to lowercase string
                description.replace("\"", "\\\""), // Remove double quotes
                targetUrl);

        // Write the status to the connection output stream
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = commitStatus.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);

        } catch (IOException e) {
            System.err.println("Error writing to connection output stream: " + e.getMessage());
        }

        int responseCode = connection.getResponseCode();
        // Read the error response from GitHub
        if (responseCode != 201) {
            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) { // Check if error stream exists
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(errorStream, StandardCharsets.UTF_8))) {
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    System.err.println("GitHub API Error Response: " + response.toString());
                }
            } else {
                System.err.println("No error stream available.");
            }
            throw new RuntimeException("Failed to update status: HTTP " + responseCode);
        } else {
            // Read success response
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("GitHub API Success Response: " + response.toString());
            }
        }
    }
}