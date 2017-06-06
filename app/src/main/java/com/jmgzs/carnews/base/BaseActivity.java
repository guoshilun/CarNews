package com.jmgzs.carnews.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by mac on 17/6/5.
 * Description:
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContent(savedInstanceState));
        initView();
    }

    @Override
    public void onClick(View view) {

    }

    protected abstract int getContent(Bundle save);
    protected abstract void initView();


    protected <E extends View> E getView(int resID) {
        try {
            return (E) findViewById(resID);
        } catch (ClassCastException e) {
            throw e;
        }
    }

    protected <E extends View>E getView(View rootView,int resID){
        try {
            if (null != rootView){
                return (E)findViewById(resID);
            }else throw new NullPointerException();
        }catch (Exception e){
            throw e;
        }
    }
}
