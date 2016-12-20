package waterbird.space.http.request;

import waterbird.space.http.listener.HttpListener;
import waterbird.space.http.parser.DataParser;
import waterbird.space.http.parser.TestDataParser;
import waterbird.space.http.request.param.HttpParamModel;

/**
 * Created by 高文文 on 2016/12/20.
 */

public class ExampleBaseRequest extends BaseRequest<String> {
    public ExampleBaseRequest(String uri) {
        super(uri);
    }

    public ExampleBaseRequest(HttpParamModel paramModel) {
        super(paramModel);
    }

    public ExampleBaseRequest(HttpParamModel paramModel, HttpListener<String> listener) {
        super(paramModel, listener);
    }

    public ExampleBaseRequest(String uri, HttpParamModel paramModel) {
        super(uri, paramModel);
    }

    @Override
    public DataParser<String> createDataParser() {
        return new TestDataParser();
    }
}
