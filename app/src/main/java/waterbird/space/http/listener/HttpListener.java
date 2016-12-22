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
 *
 * 1. 监听器监控请求到响应的整个生命周期，生命周期包括：
 *      {@link #onStart}, {@link #onSuccess}，{@link #onFailure}, {@link #onCancel},
 *      {@link #onLoading}, {@link #onUpLoading}, {@link #onRetry}, {@link #onRedirect},
 *      {@link #onEnd}
 * 2. 允许责任链模式的监听器链
 */

public abstract class HttpListener<Data> {
    private static final String TAG = "HttpListener";
    /** Handler 处理事件类型 */
    private static final int M_START = 1;
    private static final int M_SUCCESS = 2;
    private static final int M_FAILURE = 3;
    private static final int M_CANCEL = 4;
    private static final int M_READING = 5;
    private static final int M_UPLOADING = 6;
    private static final int M_RETRY = 7;
    private static final int M_REDIRECT = 8;
    private static final int M_END = 9;

    /** 处理监听事件的句柄，用于调用 "Callback API" 实现子线程与UI线程的交互 */
    private HttpHandler handler;

    /**
     * 标识监听器运行在UI线程还是子线程
     */
    private boolean runOnUiThread = false;

    /**
     * 下载或读取数据时是否调用回调函数onLoadIng显示进度
     */
    private boolean readingNotify = false;

    /**
     * 上传数据还是是否调用onUploading显示进度
     */
    private boolean uploadingNotify = false;

    /**
     *  延迟发送Handler消息的时间
     */
    private long delayMillis;

    /**
     *  责任链
     *      1. 由每一个对象对其下家的引用而连接起来形成一条链。
     *      2. 请求在这个链上传递，直到链上的某一个对象决定处理此请求，并返回相应的结果。
     *      3. 责任链末尾必须是默认处理类。
     *   优点：实现了请求者与处理者代码分离，提高系统灵活性和可扩展性；
     *   缺点：每次都要从链头处理
     */
    private HttpListener<Data> linkedListener;

    /*______________________  Constructors   _________________________________*/

    public HttpListener(boolean runOnUiThread, boolean readingNotify, boolean uploadingNotify) {
        setRunOnUiThread(runOnUiThread);
        this.readingNotify = readingNotify;
        this.uploadingNotify = uploadingNotify;
    }

    public HttpListener(boolean runOnUiThread) {
        setRunOnUiThread(runOnUiThread);
    }

    public HttpListener(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    /**  默认在UI线程中运行 */
    public HttpListener() {
        this(true);
    }

    /*______________________  Enhanced getters & setters ___________________________*/

    public final  HttpListener<Data> getLinkedListener() {
        return linkedListener;
    }

    /**
     *  若添加过了监听器，则报异常；否则直接添加后继监听器
     *  例子： 监听器：La Lb Lc 实现责任链：La -> Lb -> Lc
     *      La setLinkedListener Lb
     *      Lb setLinkedListener Lc
     */
    public final HttpListener<Data> setLinkedListener(HttpListener<Data> linkedListener) {
        if(this.linkedListener != null) {
            HttpListener<Data> next = this.linkedListener;
            for(; next != null; next = next.getLinkedListener()) {
                if(next == linkedListener) {
                    throw new RuntimeException("[Circular Found !]Detect Repeated HttpListener { " + linkedListener + " }");
                }
            }
        }
        this.linkedListener = linkedListener;
        return this;
    }

    public final boolean isRunOnUiThread() {
        return runOnUiThread;
    }

    public final HttpListener<Data> setRunOnUiThread(boolean runOnUiThread) {
        this.runOnUiThread = runOnUiThread;
        if(runOnUiThread) {
            handler = new HttpHandler(Looper.getMainLooper());
        } else {
            throw new RuntimeException("[Handler =" + handler + "] " + "Should Explicity Set HttpHandler");
        }

        return this;
    }

    public final boolean isReadingNotify() {
        return readingNotify;
    }

    public final HttpListener<Data> setReadingNotify(boolean readingNotify) {
        this.readingNotify = readingNotify;
        return this;
    }

    public final boolean isUploadingNotify() {
        return uploadingNotify;
    }

    public final HttpListener<Data> setUploadingNotify(boolean uploadingNotify) {
        this.uploadingNotify = uploadingNotify;
        return this;
    }

    public final long getDelayMillis() {
        return delayMillis;
    }

    public final HttpListener<Data> setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
        return this;
    }

    public void setHandler(HttpHandler handler) {
        this.handler = handler;
    }

    /*_____________________  HttpHanlder    ________________________________*/

    /** inner class hold an implicit reference to outer class   */
    private class HttpHandler extends Handler {
        public HttpHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if(disableListener()) {
                /** 不使用监听器 */
                return;
            }
            Object[] data;
            switch (msg.what) {
                case M_START:
                    onStart((BaseRequest<Data>) msg.obj);
                    break;
                case M_SUCCESS:
                    data = (Object[])msg.obj;
                    onSuccess((Data)data[0], (Response<Data>)data[1]);
                    break;
                case M_FAILURE:
                    data = (Object[])msg.obj;
                    onFailure((HttpException) data[0], (Response<Data>)data[1]);
                    break;
                case M_CANCEL:
                    data = (Object[])msg.obj;
                    onCancel((Data)data[0], (Response<Data>)data[1]);
                    break;
                case M_READING:
                    data = (Object[])msg.obj;
                    onLoading((BaseRequest<Data>)data[0], (Long)data[1], (Long)data[2]);
                    break;
                case M_UPLOADING:
                    data = (Object[])msg.obj;
                    onUpLoading((BaseRequest<Data>)data[0], (Long)data[1], (Long)data[2]);
                    break;
                case M_RETRY:
                    data = (Object[])msg.obj;
                    onRetry((BaseRequest<Data>)data[0], (Integer) data[1], (Integer)data[2]);
                    break;
                case M_REDIRECT:
                    data = (Object[])msg.obj;
                    onRedirect((BaseRequest<Data>)data[0], (Integer) data[1], (Integer)data[2]);
                    break;
                case M_END:
                    onEnd((Response<Data>)msg.obj);
                    break;
            }

        }
    }

   /*___________________     called by waterbird-http   ________________________*/

    public final void notifyCallStart(BaseRequest<Data> request) {
        if(disableListener()) {
            return;
        }

        if(runOnUiThread) {
            Message msg = Message.obtain();
            msg.what = M_START;
            msg.obj = request;
            handler.sendMessage(msg);
        } else {
            onStart(request);
        }

        //责任链模式，将消息传递给后面的监听器继续处理
        if(linkedListener != null) {
            linkedListener.notifyCallStart(request);
        }
    }

    public final void notifyCallSuccess(Data data, Response<Data> response) {
        delayOrNot();
        if(disableListener()) {
            return;
        }

        if(runOnUiThread) {
            Message message = handler.obtainMessage(M_SUCCESS);
            message.obj = new Object[]{data, response};
            handler.sendMessage(message);
        } else {
            onSuccess(data, response);
        }

        if(linkedListener != null) {
            linkedListener.notifyCallSuccess(data, response);
        }
    }

    public final void notifyCallFailure(HttpException e, Response<Data> response) {
        delayOrNot();
        if(disableListener()) {
            return;
        }

        if(runOnUiThread) {
            Message message = handler.obtainMessage(M_FAILURE);
            message.obj = new Object[]{e, response};
            handler.sendMessage(message);
        } else {
            onFailure(e, response);
        }

        if(linkedListener != null) {
            linkedListener.notifyCallFailure(e, response);
        }
    }

    public final void notifyCallCancel(Data data, Response<Data> response) {
        if(HttpLog.isPrint && response != null) {
            HttpLog.d(TAG, "Request is cancelled! isCancelled=" + response.getRequest().isCancelled()
            + "Thread isInterrupted=" + Thread.currentThread().isInterrupted());
        }

        delayOrNot();
        if(disableListener()) {
            return;
        }

        if(runOnUiThread) {
            Message message = handler.obtainMessage(M_CANCEL);
            message.obj = new Object[]{data, response};
            handler.sendMessage(message);
        } else {
            onCancel(data, response);
        }


        if(linkedListener != null) {
            linkedListener.notifyCallCancel(data, response);
        }

    }


    public final void notifyCallLoading(BaseRequest<Data> request, long total, long len) {
        if(disableListener()) {
            return;
        }

        if(readingNotify) {
            if(runOnUiThread) {
                Message msg = handler.obtainMessage(M_READING);
                msg.obj = new Object[]{request, total, len};
                handler.sendMessage(msg);
            } else {
                onLoading(request, total, len);
            }
        }

        if(linkedListener != null) {
            linkedListener.notifyCallLoading(request, total, len);
        }
    }

    public final void notifyCallUploading(BaseRequest<Data> request, long total, long len) {
        if(disableListener()) {
            return;
        }

        if(uploadingNotify) {
            if(runOnUiThread) {
                Message msg = handler.obtainMessage(M_UPLOADING);
                msg.obj = new Object[]{request, total, len};
                handler.sendMessage(msg);
            } else {
                onUpLoading(request, total, len);
            }
        }

        if(linkedListener != null) {
            linkedListener.notifyCallUploading(request, total, len);
        }
    }

    public final void notifyCallRetry(BaseRequest<Data> request, int max, int times) {
        if(disableListener()) {
            return;
        }

        if(runOnUiThread) {
            Message message = handler.obtainMessage(M_RETRY);
            message.obj = new Object[]{request, max, times};
            handler.sendMessage(message);
        } else {
            onRetry(request, max, times);
        }

        if(linkedListener != null) {
            linkedListener.notifyCallRetry(request, max, times);
        }
    }

    public final void notifyCallRedirect(BaseRequest<Data> request, int max, int times) {
        if(disableListener()) {
            return;
        }

        if(runOnUiThread) {
            Message message = handler.obtainMessage(M_REDIRECT);
            message.obj = new Object[]{request, max, times};
            handler.sendMessage(message);
        } else {
            onRedirect(request, max, times);
        }

        if(linkedListener != null) {
            linkedListener.notifyCallRedirect(request, max, times);
        }
    }

    public final void notifyCallEnd(Response<Data> resonse) {
        if(disableListener()) {
            return;
        }

        if(runOnUiThread) {
            Message message = handler.obtainMessage(M_END);
            message.obj = resonse;
            handler.sendMessage(message);
        } else {
            onEnd(resonse);
        }

        if(linkedListener != null) {
            linkedListener.notifyCallEnd(resonse);
        }
    }

    private boolean delayOrNot() {
        if (delayMillis > 0) {
            try {
                Thread.sleep(delayMillis);
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*______________________    Callback  API  executed in UI-Thread  _________________________*/

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
