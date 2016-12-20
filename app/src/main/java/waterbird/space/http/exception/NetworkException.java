package waterbird.space.http.exception;

/**
 * Created by 高文文 on 2016/12/20.
 */

public enum NetworkException {
    NetworkUnavailable("Network Not Available","网络不可用"),
    NetwrokUnstable("Network Not Stable", "网络不稳定"),
    NetworkDisabled("Network Disabled", "网络被禁用"),
    NetworkUnreachable("Network Unreachable", "无法访问网络");

    NetworkException(String name, String chiName) {
        this.reason = name;
        this.chiReason = chiName;
    }
    public String reason;
    public String chiReason;
    private static final String TAG = "NetworkException";

    @Override
    public String toString() {
        return "NetworkException{" +
                "reason='" + reason + '\'' +
                ", chiReason='" + chiReason + '\'' +
                '}';
    }
}
