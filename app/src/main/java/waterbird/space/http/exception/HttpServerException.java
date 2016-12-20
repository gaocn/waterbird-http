package waterbird.space.http.exception;

import waterbird.space.http.data.HttpStatus;

/**
 * Created by 高文文 on 2016/12/20.
 * exception happen in server
 */

public class HttpServerException extends HttpException{
    private static final long serialVersionUID = -7573597380425118054L;
    private ServerException exceptionType;
    private HttpStatus status;

    public HttpServerException(ServerException exceptionType) {
        super(exceptionType.toString());
        this.exceptionType = exceptionType;
    }

    public HttpServerException(HttpStatus status) {
        super(status.toString());
        this.status = status;
        if(status.getCode() >= 500) {
            exceptionType = ServerException.ServerInternalError;
        } else {
            exceptionType = ServerException.ServerRejectClien;
        }
    }


    public ServerException getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(ServerException exceptionType) {
        this.exceptionType = exceptionType;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "HttpServerException{" +
                "exceptionType=" + exceptionType +
                ", status=" + status +
                '}';
    }
}
