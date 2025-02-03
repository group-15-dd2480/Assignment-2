import org.dd2480.handlers.RootHandler;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.javalin.http.Context;

public class RootEndpointTest {

    private final Context ctx = mock(Context.class);

    @Test
    public void get_return_200() {
        RootHandler handler = new RootHandler();
        try {
            handler.handle(ctx);
        } catch (Exception ex) {
            assert false;
        }
        verify(ctx).status(200);
        verify(ctx).result("Hello, world!");
    }

}
