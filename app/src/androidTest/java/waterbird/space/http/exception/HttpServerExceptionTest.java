package waterbird.space.http.exception;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Random;

/**
 * Created by 高文文 on 2016/12/20.
 */
/*
    //error_code = 500
    ServerInternalError("Server Internal Exception", "服务器内部错"),
    //error_code = 400
    ServerRejectClien("Server Reject Client Exception", "服务器拒绝或无法提供服务"),
    //redirect too many
    RedirectTooMuch("Server Redirected Too Much", "重定向次数过多");
 */
public class HttpServerExceptionTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testServerInternalError() throws Exception{
        thrown.expect(HttpServerException.class);
        thrown.expectMessage("Server Internal Exception");
        testServer();
    }

    @Test
    public void testServerRejectClien() throws Exception{
        thrown.expect(HttpServerException.class);
        thrown.expectMessage("Server Reject Client Exception");
        testServer();
    }

    @Test
    public void testRedirectTooMuch() throws Exception{
        thrown.expect(HttpServerException.class);
        thrown.expectMessage("Server Redirected Too Much");
        testServer();
    }

    private void testServer() throws Exception{
        int code = new Random().nextInt(3);
        switch (code) {
            case 0 :
                throw new HttpServerException(ServerException.ServerInternalError);
            case 1:
                throw new HttpServerException(ServerException.ServerRejectClient);
            case 2:
                throw new HttpServerException(ServerException.RedirectTooMuch);
        }
    }

    @Test
    public void testAllCases() throws Exception{
        testServerRejectClien();
        testServerInternalError();
        testRedirectTooMuch();
    }
}