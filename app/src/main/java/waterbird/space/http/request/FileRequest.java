package waterbird.space.http.request;

import java.io.File;

import waterbird.space.http.parser.DataParser;
import waterbird.space.http.parser.impl.FileParser;
import waterbird.space.http.request.param.HttpParamModel;
import waterbird.space.http.request.param.NonHttpParam;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class FileRequest extends BaseRequest<File> {
    @NonHttpParam
    private File savedFile;

    public FileRequest(String uri) {
        super(uri);
    }

    public FileRequest(HttpParamModel paramModel) {
        super(paramModel);
    }

    public FileRequest(String uri, File savedFile) {
        super(uri);
        this.savedFile = savedFile;
    }

    public FileRequest(HttpParamModel paramModel, File savedFile) {
        super(paramModel);
        this.savedFile = savedFile;
    }

    public FileRequest(String uri, String savedPath) {
        super(uri);
        setFilePath(savedPath);
    }

    public FileRequest(HttpParamModel paramModel, String savedPath) {
        super(paramModel);
        setFilePath(savedPath);
    }

    private FileRequest setFilePath(String savedToPath) {
        if(savedToPath != null) {
            savedFile = new File(savedToPath);
        }
        return this;
    }

    @Override
    public DataParser<File> createDataParser() {
        return new FileParser(savedFile);
    }

    @Override
    public File getCachedFile() {
        return savedFile != null ? savedFile : super.getCachedFile();
    }
}
