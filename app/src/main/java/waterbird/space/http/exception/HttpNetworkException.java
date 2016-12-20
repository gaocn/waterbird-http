package waterbird.space.http.exception;

/**
 * Created by 高文文 on 2016/12/20.
 */

public class HttpNetworkException extends HttpException {
    private static final long serialVersionUID = 3814967573717529086L;
    private NetworkException exceptionType;

    public HttpNetworkException(NetworkException type) {
        super(type.toString());
        exceptionType = type;
    }

    /**
     * 其他原因， 例如防火墙，网络信号差， 导致网络不稳定
     * @param cause
     */
    public HttpNetworkException(Throwable cause) {
        super(cause.toString(), cause);
        exceptionType = NetworkException.NetwrokUnstable;
    }

    public NetworkException getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(NetworkException exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public String toString() {
        return "HttpNetworkException{" +
                "exceptionType=" + exceptionType +
                '}';
    }
}
