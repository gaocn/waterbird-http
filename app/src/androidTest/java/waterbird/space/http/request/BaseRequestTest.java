package waterbird.space.http.request;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import waterbird.space.http.log.HttpLog;
import waterbird.space.http.parser.DataParser;
import waterbird.space.http.parser.TestDataParser;
import waterbird.space.http.request.builder.JsonQueryBuilder;
import waterbird.space.http.request.param.CacheMode;
import waterbird.space.http.request.param.HttpMethods;

/**
 * Created by 高文文 on 2016/12/20.
 */
@RunWith(AndroidJUnit4.class)
public class BaseRequestTest {
    private static final String TAG = "BaseRequestTest";
    Context appContext;
    BaseRequest<String> request;

    @Before
    public void getContext() {
        appContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void constructRequest() {

        HttpLog.isPrint = true;

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Connection","keep-alive");
        headers.put("Content-Type", "text/html");
        headers.put("Host", "hm.baidu.com");
        headers.put("User-Agent", "Mozilla/5.0(Windows NT 10.0; WOW64) Chrome/55.0.2883.87 Safari/537.36");

        LinkedHashMap<String, String> requestParams = new LinkedHashMap<String, String>();
        requestParams.put("parent=govind", "md5=gab34febc412");

        request = new BaseRequest<String>("userManagement/add?id=2&name=govind&pwd=123") {
            @Override
            public DataParser<String> createDataParser() {
                return new TestDataParser();
            }
        };
        request.setBaseUrl("www.govind.space")
                .setId(20164502465878L)
                .setMethod(HttpMethods.GET)
                .setTag(this.getClass().getSimpleName())
                .setQueryBuilder(new JsonQueryBuilder())

                  //TODO
                .setParamModel(null)
                .setHttpBody(null)
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

        HttpLog.d(TAG, request.toString());

    }

    @Test
    public void getCachedFile() throws Exception {

    }

    @Test
    public void addUrlParam() throws Exception {

    }

    @Test
    public void addUrlParam1() throws Exception {

    }

    @Test
    public void createFullUri() throws Exception {

    }

    @Test
    public void addUrlPrefix() throws Exception {

    }

    @Test
    public void addUrlSuffix() throws Exception {

    }

    @Test
    public void addHeader() throws Exception {

    }

    @Test
    public void addHeader1() throws Exception {

    }

    @Test
    public void addHeader2() throws Exception {

    }

}