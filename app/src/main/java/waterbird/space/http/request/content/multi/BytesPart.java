package waterbird.space.http.request.content.multi;

import java.io.IOException;
import java.io.OutputStream;

import waterbird.space.http.data.Constants;
import waterbird.space.http.log.HttpLog;
import waterbird.space.http.utils.StringCodingUtils;

/**
 * Created by 高文文 on 2016/12/27.
 */

public class BytesPart extends BasePart {
    private static final String TAG = "BytesPart";
    public byte[] data;

    public BytesPart(String key, byte[] data) {
        this(key, null, data);
    }

    public BytesPart(String key, String mimeType, byte[] data) {
        super(key, mimeType);
        this.data = data;
    }

    @Override
    protected byte[] createContentType() {
        return StringCodingUtils.getBytes(Constants.CONTENT_TYPE + ": " + mimeType + "\r\n", infoCharset);
    }

    @Override
    protected byte[] createContentDisposition() {
        return StringCodingUtils.getBytes("Content-Disposition: form-data; name=\"" + key + "\"\r\n", infoCharset);
    }

    @Override
    public long getTotalLength() throws IOException {
        if (HttpLog.isPrint) if (HttpLog.isPrint) HttpLog.v(TAG, TAG + "内容长度 header ： " + header.length + " ,body: "
                + data.length + " ," + "换行：" + CR_LF.length);
        return header.length + data.length + CR_LF.length;
    }

    @Override
    public byte[] getTransferEncoding() {
        return TRANSFER_ENCODING_BINARY;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(data);
        out.write(CR_LF);
        updateProgress(data.length + CR_LF.length);
    }
}
