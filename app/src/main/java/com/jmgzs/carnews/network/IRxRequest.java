package com.jmgzs.carnews.network;


import java.util.Map;

/**
 * Created by Wxl on 2017/6/8.
 */

public interface IRxRequest {

    String getUrl();

    String getBody();

    Map<String, String> getHeaders();
}
