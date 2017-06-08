package com.jmgzs.carnews.network.annotation;

/**boolean type filter
 * Created by WuXianLiang on 2016/9/8.
 */
public class BooleanJsonFilter extends BaseCompositeFilter {

    public BooleanJsonFilter() {
        IJsonFilter notNullFilter = new NotNullJsonFilter();
        mParent = new DefaultValueJsonFilter<Boolean>(notNullFilter) {

            @Override
            protected Boolean getDefaultValue(JsonElement annotation) {
                return annotation.booleanDefaultValue();
            }
        };
    }
}
