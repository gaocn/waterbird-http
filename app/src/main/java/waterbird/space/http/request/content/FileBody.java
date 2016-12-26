package waterbird.space.http.request.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import waterbird.space.http.data.Constants;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class FileBody extends HttpBody {
    private File file;

    public FileBody(File file) {
        this(file, Constants.MIME_TYPE_OCTET_STREAM);
    }
    public FileBody(File file, String contentType) {
        this.file = file;
        this.contentType = contentType;
    }

    public File getFile() {
        return file;
    }

    @Override
    public long getContentLength() {
        return file.length();
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        final InputStream inputStream = new FileInputStream(file);
        try {
            final byte[] buffer = new byte[OUTPUT_BUFFER_SIZE];
            int count = 0;
            while((count = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            outputStream.flush();
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
