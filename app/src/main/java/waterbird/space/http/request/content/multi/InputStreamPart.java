package waterbird.space.http.request.content.multi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import waterbird.space.http.HttpConfig;
import waterbird.space.http.data.Constants;
import waterbird.space.http.log.HttpLog;
import waterbird.space.http.utils.StringCodingUtils;

/**
 * Created by 高文文 on 2016/12/27.
 */

public class InputStreamPart extends BasePart {
    private static final String TAG = "InputStreamPart";
    protected InputStream inputStream;
    protected String fileName;

    public InputStreamPart(String key, InputStream inputStream) {
        this(key, Constants.MIME_TYPE_OCTET_STREAM, inputStream);
    }

    public InputStreamPart(String key, String mimeType, InputStream inputStream) {
        super(key, mimeType);
        this.inputStream = inputStream;
    }

    public InputStreamPart(String key, String mimeType, InputStream inputStream, String fileName) {
        super(key, mimeType);
        if(inputStream == null) {
            throw new NullPointerException("InputStream MUST NOT be null");
        }
        this.inputStream = inputStream;
        this.fileName = fileName;
    }

    @Override
    protected byte[] createContentType() {
        return StringCodingUtils.getBytes(Constants.CONTENT_TYPE + ": " + mimeType + "\r\n", infoCharset);
    }

    @Override
    protected byte[] createContentDisposition() {
        String dis = "Content-Disposition: form-data; name=\"" + key;
        return fileName == null ? StringCodingUtils.getBytes(dis + "\"\r\n", infoCharset)
                : StringCodingUtils.getBytes(dis + "\"; filename=\"" + fileName + "\"\r\n", infoCharset);
    }

    @Override
    public long getTotalLength() throws IOException {
        long len = inputStream.available();
        if (HttpLog.isPrint) {
            HttpLog.v(TAG, TAG + "内容长度 header ： " + header.length + " ,body: " + len + " ," +
                    "换行：" + CR_LF.length);
        }
        return header.length + len + CR_LF.length;
    }

    @Override
    public byte[] getTransferEncoding() {
        return TRANSFER_ENCODING_BINARY;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        try {
            int count = 0;
            byte[] buffer = new byte[HttpConfig.DEFAULT_BUFFER_SIZE];

            while((count = inputStream.read(buffer)) != -1) {

                out.write(buffer, 0, count);
                updateProgress(count);
            }
            out.write(CR_LF);
            updateProgress(CR_LF.length);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
