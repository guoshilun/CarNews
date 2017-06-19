package com.jmgzs.carnews.bean;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by mac on 17/6/8.
 * Description:
 */

public class BaseInfo implements Serializable {


    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
