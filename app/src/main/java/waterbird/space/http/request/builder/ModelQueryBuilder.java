package waterbird.space.http.request.builder;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import waterbird.space.http.data.Charsets;
import waterbird.space.http.data.Constants;
import waterbird.space.http.data.NameValuePair;
import waterbird.space.http.log.HttpLog;
import waterbird.space.http.request.param.HttpCustomParam;
import waterbird.space.http.request.param.HttpParam;
import waterbird.space.http.request.param.HttpParamModel;
import waterbird.space.http.request.param.HttpRichParamModel;
import waterbird.space.http.request.param.NonHttpParam;

/**
 * Created by 高文文 on 2016/12/23.
 *
 * 实现HttpParamModel接口的类需要
 */

public abstract class ModelQueryBuilder {
    private static final String TAG = "ModelQueryBuilder";
    private String charset = Charsets.UTF_8;

    protected abstract CharSequence buildSecondaryValue(Object model);

    /**
     *  对实现{@link HttpParamModel}的类，通过此方法对其中的域构建为Key=Value形式存在{@link LinkedHashMap<String, String>}中
     * @param model
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public LinkedHashMap<String, String> buildPrimaryMap(HttpParamModel model) throws IllegalAccessException, InvocationTargetException {
        if(model == null) return null;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        ArrayList<Field> fields = getAllDeclacredFields(model.getClass());

        //put all fields and value into map
        for(int i = 0, size = fields.size(); i < size; i++) {
            Field field = fields.get(i);
            field.setAccessible(true);
            HttpParam keyAnnotation = field.getAnnotation(HttpParam.class);
            String key = keyAnnotation != null ? keyAnnotation.value() : field.getName();
            Object value = field.get(model);
            if(value != null) {
                if(isPrimitive(value)) {
                    /**1. 原始类型值 */
                    map.put(key, value.toString());
                } else if(value instanceof HttpCustomParam) {
                    /**2. 自定义类型值 */
                    Method[] methods = value.getClass().getDeclaredMethods();
                    for(Method method : methods) {
                        if(method.getAnnotation(HttpCustomParam.CustomValueBuilder.class) != null) {
                            method.setAccessible(true);
                            Object retVal = method.invoke(value);
                            if(retVal != null) {
                                map.put(key, retVal.toString());
                            }
                            break;
                        }

                    }
                } else {
                    /** 3. 其他类型值，通过{@link buildSecondaryValue(model)} 提供 */
                    CharSequence charSeq = buildSecondaryValue(model);
                    if(charSeq != null) {
                        map.put(key, charSeq.toString());
                    }
                }
            }

        }

        return map;
    }

    /**
     * 对实现{@link HttpParamModel}的类，存放在{@link LinkedList<NameValuePair>}中
     * @param model
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public LinkedList<NameValuePair> buildPrimaryPair(HttpParamModel model) throws InvocationTargetException, IllegalAccessException {
        LinkedHashMap<String, String> params = buildPrimaryMap(model);
        LinkedList<NameValuePair> pairs = new LinkedList<>();
        for(Map.Entry<String, String> entry : params.entrySet()) {
            pairs.add(new NameValuePair(entry.getKey(), entry.getValue()));
        }
        return pairs;
    }

    /**
     * 捕获{@link #buildPrimaryPair(HttpParamModel)}方法抛出的异常
     * @param model
     * @return
     */
    public LinkedList<NameValuePair> buildPrimaryPairSafelyl(HttpParamModel model) {
        try {
            return buildPrimaryPair(model);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*__________________     Util Methods _____________________*/

    protected static boolean isPrimitive(Object value) {
        return value instanceof Number
                || value instanceof CharSequence
                || value instanceof Boolean
                || value instanceof Character;
    }

    /** 静态常量、注解NonHttpParam注释的变量、编译器生成的变量均忽略 */
    protected static boolean isIgnoredField(Field field) {
        return (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()))
                || field.getAnnotation(NonHttpParam.class) != null
                || field.isSynthetic();
    }

    /**
     * 返回clazz及其所有祖先类的变量域，不包括静态常量，NonHttpParam注释的和编译器生成的；
     * 对{@link waterbird.space.http.request.param.HttpRichParamModel}类，不做处理
     */
    protected static ArrayList<Field> getAllDeclacredFields(Class<?> clazz) {
        ArrayList<Field> fields = new ArrayList<>();

        Class targetClazz = clazz;
        while(targetClazz != null && targetClazz != HttpRichParamModel.class && targetClazz != Object.class) {
           for(Field field : targetClazz.getDeclaredFields()) {
               if(!isIgnoredField(field)) {
                   fields.add(field);
               }
           }
            targetClazz = targetClazz.getSuperclass();
        }
        
        if(HttpLog.isPrint) {
            Log.d(TAG, "getAllDeclacredFields: [ Fields of class " + clazz.getSimpleName() + "] " + fields);
        }
        
        return fields;
    }

    /**
     * append encoded key and "=" to StringBuilder
     */
    public StringBuilder buildUriKey(StringBuilder sb, String key) throws UnsupportedEncodingException {
        if(key != null && !key.isEmpty()) {
            sb.append(encode(key)).append(Constants.EQUALS);
        }
        return sb;
    }

    public String  decode(String content) throws UnsupportedEncodingException {
        return URLDecoder.decode(content, charset);
    }

    public String encode(String content) throws UnsupportedEncodingException {
        return URLEncoder.encode(content, charset);
    }
}

