package com.jmgzs.carnews.network.annotation;

import java.util.ArrayList;
import java.util.List;

/**byte type filter
 * Created by WuXianLiang on 2016/9/8.
 */
public class ByteJsonFilter extends BaseCompositeFilter {

    public ByteJsonFilter() {
        IJsonFilter notNullFilter = new NotNullJsonFilter();
        IJsonFilter defaultValueJsonFilter = new DefaultValueJsonFilter<Byte>(notNullFilter) {

            @Override
            protected Byte getDefaultValue(JsonElement annotation) {
                return annotation.byteDefaultValue();
            }
        };
        IJsonFilter includeRangeFilter = new IncludeRangeJsonFilter<Byte>(defaultValueJsonFilter) {

            @Override
            protected Byte convertDataToTarget(Object data) throws JsonElement.JsonNotInvalidException {
                if (data instanceof Integer){
                    return (byte)(int)data;
                }
                return null;
            }

            @Override
            protected List<Byte> getRange(JsonElement annotation) {
                if (annotation.byteIncludeRange().length < 1){
                    return null;
                }
                List<Byte> data = new ArrayList<>();
                for (byte item : annotation.byteIncludeRange()){
                   data.add(item);
                }
                return data;
            }
        };
        mParent = new BaseComparableJsonFilter<Byte>(includeRangeFilter) {

            @Override
            protected Byte convertDataToTarget(Number data) throws JsonElement.JsonNotInvalidException {
                if (data instanceof Integer){
                    return (byte)(int)data;
                }
                return null;
            }

            @Override
            protected Comparable<Byte> getMinValue(JsonElement annotation) {
                return annotation.byteMinValue();
            }

            @Override
            protected Comparable<Byte> getMaxValue(JsonElement annotation) {
                return annotation.byteMaxValue();
            }
        };
    }
}
