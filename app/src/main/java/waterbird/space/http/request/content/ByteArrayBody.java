package waterbird.space.http.request.content;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import waterbird.space.http.data.Constants;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class ByteArrayBody extends HttpBody {
    private byte[] data;

    public ByteArrayBody(byte[] data) {
        this(data, Constants.MIME_TYPE_OCTET_STREAM);
    }
    public ByteArrayBody(byte[] data, String contenType) {
        this.data = data;
        this.contentType = contenType;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public long getContentLength() {
        return data.length;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(data);
        outputStream.flush();
    }

    @Override
    public String toString() {
        return "ByteArrayBody{" +
                "data=" + Arrays.toString(data) +
                '}' + super.toString();
    }
}
