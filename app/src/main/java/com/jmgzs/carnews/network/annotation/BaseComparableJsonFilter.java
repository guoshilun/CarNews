package com.jmgzs.carnews.network.annotation;


import com.jmgzs.carnews.util.L;

import java.lang.reflect.Field;

/**
 * 可比较json过滤器，包含最大最小值检测
 * Created by WuXianLiang on 2016/9/8.
 */
public abstract class BaseComparableJsonFilter<T extends Number> extends BaseCompositeFilter {

    public BaseComparableJsonFilter() {
    }

    public BaseComparableJsonFilter(IJsonFilter parent) {
        super(parent);
    }

    protected void filterMinValue(JsonElement annotation, Field field, Object data, Object target) throws JsonElement.JsonNotInvalidException {
        if (data == null){
            try {
                field.setAccessible(true);
                data = field.get(target);
            } catch (IllegalAccessException e) {
                throw new JsonElement.JsonNotInvalidException("Min Comparable cannot access to field error of \"" + field.getName() + "\" !");
            }
        }
        if (!(data instanceof Comparable)) {
            throw new JsonElement.JsonNotInvalidException(field.getName() + " cannot be null!");
        }
        Comparable<T> min = getMinValue(annotation);
        Class<?> classT = JsonElementUtils.getGenericType(this);
        L.e("data:"+data.getClass()+"\tT:"+classT);
        if (!Number.class.isAssignableFrom(data.getClass())){
            throw new JsonElement.JsonNotInvalidException("Min Comparable Generic Type error of \"" + field.getName() + "\" ! need: " +Number.class +" get data: "+data.getClass());
        }else{
            try {
                T value = convertDataToTarget((Number)data);
                if (value == null){
                    throw new JsonElement.JsonNotInvalidException("Min Comparable Generic Type error of \"" + field.getName() + "\" ! " +data.getClass() +" cannot cast to "+classT);
                }
                if (min.compareTo(value) > 0) {
                    JsonElement.JsonNotInvalidException ex = new JsonElement.JsonNotInvalidException();
                    ex.setTooLowMessage(field.getName(), classT.getName(), data, min);
                    throw ex;
                }
            }catch(JsonElement.JsonNotInvalidException e){
                throw e;
            }catch (Exception e) {
//            e.printStackTrace();
                throw new JsonElement.JsonNotInvalidException("Min Comparable Generic Type error of \"" + field.getName() + "\" ! " +data.getClass() +" cannot cast to "+classT);
            }
        }
    }

    protected void filterMaxValue(JsonElement annotation, Field field, Object data, Object target) throws JsonElement.JsonNotInvalidException {
        if (data == null){
            try {
                field.setAccessible(true);
                data = field.get(target);
            } catch (IllegalAccessException e) {
                throw new JsonElement.JsonNotInvalidException("Max Comparable cannot access to field error of \"" + field.getName() + "\" !");
            }
        }
        if (!(data instanceof Comparable)) {
            throw new JsonElement.JsonNotInvalidException(field.getName() + " cannot be null!");
        }
        Comparable<T> max = getMaxValue(annotation);
        Class<?> classT = JsonElementUtils.getGenericType(this);
        L.e("data:"+data.getClass()+"\tT:"+classT);
        if (!Number.class.isAssignableFrom(data.getClass())){
            throw new JsonElement.JsonNotInvalidException("Max Comparable Generic Type error of \"" + field.getName() + "\" ! need: " + Number.class +" get data: "+data.getClass());
        }else{
            try {
                T value = convertDataToTarget((Number)data);
                if (value == null){
                    throw new JsonElement.JsonNotInvalidException("Max Comparable Generic Type error of \"" + field.getName() + "\" ! " + data.getClass() +" cannot cast to "+classT);
                }
                if (max.compareTo(value) < 0) {
                    JsonElement.JsonNotInvalidException ex = new JsonElement.JsonNotInvalidException();
                    ex.setTooHighMessage(field.getName(), classT.getName(), data, max);
                    throw ex;
                }
            } catch (Exception e) {
//            e.printStackTrace();
                throw new JsonElement.JsonNotInvalidException("Max Comparable Generic Type error of \"" + field.getName() + "\" ! " + data.getClass() +" cannot cast to "+classT);
            }
        }
    }

    /**
     * convert json data type to comparable target type
     * @param data
     * @return
     * @throws JsonElement.JsonNotInvalidException
     */
    protected abstract T convertDataToTarget(Number data) throws JsonElement.JsonNotInvalidException;

    /**
     * Get the min value from annotation
     * @param annotation
     * @return
     */
    protected abstract Comparable<T> getMinValue(JsonElement annotation);

    /**
     * Get the max value from annotation
     * @param annotation
     * @return
     */
    protected abstract Comparable<T> getMaxValue(JsonElement annotation);

    @Override
    public void filter(JsonElement annotation, Field field, Object data, Object target) throws JsonElement.JsonNotInvalidException {

        super.filter(annotation, field, data, target);
        filterMinValue(annotation, field, data, target);
        filterMaxValue(annotation, field, data, target);
    }
}
