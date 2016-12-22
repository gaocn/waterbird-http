package waterbird.space.http.listener;

import waterbird.space.http.exception.HttpException;
import waterbird.space.http.log.HttpLog;
import waterbird.space.http.request.BaseRequest;
import waterbird.space.http.response.Response;

/**
 * Created by 高文文 on 2016/12/22.
 */

public class StringHttpListener extends HttpListener<String> {
    private static final String TAG = "StringHttpListener";

    public StringHttpListener(boolean runOnUiThread, boolean readingNotify, boolean uploadingNotify) {
        super(runOnUiThread, readingNotify, uploadingNotify);
    }

    public StringHttpListener(boolean runOnUiThread) {
        super(runOnUiThread);
    }

    public StringHttpListener(long delayMillis) {
        super(delayMillis);
    }

    public StringHttpListener() {
        super();
    }

    @Override
    public boolean disableListener() {
        return super.disableListener();
    }

    @Override
    public void onStart(BaseRequest<String> request) {
        HttpLog.d(TAG, "onStart,  request=" + request);
    }

    @Override
    public void onSuccess(String s, Response<String> response) {
        HttpLog.d(TAG, "onSuccess, [data= " + s + " response=" + response + "]");
    }

    @Override
    public void onFailure(HttpException e, Response<String> response) {
        e.printStackTrace();
        HttpLog.d(TAG, "onFailure, [HttpException= " + e + " response=" + response + "]");
    }

    @Override
    public void onCancel(String s, Response<String> response) {
        HttpLog.d(TAG, "onCancel, [data= " + s + " response=" + response + "]");
    }

    @Override
    public void onLoading(BaseRequest<String> request, long total, long len) {
        HttpLog.d(TAG, "onLoading, [request= " + request + "total=" + total + " len=" + len + "]");
    }

    @Override
    public void onUpLoading(BaseRequest<String> request, long total, long len) {
        HttpLog.d(TAG, "onUpLoading, [request= " + request + "total=" + total + " len=" + len + "]");
    }

    @Override
    public void onRetry(BaseRequest<String> request, int max, int times) {
        HttpLog.d(TAG, "onRetry, [request= " + request + "max=" + max + " times=" + times + "]");
    }

    @Override
    public void onRedirect(BaseRequest<String> request, int max, int times) {
        HttpLog.d(TAG, "onRedirect, [request= " + request + "max=" + max + " times=" + times + "]");
    }

    @Override
    public void onEnd(Response<String> resonse) {
        HttpLog.d(TAG, "onEnd, [resonse= " + resonse + "]");
    }
}
