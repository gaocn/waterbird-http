package waterbird.space.http.request.content;

import waterbird.space.http.data.Constants;
import waterbird.space.http.data.Json;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class JsonBody extends StringBody {

    public JsonBody(Object param) {
    this(param, Constants.DEFAULT_CHARSET);
}
    public JsonBody(String param) {
        this(param, Constants.DEFAULT_CHARSET);
    }

    public JsonBody(Object json, String charset) {
        super(Json.get().toJson(json), charset, Constants.MIME_TYPE_JSON);
    }

    public JsonBody(String json, String charset) {
        super(json, charset, Constants.MIME_TYPE_JSON);
    }

    @Override
    public String toString() {
        return "JsonBody{}" + super.toString();
    }
}
