package com.jmgzs.lib_network.network.annotation;

import java.util.ArrayList;
import java.util.List;

/**character type filter
 * Created by WuXianLiang on 2016/9/14.
 */
public class CharacterJsonFilter extends BaseCompositeFilter{
    public CharacterJsonFilter() {
        IJsonFilter notNullFilter = new NotNullJsonFilter();
        IJsonFilter includeRangeFilter = new IncludeRangeJsonFilter<Character>(notNullFilter) {

            @Override
            protected Character convertDataToTarget(Object data) throws JsonElement.JsonNotInvalidException {
                if (data instanceof String){
                    return ((String) data).charAt(0);
                }else if (data instanceof Character){
                    return (Character) data;
                }else if (data instanceof Integer){
                    return (char)(int) data;
                }
                return null;
            }

            @Override
            protected List<Character> getRange(JsonElement annotation) {
                if (annotation.charIncludeRange().length < 1){
                    return null;
                }
                List<Character> data = new ArrayList<>();
                for (char item : annotation.charIncludeRange()){
                    data.add(item);
                }
                return data;
            }
        };
        mParent = new DefaultValueJsonFilter<Character>(includeRangeFilter) {

            @Override
            protected Character getDefaultValue(JsonElement annotation) {
                return annotation.charDefaultValue();
            }
        };
    }
}
