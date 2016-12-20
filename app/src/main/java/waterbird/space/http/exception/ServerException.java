package waterbird.space.http.exception;

/**
 * Created by 高文文 on 2016/12/20.
 */

public enum  ServerException {
    //error_code = 500
    ServerInternalError("Server Internal Exception", "服务器内部错"),
    //error_code = 400
    ServerRejectClient("Server Reject Client Exception", "服务器拒绝或无法提供服务"),
    //redirect too many
    RedirectTooMuch("Server Redirected Too Much", "重定向次数过多");

    ServerException(String name, String chiName) {
        this.reason = name;
        this.chiReason = chiName;
    }

    public String reason;
    public String chiReason;
    private static final String TAG = "ServerException";

    @Override
    public String toString() {
        return "ServerException{" +
                "reason='" + reason + '\'' +
                ", chiReason='" + chiReason + '\'' +
                '}';
    }
}
