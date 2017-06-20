package com.jmgzs.lib_network.network.annotation;

import java.lang.reflect.Field;

/**json过滤器接口，用于进行JsonElement注解类的功能实现
 * Created by WuXianLiang on 2016/9/8.
 */
public interface IJsonFilter {

    /**
     * 根据定义规则验证json数据，出错则抛出异常中断解析
     * @param annotation 注解对象，获取定义的规则值
     * @param field 当前需要判断的域
     * @param data 解析出的对应json数据
     * @param target 需要赋值的对象
     * @throws JsonElement.JsonNotInvalidException 当不符合验证规则时抛出该异常
     */
    void filter(JsonElement annotation, Field field, Object data, Object target) throws JsonElement.JsonNotInvalidException;

}
