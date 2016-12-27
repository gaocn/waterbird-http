package waterbird.space.http.request.content.multi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import waterbird.space.http.HttpConfig;
import waterbird.space.http.data.Constants;
import waterbird.space.http.log.HttpLog;
import waterbird.space.http.utils.StringCodingUtils;

/**
 * Created by 高文文 on 2016/12/27.
 * 上传文件
 */

public class FilePart extends BasePart {
    private static final String TAG = "FilePart";
    public File file;

    public FilePart(String key, File file) {
        this(key, Constants.MIME_TYPE_OCTET_STREAM, file);
    }

    public FilePart(String key, String mimeType, File file) {
        super(key, mimeType);
        this.file = file;
    }

    @Override
    protected byte[] createContentType() {
        return StringCodingUtils.getBytes(Constants.CONTENT_TYPE + ": " + mimeType + "\r\n", infoCharset);
    }

    @Override
    protected byte[] createContentDisposition() {
        String dis = "Content-Disposition: form-data; name=\"" + key;
        return StringCodingUtils.getBytes(dis + "\"; filename=\"" + file.getName() + "\"\r\n", infoCharset);
    }

    @Override
    public long getTotalLength() throws IOException {
        long len = file.length();
        if (HttpLog.isPrint) {
            HttpLog.v(TAG, TAG + " 内容长度header ： " + header.length
                    + " ,body: " + len + " ," + "换行：" + CR_LF.length);
        }
        return header.length + len + CR_LF.length;
    }

    @Override
    public byte[] getTransferEncoding() {
        return TRANSFER_ENCODING_BINARY;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            int count = 0;
            byte[] buffer = new byte[HttpConfig.DEFAULT_BUFFER_SIZE];

            while((count = is.read(buffer)) != -1) {

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
                if(is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
