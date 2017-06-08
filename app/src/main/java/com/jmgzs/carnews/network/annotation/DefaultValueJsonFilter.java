package com.jmgzs.carnews.network.annotation;

import java.lang.reflect.Field;

/**Default value json filter to set default value which is set in annotation
 * Created by WuXianLiang on 2016/9/8.
 */
public abstract class DefaultValueJsonFilter<T> extends BaseCompositeFilter {

    public DefaultValueJsonFilter() {
    }

    public DefaultValueJsonFilter(IJsonFilter parent) {
        super(parent);
    }

    protected abstract T getDefaultValue(JsonElement annotation);

    @Override
    public void filter(JsonElement annotation, Field field, Object data, Object target) throws JsonElement.JsonNotInvalidException {
        super.filter(annotation, field, data, target);
        T value;
        if (data == null) {
            value = getDefaultValue(annotation);

            boolean defaultAccessible = field.isAccessible();
            if (!defaultAccessible) {
                field.setAccessible(true);
            }
            try {
                field.set(target, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!defaultAccessible) {
                field.setAccessible(false);
            }
        }
    }
}
