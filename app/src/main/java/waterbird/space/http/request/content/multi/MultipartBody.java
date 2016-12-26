package waterbird.space.http.request.content.multi;

import java.io.IOException;
import java.io.OutputStream;
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

    //TODO


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
