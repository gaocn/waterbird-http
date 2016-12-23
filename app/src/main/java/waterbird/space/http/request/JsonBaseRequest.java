package waterbird.space.http.request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import waterbird.space.http.parser.DataParser;
import waterbird.space.http.request.param.HttpParamModel;

/**
 * Created by 高文文 on 2016/12/23.
 */

public abstract class JsonBaseRequest<T> extends BaseRequest<T> {

    protected Type resultType;

    public JsonBaseRequest(String uri) {
        super(uri);
    }

    public JsonBaseRequest(HttpParamModel paramModel) {
        super(paramModel);
    }

    public JsonBaseRequest(String uri, HttpParamModel paramModel) {
        super(uri, paramModel);
    }


    public Type getResultType() {
        if(resultType == null) {
            resultType = ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        return resultType;
    }

    public <R extends JsonBaseRequest<T>> R setResultType(Type resultType) {
        this.resultType = resultType;
        return (R)this;
    }


    @Override
    public DataParser<T> createDataParser() {
//        return new JsonParser<T>(getResultType());
        return null;
    }
}
