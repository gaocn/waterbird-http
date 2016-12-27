package waterbird.space.http.request.content.multi;

import java.io.UnsupportedEncodingException;

import waterbird.space.http.data.Constants;
import waterbird.space.http.utils.StringCodingUtils;

/**
 * Created by 高文文 on 2016/12/27.
 */

public class StringPart extends BytesPart {
    private static final String TAG = "StringPart";
    protected String charset;
    protected  String mimeType;

    public StringPart(String key, String data) {
        this(key, data, Constants.DEFAULT_CHARSET, Constants.MIME_TYPE_TEXT);
    }

    public StringPart(String key, String data, String charset, String mimeType) {
        super(key, mimeType, getBytes(data, charset));
        this.charset = charset;
        this.mimeType = mimeType == null ? Constants.MIME_TYPE_TEXT : mimeType;
    }

    public static byte[] getBytes(String string, String charset) {
        try {
            return string.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] getTransferEncoding() {
        return TRANSFER_ENCODING_8BIT;
    }

    @Override
    protected byte[] createContentType() {
        return StringCodingUtils.getBytes(Constants.CONTENT_TYPE + ": " + mimeType + " " + Constants.CHARSET_PARAM + charset +
                "\r\n", infoCharset);
    }
}
