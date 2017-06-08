package com.jmgzs.carnews.network.annotation;

import java.util.ArrayList;
import java.util.List;

/**long type filter
 * Created by WuXianLiang on 2016/9/8.
 */
public class LongJsonFilter extends BaseCompositeFilter{

    public LongJsonFilter() {
        IJsonFilter notNullFilter = new NotNullJsonFilter();
        IJsonFilter defaultValueJsonFilter = new DefaultValueJsonFilter<Long>(notNullFilter) {

            @Override
            protected Long getDefaultValue(JsonElement annotation) {
                return annotation.longDefaultValue();
            }
        };
        IJsonFilter includeRangeFilter = new IncludeRangeJsonFilter<Long>(defaultValueJsonFilter) {

            @Override
            protected Long convertDataToTarget(Object data) throws JsonElement.JsonNotInvalidException {
                if (data instanceof Integer){
                    return (long)(int)data;
                }else if (data instanceof Long){
                    return (long)data;
                }else if (data instanceof Double){
                    return (long)(double)data;
                }else if (data instanceof Float){
                    return (long)(float)data;
                }
                return null;
            }

            @Override
            protected List<Long> getRange(JsonElement annotation) {
                if (annotation.longIncludeRange().length < 1){
                    return null;
                }
                List<Long> data = new ArrayList<>();
                for (long item : annotation.longIncludeRange()){
                    data.add(item);
                }
                return data;
            }
        };
        mParent = new BaseComparableJsonFilter<Long>(includeRangeFilter) {
            @Override
            protected Long convertDataToTarget(Number data) throws JsonElement.JsonNotInvalidException {
                if (data instanceof Integer){
                    return (long)(int)data;
                }else if (data instanceof Long){
                    return (long)data;
                }else if (data instanceof Double){
                    return (long)(double)data;
                }else if (data instanceof Float){
                    return (long)(float)data;
                }
                return null;
            }
            @Override
            protected Comparable<Long> getMinValue(JsonElement annotation) {
                return annotation.longMinValue();
            }

            @Override
            protected Comparable<Long> getMaxValue(JsonElement annotation) {
                return annotation.longMaxValue();
            }
        };
    }

}
