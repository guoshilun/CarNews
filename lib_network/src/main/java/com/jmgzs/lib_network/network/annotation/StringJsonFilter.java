package com.jmgzs.lib_network.network.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * String type filter
 * Created by WuXianLiang on 2016/9/14.
 */
public class StringJsonFilter extends BaseCompositeFilter {

    public StringJsonFilter() {
        IJsonFilter notNullFilter = new NotNullJsonFilter();
        IJsonFilter defaultValueJsonFilter = new DefaultValueJsonFilter<String>(notNullFilter) {

            @Override
            protected String getDefaultValue(JsonElement annotation) {
                return annotation.strDefaultValue();
            }
        };
        mParent = new IncludeRangeJsonFilter<String>(defaultValueJsonFilter) {

            @Override
            protected String convertDataToTarget(Object data) throws JsonElement.JsonNotInvalidException {
                if (data instanceof String) {
                    return (String) data;
                }
                return null;
            }

            @Override
            protected List<String> getRange(JsonElement annotation) {
                if (annotation.strIncludeRange().length < 1) {
                    return null;
                }
                List<String> data = new ArrayList<>();
                try {
                    data.addAll(Arrays.asList(annotation.strIncludeRange()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return data;
            }
        };
    }
}
