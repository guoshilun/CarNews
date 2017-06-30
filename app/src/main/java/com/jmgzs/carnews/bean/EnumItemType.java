package com.jmgzs.carnews.bean;


/**
 * Created by mac on 17/6/30.
 * Description:
 */

public enum  EnumItemType {

    IMAGE(0),IMAGE3(1),ADV(2);

    private int value ;
    EnumItemType(int v){
        this.value = v ;
    }

    public int getValue(){
        return value;
    }
}
