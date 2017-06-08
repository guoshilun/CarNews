package com.jmgzs.carnews.network.annotation;

import java.lang.reflect.Field;

/**json filter check not null
 * Created by WuXianLiang on 2016/9/8.
 */
public class NotNullJsonFilter extends BaseCompositeFilter{

    public NotNullJsonFilter() {
    }

    public NotNullJsonFilter(IJsonFilter parent) {
        super(parent);
    }

    @Override
    public void filter(JsonElement annotation, Field field, Object data, Object target) throws JsonElement.JsonNotInvalidException {
        super.filter(annotation, field, data, target);
        if (annotation.isNotNull()) {
            if (data == null) {
                throw new JsonElement.JsonNotInvalidException(field.getName() + " cannot be null!");
            }
        }
    }
}
