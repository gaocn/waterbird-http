package waterbird.space.http.data;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by 高文文 on 2016/12/23.
 */

public class GsonImpl extends Json {

    private Gson gson =  new Gson();

    @Override
    public String toJson(Object src) {
        return gson.toJson(src);
    }

    @Override
    public <T> T toObject(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    @Override
    public <T> T toObject(String json, Type clazz) {
        return gson.fromJson(json, clazz);
    }

    @Override
    public <T> T toObject(byte[] bytes, Class<T> clazz) {
        return gson.fromJson(new String(bytes), clazz);
    }
}
