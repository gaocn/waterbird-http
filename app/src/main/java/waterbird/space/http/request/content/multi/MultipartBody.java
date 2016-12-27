package waterbird.space.http.request.content.multi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import waterbird.space.http.data.Constants;
import waterbird.space.http.request.content.HttpBody;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class MultipartBody extends HttpBody {
    private LinkedList<BasePart> httpParts= new LinkedList<>();
    private long totalSize;
    private long bytesWritten;
    BoundaryCreater boundaryCreater;

    public MultipartBody() {
        boundaryCreater = new BoundaryCreater();
        //Content-Type: multipart/form-data; boundary=${bound}
        contentType = Constants.MIME_TYPE_FORM_DATA + Constants.BOUNDARY_PARAM + boundaryCreater.getBoundary();
    }

    public MultipartBody addPart(BasePart part) {
        if(part == null) {
            return this;
        }

        /**  note: set multipart to every part, so that we can get progress of these part */
        part.setMultipartBody(this);
        part.createHeader(boundaryCreater.getBoundaryLine());
        httpParts.add(part);

        return this;
    }

    public MultipartBody addPart(String key, InputStream inputStream, String fileName, String mimeType) {
        return addPart(new InputStreamPart(key, mimeType, inputStream, fileName));
    }

    public MultipartBody addPart(String key, String data, String charset, String mimeType) throws UnsupportedEncodingException {
        return addPart(new StringPart(key, data, charset, mimeType));
    }

    public MultipartBody addPart(String key, String mimeType, byte[] data) {
        return addPart(new BytesPart(key, mimeType, data));
    }

    public MultipartBody addPart(String key, String mimeType, File file) throws FileNotFoundException {
        return addPart(new FilePart(key, mimeType, file));
    }



    public long getContentLength() {
        long contentLen = -1;
        try {
            for (BasePart part : httpParts) {
                long len = 0;
                len = part.getTotalLength();
                if (len < 0) {
                    return -1;
                }
                contentLen += len;
            }
            contentLen += boundaryCreater.getBoundaryEnd().length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentLen;
    }

    public void writeTo(final OutputStream outstream) throws IOException {
        bytesWritten = 0;
        totalSize = (int) getContentLength();
        for (BasePart part : httpParts) {
            part.writeToServer(outstream);
        }
        outstream.write(boundaryCreater.getBoundaryEnd());
        updateProgress(boundaryCreater.getBoundaryEnd().length);
    }


    protected void updateProgress(long count) {
        bytesWritten += count;
        if (httpListener != null) {
            httpListener.notifyCallUploading(request, totalSize, bytesWritten);
        }
    }



    /*____________________    setters&getters       _________________________*/

    public LinkedList<BasePart> getHttpParts() {
        return httpParts;
    }

    public MultipartBody setHttpParts(LinkedList<BasePart> httpParts) {
        this.httpParts = httpParts;
        return this;
    }

    public BoundaryCreater getBoundaryCreater() {
        return boundaryCreater;
    }
}
