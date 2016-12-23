package waterbird.space.http.parser;

import waterbird.space.http.request.BaseRequest;

/**
 * Created by 高文文 on 2016/12/19.
 */

public abstract class DataParser<T> {
    private BaseRequest request;

    public abstract  <T> T getData();
    public abstract <T> T getRawString();

    /*__________________     setters & getters ____________________*/

    public BaseRequest getRequest() {
        return request;
    }

    public void setRequest(BaseRequest request) {
        this.request = request;
    }
}
