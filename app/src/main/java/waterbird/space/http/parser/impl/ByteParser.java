package waterbird.space.http.parser.impl;

import java.io.IOException;
import java.io.InputStream;

import waterbird.space.http.parser.MemCacheableParser;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class ByteParser extends MemCacheableParser<byte[]> {
    @Override
    protected boolean tryKeepToCache(byte[] data) throws IOException {
        return keepToCache(data);
    }

    @Override
    protected byte[] parseDiskCache(InputStream stream, long length) throws IOException {
        return streamToByteArray(stream, length);
    }

    @Override
    protected byte[] parseNetowrkStream(InputStream inputStream, long len, String charset) throws IOException {
        return streamToByteArray(inputStream, len);
    }
}
