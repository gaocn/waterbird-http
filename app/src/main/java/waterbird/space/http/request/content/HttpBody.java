package waterbird.space.http.request.content;

import java.io.IOException;
import java.io.OutputStream;

import waterbird.space.http.HttpConfig;
import waterbird.space.http.listener.HttpListener;
import waterbird.space.http.request.BaseRequest;

/**
 * Created by 高文文 on 2016/12/19.
 */

public abstract class HttpBody {
    protected static final int OUTPUT_BUFFER_SIZE = HttpConfig.DEFAULT_BUFFER_SIZE;
    protected HttpListener httpListener;
    protected BaseRequest request;
    protected String contentType;
    protected String contentEncoding;

    public HttpListener getHttpListener() {
        return httpListener;
    }

    public void setHttpListener(HttpListener httpListener) {
        this.httpListener = httpListener;
    }

    public BaseRequest getRequest() {
        return request;
    }

    public void setRequest(BaseRequest request) {
        this.request = request;
        setHttpListener(request.getHttpListener());
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    /********       Abstract API           *********/
    public abstract long getContentLength();
    public abstract void writeTo(OutputStream outputStream) throws IOException;
}
