package org.dd2480.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.dd2480.Commit;
import org.dd2480.GithubStatus;
import org.dd2480.builder.BuildResult;
import org.dd2480.builder.BuildStatus;
import org.dd2480.builder.Builder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Handler for the webhook endpoint.
 *
 * Respond on {@code POST /webhook}
 *
 * Accepts GitHub webhook events and verifies the signature.
 *
 * Only {@code ping} and {@code push} events are supported.
 */
public class WebhookHandler implements Handler {

    public String githubWebhookSecret = System.getenv().getOrDefault("GITHUB_WEBHOOK_SECRET",
            "testingsecretdontuseme");
    private static final String SIGNATURE_ALGORITHM = "HmacSHA256";
    private static final Logger logger = LoggerFactory.getLogger(WebhookHandler.class);
    private static final Gson gson = new Gson();

    public Builder builder;
    public GithubStatus githubStatus;

    public WebhookHandler(Builder builder) {
        this.builder = builder;
        this.githubStatus = new GithubStatus();
    }

    @Override
    public void handle(@NotNull Context ctx) {
        String event = ctx.header("X-GitHub-Event");
        String signature = ctx.header("X-Hub-Signature-256");
        String body = ctx.body();

        // 400 if missing signature
        if (signature == null || signature.isEmpty()) {
            logger.warn("Received webhook event without signature");
            ctx.status(400);
            ctx.result("No signature");
            return;
        }

        // 400 if missing event type
        if (event == null || event.isEmpty()) {
            logger.warn("Received webhook event without event type");
            ctx.status(400);
            ctx.result("No event type");
            return;
        }

        // Verify the signature
        if (!verifySignature(signature, githubWebhookSecret, body)) {
            logger.warn("Received webhook event with invalid signature");
            ctx.status(400);
            ctx.result("Invalid signature");
            return;
        }

        switch (event) {
            case "ping" -> {
                handlePing(ctx);
            }
            case "push" -> {
                handlePush(ctx, body);
            }
            default -> {
                logger.warn("Received webhook event with unknown event type: " + event);
                ctx.status(204);
            }
        }
    }

    /**
     * Handle a ping event.
     *
     * @param ctx The Javalin context for the request
     */
    private void handlePing(Context ctx) {
        ctx.result("pong");
        ctx.status(200);
    }

    /**
     * Handle a push event.
     *
     * @param ctx The Javalin context for the request
     */
    private void handlePush(Context ctx, String body) {
        WebhookJson json;
        try {
            json = gson.fromJson(body, WebhookJson.class);
        } catch (JsonSyntaxException e) {
            ctx.status(400);
            ctx.result("Invalid JSON");
            return;
        }
        String owner = json.repository.owner.name;
        String name = json.repository.name;
        String branch = json.ref.replace("refs/heads/", "");
        for (WebhookJson.WebhookJsonCommit commitJson : json.commits) {
            Commit commit = new Commit(
                    owner,
                    name,
                    commitJson.id,
                    commitJson.message,
                    branch);
            try {
                githubStatus.setCommitStatus(commit, BuildStatus.PENDING, "Build pending",
                        "http://localhost:3333/builds/" + commit.hash);
            } catch (IOException e) {
                logger.error("Failed to set commit status", e);
            } catch (IllegalStateException e) {
                logger.error("Failed to set commit status, missing token?");
            }
            BuildResult result = builder.buildProject(commit);
            try {
                githubStatus.setCommitStatus(commit, result.status, "Build " + result.status.toString().toLowerCase(),
                        "http://localhost:3333/builds/" + commit.hash);
            } catch (IOException e) {
                logger.error("Failed to set commit status", e);
            } catch (IllegalStateException e) {
                logger.error("Failed to set commit status, missing token?");
            }
        }

        logger.info("Received push event for " + owner + "/" + name + " on branch " + branch);

        ctx.result("push");
        ctx.status(200);
    }

    /**
     * Verify the signature of some string payload.
     *
     * @param signature The signature to verify
     * @param key the key to use for the HMAC algorithm
     * @param payload the payload to verify
     * @return true iff the signature is valid, false if not or if an error
     * occurred
     */
    public boolean verifySignature(String signature, String key, String payload) {
        try {
            // Calculate the HMAC256 hash of the payload
            Mac mac = Mac.getInstance(SIGNATURE_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), SIGNATURE_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte b : hmacBytes) {
                hash.append(String.format("%02x", b));
            }
            String expectedSignature = "sha256=" + hash.toString();
            // Compare the calculated hash with the expected hash
            return MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            logger.error("No such algorithm when verifying signature", e);
        } catch (InvalidKeyException e) {
            logger.error("Invalid key when verifying signature", e);
        } catch (IllegalStateException e) {
            logger.error("Illegal state when verifying signature", e);
        }

        return false;
    }

    /**
     * Class to represent the JSON payload of a GitHub push event webhook
     * payload.
     */
    private class WebhookJson {

        String ref;
        List<WebhookJsonCommit> commits;
        WebhookJsonRepository repository;

        private class WebhookJsonCommit {

            public String id;
            public String message;
        }

        private class WebhookJsonRepository {

            public String name;
            public WebhookJsonRepositoryOwner owner;

            private class WebhookJsonRepositoryOwner {

                public String name;
            }
        }
    }

}
