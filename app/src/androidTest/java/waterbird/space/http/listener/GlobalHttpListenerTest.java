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
import waterbird.space.http.exception.HttpException;
import waterbird.space.http.log.HttpLog;
import waterbird.space.http.request.BaseRequest;
import waterbird.space.http.request.ExampleBaseRequest;
import waterbird.space.http.request.builder.JsonQueryBuilder;
import waterbird.space.http.request.param.CacheMode;
import waterbird.space.http.request.param.HttpMethods;
import waterbird.space.http.response.Response;

/**
 * Created by 高文文 on 2016/12/22.
 */
public class GlobalHttpListenerTest {
    private static final String TAG = "GlobalHttpListenerTest";
    ExampleBaseRequest request;

    @Before
    public void createGlobalListener() {
        HttpLog.isPrint = true;

        GlobalHttpListener listener = new GlobalHttpListener() {
            @Override
            public void onStart(BaseRequest<?> request) {
                HttpLog.d(TAG, "onStart,  request=" + request);
            }

            @Override
            public void onCancel(Object data, Response<?> response) {
                HttpLog.d(TAG, "onStart, [data= " + data + " response=" + response + "]");
            }

            @Override
            public void onSuccess(Object data, Response<?> response) {
                HttpLog.d(TAG, "onSuccess, [data= " + data + " response=" + response + "]");
            }

            @Override
            public void onFailure(HttpException e, Response<?> response) {
                e.printStackTrace();
                HttpLog.d(TAG, "onFailure, [HttpException= " + e + " response=" + response + "]");

            }
        };


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
                .setGlobalHttpListener(listener)

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
    public void testGlobalHttpListener() {
        request.getGlobalHttpListener().notifyCallStart(request);
        request.getGlobalHttpListener().notifyCallSuccess("hah 成功了", null);
        request.getGlobalHttpListener().notifyCallFailure(new HttpClientException(ClientException.IllegalSchema), null);
        request.getGlobalHttpListener().notifyCallCancel("请求被取消没有被处理", null);
    }
}