package com.jmgzs.carnews.network.annotation;

import java.util.ArrayList;
import java.util.List;

/**int type filter
 * Created by WuXianLiang on 2016/9/8.
 */
public class IntJsonFilter extends BaseCompositeFilter{

    public IntJsonFilter() {
        IJsonFilter notNullFilter = new NotNullJsonFilter();
        IJsonFilter defaultValueJsonFilter = new DefaultValueJsonFilter<Integer>(notNullFilter) {

            @Override
            protected Integer getDefaultValue(JsonElement annotation) {
                return annotation.intDefaultValue();
            }
        };
        IJsonFilter includeRangeFilter = new IncludeRangeJsonFilter<Integer>(defaultValueJsonFilter) {

            @Override
            protected Integer convertDataToTarget(Object data) throws JsonElement.JsonNotInvalidException {
                if (data instanceof Integer){
                    return (int)data;
                }else if (data instanceof Long){
                    return (int)(long)data;
                }else if (data instanceof Double){
                    return (int)(double)data;
                }else if (data instanceof Float){
                    return (int)(float)data;
                }
                return null;
            }

            @Override
            protected List<Integer> getRange(JsonElement annotation) {
                if (annotation.intIncludeRange().length < 1){
                    return null;
                }
                List<Integer> data = new ArrayList<>();
                for (int item : annotation.intIncludeRange()){
                    data.add(item);
                }
                return data;
            }
        };
        mParent = new BaseComparableJsonFilter<Integer>(includeRangeFilter) {
            @Override
            protected Integer convertDataToTarget(Number data) throws JsonElement.JsonNotInvalidException {
                if (data instanceof Integer){
                    return (int)data;
                }else if (data instanceof Long){
                    return (int)(long)data;
                }else if (data instanceof Double){
                    return (int)(double)data;
                }else if (data instanceof Float){
                    return (int)(float)data;
                }
                return null;
            }
            @Override
            protected Comparable<Integer> getMinValue(JsonElement annotation) {
                return annotation.intMinValue();
            }

            @Override
            protected Comparable<Integer> getMaxValue(JsonElement annotation) {
                return annotation.intMaxValue();
            }
        };
    }
}
