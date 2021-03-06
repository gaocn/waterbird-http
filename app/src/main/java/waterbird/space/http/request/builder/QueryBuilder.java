package waterbird.space.http.request.builder;

import java.util.LinkedHashMap;

import waterbird.space.http.request.param.HttpParamModel;

/**
 * Created by 高文文 on 2016/12/19.
 */

public abstract class QueryBuilder {
    public abstract LinkedHashMap<String, String> buildPrimaryMap(HttpParamModel paramModel);

}
