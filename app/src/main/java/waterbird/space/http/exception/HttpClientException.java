package waterbird.space.http.exception;

/**
 * Created by 高文文 on 2016/12/20.
 */

public class HttpClientException extends HttpException {
    private static final long serialVersionUID = -428451550033078957L;
    private ClientException exceptionType;

    public HttpClientException(ClientException exceptionType) {
        super(exceptionType.toString());
        this.exceptionType = exceptionType;
    }

    public HttpClientException(Throwable cause) {
        super(cause.toString(), cause);
        exceptionType = ClientException.UnkownException;
    }


    public ClientException getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(ClientException exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public String toString() {
        return "HttpClientException{" +
                "exceptionType=" + exceptionType +
                '}';
    }
}
