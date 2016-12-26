package waterbird.space.http.parser.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import waterbird.space.http.parser.FileCacheableParser;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class FileParser extends FileCacheableParser<File> {

    public FileParser(){}

    public FileParser(File file) {
        this.file = file;
    }

    @Override
    protected File parseDiskCache(File file) throws IOException {
        return file;
    }

    @Override
    protected File parseNetowrkStream(InputStream inputStream, long len, String charset) throws IOException {
        return streamToFile(inputStream, len);
    }
}
