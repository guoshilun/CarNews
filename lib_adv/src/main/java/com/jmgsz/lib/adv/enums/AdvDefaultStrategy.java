package com.jmgsz.lib.adv.enums;

import com.jmgsz.lib.adv.interfaces.IAdvStrategy;

/**
 * Created by Wxl on 2017/7/17.
 */

public enum AdvDefaultStrategy {
    LOAD_CACHE_AND_REQUEST(true, IAdvStrategy.AdvStrategyBuilder.CACHE_ON_START, IAdvStrategy.AdvStrategyBuilder.REQUEST_ANYWAY, IAdvStrategy.AdvStrategyBuilder.RETURN_NOT);

    private IAdvStrategy strategy;

    AdvDefaultStrategy(boolean isUseCache, int cacheStrgy, int requestStrgy, int returnStrgy) {
        strategy = new IAdvStrategy.AdvStrategyBuilder().setUseCache(isUseCache).setCacheStrategy(cacheStrgy).setRequestStrategy(requestStrgy).setReturnStrategy(returnStrgy).build();
    }
}
