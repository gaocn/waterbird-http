package waterbird.space.http.request.content.multi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import waterbird.space.http.data.Constants;
import waterbird.space.http.utils.StringCodingUtils;

/**
 * Created by 高文文 on 2016/12/26.
 *
 * 抽象上传类
 */

public abstract class BasePart {
    protected static final Charset charset = BoundaryCreater.charset;
    public static final byte[] CR_LF = StringCodingUtils.getBytes("\r\n", charset);
    public static final byte[] TRANSFER_ENCODING_BINARY =
            StringCodingUtils.getBytes("Content-Transfer-Encoding: binary\r\n", charset);
    public static final byte[] TRANSFER_ENCODING_8BIT =
            StringCodingUtils.getBytes("Content-Transfer-Encoding: 8bit\r\n", charset);

    protected String key;
    protected String mimeType = Constants.MIME_TYPE_OCTET_STREAM;
    protected MultipartBody multipartBody;
    public byte[] header;

    protected BasePart(String key, String mimeType) {
        this.key = key;
        if (mimeType != null) {
            this.mimeType = mimeType;
        }
    }

    //此方法需要被调用以产生header（开发者无需自己调用，Entity会调用它）
    public byte[] createHeader(byte[] boundaryLine) {
        ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
        try {
            headerStream.write(boundaryLine);
            headerStream.write(createContentDisposition());
            headerStream.write(createContentType());
            headerStream.write(getTransferEncoding());
            headerStream.write(CR_LF);
            header = headerStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return header;
    }

    protected abstract byte[] createContentType();

    protected abstract byte[] createContentDisposition();

    public abstract long getTotalLength() throws IOException;

    public abstract byte[] getTransferEncoding();

    public abstract void writeTo(OutputStream out) throws IOException;

    public void writeToServer(OutputStream out) throws IOException {
        if (header == null) {
            throw new RuntimeException("Not call createHeader()，未调用createHeader方法");
        }
        out.write(header);
        updateProgress(header.length);
        writeTo(out);
    }

    protected void updateProgress(int length) {
        if (multipartBody != null) {
            multipartBody.updateProgress(length);
        }
    }

    public MultipartBody getMultipartBody() {
        return multipartBody;
    }

    public void setMultipartBody(MultipartBody multipartBody) {
        this.multipartBody = multipartBody;
    }
}
