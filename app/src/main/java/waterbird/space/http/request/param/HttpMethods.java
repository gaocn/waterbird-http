package waterbird.space.http.request.param;

/**
 * Created by 高文文 on 2016/12/19.
 */

public enum HttpMethods {

    /************************* HTTP Query Request Methods ********************************/
    /**
     * 该方法用以获取资源的表述。
     * 请求：只有header，没有body。
     * 响应：对应请求URI的资源表述，通常带有body。
     */
    GET("GET"),

    /**
     * 使用该方法可以获取与GET响应相同的header，但是响应中没有任何body。
     */
    HEAD("HEAD"),

    /**
     * 回显服务器接收到的header。支持该方法的服务器可能存在XST安全隐患。
     * 请求：header与body。
     * 响应：body中包含整个请求消息。
     */
    TRACE("TRACE"),

    /**
     * 使用该方法来获取资源支持的HTTP方法列表，或者ping服务器。
     * 请求：只有header没有body。
     * 响应：默认只有header，但是也可以在body中添加内容，比如描述性文字

         # 测试对应资源所支持的方法
         OPTIONS /test-options HTTP/1.1
         Host: localhost
         # 响应
         HTTP/1.1 204 No Content
         Allow: GET, POST, OPTIONS
     */
    OPTIONS("OPRION"),

    /**
     * 使用该方法来删除资源。对于客户端而言，资源在成功响应后，就不复存在了。
     * 请求：只有header，没有body。
     * 响应：成功或失败。body中可以包含操作的状态。
     */
    DELETE("DELETE"),

    /************************* HTTP Update Request Methods ********************************/
    /**
     * 让资源在服务器上执行一系列操作，如创建新资源、更新资源、变更资源等。
     * POST /prompt/delete HTTP/1.1
     */
    POST("POST"),

    /**
     * 完整地更新或替换一个现有资源
     * 请求：一个资源的表述。
     * 响应：更新的状态。
     */
    PUT("PUT"),

    /**
     * 实体中包含一个表，表中说明与该URI所表示的原内容的区别。
     *  incremental update
     */
    PATCH("PATCH");

    HttpMethods(String name) {
        this.httpMethodName = name;
    }
    private String httpMethodName;

    public String getHttpMethodName() {
        return httpMethodName;
    }
}
