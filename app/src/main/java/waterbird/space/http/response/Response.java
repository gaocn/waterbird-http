package waterbird.space.http.response;


import java.util.ArrayList;

import waterbird.space.http.data.HttpStatus;
import waterbird.space.http.data.NameValuePair;
import waterbird.space.http.exception.HttpException;
import waterbird.space.http.request.BaseRequest;

/**
 * Use Facade
 *  providing developers with easy access to the results of
 *      {@link waterbird.space.http.WaterBirdHttp#execute(waterbird.space.http.request.BaseRequest)},
 *  and with information of status,request,charset,etc.
 *
 * Facade模式：
 *      外观的包装，使应用程序只能看到外观对象，而不会看到具体的细节对象，这样无疑会降低应用程序的复杂度，并且提高了程序的可维护性。
 *
 */
public interface Response<T> {


    ArrayList<NameValuePair> getHeaders();

    HttpStatus getHttpStatus();

    T getResult();

    <R extends BaseRequest<T>> R getRequest();

    long getReadedLength();

    long getContentLength();


    String getContentEncoding();

    String getContentType();

    String getCharSet();

    long getUseTime();

    boolean isConnectSuccess();

    int getRetryTimes();

    int getRedirectTimes();

    HttpException getException();

    boolean isCacheHit();

    String getRawString();

    Response<T> setTag(Object tag);

    Object getTag();

    String resToString();

    void printInfo();

}
