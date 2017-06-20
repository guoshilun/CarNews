package com.jmgzs.lib_network.network.annotation;

import java.util.ArrayList;
import java.util.List;

/**float type filter
 * Created by WuXianLiang on 2016/9/8.
 */
public class FloatJsonFilter extends BaseCompositeFilter{

    public FloatJsonFilter() {
        IJsonFilter notNullFilter = new NotNullJsonFilter();
        IJsonFilter defaultValueJsonFilter = new DefaultValueJsonFilter<Float>(notNullFilter) {

            @Override
            protected Float getDefaultValue(JsonElement annotation) {
                return annotation.floatDefaultValue();
            }
        };
        IJsonFilter includeRangeFilter = new IncludeRangeJsonFilter<Float>(defaultValueJsonFilter) {

            @Override
            protected Float convertDataToTarget(Object data) throws JsonElement.JsonNotInvalidException {
                if (data instanceof Integer){
                    return (float)(int)data;
                }else if (data instanceof Long){
                    return (float)(long)data;
                }else if (data instanceof Double){
                    return (float)(double)data;
                }else if (data instanceof Float){
                    return (float)data;
                }
                return null;
            }

            @Override
            protected List<Float> getRange(JsonElement annotation) {
                if (annotation.floatIncludeRange().length < 1){
                    return null;
                }
                List<Float> data = new ArrayList<>();
                for (float item : annotation.floatIncludeRange()){
                    data.add(item);
                }
                return data;
            }
        };
        mParent = new BaseComparableJsonFilter<Float>(includeRangeFilter) {
            @Override
            protected Float convertDataToTarget(Number data) throws JsonElement.JsonNotInvalidException {
                if (data instanceof Integer){
                    return (float)(int)data;
                }else if (data instanceof Long){
                    return (float)(long)data;
                }else if (data instanceof Double){
                    return (float)(double)data;
                }else if (data instanceof Float){
                    return (float)data;
                }
                return null;
            }
            @Override
            protected Comparable<Float> getMinValue(JsonElement annotation) {
                return annotation.floatMinValue();
            }

            @Override
            protected Comparable<Float> getMaxValue(JsonElement annotation) {
                return annotation.floatMaxValue();
            }
        };
    }
}
