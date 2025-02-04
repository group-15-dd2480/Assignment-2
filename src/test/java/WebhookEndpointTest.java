import org.dd2480.handlers.WebhookHandler;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.javalin.http.Context;

public class WebhookEndpointTest {

    private final Context ctx = mock(Context.class);

    @Test
    public void missing_signature_header_respond_400() {
        WebhookHandler handler = new WebhookHandler();

        when(ctx.header("X-GitHub-Event")).thenReturn("ping");
        when(ctx.header("X-Hub-Signature-256")).thenReturn(null);

        handler.handle(ctx);

        verify(ctx).status(400);
        verify(ctx).result("No signature");
    }

    @Test
    public void missing_event_header_respond_400() {
        WebhookHandler handler = new WebhookHandler();

        when(ctx.header("X-GitHub-Event")).thenReturn(null);
        when(ctx.header("X-Hub-Signature-256")).thenReturn("sha256=1234");

        handler.handle(ctx);

        verify(ctx).status(400);
        verify(ctx).result("No event type");
    }

    @Test
    public void invalid_signature_respond_400() {
        WebhookHandler handler = new WebhookHandler();

        when(ctx.header("X-GitHub-Event")).thenReturn("ping");
        when(ctx.header("X-Hub-Signature-256")).thenReturn("sha256=1234");
        when(ctx.body()).thenReturn("body");

        handler.handle(ctx);

        verify(ctx).status(400);
        verify(ctx).result("Invalid signature");
    }

    @Test
    public void valid_ping_event_handle_ping() {
        WebhookHandler handler = new WebhookHandler();

        when(ctx.header("X-GitHub-Event")).thenReturn("ping");
        when(ctx.header("X-Hub-Signature-256"))
                .thenReturn("sha256=3ef1e4784186add576ca5c6442a3538437e3cde305289be300f3567a47ef697c");
        when(ctx.body()).thenReturn("body");

        handler.handle(ctx);

        verify(ctx).status(200);
        verify(ctx).result("pong");
    }

    @Test
    public void valid_push_event_handle_push() {
        WebhookHandler handler = new WebhookHandler();

        when(ctx.header("X-GitHub-Event")).thenReturn("push");
        when(ctx.header("X-Hub-Signature-256"))
                .thenReturn("sha256=3ef1e4784186add576ca5c6442a3538437e3cde305289be300f3567a47ef697c");
        when(ctx.body()).thenReturn("body");

        handler.handle(ctx);

        verify(ctx).status(200);
    }

    @Test
    public void unknown_event_respond_204() {
        WebhookHandler handler = new WebhookHandler();

        when(ctx.header("X-GitHub-Event")).thenReturn("unknown");
        when(ctx.header("X-Hub-Signature-256"))
                .thenReturn("sha256=3ef1e4784186add576ca5c6442a3538437e3cde305289be300f3567a47ef697c");
        when(ctx.body()).thenReturn("body");

        handler.handle(ctx);

        verify(ctx).status(204);
    }

}
