package com.jmgzs.carnews.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mac on 17/6/14.
 * Description:
 */

public class TimeUtils {

    public static final String F = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat sdf = new SimpleDateFormat(F, Locale.CHINA);

    private TimeUtils() {

    }

    /**
     * 2分钟   刚刚
     * 1小时内 x分钟前
     * 当天    x小时前
     * 昨天    昨天xx:xx
     * 前天    前天xx:xx
     * 其他    3天前
     *
     * @return
     */
    public static String getTimeFromDateString(String dateStr) {
        if (isNull(dateStr)) return "";
        try {
            if (dateStr.length() > F.length()) dateStr = dateStr.substring(0, F.length());
            return getTimeBefore(sdf.parse(dateStr), getHHmm(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 根据日期，返回其是多久之前
     *
     * @param date
     * @return
     */
    private static String getTimeBefore(Date date, String dateStr) {
        long delta = System.currentTimeMillis() - date.getTime();

        long min = delta / (1000 * 60);
        if (min < 2) {
            return "刚刚";
        } else if (min < 60) {
            return min + "分钟前";
        }

        long hour = min / 60;
        if (hour < 24) {
            return hour + "小时前";
        }
        long day = hour / 24;
        if (day < 2) {
            return "昨天 " + dateStr;
        } else if (day < 3) {
            return "前天 " + dateStr;
        } else {
            return "3天前";
        }
//
//        long week = day / 7;
//        if (week < 5) {
//            return week + "周前";
//        }
//
//        long month = day / 30;
//        if (month < 12) {
//            return month + "月前";
//        }
//
//        long year = month / 12;
//        return year + "年前";
    }

    private static String getHHmm(String d) {
        return d.length() < 16 ? "" : d.substring(11, 16);
    }

    public static boolean isNull(Object o) {
        return o == null || o.toString().length() == 0 || o.toString().equalsIgnoreCase("NULL");
    }
}
