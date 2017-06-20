package com.jmgzs.lib_network.network.annotation;

import java.util.ArrayList;
import java.util.List;

/**
 * double type filter
 * Created by WuXianLiang on 2016/9/8.
 */
public class DoubleJsonFilter extends BaseCompositeFilter {

    public DoubleJsonFilter() {
        IJsonFilter notNullFilter = new NotNullJsonFilter();
        IJsonFilter defaultValueJsonFilter = new DefaultValueJsonFilter<Double>(notNullFilter) {

            @Override
            protected Double getDefaultValue(JsonElement annotation) {
                return annotation.doubleDefaultValue();
            }
        };
        IJsonFilter includeRangeFilter = new IncludeRangeJsonFilter<Double>(defaultValueJsonFilter) {

            @Override
            protected Double convertDataToTarget(Object data) throws JsonElement.JsonNotInvalidException {
                if (data instanceof Integer){
                    return (double)(int)data;
                }else if (data instanceof Long){
                    return (double)(long)data;
                }else if (data instanceof Double){
                    return (double)data;
                }else if (data instanceof Float){
                    return (double)(float)data;
                }
                return null;
            }

            @Override
            protected List<Double> getRange(JsonElement annotation) {
                if (annotation.doubleIncludeRange().length < 1){
                    return null;
                }
                List<Double> data = new ArrayList<>();
                for (double item : annotation.doubleIncludeRange()){
                    data.add(item);
                }
                return data;
            }
        };
        mParent = new BaseComparableJsonFilter<Double>(includeRangeFilter) {

            @Override
            protected Double convertDataToTarget(Number data) throws JsonElement.JsonNotInvalidException {
                if (data instanceof Integer){
                    return (double)(int)data;
                }else if (data instanceof Long){
                    return (double)(long)data;
                }else if (data instanceof Double){
                    return (double)data;
                }else if (data instanceof Float){
                    return (double)(float)data;
                }
                return null;
            }
            @Override
            protected Comparable<Double> getMinValue(JsonElement annotation) {
                return annotation.doubleMinValue();
            }

            @Override
            protected Comparable<Double> getMaxValue(JsonElement annotation) {
                return annotation.doubleMaxValue();
            }
        };
    }
}
