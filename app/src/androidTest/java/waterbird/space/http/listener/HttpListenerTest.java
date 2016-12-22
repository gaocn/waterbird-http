package waterbird.space.http.listener;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import waterbird.space.http.exception.ClientException;
import waterbird.space.http.exception.HttpClientException;
import waterbird.space.http.log.HttpLog;
import waterbird.space.http.request.ExampleBaseRequest;
import waterbird.space.http.request.builder.JsonQueryBuilder;
import waterbird.space.http.request.param.CacheMode;
import waterbird.space.http.request.param.HttpMethods;

/**
 * Created by 高文文 on 2016/12/22.
 */
public class HttpListenerTest {
    private static final String TAG = "HttpListenerTest";
    ExampleBaseRequest request;

    @Before
    public void init() {
        HttpLog.isPrint = true;
        Context appContext = InstrumentationRegistry.getTargetContext();

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Connection","keep-alive");
        headers.put("Content-Type", "text/html");
        headers.put("Host", "hm.baidu.com");
        headers.put("User-Agent", "Mozilla/5.0(Windows NT 10.0; WOW64) Chrome/55.0.2883.87 Safari/537.36");

        LinkedHashMap<String, String> requestParams = new LinkedHashMap<String, String>();
        requestParams.put("md5", "eab34febc41212313bf3cd4a");

        request = new ExampleBaseRequest("http://www.govind.space/add?name=govind&pwd=123");
        request .setId(20164502465878L)
                .setMethod(HttpMethods.GET)
                .setTag(this.getClass().getSimpleName())
                .setQueryBuilder(new JsonQueryBuilder())

                //TODO
                .setParamModel(null)
                .setHttpBody(null)
                .setHttpListener(null)
                .setGlobalHttpListener(null)

                .setCacheDir(new File(appContext.getCacheDir().getAbsolutePath(), "request_caches").getAbsolutePath())
                .setCacheMode(CacheMode.NetOnly)
                .setCharSet("utf-8")
                .setConnectionTimeout(5000)
                .setExpiredCachedTime(10, java.util.concurrent.TimeUnit.MINUTES)
                .setMaxRetryTimes(2)
                .setMaxRediectTimes(3)
                .setSocketTimeout(3000)
                .setHeaders(headers)
                .setRequestParams(requestParams);
    }


    @Test
    public void testHttpListenerLifeCycle() {
        HttpListener listener = new StringHttpListener(true, true, true);
        request.setHttpListener(listener);

        request.getHttpListener().notifyCallStart(request);
        request.getHttpListener().notifyCallSuccess("hah 成功了", null);
        request.getHttpListener().notifyCallFailure(new HttpClientException(ClientException.IllegalSchema), null);
        request.getHttpListener().notifyCallCancel("请求被取消没有被处理", null);

        request.getHttpListener().notifyCallLoading(request, 100L, 45L);
        request.getHttpListener().notifyCallUploading(request, 100L, 89L);
        request.getHttpListener().notifyCallRetry(request, 2, 1);
        request.getHttpListener().notifyCallRedirect(request, 3, 2);
        request.getHttpListener().notifyCallEnd(null);

    }


    @Test
    public void testHttpListenerChain() {
        HttpListener listener1 = new StringHttpListener(true, true, true);
        HttpListener listener2 = new StringHttpListener(true, false, true);
        HttpListener listener3 = new StringHttpListener(true, false, false);
        listener1.setLinkedListener(listener2);
        listener2.setLinkedListener(listener3);

        request.setHttpListener(listener1);

        request.getHttpListener().notifyCallStart(request);
        request.getHttpListener().notifyCallSuccess("hah 成功了", null);
        request.getHttpListener().notifyCallFailure(new HttpClientException(ClientException.IllegalSchema), null);
        request.getHttpListener().notifyCallCancel("请求被取消没有被处理", null);

        request.getHttpListener().notifyCallLoading(request, 100L, 45L);
        request.getHttpListener().notifyCallUploading(request, 100L, 89L);
        request.getHttpListener().notifyCallRetry(request, 2, 1);
        request.getHttpListener().notifyCallRedirect(request, 3, 2);
        request.getHttpListener().notifyCallEnd(null);
    }

}
