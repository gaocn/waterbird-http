package waterbird.space.http.request.builder;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import waterbird.space.http.data.Constants;
import waterbird.space.http.request.param.HttpCustomParam;

/**
 * Created by 高文文 on 2016/12/23.
 *
 *  when uri query parameter's value is complex, build value into default style.
 * in this case, value will intelligently translate to default string.
 * <p/>
 * such as :
 * http://def.so? mapkey={k=v,k1=v1} & arraykey={v1,v2,v3} &
 * fieldName={field1Name=value1, field2Name=value2}
 * <p/>
 * rule is :
 * Map : map={k=v,k1=v1}
 * Array : k={v1,v2,v3}
 * JavaObject(model) : fieldName={field1Name=value1, field2Name=value2}
 */

public class SimpleQueryBuilder extends ModelQueryBuilder {
    private static final String TAG = "SimpleQueryBuilder";

    @Override
    protected CharSequence buildSecondaryValue(Object model) {
        try {
            StringBuilder stringBuilder = new StringBuilder();

            if(model instanceof Collection<?> || model instanceof Object[]) {
                /** 1. 数组，集合
                 *      use '[' and ']' to enclose data, use ',' to plit array value
                 */
                Object[] objects = model instanceof Collection<?> ? ((Collection) model).toArray() : (Object[]) model;
                buildUriKey(stringBuilder, null).append(Constants.ARRAY_ECLOSING_LEFT);
                int i = 0, size = objects.length;
                for (Object v : objects) {
                    buildValueRecursively(stringBuilder, null, v,  ++i == size ? Constants.NONE_SPLIT : Constants.SECOND_LEVEL_SPLIT);
                }
                stringBuilder.append(Constants.ARRAY_ECLOSING_RIGHT);
            } else if (model instanceof Map<?, ?>) {
                /**2. Hash表
                 *      use '{' and '}' to enclose data, use ',' to split array value.
                 */
                Map<?, ?> map = (Map<?, ?>) model;
                buildUriKey(stringBuilder, null).append(Constants.KV_ECLOSING_LEFT);
                int i = 0, size = map.size();
                for (Map.Entry<?, ?> v : map.entrySet()) {
                    if (v.getKey() instanceof CharSequence || v.getKey() instanceof Character) {
                        buildValueRecursively(stringBuilder, v.getKey().toString(), v.getValue(), ++i == size
                                ? Constants.NONE_SPLIT
                                : Constants.SECOND_LEVEL_SPLIT);
                    } else {
                        buildValueRecursively(stringBuilder, v.getKey().getClass().getSimpleName(), v.getValue(), ++i == size
                                ? Constants.NONE_SPLIT
                                : Constants.SECOND_LEVEL_SPLIT);
                    }
                }
                stringBuilder.append(Constants.KV_ECLOSING_RIGHT);
            } else {
                /**3. 其他 JOPO
                 *      find all field.
                 */
                ArrayList<Field> fieldList = getAllDeclacredFields(model.getClass());
                for (int i = 0, size = fieldList.size() - 1; i <= size; i++) {
                    Field f = fieldList.get(i);
                    f.setAccessible(true);
                    String key = f.getName();
                    Object value = f.get(model);
                    if (value != null) {
                        // value is primitive
                        stringBuilder.append(Constants.KV_ECLOSING_LEFT);
                        buildValueRecursively(stringBuilder, key, value, i == size ? Constants.NONE_SPLIT : Constants.SECOND_LEVEL_SPLIT);
                        stringBuilder.append(Constants.KV_ECLOSING_RIGHT);
                    }
                }
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void buildValueRecursively(StringBuilder sb, String key, Object value, String split) throws UnsupportedEncodingException, InvocationTargetException, IllegalAccessException {
        if(value == null) return;
        if (value instanceof Number || value instanceof CharSequence || value instanceof Character || value instanceof Boolean) {
            // when value is primitive , build as "key=value"
            buildUriKey(sb, key).append(encode(value.toString())).append(split);
        } else if (value instanceof HttpCustomParam) {
            // when value is inherited from Request.Builder , build as
            // "key="+method.invoke().
            Method methods[] = HttpCustomParam.class.getDeclaredMethods();
            for (Method m : methods) {
                // invoke the method which has specified Annotation
                if (m.getAnnotation(HttpCustomParam.CustomValueBuilder.class) != null) {
                    m.setAccessible(true);
                    Object v = m.invoke(value);
                    if (v != null) {
                        buildUriKey(sb, key).append(encode(v.toString())).append(split);
                    }
                    break;
                }
            }
        } else if (value instanceof Collection<?> || value instanceof Object[]) {
            // when value is array ,use '[' and ']' to enclose data, use ',' to
            // split array value.
            Object[] objs = value instanceof Collection<?> ? ((Collection<?>) value).toArray() : (Object[]) value;
            buildUriKey(sb, key).append(Constants.ARRAY_ECLOSING_LEFT);
            int i = 0, size = objs.length;
            for (Object v : objs) {
                buildValueRecursively(sb, null, v, ++i == size ? Constants.NONE_SPLIT : Constants.SECOND_LEVEL_SPLIT);
            }
            sb.append(Constants.ARRAY_ECLOSING_RIGHT).append(split);
        } else if (value instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) value;
            // when value is map ,use '{' and '}' to enclose data, use ',' to
            // split array value.
            buildUriKey(sb, key).append(Constants.KV_ECLOSING_LEFT);
            int i = 0, size = map.size();
            for (Map.Entry<?, ?> v : map.entrySet()) {
                if (v.getKey() instanceof CharSequence || v.getKey() instanceof Character) {
                    buildValueRecursively(sb, v.getKey().toString(), v.getValue(), ++i == size
                            ? Constants.NONE_SPLIT
                            : Constants.SECOND_LEVEL_SPLIT);
                } else {
                    buildValueRecursively(sb, v.getKey().getClass().getSimpleName(), v.getValue(), ++i == size
                            ? Constants.NONE_SPLIT
                            : Constants.SECOND_LEVEL_SPLIT);
                }
            }
            sb.append(Constants.KV_ECLOSING_RIGHT).append(split);
        } else {
            buildUriKey(sb, key);
            sb.append(Constants.KV_ECLOSING_LEFT);
            // find all field.
            ArrayList<Field> fieldList = getAllDeclacredFields(value.getClass());
            for (int i = 0, size = fieldList.size() - 1; i <= size; i++) {
                Field f = fieldList.get(i);
                f.setAccessible(true);
                String nextKey = f.getName();
                Object nextValue = f.get(value);
                if (nextValue != null) {
                    sb.append(Constants.KV_ECLOSING_LEFT);
                    buildValueRecursively(sb, nextKey, nextValue, i == size ? Constants.NONE_SPLIT : Constants.SECOND_LEVEL_SPLIT);
                    sb.append(Constants.KV_ECLOSING_RIGHT);
                }
            }
            sb.append(Constants.KV_ECLOSING_RIGHT).append(split);
        }
    }
}
