package com.jmgzs.lib_network.network;


import java.util.Map;

/**
 * Created by Wxl on 2017/6/8.
 */

public interface IRxRequest {

    String getUrl();

    String getBody();

    Map<String, String> getHeaders();
}
