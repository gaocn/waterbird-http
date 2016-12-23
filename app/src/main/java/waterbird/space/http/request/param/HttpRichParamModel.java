package waterbird.space.http.request.param;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;

import waterbird.space.http.listener.HttpListener;
import waterbird.space.http.request.JsonRequest;
import waterbird.space.http.request.builder.ModelQueryBuilder;
import waterbird.space.http.request.content.HttpBody;

/**
 * Created by 高文文 on 2016/12/19.
 *
 * 实现{@link HttpRichParamModel}的类会被解析为HTTP Parameters
 */

public abstract class HttpRichParamModel<T> implements HttpParamModel {
    private static final String TAG = "HttpRichParamModel";
    HttpListener<T> httpListener;

    /**
     *  request parameters are attached to URL as query string
     *  请求参数作为查询字符串的形式最佳到URL
     */
    public boolean isFieldsAttachToUrl() {
        return true;
    }

    public final LinkedHashMap<String, String> getHeader(){
        return createHeader();
    }

    public final HttpBody getHttpBody() {
        return createHttpBody();
    }

    public final HttpListener<T> getHttpListener() {
        if(httpListener == null) {
            httpListener = createHttpListener();
        }
        return httpListener;
    }

    public ModelQueryBuilder getModelQueryBuilder() {
        return createModelQueryBuilder();
    }

    /*______________________   should be implemented     ____________________________*/

    /**
     * create headers of request
     */
    protected LinkedHashMap<String,String> createHeader() {
        return null;
    }

    /**
     * create body for request(PUT，POST...)
     * @return {@link StringBody} {@link UrlEncodedFormBody}, {@link MultipartBody}...
     */
    protected HttpBody createHttpBody() {
        return null;
    }

    /**
     * create HttpListener for request
     */
    protected HttpListener<T> createHttpListener() {
        return null;
    }

    protected <R extends HttpRichParamModel<T>>R setHttpListener(HttpListener<T> listener) {
        this.httpListener = listener;
        return (R) this;
    }

    /**
     * create URI Query Builder for request
     */
    public ModelQueryBuilder createModelQueryBuilder() {
        return null;
    }

    public JsonRequest<T> buildRequest() {
        Type  type = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return new JsonRequest<T>(this, type);
    }

}
