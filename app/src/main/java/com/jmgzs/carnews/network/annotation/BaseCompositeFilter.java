package com.jmgzs.carnews.network.annotation;

import java.lang.reflect.Field;

/**Filter using Decorative pattern
 * Created by WuXianLiang on 2016/9/8.
 */
public class BaseCompositeFilter implements IJsonFilter{

    protected IJsonFilter mParent;

    public BaseCompositeFilter() {
    }

    public BaseCompositeFilter(IJsonFilter parent) {
        this.mParent = parent;
    }

    @Override
    public void filter(JsonElement annotation, Field field, Object data, Object target) throws JsonElement.JsonNotInvalidException {
        if (mParent != null){
            mParent.filter(annotation, field, data, target);
        }
    }

}
