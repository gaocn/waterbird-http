package waterbird.space.http.request;

import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import waterbird.space.http.data.NameValuePair;
import waterbird.space.http.log.HttpLog;
import waterbird.space.http.request.builder.JsonQueryBuilder;
import waterbird.space.http.request.param.CacheMode;
import waterbird.space.http.request.param.HttpMethods;
import waterbird.space.http.utils.UriUtil;

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

    @Before
    public void constructRequest() {

        HttpLog.isPrint = true;

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Connection","keep-alive");
        headers.put("Content-Type", "text/html");
        headers.put("Host", "hm.baidu.com");
        headers.put("User-Agent", "Mozilla/5.0(Windows NT 10.0; WOW64) Chrome/55.0.2883.87 Safari/537.36");

        LinkedHashMap<String, String> requestParams = new LinkedHashMap<String, String>();
        requestParams.put("md5", "eab34febc41212313bf3cd4a");

        request = new ExampleBaseRequest("userManagement/add?id=2&name=govind&pwd=123");
        request .setBaseUrl("http://www.govind.space")
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
        File file = request.getCachedFile();
        Assert.assertTrue(file.getAbsolutePath().equals("/data/data/waterbird.space.http/cache/request_caches/" + request.getCacheKey()));
    }

    @Test
    public void addUrlParam() throws Exception {
        List<NameValuePair> list = new ArrayList<>();
        list.add(new NameValuePair("addUrlParamTest1", "tValue1"));
        list.add(new NameValuePair("addUrlParamTest2", "tValue2"));
        request.addUrlParam(list);
        HttpLog.d(TAG, request.toString());
    }

    @Test
    public void addUrlParam1() throws Exception {
        request.addUrlParam("addUrlParam3","tValue3");
        HttpLog.d(TAG, request.toString());
    }

    @Test
    public void createFullUri() throws Exception {
        String uri = "search?scope=bbs&q=C语言";
        request.setUri(uri);
        HttpLog.d(TAG, "createFullURI: "+ request.createFullUri());
    }

    @Test
    public void addUrlPrefix() throws Exception {
        //uri = userManagement/add?id=2&name=govind&pwd=123
        request.addUrlPrefix("addUrlPrefixTest");
        Assert.assertTrue(request.getUri().equals("addUrlPrefixTestuserManagement/add?id=2&name=govind&pwd=123"));

    }

    @Test
    public void addUrlSuffix() throws Exception {
        //uri = userManagement/add?id=2&name=govind&pwd=123
        request.addUrlSuffix("addUrlSuffixTest");
        Assert.assertTrue(request.getUri().equals("userManagement/add?id=2&name=govind&pwd=123addUrlSuffixTest"));
    }

    @Test
    public void addHeader() throws Exception {
        List<NameValuePair> list = new ArrayList<>();
        list.add(new NameValuePair("addHeaderTest1", "tValue1"));
        list.add(new NameValuePair("addHeaderTest2", "tValue2"));
        request.addHeader(list);
        HttpLog.d(TAG, request.toString());
    }

    @Test
    public void addHeader1() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("addHeader1Test1", "addHeader1TestValue1");
        map.put("addHeader1Test2", "addHeader1TestValue2");
        request.addHeader(map);
        HttpLog.d(TAG, request.toString());
    }

    @Test
    public void addHeader2() throws Exception {
        request.addHeader("addHeader2Test", "addHeader2TestValue");
        HttpLog.d(TAG, request.toString());
    }

    @Test
    public void testUriUtil() {
        Uri uri = Uri.parse(request.getUri());

        for(Map.Entry<String, String> entry : UriUtil.getQueryParameter(uri).entrySet()) {
            HttpLog.d("** URIUtilTest map**: ", "[" + entry.getKey() +", " + entry.getValue() + "]");
        }

        for (String key : UriUtil.getQueryParameterNames(uri)) {
            for (String value : UriUtil.getQueryParameterValues(uri, key)) {
                HttpLog.d("** URIUtilTest **: ", "[" + key +", " + value + "]");
            }
        }
    }
}