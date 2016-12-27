package waterbird.space.http.request.content;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import waterbird.space.http.data.Constants;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class StringBody extends HttpBody {
    private static final String TAG = "StringBody";
    protected String charset;
    protected String mimeType;
    protected String string;
    protected byte[] content;

    public StringBody(String string) {
        this(string, null, null);
    }

    public StringBody(String string,String charset, String mimeType) {
        if(mimeType == null) {
            mimeType = Constants.MIME_TYPE_TEXT;
        }
        if(charset == null) {
            charset = Constants.DEFAULT_CHARSET;
        }
        this.charset = charset;
        this.mimeType = mimeType;
        this.string = string;
        try {
            this.content = string.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Content-Type:text/plain;infoCharset=utf-8
        this.contentType = mimeType + Constants.CHARSET_PARAM + charset;
    }

    @Override
    public long getContentLength() {
        return content.length;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(content);
        outputStream.flush();
    }

    public String getCharset() {
        return charset;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getString() {
        return string;
    }

    public byte[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "StringBody{" +
                "infoCharset='" + charset + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", string='" + string + '\'' +
                '}';
    }
}
