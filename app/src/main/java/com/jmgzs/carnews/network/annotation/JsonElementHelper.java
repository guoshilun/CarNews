package com.jmgzs.carnews.network.annotation;

import java.lang.reflect.Field;

/**JsonElement注释功能实现类
 * Created by WuXianLiang on 2016/9/7.
 */
public class JsonElementHelper {

    /**
     * 检测域对应的json数据是否符合要求
     * @param field 需要设置的数据域
     * @param data json对象
     * @param target 需要解析的数据类
     * @throws JsonElement.JsonNotInvalidException
     */
    public static void checkJsonValidation(Field field, Object data, Object target) throws JsonElement.JsonNotInvalidException{
        if (field == null || target == null || !field.getDeclaringClass().isInstance(target)){
            return;
        }
        JsonElement annotation = field.getAnnotation(JsonElement.class);
        if (annotation == null){
            return;
        }
        Class valueTypeClass = annotation.valueType();
        if (valueTypeClass == null){
            return;
        }
        IJsonFilter filter = JsonFilterFactory.getFilterInstance(valueTypeClass);
        filter.filter(annotation, field, data, target);
    }
}
