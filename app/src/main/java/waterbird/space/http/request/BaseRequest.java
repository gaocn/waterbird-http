package waterbird.space.http.request;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import waterbird.space.http.data.Constants;
import waterbird.space.http.data.NameValuePair;
import waterbird.space.http.exception.ClientException;
import waterbird.space.http.exception.HttpClientException;
import waterbird.space.http.listener.GlobalHttpListener;
import waterbird.space.http.listener.HttpListener;
import waterbird.space.http.log.HttpLog;
import waterbird.space.http.parser.DataParser;
import waterbird.space.http.request.builder.QueryBuilder;
import waterbird.space.http.request.content.HttpBody;
import waterbird.space.http.request.param.CacheMode;
import waterbird.space.http.request.param.HttpMethods;
import waterbird.space.http.request.param.HttpParamModel;
import waterbird.space.http.request.param.HttpRichParamModel;
import waterbird.space.http.utils.HexUtil;
import waterbird.space.http.utils.MD5Util;
import waterbird.space.http.utils.UriUtil;


/**
 * Base Reuest for {@link waterbird.space.http.WaterBirdHttp}
 * Created by 高文文 on 2016/12/19.
 */

public abstract class BaseRequest<T> {
    private static final String TAG = "BaseRequest";
    /**
     *^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$
     * Encoded URL:
     */
    //TODO  有待验证
    private static final String ENCODED_URL_PATTERN = "^.+\\?(%[0-9a-fA-F]+|[=&0-9a-zA-Z_#\\-\\.\\*])*$";

    /**
     * give an id to request
     */
    private long id;

    /**
     * base url for request
     *
     * format:  schema + host + [path]
     * example: http|https://www.govind.space
     *
     * if {@link #}
     *
     */
    private String baseUrl;

    /**
     * uri for http request
     * example:  /path/index.html
     *
     * general uri is a path which will be appened in {@link #baseUrl}, if baseUrl is null uri must
     * be completed provided in format "schema + host + path";
     */
    private String uri;

    /**
     * {@link #fullUri} will be constructed using baseUrl + uri
     *
     * example:  https://www.govind.space/path/index.html
     */
    private String fullUri;

    /**
     * HttpMethods such as:  get, post, put, delete, head, etc
     *
     * Note: default HttpMethod is {@link HttpMethods#GET}, methods list is:
     *
     *  {@link HttpMethods#GET}, {@link HttpMethods#HEAD}, {@link HttpMethods#TRACE},
     *  {@link HttpMethods#OPTIONS}, {@link HttpMethods#DELETE}, {@link HttpMethods#PUT},
     *  {@link HttpMethods#POST}, {@link HttpMethods#PATCH}
     */
    private HttpMethods method;

    /**
     * custom tag of request
     */
    private Object tag;

    /**
     * charset of requset, default is utf-8
     */
    private String charSet;


    /**
     * max times of retries if request failed
     */
    private int maxRetryTimes = -1;

    /**
     * max times of redirecting
     */
    private int maxRediectTimes = -1;

    /**
     * connected timeout of http request
     */
    private int connectionTimeout = -1;

    /**
     * socket timeouot of TCP/IP connection
     */
    private int socketTimeout = -1;

    /**
     * callback api of start, success, failure, retry, redirect, loading, and so on..
     */
    private HttpListener<T> httpListener;


    /**
     * indicating if this request has been cancelled
     */
    private AtomicBoolean isCancelled = new AtomicBoolean();

    /**
     * request cache mode
     *
     * default Cachemode is {@link CacheMode#NetOnly}, means use no cache
     *
     * available cachemode are:
     *  {@link CacheMode#NetOnly}, {@link CacheMode#NetFirst}
     *  {@link CacheMode#CacheFirst}, {@link CacheMode#CacheOnly}
     */
    private CacheMode cacheMode;

    /**
     *  key for cached data
     */
    private String cacheKey;

    /**
     *  dir for cached data
     */
    private String cacheDir;

    /**
     *  expired cache time for cache data in millis
     */
    private long expiredCacheTimeInMillis = -1;

    /**
     * intelligentlt translate JOPO into Key-Value(Map: key=value) parameters
     */
    private HttpParamModel paramModel;



    /**
     * use {@link QueryBuilder} to custom request into multiple format such as xml, json, etc
     * default query builder translate request into json format
     *
     * Note: default query model is {@link waterbird.space.http.request.builder.JsonQueryBuilder}
     */
    private QueryBuilder queryBuilder;


    /**
     * body of http request for post, put
     */
    private HttpBody httpBody;


    /**
     * add custom header parameters to request
     */
    private Map<String, String> headers;

    /**
     * key-value pair parameters of request
     */
    private Map<String, String> requestParams;

    /**
     * custom http data parser.
     *
     * such as:
     * { com.litesuits.http.parser.impl.BytesParser}
     * { com.litesuits.http.parser.impl.StringParser}
     * { com.litesuits.http.parser.impl.FileParser}
     * { com.litesuits.http.parser.impl.BitmapParser}
     * { com.litesuits.http.parser.impl.JsonParser}
     */
    protected DataParser<T> dataParser;

    /**
     * global http listener for request
     */
    private GlobalHttpListener globalHttpListener;

    /**
     * parameters field used for what ?? unkown ??  ^_^
     */
    private Map<String, Field> paramFieldMap = null;

    /*_______________________  contructors ____________________*/
    public BaseRequest(String uri) {
        this.uri = uri;
    }

    public BaseRequest(HttpParamModel paramModel) {
        setParamModel(paramModel);
    }

    public BaseRequest(HttpParamModel paramModel, HttpListener<T> listener) {
        this(paramModel);
        setHttpListener(listener);
    }

    public BaseRequest(String uri, HttpParamModel paramModel) {
        this(uri);
        setParamModel(paramModel);
    }

    /*_______________________  Abstract Method ___________________*/

    /**
     *  data parser user to parse request
     */
    public abstract DataParser<T> createDataParser();


    /*______________________  getters & setters __________________*/

    /**
     * get data patser, which should be a sub class of DataParser
     * @param <D>
     * @return
     */
    public <D extends DataParser<T>> D getDataParser() {
        if(dataParser == null) {
            setDataParser(createDataParser());
        }
        return (D)dataParser;
    }

    /**
     * set data parser for  request, and return this request for "chaining" invoking
     *
     * @param dataParser
     * @param <S>
     * @return
     */
    public <S extends BaseRequest<T>> S setDataParser(DataParser<T> dataParser) {
        this.dataParser = dataParser;
        this.dataParser.setRequest(this);
        return (S)this;
    }

    public long getId() {
        return id;
    }

    public <S extends BaseRequest<T>> S setId(long id) {
        this.id = id;
        return (S)this;
    }

    public String getUri() {
        return uri;
    }

    public <S extends BaseRequest<T>> S setUri(String uri) {
        this.uri = uri;
        return (S)this;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public <S extends BaseRequest<T>> S setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return (S)this;
    }

    public HttpMethods getMethod() {
        return method;
    }

    public <S extends BaseRequest<T>> S setMethod(HttpMethods method) {
        this.method = method;
        return (S)this;
    }

    public Object getTag() {
        return tag;
    }

    public <S extends BaseRequest<T>> S setTag(Object tag) {
        this.tag = tag;
        return (S)this;
    }

    public String getCharSet() {
        return charSet;
    }

    public <S extends BaseRequest<T>> S setCharSet(String charSet) {
        this.charSet = charSet;
        return (S)this;
    }

    /*$$$$$$$$$$$$$$$  time related getter and setters  $$$$$$$$$$$$$$$$$$*/

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public <S extends BaseRequest<T>> S setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
        return (S)this;
    }

    public int getMaxRediectTimes() {
        return maxRediectTimes;
    }

    public <S extends BaseRequest<T>> S setMaxRediectTimes(int maxRediectTimes) {
        this.maxRediectTimes = maxRediectTimes;
        return (S)this;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public <S extends BaseRequest<T>> S setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return (S)this;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public <S extends BaseRequest<T>> S setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return (S)this;
    }

    public <S extends BaseRequest<T>> S setHttpListener(HttpListener httpListener) {
        this.httpListener = httpListener;
        return (S)this;
    }

    public HttpListener getHttpListener() {
        return httpListener;
    }

    public boolean isCancelled() {
        return isCancelled.get();
    }

    public CacheMode getCacheMode() {
        return cacheMode;
    }

    public <S extends BaseRequest<T>> S setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
        return (S)this;
    }

    public String getCacheKey() {
        if(cacheKey == null) {
            cacheKey = HexUtil.encodeHexStr(MD5Util.md5(getUri()));
            if(HttpLog.isPrint) {
                Log.d(TAG, "generate cache key: " + cacheKey);
            }
        }
        return cacheKey;
    }

    public <S extends BaseRequest<T>> S setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return (S)this;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public <S extends BaseRequest<T>> S setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
        return (S)this;
    }

    public long getExpiredCacheTimeInMillis() {
        return expiredCacheTimeInMillis;
    }

    public <S extends BaseRequest<T>> S setExpiredCacheTimeInMillis(long expiredCacheTimeInMillis) {
        this.expiredCacheTimeInMillis = expiredCacheTimeInMillis;
        return (S)this;
    }

    public HttpParamModel getParamModel() {
        return paramModel;
    }

    public <S extends BaseRequest<T>> S setParamModel(HttpParamModel paramModel) {
        this.paramModel = paramModel;
        return (S)this;
    }

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public <S extends BaseRequest<T>> S setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
        return (S)this;
    }

    public HttpBody getHttpBody() {
        return httpBody;
    }

    public <S extends BaseRequest<T>> S setHttpBody(HttpBody httpBody) {
        if(httpBody != null) {
            httpBody.setRequest(this);
        }
        this.httpBody = httpBody;
        return (S)this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public <S extends BaseRequest<T>> S setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return (S)this;
    }

    public Map<String, String> getRequestParams() {
        return requestParams;
    }

    public <S extends BaseRequest<T>> S setRequestParams(Map<String, String> requestParams) {
        this.requestParams = requestParams;
        return (S)this;
    }

    public GlobalHttpListener getGlobalHttpListener() {
        return globalHttpListener;
    }

    public <S extends BaseRequest<T>> S setGlobalHttpListener(GlobalHttpListener globalHttpListener) {
        this.globalHttpListener = globalHttpListener;
        return (S)this;
    }


    /*________________________ private_methods ________________________*/

    /**
     * 将所有请求参数和POJO中的参数放在一起
     */
    //TODO  next step HttpRichParamModel
    public LinkedHashMap<String, String> getBasicParams() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if(requestParams != null) {
            map.putAll(requestParams);
        }
        if(paramModel != null) {
            if(paramModel instanceof HttpRichParamModel
                    &&!((HttpRichParamModel)paramModel).isFieldsAttachToUrl()) {
                return map;
            }
            map.putAll(getQueryBuilder().buildPrimaryMap(paramModel));
        }

        return map;
    }

     /*________________________ enhanced setters & getters ________________________*/

    public File getCachedFile() {
        if(cacheDir == null) {
            throw new RuntimeException("waterbird-http cache dir for request is null");
        }
        return new File(cacheDir, getCacheKey());
    }

    public <S extends BaseRequest<T>> S setCacheMode(CacheMode mode, String key) {
        this.cacheMode = mode;
        this.cacheKey = key;
        return (S) this;
    }

    public <S extends BaseRequest<T>> S setCacheMode(CacheMode mode, long expiredTime, TimeUnit unit) {
        this.cacheMode = mode;
        this.expiredCacheTimeInMillis = unit.toMillis(expiredTime);
        return (S) this;
    }

    public <S extends BaseRequest<T>> S setExpiredCachedTime(long expiredTime, TimeUnit unit) {
        this.expiredCacheTimeInMillis = unit.toMillis(expiredTime);
        return (S) this;
    }

    public boolean isCancelledOrInterrupted() {
        return isCancelled.get() || Thread.currentThread().isInterrupted();
    }

    /**
     * cancel this request
     */
    public void cancel() {
        this.isCancelled.set(true);
    }

    public <S extends BaseRequest<T>> S addUrlParam(String key, String value) {
        if(value != null) {
            if(requestParams == null) {
                requestParams = new LinkedHashMap<>();
            }
            requestParams.put(key, value);
        }
        return (S) this;
    }

    public <S extends BaseRequest<T>> S addUrlParam(List<NameValuePair> list) {
        if(list != null) {
            if(requestParams == null) {
                requestParams = new LinkedHashMap<>();
            }
            for(NameValuePair nvp : list) {
                requestParams.put(nvp.getName(), nvp.getValue());
            }
        }
        return (S) this;
    }


    /**
     * 构建完整的URI
     * @return
     * @throws HttpClientException
     */
    //TODO  需要完整测试
    public String createFullUri() throws HttpClientException {
       if(uri == null || !uri.startsWith(Constants.SCHEME_HTTP)) {
           if(baseUrl == null) {
               throw new HttpClientException(ClientException.UrlIsNull);
           } else if(!baseUrl.startsWith(Constants.SCHEME_HTTP)) {
               throw new HttpClientException(ClientException.IllegalSchema);
           }
           uri = uri == null ? baseUrl : baseUrl + uri;
       }
        try {
            StringBuilder stringBuilder = new StringBuilder();
            boolean hasQuestionMark = uri.contains("?");
            if(hasQuestionMark && !uri.matches(ENCODED_URL_PATTERN)) {
                //带有查询字符串的uri，并且不是encode uri，需要解析其中的参数
                Uri uri = Uri.parse(this.uri);
                Uri.Builder builder = uri.buildUpon();
                builder.query(null);
                //TODO  什么意思？？
                for (String key : UriUtil.getQueryParameterNames(uri)) {
                    for (String value : UriUtil.getQueryParameterValues(uri, key)) {
                        builder.appendQueryParameter(key, value);
                    }
                }

                if(HttpLog.isPrint) {
                    HttpLog.d(TAG, "param origin uri: " + uri);
                }

                uri = builder.build();

                if(HttpLog.isPrint) {
                    HttpLog.d(TAG, "param encode uri: " + uri);
                }
                stringBuilder.append(uri);
            } else {
                stringBuilder.append(uri);
            }

            if(requestParams == null && paramModel == null) {
                return stringBuilder.toString();
            }

            LinkedHashMap<String, String> map = getBasicParams();
            int size = map.size();
            if(size > 0) {
                if(!hasQuestionMark) {
                    stringBuilder.append("?");
                } else if(uri.contains("=")) {
                    stringBuilder.append("&");
                }

                int i = 0;
                for(Map.Entry<String, String> v : map.entrySet()) {
                    stringBuilder.append(URLEncoder.encode(v.getKey(), charSet))
                            .append("=")
                            .append(URLEncoder.encode(v.getValue(), charSet));
                    if(++i != size) {
                        stringBuilder.append("&");
                    }
                }
            }
            if(HttpLog.isPrint) {
                HttpLog.d(TAG, "FullURL = " + stringBuilder.toString());
            }
            fullUri = stringBuilder.toString();
            return fullUri;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     *  if uri is "www.abc.com"， then we should add prefix "http://" or "https://"
     */
    public <S extends BaseRequest<T>> S addUrlPrefix(String prefix) {
        setUri(prefix + uri);
        return (S) this;
    }

    /**
     *  for url "www.abc.com/userManagement/add"
     *  we can do this by firstly set uri="www.abc.com/", then addUrlSuffix "userManagement/add"
     *
     *  Note: there may have format error if we  not append slash to uri such as "www.abc.com/"
     */
    public <S extends BaseRequest<T>> S addUrlSuffix(String suffix) {
        setUri(uri + suffix);
        return (S) this;
    }

    /**
     * 设置消息体和HTTP请求方法
     */
    public <S extends BaseRequest<T>> S setHttpBody(HttpBody httpBody, HttpMethods method) {
        setHttpBody(httpBody);
        setMethod(method);
        return (S) this;
    }

    /**************      addHeader API     ********************/
    public <S extends BaseRequest<T>> S addHeader(List<NameValuePair> values) {
        if(values != null) {
            if(headers == null) {
                headers = new LinkedHashMap<>();
            }
            for(NameValuePair value : values) {
                headers.put(value.getName(), value.getValue());
            }
        }
        return (S)this;
    }

    public <S extends BaseRequest<T>> S addHeader(String key, String value) {
        if (value != null) {
            if (headers == null) {
                headers = new LinkedHashMap<String, String>();
            }
            headers.put(key, value);
        }
        return (S)this;
    }

    public <S extends BaseRequest<T>> S addHeader(Map<String, String> map) {
        if (map != null) {
            if (headers == null) {
                headers = new LinkedHashMap<String, String>();
            }
            headers.putAll(map);
        }
        return (S)this;
    }

    /**
     * return true if {@link #cacheMode} set to be cachable
     * @return
     */
    public boolean isCacheMode() {
        return cacheMode != null && cacheMode != CacheMode.NetOnly;
    }



    /**********             注解解析  尚未实现         *************/
    //TODO
    private void readParamFromAnnotations(HttpParamModel model) throws IllegalAccessException {

    }
    private String handleAnnotation(HttpParamModel model, String value) throws IllegalAccessException {

        return "";
    }



    /*________________________ string_methods ______________________________________*/
    @Override
    public String toString() {
        return reqToString();
    }

    /**
     *  打印数据的同时测试getter方法
     */
    public String reqToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n________________ request-start ________________")
                .append("\n baseUrl          : ").append(getBaseUrl())
                .append("\n uri              : ").append(getUri())
                .append("\n full uri         : ").append(fullUri)
                .append("\n id               : ").append(getId())
                .append("\n method           : ").append(getMethod())
                .append("\n tag              : ").append(getTag())
                .append("\n class            : ").append(this.getClass().getSimpleName())
                .append("\n charSet          : ").append(getCharSet())
                .append("\n socketTimeout    : ").append(getSocketTimeout())
                .append("\n maxRetryTimes    : ").append(getMaxRetryTimes())
                .append("\n maxRedirectTimes : ").append(getMaxRediectTimes())
                .append("\n connectionTimeout: ").append(getConnectionTimeout())
                .append("\n cacheExpireMillis: ").append(getExpiredCacheTimeInMillis())
                .append("\n httpListener     : ").append(getHttpListener())
                .append("\n cancelled        : ").append(isCancelled())
                .append("\n cacheMode        : ").append(getCacheMode())
                .append("\n cacheKey         : ").append(getCacheKey())
                .append("\n cacheDir         : ").append(getCacheDir())
                .append("\n paramModel       : ").append(getParamModel())
                .append("\n queryBuilder     : ").append(getQueryBuilder())
                .append("\n httpBody         : ").append(getHttpBody())
                .append("\n dataParser       : ").append(getDataParser())
                .append("\n header           ");
        if (headers == null) {
            sb.append(": null");
        } else {
            for (Map.Entry<String, String> en : headers.entrySet()) {
                sb.append("\n|    ").append(String.format("%-20s", en.getKey())).append(" = ").append(en.getValue());
            }
        }
        sb.append("\n paramMap         ");
        if (requestParams == null) {
            sb.append(": null");
        } else {
            for (Map.Entry<String, String> en : requestParams.entrySet()) {
                sb.append("\n|    ").append(String.format("%-20s", en.getKey())).append(" = ").append(en.getValue());
            }
        }
        sb.append("\n________________ request-end ________________");
        return sb.toString();
    }
}
