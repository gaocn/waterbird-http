package waterbird.space.http.request.builder;

import org.junit.Test;

import waterbird.space.http.request.JsonBaseRequest;

/**
 * Created by 高文文 on 2016/12/23.
 */
public class ModelQueryBuilderTest {
    @Test
    public void getAllDeclacredFields() throws Exception {
        ModelQueryBuilder.getAllDeclacredFields(JsonBaseRequest.class);
    }

}