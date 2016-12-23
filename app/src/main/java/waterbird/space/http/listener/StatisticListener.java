package waterbird.space.http.listener;

import android.os.SystemClock;

import waterbird.space.http.data.StatisticsInfo;
import waterbird.space.http.request.BaseRequest;
import waterbird.space.http.response.InternalResponse;
import waterbird.space.http.response.Response;

/**
 * Created by 高文文 on 2016/12/23.
 *
 * 实现HTTP请求发出到响应结束这段时间内的信息收集；
 */

public class StatisticListener {
    private static final String TAG = "StatisticListener";
    private StatisticsInfo statisticsInfo;
    private InternalResponse internalResponse;
    private long total;

    private long connect;
    private long connectStart;

    private long read;
    private long readStart;

    private long headLen;
    private long readLen;

    public StatisticListener(StatisticsInfo statisticsInfo, InternalResponse response) {
        this.statisticsInfo = statisticsInfo;
        this.internalResponse = response;
    }

    public void onStart(BaseRequest request) {
        total = SystemClock.uptimeMillis();
    }

    public void onEnd(Response response) {
        if(total > 0) {
            //计算请求到响应需要的总时间
            total = SystemClock.uptimeMillis() - total;
            internalResponse.setUsedTime(total);
            statisticsInfo.addConnectTime(total);

            headLen = internalResponse.getContentLength();
            readLen = internalResponse.getReadedLength();  //下载数据，下载的时候设置
            long len = 0;
            if(readLen   > 0) {
                len = readLen;
            }

            if(len == 0 && headLen > 0) {
                len = headLen;
            }
            statisticsInfo.addDataLength(len);

        }
    }

    public void onRetry(BaseRequest request, int max, int times) {}
    public void onRedirect(BaseRequest request){}

    public void onPreConnect(BaseRequest request) {
        connectStart = SystemClock.uptimeMillis();
    }

    /** 会有多次连接操作，这里计算累积请求时间 */
    public void onAfterConnect(BaseRequest request) {
        connect += SystemClock.uptimeMillis() - connectStart;
    }

    public void onPreRead(BaseRequest request) {
        readStart = SystemClock.uptimeMillis();
    }
    /** 会有多次连接操作，这里计算累积请求时间 */
    public void onAfterRead(BaseRequest request) {
        read += SystemClock.uptimeMillis() - readStart;
    }

    @Override
    public String toString() {
        return resToString();
    }

    public String resToString() {
        return
                "\n[length]   headerLen: " + headLen
                        + " B,    readedLen: " + readLen + " B,    global total len: "
                        + statisticsInfo.getDataLength() + " B"
                        + "\n[time]   connect  : " + connect + " MS,    read: "
                        + read + " MS,    total: " + total + " MS,    global total time: "
                        + statisticsInfo.getConnectTime() + " MS";
    }
}
