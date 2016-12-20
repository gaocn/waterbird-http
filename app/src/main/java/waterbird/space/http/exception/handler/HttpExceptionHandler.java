package waterbird.space.http.exception.handler;

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
 *
 * Handle Http Exception on UI Thread
 */

public abstract class HttpExceptionHandler {

    /**
     * handle kinds of exceptions
     */
    public HttpExceptionHandler handlerException(HttpException e) {
        if(e != null) {
            if(e instanceof HttpClientException) {
                HttpClientException exception = (HttpClientException) e;
                onClientException(exception, exception.getExceptionType());
            } else if(e instanceof HttpServerException) {
                HttpServerException exception = (HttpServerException) e;
                onServerException(exception, exception.getExceptionType(), exception.getStatus());
            } else if(e instanceof HttpNetworkException) {
                HttpNetworkException exception = (HttpNetworkException) e;
                onNetworkException(exception, exception.getExceptionType());
            } else {
                HttpClientException exception = (HttpClientException) e;
                onClientException(exception, exception.getExceptionType());
            }
            e.setHandled(true);
        }
        return this;
    }


    /**
     * callback function to be processed on UI thread, such as url is null other client exception
     * @param e
     * @param type
     */
    protected abstract void onClientException(HttpClientException e, ClientException type);

    /**
     * processed on UI thread
     * callback function when server error happened status_code in[400, 599]
     * [400, 499]: client error caused server to deny offering service
     * [500, 599]: server error cased server unable to offering service
     * in both case: network is avaiable

     * @param e
     * @param type
     */
    protected abstract void onServerException(HttpServerException e, ServerException type, HttpStatus status);

    /**
     * processed on UI thread
     * callback function when network is unavaiable, distable, etc
     * @param e
     * @param type
     */
    protected abstract void onNetworkException(HttpNetworkException e, NetworkException type);
}
