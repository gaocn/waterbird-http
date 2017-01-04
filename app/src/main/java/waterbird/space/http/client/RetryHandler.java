package waterbird.space.http.client;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashSet;

import javax.net.ssl.SSLException;

import waterbird.space.http.exception.HttpNetworkException;
import waterbird.space.http.exception.NetworkException;
import waterbird.space.http.log.HttpLog;
import waterbird.space.http.utils.NetworkUtil;

/**
 * Created by 高文文 on 2016/12/30.
 *
 * 试验发现，当url非法时，{@link HttpURLConnection}报MalformedURLException异常，而apache httpclient会报IllegalStateException异常。
 * 当url非法，和ssl错误时，不用重试。
 * 当有可用网络但连接不稳定时，一般会报IO异常，此种情况尝试重试，以提高成功率。
 * 继承StandardHttpRequestRetryHandler因用到其判断请求方式是否幂等和连接是否取消等方法。
 */

public class RetryHandler {
    private static final String TAG = "RetryHandler";

    /**
     * 若请求过程中，出现异常在白名单中，则运行进行重传
     */
    private HashSet<Class<?>> whitelist = new HashSet<>();

    /**
     *若请求过程中，出现异常在黑名单中，则不进行重传
     */
    private HashSet<Class<?>> blacklist = new HashSet<>();


    /**
     * 下一次重试连接前等待的时间
     */
    private int retrySleepTimeMS;

    public RetryHandler(int retrySleepTimeMS) {
        this.retrySleepTimeMS = retrySleepTimeMS;
        whitelist.add(SocketException.class);
        whitelist.add(SocketTimeoutException.class);

        blacklist.add(MalformedURLException.class);
        blacklist.add(UnknownHostException.class);
        blacklist.add(FileNotFoundException.class);
        blacklist.add(SSLException.class);
    }

    public RetryHandler setRetrySleepTimeMS(int retrySleepTimeMS) {
        this.retrySleepTimeMS = retrySleepTimeMS;
        return this;
    }

    protected boolean isInList(HashSet<Class<?>> list, Throwable error) {
        for(Class<?> c : list) {
            if(c.isInstance(error)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据参数判断是否进行请求重传操作
     *
     * @param exception
     * @param retryCount
     * @param maxRetries
     * @param appContext
     * @return
     * @throws HttpNetworkException
     * @throws InterruptedException
     */
    public boolean retryRequest(IOException exception, int retryCount, int maxRetries, Context appContext) throws HttpNetworkException, InterruptedException {
        boolean retry = true;

        //1. 判断是否重新请求
        if(retryCount > maxRetries) {
            if (HttpLog.isPrint) {
                HttpLog.d(TAG, "retry count > max retry times..");
            }
            throw new HttpNetworkException(exception);
        } else if (isInList(blacklist, exception)) {
            if (HttpLog.isPrint) {
                HttpLog.w(TAG, "exception in blacklist..");
            }
            retry = false;
        } else if(isInList(whitelist, exception)) {
            if (HttpLog.isPrint) {
                HttpLog.w(TAG, "exception in whitelist..");
            }
            retry = true;
        }

        //2. 根据结果进行重发请求
        if(retry) {
            if(appContext != null) {
                if(NetworkUtil.isConnected(appContext)) {
                    HttpLog.d(TAG, "Network isConnected, retry now");
                } else if (NetworkUtil.isConnectedOrConnecting(appContext)) {
                    if (HttpLog.isPrint) {
                        HttpLog.v(TAG, "Network is Connected Or Connecting, wait for retey : "
                                + retrySleepTimeMS + " ms");
                    }
                    Thread.sleep(retrySleepTimeMS);
                } else {
                    HttpLog.d(TAG, "Without any Network , immediately cancel retry");
                    throw new HttpNetworkException(NetworkException.NetworkUnavailable);
                }
            } else {
                if (HttpLog.isPrint) {
                    HttpLog.v(TAG, "app context is null..");
                    HttpLog.v(TAG, "wait for retry : " + retrySleepTimeMS + " ms");
                }
                Thread.sleep(retrySleepTimeMS);
            }
        }

        if (HttpLog.isPrint) {
            HttpLog.i(TAG, "retry: " + retry + " , retryCount: " + retryCount + " , exception: " + exception);
        }

        return retry;
    }

}
