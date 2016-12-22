package waterbird.space.http.listener;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import waterbird.space.http.exception.HttpException;
import waterbird.space.http.log.HttpLog;
import waterbird.space.http.request.BaseRequest;
import waterbird.space.http.response.Response;

/**
 * Created by 高文文 on 2016/12/19.
 *
 */

public abstract class GlobalHttpListener {
    private static final String TAG = "GlobalHttpListener";

    /** Handler 处理事件类型 */
    private static final int M_START = 1;
    private static final int M_SUCCESS = 2;
    private static final int M_FAILURE = 3;
    private static final int M_CANCEL = 4;

    private HttpHandler handler;
    private boolean runOnUiThread = true;

    /*______________________  Constructors   _________________________________*/
    public GlobalHttpListener(boolean runOnUiThread) {
        setRunOnUiThread(runOnUiThread);
    }
    /**  默认在UI线程中运行 */
    public GlobalHttpListener() {
        this(true);
    }

    /*______________________  Enhanced getters & setters ___________________________*/
    public final boolean isRunOnUiThread() {
        return runOnUiThread;
    }

    public final GlobalHttpListener setRunOnUiThread(boolean runOnUiThread) {
        this.runOnUiThread = runOnUiThread;
        if(runOnUiThread) {
            handler = new HttpHandler(Looper.getMainLooper());
        } else {
            handler = null;
        }
        return this;
    }

    /*_____________________  HttpHanlder    ________________________________*/

    /** inner class hold an implicit reference to outer class   */
    private class HttpHandler extends Handler {
        private HttpHandler(Looper looper) {
            super(looper);
        }
        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            Object[] data;
            switch (msg.what) {
                case M_START:
                    onStart((BaseRequest<Object>) msg.obj);
                    break;
                case M_SUCCESS:
                    data = (Object[]) msg.obj;
                    onSuccess(data[0], (Response<Object>) data[1]);
                    break;
                case M_FAILURE:
                    data = (Object[]) msg.obj;
                    onFailure((HttpException) data[0], (Response<Object>) data[1]);
                    break;
                case M_CANCEL:
                    data = (Object[]) msg.obj;
                    onCancel(data[0], (Response<Object>) data[1]);
                    break;
            }
        }
    }

    /*___________________     called by waterbird-http   ________________________*/

    public final void notifyCallStart(BaseRequest<?> req) {
        if (runOnUiThread) {
            Message msg = handler.obtainMessage(M_START);
            msg.obj = req;
            handler.sendMessage(msg);
        } else {
            onStart(req);
        }
    }

    public final void notifyCallSuccess(Object data, Response<?> response) {
        if (runOnUiThread) {
            Message msg = handler.obtainMessage(M_SUCCESS);
            msg.obj = new Object[]{data, response};
            handler.sendMessage(msg);
        } else {
            onSuccess(data, response);
        }
    }

    public final void notifyCallFailure(HttpException e, Response<?> response) {
        if (runOnUiThread) {
            Message msg = handler.obtainMessage(M_FAILURE);
            msg.obj = new Object[]{e, response};
            handler.sendMessage(msg);
        } else {
            onFailure(e, response);
        }
    }

    public final void notifyCallCancel(Object data, Response<?> response) {
        if (HttpLog.isPrint && response != null) {
            HttpLog.w(TAG, "Request be Cancelled!  isCancelled: " + response.getRequest().isCancelled()
                    + "  Thread isInterrupted: " + Thread.currentThread().isInterrupted());
        }
        if (runOnUiThread) {
            Message msg = handler.obtainMessage(M_CANCEL);
            msg.obj = new Object[]{data, response};
            handler.sendMessage(msg);
        } else {
            onCancel(data, response);
        }
    }

    /*____________ developer override method ____________*/
    public void onStart(BaseRequest<?> request){}

    public abstract void onSuccess(Object data, Response<?> response);

    public abstract void onFailure(HttpException e, Response<?> response);

    public void onCancel(Object data, Response<?> response){}
}
