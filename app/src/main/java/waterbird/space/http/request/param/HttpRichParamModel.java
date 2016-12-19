package waterbird.space.http.request.param;

/**
 * Created by 高文文 on 2016/12/19.
 */

public class HttpRichParamModel<T> implements HttpParamModel {

    public boolean isFieldsAttachToUrl() {
        return true;
    }
}
