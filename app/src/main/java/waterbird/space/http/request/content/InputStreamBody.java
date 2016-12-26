package waterbird.space.http.request.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import waterbird.space.http.data.Constants;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class InputStreamBody extends HttpBody {
    private static final String TAG = "InputStreamBody";
    protected InputStream inputStream;
    protected long length;

    public InputStreamBody(InputStream inputStream) {
        this(inputStream, null);
    }
    public InputStreamBody(InputStream inputStream, String contentType) {
        this(inputStream, contentType, -1);
    }
    public InputStreamBody(InputStream inputStream, String contentType, long length) {
        this.inputStream = inputStream;
        this.length = length;
        this.contentType = contentType != null ? contentType : Constants.MIME_TYPE_OCTET_STREAM;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public long getContentLength() {
        return length;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        if(inputStream == null) {
            return;
        }

        try {
            final byte[] buffer = new byte[OUTPUT_BUFFER_SIZE];
            int count = 0;

            /** consume data until EOF */
            if(length < 0) {
                while((count = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, count);
                }
            } else {
                /** continue to consume data, data length <= {@link #length}*/
                long remaining = length;
                while (remaining > 0) {
                    count = inputStream.read(buffer, 0, (int)Math.min(OUTPUT_BUFFER_SIZE, remaining));
                    if(count == -1) {
                        break;
                    }
                    outputStream.write(buffer, 0, count);
                    remaining -= count;
                }
            }
            outputStream.flush();
        } catch (Exception e) {
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
