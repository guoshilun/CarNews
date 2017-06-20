package com.jmgzs.lib_network.network.annotation;

import android.text.TextUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * json解析注解，用于在json反射生成实例时的数据校验功能
 * Created by WuXianLiang on 2016/9/7.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonElement {

    //必填字段，标识当前注解的域类型
    Class valueType();

    //是否对应json值可以为空
    boolean isNotNull() default false;

    //值的包含范围
    byte[] byteIncludeRange() default {};

    short[] shortIncludeRange() default {};

    int[] intIncludeRange() default {};

    long[] longIncludeRange() default {};

    float[] floatIncludeRange() default {};

    double[] doubleIncludeRange() default {};

    char[] charIncludeRange() default {};

    String[] strIncludeRange() default {};

    //值的最大最小值
    byte byteMaxValue() default Byte.MAX_VALUE;

    byte byteMinValue() default Byte.MIN_VALUE;

    short shortMinValue() default Short.MIN_VALUE;

    short shortMaxValue() default Short.MAX_VALUE;

    int intMinValue() default Integer.MIN_VALUE;

    int intMaxValue() default Integer.MAX_VALUE;

    long longMinValue() default Long.MIN_VALUE;

    long longMaxValue() default Long.MAX_VALUE;

    float floatMinValue() default Float.MIN_VALUE;

    float floatMaxValue() default Float.MAX_VALUE;

    double doubleMinValue() default Double.MIN_VALUE;

    double doubleMaxValue() default Double.MAX_VALUE;

    //默认值
    boolean booleanDefaultValue() default false;

    byte byteDefaultValue() default 0;

    short shortDefaultValue() default 0;

    int intDefaultValue() default 0;

    long longDefaultValue() default 0;

    float floatDefaultValue() default 0;

    double doubleDefaultValue() default 0;

    char charDefaultValue() default 0;

    String strDefaultValue() default "";

    class JsonNotInvalidException extends Exception {

        private String msg;

        public JsonNotInvalidException() {
        }

        public JsonNotInvalidException(String msg) {
            super(msg);
        }

        public void setTooHighMessage(String fieldName, String className, Object value, Object maxValue){
            setOutOfRangeMessage(false, fieldName, className, value, maxValue);
        }

        public void setTooLowMessage(String fieldName, String className, Object value, Object minValue){
            setOutOfRangeMessage(true, fieldName, className, value, minValue);
        }

        private void setOutOfRangeMessage(boolean isLow, String fieldName, String className, Object value, Object rangeValue){
            msg = fieldName + " (" + className + ") is "+ value + (isLow ? ", lower than min " : ", higher than max ") + rangeValue;
        }

        @Override
        public String getMessage() {
            if (TextUtils.isEmpty(msg)){
                return super.getMessage();
            }else{
                return msg;
            }
        }
    }
}
