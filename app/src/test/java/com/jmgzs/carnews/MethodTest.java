package com.jmgzs.carnews;

import com.jmgzs.lib_network.utils.L;
import com.jmgzs.carnews.util.TimeUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mac on 17/6/14.
 * Description:
 */

public class MethodTest {

    @Test
    public void testTime() {
        String ss = TimeUtils.getTimeFromDateString("2017-06-14 12:10:00");//今天
        L.syso(ss);
        ss = TimeUtils.getTimeFromDateString("2017-06-14 15:44:00");//今天之前
        L.syso(ss);
        ss = TimeUtils.getTimeFromDateString("2017-06-14 16:39:00");//今天之前
        L.syso(ss);
        ss = TimeUtils.getTimeFromDateString("2017-06-15 16:34:00");//超前
        L.syso(ss);
        ss = TimeUtils.getTimeFromDateString("2017-06-13 16:34:00");//昨天
        L.syso(ss);
        ss = TimeUtils.getTimeFromDateString("2017-06-12 16:42:00");//前天
        L.syso(ss);
        ss = TimeUtils.getTimeFromDateString("2017-06-10 12:10:00");//更早
        L.syso(ss);

        double v = 720/(376.875-18);
        L.syso(v +" ===");
    }

    @Test
    public void testCollections(){
        List<String> a = new ArrayList<>(null);

    }
}
