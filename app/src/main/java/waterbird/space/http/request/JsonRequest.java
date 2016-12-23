package waterbird.space.http.request;

import java.lang.reflect.Type;

import waterbird.space.http.data.TypeToken;
import waterbird.space.http.request.param.HttpParamModel;

/**
 * Created by 高文文 on 2016/12/23.
 */

public class JsonRequest<T> extends JsonBaseRequest<T> {
    public JsonRequest(String uri, Type resultType) {
        super(uri);
        setResultType(resultType);
    }

    public JsonRequest(HttpParamModel paramModel, Type resultType) {
        super(paramModel);
        setResultType(resultType);
    }

    public JsonRequest(String uri, HttpParamModel paramModel) {
        super(uri, paramModel);
    }

    public JsonRequest(String url, TypeToken<T> resultType) {
        super(url);
        setResultType(resultType.getType());
    }

    public JsonRequest(HttpParamModel model, TypeToken<T> resultType) {
        super(model);
        setResultType(resultType.getType());
    }

    /*

    这里没有使用 因为和父类中的方法一样，不需要重写
    @Override
    public DataParser<T> createDataParser() {
        return new JsonParser<T>(resultType);
    }

    */
}
