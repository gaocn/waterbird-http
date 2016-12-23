package waterbird.space.http.parser;

/**
 * Created by 高文文 on 2016/12/20.
 */

public class TestDataParser extends DataParser {
    @Override
    public Object getData() {
        return "Haha~~~String";
    }

    @Override
    public Object getRawString() {
        return "rawSring: haha~~~";
    }
}
