package waterbird.space.http.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import waterbird.space.http.HttpConfig;
import waterbird.space.http.data.Constants;
import waterbird.space.http.listener.HttpListener;
import waterbird.space.http.request.BaseRequest;

/**
 * Created by 高文文 on 2016/12/19.
 *
 * base data parser corresponding to request/builder
 */

public abstract class DataParser<T> {
    private static final String TAG = "DataParser";
    /**
     * reference to request, conveniently fetch necessary data from request
     */
    protected BaseRequest request;

    /**
     * data to be parsed
     */
    protected T data;

    /**
     * data length
     */
    protected long readLength;

    protected String charset = Constants.DEFAULT_CHARSET;
    protected int bufferSize = HttpConfig.DEFAULT_BUFFER_SIZE;


    /**
     * source 1: from network
     */
    public T readFromNetworkStream(InputStream inputStream, long len, String charset) throws IOException {
        try {
            if(inputStream != null) {
                this.data = parseNetowrkStream(inputStream, len, charset);
            }
        } finally {
            inputStream.close();
        }
        return this.data;
    }

    /**
     * source 2: from disk
     */
    public abstract T readFromDiskCache(File file) throws IOException;

    /**
     * source 3: from memory cache
     */
    public final T readFromMemCache(T data) {
        if(isMemCacheSupport()) {
            this.data = data;
        }
        return this.data;
    }

    /**
     * identify if this data parser support memory cache
     */
    protected abstract boolean isMemCacheSupport();

    /**
     * parse network stream
     */
    protected abstract T parseNetowrkStream(InputStream inputStream, long len, String charset) throws IOException;

    /**
     *  get initial  raw data
     */
    public final T getData() {
        return data;
    }

    /**
     * data length
     */
    public final long getReadedLength(){
        return readLength;
    }

    /**
     * raw data in string format
     */
    public String  getRawString() {
        return null;
    }

    /**
     * translate original bytes to custom bytes
     * if data is encrypted, override this method to decrypt it
     * @param bytes  data from server
     * @return decrypted data
     */
    protected byte[] translateToBytes(byte[] bytes) {
        return bytes;
    }
    /**
     * notify readed length to listener
     */
    protected final void notifyReadingStatus(long total, long len) {
        HttpListener<T> listener = request.getHttpListener();
        if(listener != null) {
            listener.notifyCallLoading(request, total, len);
        }
    }



    /*__________________     setters & getters ____________________*/

    public BaseRequest getRequest() {
        return request;
    }

    public void setRequest(BaseRequest request) {
        this.request = request;
        if(request.getCharSet() != null) {
            this.charset = request.getCharSet();
        }
    }

    @Override
    public String toString() {
        return "DataParser{" +
                "bufferSize=" + bufferSize +
                ", readLength=" + readLength +
                '}';
    }
}
