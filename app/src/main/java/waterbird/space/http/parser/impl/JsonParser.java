package waterbird.space.http.parser.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import waterbird.space.http.data.Json;
import waterbird.space.http.parser.MemCacheableParser;

/**
 * Created by 高文文 on 2016/12/26.
 *
 * parse inputstream to JOPO
 */

public class JsonParser<T> extends MemCacheableParser<T> {
    private static final String TAG = "JsonParser";
    protected Type clazz;
    protected String json;

    public JsonParser(Type clazz) {
        this.clazz = clazz;
    }

    @Override
    protected boolean tryKeepToCache(T data) throws IOException {
        return keepToCache(json);
    }

    @Override
    protected T parseDiskCache(InputStream stream, long length) throws IOException {
        json =  streamToString(stream, length, charset);
        return Json.get().toObject(json, clazz);
    }

    @Override
    protected T parseNetowrkStream(InputStream inputStream, long len, String charset) throws IOException {
        json = streamToString(inputStream, len, charset);
        return Json.get().toObject(json, clazz);
    }

    @Override
    public String getRawString() {
        return json;
    }

    /**
     * sjon to POJO object
     */
    public <C> C getJsonModel(Class<C> clazz) {
        return Json.get().toObject(json, clazz);
    }

    @Override
    public String toString() {
        return "JsonParser{" +
                "clazz=" + clazz +
                ", json='" + json + '\'' +
                '}' + super.toString();
    }
}
