package com.jmgzs.carnews.network;

/**
 * Created by Wxl on 2017/6/8.
 */

public class Urls {

//    public static final String BASE_URL = "http://10.16.80.50:9321/";//Test Url
    public static final String BASE_URL= "http://wnews.mjmobi.com/";
    public static final String URL_NEWS = BASE_URL + "news_info/api/news_pinterest/?type_key=1&start_key=%1$s&channel_id=%2$s";
//    public static final String URL_NEWS = BASE_URL + "news_info/api/news_pinterest/?search_key=%1$s";
    public static final String URL_INFO = BASE_URL + "news_info/api/get_article_detail/?aid=";


    public static String getUrlNews(String searchKey, String channelId) {
//        return String.format(URL_NEWS, searchKey);
        return String.format(URL_NEWS, searchKey, channelId);
    }

    public static String getUrlInfo(int aid) {
        return URL_INFO + aid;
    }

}
