package org.dd2480.handlers;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final String GITHUB_WEBHOOK_SECRET = System.getenv().getOrDefault("GITHUB_WEBHOOK_SECRET",
            "testingsecretdontuseme");
    private static final String SIGNATURE_ALGORITHM = "HmacSHA256";
    private static final Logger logger = LoggerFactory.getLogger(WebhookHandler.class);

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
        if (!verifySignature(signature, GITHUB_WEBHOOK_SECRET, body)) {
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
                handlePush(ctx);
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
    private void handlePush(Context ctx) {
        // TODO: Handle push event properly once build system is implemented
        ctx.result("push");
        ctx.status(200);
    }

    /**
     * Verify the signature of some string payload.
     * 
     * @param signature The signature to verify
     * @param key       the key to use for the HMAC algorithm
     * @param payload   the payload to verify
     * @return true iff the signature is valid, false if not or if an error occurred
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

}
