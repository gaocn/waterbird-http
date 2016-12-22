package waterbird.space.http.listener;

import waterbird.space.http.exception.HttpException;
import waterbird.space.http.request.BaseRequest;
import waterbird.space.http.response.Response;

/**
 * Created by 高文文 on 2016/12/19.
 */

public abstract class HttpListener<Data> {
    private HttpListener<Data> linkedListener;








    /*______________________     Callback  API    ____________________________*/

    /** 主要包括在请求响应的整个生命周期中需要使用的方法 */

    public boolean disableListener() {
        return false;
    }

    public void onStart(BaseRequest<Data> request) {}

    public void onSuccess(Data data, Response<Data> response) {}

    public void onFailure(HttpException e, Response<Data> response) {}

    public void onCancel(Data data, Response<Data> response) {}

    public void onLoading(BaseRequest<Data> request, long total, long len){}

    public void onUpLoading(BaseRequest<Data> request, long total, long len){}

    public void onRetry(BaseRequest<Data> request, int max, int times) {}

    public void onRedirect(BaseRequest<Data> request, int max, int times) {}

    public void onEnd(Response<Data> resonse) {}
}
