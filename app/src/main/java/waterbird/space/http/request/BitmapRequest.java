package waterbird.space.http.request;

import android.graphics.Bitmap;

import java.io.File;

import waterbird.space.http.parser.DataParser;
import waterbird.space.http.parser.impl.BitmapParser;
import waterbird.space.http.request.param.HttpParamModel;
import waterbird.space.http.request.param.NonHttpParam;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class BitmapRequest extends BaseRequest<Bitmap> {
    @NonHttpParam
    protected File savedFile;

    public BitmapRequest(HttpParamModel paramModel) {
        super(paramModel);
    }

    public BitmapRequest(HttpParamModel paramModel, File savedToFile) {
        super(paramModel);
        this.savedFile = savedToFile;
    }

    public BitmapRequest(HttpParamModel paramModel, String savedToPath) {
        super(paramModel);
        setFilePath(savedToPath);
    }

    private BitmapRequest setFilePath(String savedToPath) {
        if(savedToPath != null) {
            savedFile = new File(savedToPath);
        }
        return this;
    }

    public BitmapRequest(String uri) {
        super(uri);
    }

    public BitmapRequest(String uri, File savedFile) {
        super(uri);
        this.savedFile = savedFile;
    }

    public BitmapRequest(String uri, String savedToPath) {
        super(uri);
        setFilePath(savedToPath);
    }

    @Override
    public DataParser<Bitmap> createDataParser() {
        return new BitmapParser(savedFile);
    }

    public File getCachedFile() {
        return savedFile != null ? savedFile : super.getCachedFile();
    }

}
