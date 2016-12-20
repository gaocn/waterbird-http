package waterbird.space.http.exception;

/**
 * Created by 高文文 on 2016/12/20.
 *
 *base http exception happen during request
 */

public class HttpException extends Exception {
    private static final long serialVersionUID = -395586005314497039L;
    public static boolean printStackTrace = true;
    protected boolean handled = true;

    public boolean isHandle() {
        return handled;
    }

    public <T extends HttpException> T setHandled(boolean handled) {
        this.handled = handled;
        return (T) this;
    }

    public HttpException() {
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return "HttpException{" +
                "handled=" + handled +
                "} " + super.toString();
    }
}
