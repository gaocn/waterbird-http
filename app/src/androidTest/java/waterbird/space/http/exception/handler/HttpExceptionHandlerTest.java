package waterbird.space.http.exception.handler;

import org.junit.Test;

import java.util.Random;

import waterbird.space.http.data.HttpStatus;
import waterbird.space.http.exception.ClientException;
import waterbird.space.http.exception.HttpClientException;
import waterbird.space.http.exception.HttpException;
import waterbird.space.http.exception.HttpNetworkException;
import waterbird.space.http.exception.HttpServerException;
import waterbird.space.http.exception.NetworkException;
import waterbird.space.http.exception.ServerException;

/**
 * Created by 高文文 on 2016/12/20.
 */
public class HttpExceptionHandlerTest {

    @Test
    public void handlerException() throws Exception {
        new HttpExceptionHandler(){
            @Override
            protected void onClientException(HttpClientException e, ClientException type) {
                e.printStackTrace();
            }

            @Override
            protected void onServerException(HttpServerException e, ServerException type, HttpStatus status) {
                e.printStackTrace();
            }

            @Override
            protected void onNetworkException(HttpNetworkException e, NetworkException type) {
                e.printStackTrace();
            }
        }.handlerException(randomException());
    }

    private HttpException randomException(){
        int code = new Random().nextInt(3);
        switch (code) {
            case 0 :
                return new HttpServerException(ServerException.ServerInternalError);
            case 1:
                return new HttpNetworkException(NetworkException.NetworkUnreachable);
            case 2:
                return new HttpClientException(ClientException.IllegalSchema);
        }
        return new HttpClientException(ClientException.UnkownException);
    }
}