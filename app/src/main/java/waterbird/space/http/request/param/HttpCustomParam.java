package waterbird.space.http.request.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 高文文 on 2016/12/23.
 *
 */

//TODO   unkown intention
public interface HttpCustomParam {
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CustomValueBuilder {}

    @CustomValueBuilder
    public CharSequence buildValue();
}
