package com.jmgzs.lib_network.network.annotation;

import java.util.ArrayList;
import java.util.List;

/**short type filter
 * Created by WuXianLiang on 2016/9/8.
 */
public class ShortJsonFilter extends BaseCompositeFilter{

    public ShortJsonFilter() {
        IJsonFilter notNullFilter = new NotNullJsonFilter();
        IJsonFilter defaultValueJsonFilter = new DefaultValueJsonFilter<Short>(notNullFilter) {

            @Override
            protected Short getDefaultValue(JsonElement annotation) {
                return annotation.shortDefaultValue();
            }
        };
        IJsonFilter includeRangeFilter = new IncludeRangeJsonFilter<Short>(defaultValueJsonFilter) {

            @Override
            protected Short convertDataToTarget(Object data) throws JsonElement.JsonNotInvalidException {
                if (data instanceof Integer){
                    return (short)(int)data;
                }else if (data instanceof Long){
                    return (short)(long)data;
                }else if (data instanceof Double){
                    return (short)(double)data;
                }else if (data instanceof Float){
                    return (short)(float)data;
                }
                return null;
            }

            @Override
            protected List<Short> getRange(JsonElement annotation) {
                if (annotation.shortIncludeRange().length < 1){
                    return null;
                }
                List<Short> data = new ArrayList<>();
                for (short item : annotation.shortIncludeRange()){
                    data.add(item);
                }
                return data;
            }
        };
        mParent = new BaseComparableJsonFilter<Short>(includeRangeFilter) {
            @Override
            protected Short convertDataToTarget(Number data) throws JsonElement.JsonNotInvalidException {
                if (data instanceof Integer){
                    return (short)(int)data;
                }else if (data instanceof Long){
                    return (short)(long)data;
                }else if (data instanceof Double){
                    return (short)(double)data;
                }else if (data instanceof Float){
                    return (short)(float)data;
                }
                return null;
            }
            @Override
            protected Comparable<Short> getMinValue(JsonElement annotation) {
                return annotation.shortMinValue();
            }

            @Override
            protected Comparable<Short> getMaxValue(JsonElement annotation) {
                return annotation.shortMaxValue();
            }
        };
    }
}
