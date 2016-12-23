package waterbird.space.http.request.builder;

import waterbird.space.http.data.Json;

/**
 * Created by 高文文 on 2016/12/19.
 *
 *when uri query parameter's value is complicated, build value into json.
 * in this case, value will intelligently translate to json string.
 * <p/>
 * such as:
 * http://def.so? key1 = value.toJsonString() & key2 = value.toJsonString()
 */

public class JsonQueryBuilder extends ModelQueryBuilder {

    @Override
    protected CharSequence buildSecondaryValue(Object model) {
        try {
            return Json.get().toJson(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
