package waterbird.space.http.parser.impl;

import java.io.IOException;
import java.io.InputStream;

import waterbird.space.http.parser.MemCacheableParser;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class StringParser extends MemCacheableParser<String> {

    @Override
    protected boolean tryKeepToCache(String data) throws IOException {
        return keepToCache(data);
    }

    @Override
    protected String parseDiskCache(InputStream stream, long length) throws IOException {
        return streamToString(stream, length, charset);
    }

    @Override
    protected String parseNetowrkStream(InputStream inputStream, long len, String charset) throws IOException {
        return streamToString(inputStream, len, charset);
    }
}
