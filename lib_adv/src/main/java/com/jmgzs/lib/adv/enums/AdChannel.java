package com.jmgzs.lib.adv.enums;

/**
 * Created by mac on 17/7/13.
 * Description:
 */

public enum AdChannel {

    WIFI(1), TANX(2), BAIDU(3), GOOGLE(4), ADINALL(5),
    MAX360(6), WPS(7), TOUTIAO(8), XIAOMI(9), XUNFEI(10),
    MAX_ADX(1000), ADNETWORK(1001);


    private int value;

    private AdChannel(int v) {
        this.value = v;
    }

    public int getValue() {
        return value;
    }
}
