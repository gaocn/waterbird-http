package waterbird.space.http.exception;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import waterbird.space.http.parser.DataParser;
import waterbird.space.http.request.BaseRequest;
import waterbird.space.http.request.ExampleBaseRequest;

import static org.junit.Assert.fail;

/**
 * Created by 高文文 on 2016/12/20.
 */
        /*
        UrlIsNull("Url is Null", "Url 为空！"),
        IllegalSchema("illegal schema", "非法的协议schema"),
        ContextNeeded("Context Needed[detect or disable network, etc]", "需要Context[检测或禁用网络]"),
        PermissionDenied("Need NETWORK_ACCESS permission", "未获取访问网络或SD卡权限"),
        UnkownException("Client unknown exception", "客户端未知异常");
     */
public class HttpClientExceptionTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testUrlIsNullExceprion() throws Exception{
        thrown.expect(HttpClientException.class);
        thrown.expectMessage("Url is Null");
        testUrl();
    }
    private void testUrl() throws HttpClientException {
        BaseRequest request = new ExampleBaseRequest("");
        if(request.getUri().isEmpty()) {
            throw new HttpClientException(ClientException.UrlIsNull);
        }
    }

    @Test
    public void testIllegalSchema() throws Exception{
        thrown.expect(HttpClientException.class);
        thrown.expectMessage("illegal schema");
        testUrl();
    }

    @Test
    public void testContextNeeded() throws Exception{
        thrown.expect(HttpClientException.class);
        thrown.expectMessage("Context Needed");
        testUrl();
    }

    @Test
    public void testPermissionDenied() throws Exception{
        thrown.expect(HttpClientException.class);
        thrown.expectMessage("Need NETWORK_ACCESS permission");
        testUrl();
    }
    @Test
    public void testUnkownException() throws Exception{
        thrown.expect(HttpClientException.class);
        thrown.expectMessage("Client unknown exception");
        testUrl();
    }

    /*若包含空指针，则该测试方法执行成功;若显示 fail("No Exception Thrown");则说明代码没由空指针*/
    @Test
    public void testTryCatch() {
        try {
            DataParser parser = null;
            parser.setRequest(new ExampleBaseRequest("test"));
            fail("No Exception Thrown");
        } catch (NullPointerException e) {
            Assert.assertTrue(e instanceof NullPointerException);

            Assert.assertTrue(e.getMessage().contains("null"));
        }
    }
}