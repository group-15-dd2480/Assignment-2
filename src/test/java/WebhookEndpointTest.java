
import java.io.IOException;

import org.dd2480.Commit;
import org.dd2480.GithubStatus;
import org.dd2480.builder.BuildResult;
import org.dd2480.builder.BuildStatus;
import org.dd2480.builder.Builder;
import org.dd2480.handlers.WebhookHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.javalin.http.Context;

public class WebhookEndpointTest {

    private final Context ctx = mock(Context.class);
    private static final Builder builder = mock(Builder.class);
    private static WebhookHandler handler;

    @BeforeAll
    public static void setup() throws IOException {
        BuildResult result = new BuildResult();
        result.status = BuildStatus.SUCCESS;
        when(builder.buildProject(any(Commit.class))).thenReturn(result);

        GithubStatus status = new GithubStatus();
        GithubStatus statusSpy = spy(status);
        doNothing().when(statusSpy).setCommitStatus(any(Commit.class), any(BuildStatus.class), anyString(), anyString());

        handler = new WebhookHandler(builder);
        handler.githubWebhookSecret = "testingsecretdontuseme";
        handler.githubStatus = statusSpy;
    }

    @Test
    public void missing_signature_header_respond_400() {
        when(ctx.header("X-GitHub-Event")).thenReturn("ping");
        when(ctx.header("X-Hub-Signature-256")).thenReturn(null);

        handler.handle(ctx);

        verify(ctx).status(400);
        verify(ctx).result("No signature");
    }

    @Test
    public void missing_event_header_respond_400() {
        when(ctx.header("X-GitHub-Event")).thenReturn(null);
        when(ctx.header("X-Hub-Signature-256")).thenReturn("sha256=1234");

        handler.handle(ctx);

        verify(ctx).status(400);
        verify(ctx).result("No event type");
    }

    @Test
    public void invalid_signature_respond_400() {
        when(ctx.header("X-GitHub-Event")).thenReturn("ping");
        when(ctx.header("X-Hub-Signature-256")).thenReturn("sha256=1234");
        when(ctx.body()).thenReturn("body");

        handler.handle(ctx);

        verify(ctx).status(400);
        verify(ctx).result("Invalid signature");
    }

    @Test
    public void valid_ping_event_handle_ping() {
        when(ctx.header("X-GitHub-Event")).thenReturn("ping");
        when(ctx.header("X-Hub-Signature-256"))
                .thenReturn("sha256=3ef1e4784186add576ca5c6442a3538437e3cde305289be300f3567a47ef697c");
        when(ctx.body()).thenReturn("body");

        handler.handle(ctx);

        verify(ctx).status(200);
        verify(ctx).result("pong");
    }

    @Test
    public void invalid_push_event_json_respond_400() {
        when(ctx.header("X-GitHub-Event")).thenReturn("push");
        when(ctx.header("X-Hub-Signature-256"))
                .thenReturn("sha256=3ef1e4784186add576ca5c6442a3538437e3cde305289be300f3567a47ef697c");
        when(ctx.body()).thenReturn("body");

        handler.handle(ctx);

        verify(ctx).status(400);
        verify(ctx).result("Invalid JSON");
    }

    @Test
    public void valid_push_event_handle_push() {
        when(ctx.header("X-GitHub-Event")).thenReturn("push");
        when(ctx.header("X-Hub-Signature-256"))
                .thenReturn("sha256=fc30fe21b7cbf89c5084b6a1da6c1d47d3968c8280aedda7687bd152d91cd09e");
        when(ctx.body()).thenReturn(
                "{\"ref\":\"refs/heads/master\",\"commits\":[{\"id\":\"12345\",\"message\":\"This is a commit message\"}],\"repository\":{\"name\":\"my-repo\",\"owner\":{\"name\":\"my-username\"}}}");

        handler.handle(ctx);

        verify(ctx).status(200);
    }

    @Test
    public void unknown_event_respond_204() {
        when(ctx.header("X-GitHub-Event")).thenReturn("unknown");
        when(ctx.header("X-Hub-Signature-256"))
                .thenReturn("sha256=3ef1e4784186add576ca5c6442a3538437e3cde305289be300f3567a47ef697c");
        when(ctx.body()).thenReturn("body");

        handler.handle(ctx);

        verify(ctx).status(204);
    }

}
