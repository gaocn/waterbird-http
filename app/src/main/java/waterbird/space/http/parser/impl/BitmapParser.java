package waterbird.space.http.parser.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import waterbird.space.http.parser.FileCacheableParser;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class BitmapParser extends FileCacheableParser<Bitmap> {
    public BitmapParser(){}

    @Override
    protected Bitmap parseDiskCache(File file) throws IOException {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    @Override
    protected Bitmap parseNetowrkStream(InputStream inputStream, long len, String charset) throws IOException {
        file = streamToFile(inputStream, len);
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }
}
