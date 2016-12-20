package waterbird.space.http.exception;

/**
 * Created by 高文文 on 2016/12/20.
 */

public enum ClientException {
    UrlIsNull("Url is Null", "Url 为空！"),
    IllegalSchema("illegal schema", "非法的协议schema"),
    ContextNeeded("Context Needed[detect or disable network, etx]", "需要Context[检测或禁用网络]"),
    PermissionDenied("Need NETWORK_ACCESS permission", "未获取访问网络或SD卡权限"),
    UnkownException("Client unknown exception", "客户端未知异常");

    ClientException(String name, String chiName) {
        this.reason = name;
        this.chiReason = chiName;
    }
    public String reason;
    public String chiReason;
    private static final String TAG = "ClientException";

    @Override
    public String toString() {
        return "ClientException{" +
                "reason='" + reason + '\'' +
                ", chiReason='" + chiReason + '\'' +
                '}';
    }
}
