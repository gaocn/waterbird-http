package waterbird.space.http.request;

import waterbird.space.http.listener.HttpListener;
import waterbird.space.http.parser.DataParser;
import waterbird.space.http.parser.impl.ByteParser;
import waterbird.space.http.request.param.HttpParamModel;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class ByteArrayRequest extends BaseRequest<byte[]> {

    public ByteArrayRequest(String uri) {
        super(uri);
    }

    public ByteArrayRequest(HttpParamModel paramModel) {
        super(paramModel);
    }

    public ByteArrayRequest(String uri, HttpParamModel paramModel) {
        super(uri, paramModel);
    }

    @Override
    public DataParser<byte[]> createDataParser() {
        return new ByteParser();
    }
}
