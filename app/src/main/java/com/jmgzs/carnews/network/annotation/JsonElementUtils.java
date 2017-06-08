package com.jmgzs.carnews.network.annotation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by WuXianLiang on 2016/9/14.
 */
public class JsonElementUtils {

    /**
     * 获取泛型的class
     *
     * @param obj
     * @return
     */
    public static Class getGenericType(Object obj) {
        return getGenericType(obj, 0);
    }

    public static Class getGenericType(Object obj, int index) {
        Type gType = obj.getClass().getGenericSuperclass();
        if (gType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) gType;
            Type[] tArgs = pType.getActualTypeArguments();
            if (tArgs != null && tArgs.length > 0) {
                for (int i = 0; i < tArgs.length; i++) {
                    if (i == index) {
                        Type t = tArgs[i];
                        return (Class<?>) t;
                    }
                }
            }
        }
        return null;
    }

}
