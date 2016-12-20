package waterbird.space.http.exception;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import waterbird.space.http.utils.NetworkUtil;

/**
 * Created by 高文文 on 2016/12/20.
 */
/*
    NetworkUnavailable("Network Not Available","网络不可用"),
    NetwrokUnstable("Network Not Stable", "网络不稳定"),
    NetworkDisabled("Network Disabled", "网络被禁用"),
    NetworkUnreachable("Network Unreachable", "无法访问网络");

 */
public class HttpNetworkExceptionTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testNetworkUnavailable() throws Exception{
        thrown.expect(HttpNetworkException.class);
        thrown.expectMessage("Network Not Available");
        networkState();
    }

    @Test
    public void testNetwrokUnstable() throws Exception{
        thrown.expect(HttpNetworkException.class);
        thrown.expectMessage("Network Not Stable");
        networkState();
    }

    @Test
    public void testNetworkDisabled() throws Exception{
        thrown.expect(HttpNetworkException.class);
        thrown.expectMessage("Network Disabled");
        networkState();
    }

    @Test
    public void testNetworkUnreachable() throws Exception{
        thrown.expect(HttpNetworkException.class);
        thrown.expectMessage("Network Unreachable");
        networkState();
    }

    private void networkState() throws Exception{
        Context context = InstrumentationRegistry.getTargetContext();
        if(!NetworkUtil.isAvailable(context)) {
            throw  new HttpNetworkException(NetworkException.NetworkUnavailable);
        } else {
            if(NetworkUtil.isMobileAvailable(context)) {
                throw new HttpNetworkException(NetworkException.NetwrokUnstable);
            } else if (!NetworkUtil.isWifiAvailable(context)) {
                throw new HttpNetworkException(NetworkException.NetworkUnreachable);
            }
        }
        throw new HttpNetworkException(NetworkException.NetworkDisabled);
    }
}