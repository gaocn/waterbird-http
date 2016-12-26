package waterbird.space.http.request;

import waterbird.space.http.listener.HttpListener;
import waterbird.space.http.parser.DataParser;
import waterbird.space.http.parser.impl.StringParser;
import waterbird.space.http.request.param.HttpParamModel;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class StringRequest extends BaseRequest<String> {
    public StringRequest(String uri) {
        super(uri);
    }

    public StringRequest(HttpParamModel paramModel) {
        super(paramModel);
    }

    public StringRequest(String uri, HttpParamModel paramModel) {
        super(uri, paramModel);
    }

    @Override
    public DataParser<String> createDataParser() {
        return new StringParser();
    }
}
