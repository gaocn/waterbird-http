package waterbird.space.http.data;

import java.lang.reflect.Type;

/**
 * Created by 高文文 on 2016/12/23.
 * with this, we can change json handler easily.
 * alibaba fastjson can not handle private attribute that without getter method.
 * so we choice the google gson.
 */

public abstract class Json {
    private static Json json;
    /**
     *  set new json instance
     * @param json
     * @return
     */
    public static Json set(Json json) {
        Json.json = json;
        return Json.json;
    }

    /**
     * set default json handler using Gson
     * @return
     */
    public static Json setDefault() {
        Json.json = new GsonImpl();
        return Json.json;
    }

    public static Json get() {
        if(Json.json == null) {
            Json.json = new GsonImpl();
        }
        return Json.json;
    }

    public abstract String toJson(Object src);

    public abstract <T> T toObject(String json, Class<T> clazz);

    public abstract <T> T toObject(String json, Type clazz);

    public abstract <T> T toObject(byte[] bytes, Class<T> clazz);
}
