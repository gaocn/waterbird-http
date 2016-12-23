package waterbird.space.http.response;

import java.util.ArrayList;

import waterbird.space.http.data.Constants;
import waterbird.space.http.data.HttpStatus;
import waterbird.space.http.data.NameValuePair;
import waterbird.space.http.exception.HttpException;
import waterbird.space.http.listener.StatisticListener;
import waterbird.space.http.log.HttpLog;
import waterbird.space.http.request.BaseRequest;

/**
 * Created by 高文文 on 2016/12/23.
 *
 * Inner Facade {@link InternalResponse} providing feature-rich capability to set&get request and response info.
 */

public class InternalResponse<T> implements Response<T> {
    private static final String TAG = "InternalResponse";

    /**
     * 默认响应结果中的默认字符集
     */
    protected String charset = Constants.DEFAULT_CHARSET;

    /**
     * http响应状态
     */
    protected HttpStatus httpStatus;

    /**
     * http请求重试次数
     */
    protected  int retryTimes;

    /**
     * http请求重定向次数
     */
    protected int redirectTimes;

    /**
     * 响应读取字节数
     */
    protected  long readedLength;

    /**
     * http响应内容字节数
     */
    protected  long contentLength;

    /**
     * http响应内容编码格式
     */
    protected String contentEncoding;

    /**
     * 响应内容类型
     */
    protected String contentType;

    /**
     * 请求到响应花费时间
     */
    protected long usedTime;

    /**
     * 响应头部
     */
    protected ArrayList<NameValuePair> headers;

    /**
     * 响应对应的请求
     */
    protected BaseRequest<T> request;

    /**
     * 请求过程发生的异常
     */
    protected HttpException exception;

    /**
     * //TODO
     */
    protected boolean isCacheHit;

    /**
     * 该响应的标识
     */
    protected Object tag;

    /**
     * 统计监听器，主要用于请求到响应过程中的时间消耗，读取内容大小等统计信息
     */
    protected StatisticListener statisticListener;

    public InternalResponse(BaseRequest<T> request) {
        this.request = request;
    }

    @Override
    public ArrayList<NameValuePair> getHeaders() {
        return headers;
    }

    public StatisticListener getStatisticListener() {
        return statisticListener;
    }

    public void setStatisticListener(StatisticListener statisticListener) {
        this.statisticListener = statisticListener;
    }

    public void setHeaders(ArrayList<NameValuePair> headers) {
        this.headers = headers;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public boolean isResultOk() {
        return getResult() != null;
    }

    @Override
    public T getResult() {
        return request.getDataParser().getData();
    }

    @Override
    public <R extends BaseRequest<T>> R getRequest() {
        return (R)request;
    }

    public void setRequest(BaseRequest<T> request) {
        this.request = request;
    }

    @Override
    public long getReadedLength() {
        return readedLength;
    }

    public InternalResponse setReadedLength(long readedLength) {
        this.readedLength = readedLength;
        return this;
    }

    @Override
    public long getContentLength() {
        return contentLength;
    }

    public InternalResponse setContentLength(long contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    @Override
    public String getContentEncoding() {
        return contentEncoding;
    }

    public InternalResponse setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
        return this;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public InternalResponse setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    @Override
    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        if(charset != null && !charset.isEmpty()) {
            this.charset = charset;
        }
    }

    @Override
    public long getUseTime() {
        return usedTime;
    }

    public long getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(long usedTime) {
        this.usedTime = usedTime;
    }

    @Override
    public boolean isConnectSuccess() {
        return httpStatus != null && httpStatus.isSuccess();
    }

    @Override
    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    @Override
    public int getRedirectTimes() {
        return redirectTimes;
    }

    public void setRedirectTimes(int redirectTimes) {
        this.redirectTimes = redirectTimes;
    }

    @Override
    public HttpException getException() {
        return exception;
    }

    public void setException(HttpException exception) {
        this.exception = exception;
    }

    @Override
    public boolean isCacheHit() {
        return isCacheHit;
    }

    public void setCacheHit(boolean cacheHit) {
        isCacheHit = cacheHit;
    }

    @Override
    public String getRawString() {
        return request.getDataParser().getRawString();
    }

    @Override
    public Response<T> setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public Object getTag() {
        return tag;
    }


    @Override
    public String toString() {
        return resToString();
    }

    @Override
    public String resToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("^_^\n")
                .append("____________________________ lite http response info start ____________________________")
                .append("\n url            : ").append(request.getUri())
                .append("\n status         : ").append(httpStatus)
                .append("\n charSet        : ").append(charset)
                .append("\n useTime        : ").append(usedTime)
                .append("\n retryTimes     : ").append(retryTimes)
                .append("\n redirectTimes  : ").append(redirectTimes)
                .append("\n readedLength   : ").append(readedLength)
                .append("\n contentLength  : ").append(contentLength)
                .append("\n contentEncoding: ").append(contentEncoding)
                .append("\n contentType    : ").append(contentType)
                .append("\n statistics     : ").append(statisticListener)
                .append("\n tag            : ").append(tag)
                .append("\n header         ");
        if (headers == null) {
            sb.append(": null");
        } else {
            for (NameValuePair nv : headers) {
                sb.append("\n|    ").append(nv);
            }
        }
        sb.append("\n ").append(request)
                .append("\n exception      : ").append(exception)
                .append("\n.")
                .append("\n _________________ data-start _________________")
                .append("\n ").append(getResult())
                .append("\n _________________ data-over _________________")
                .append("\n.")
                .append("\n model raw string     : ").append(getRawString())
                .append("\n____________________________ lite http response info end ____________________________");
        return sb.toString();
    }

    @Override
    public void printInfo() {
        HttpLog.d(TAG, resToString());
    }
}
