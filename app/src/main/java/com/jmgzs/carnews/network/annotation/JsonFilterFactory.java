package com.jmgzs.carnews.network.annotation;

/**IJsonFilter simple factory class
 * Created by WuXianLiang on 2016/9/8.
 */
public class JsonFilterFactory {

    public static IJsonFilter getFilterInstance(Class type){
        IJsonFilter filter;
        if (type == boolean.class || type == Boolean.class){
            filter = new BooleanJsonFilter();
        }else if (type == short.class || type == Short.class){
            filter = new ShortJsonFilter();
        }else if (type == int.class || type == Integer.class){
            filter = new IntJsonFilter();
        }else if (type == long.class || type == Long.class){
            filter = new LongJsonFilter();
        }else if (type == float.class || type == Float.class){
            filter = new FloatJsonFilter();
        }else if (type == double.class || type == Double.class){
            filter = new DoubleJsonFilter();
        }else if (type == char.class || type == Character.class){
            filter = new CharacterJsonFilter();
        }else if (type == byte.class || type == Byte.class){
            filter = new ByteJsonFilter();
        }else if (type == String.class){
            filter = new StringJsonFilter();
        }else{
            filter = new NotNullJsonFilter();
        }
        return filter;
    }
}
