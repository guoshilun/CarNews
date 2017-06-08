package com.jmgzs.carnews.network.annotation;

import java.lang.reflect.Field;
import java.util.List;

/**include range filter
 * Created by WuXianLiang on 2016/9/14.
 */
abstract class IncludeRangeJsonFilter<T extends Comparable> extends BaseCompositeFilter{
    public IncludeRangeJsonFilter() {
    }

    IncludeRangeJsonFilter(IJsonFilter parent) {
        super(parent);
    }

    @Override
    public void filter(JsonElement annotation, Field field, Object data, Object target) throws JsonElement.JsonNotInvalidException {
        super.filter(annotation, field, data, target);
        if (target == null){
            return;
        }
        List<T> range = getRange(annotation);
        if (range == null){
            return;
        }
        T value = convertDataToTarget(data);
        for (T item : range){
            if (item.compareTo(value) == 0){
                return;
            }
        }
        throw new JsonElement.JsonNotInvalidException("The value "+value+" is not in range!");
    }

    protected abstract T convertDataToTarget(Object data) throws JsonElement.JsonNotInvalidException;

    protected abstract List<T> getRange(JsonElement annotation);
}
