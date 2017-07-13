package com.jmgzs.carnews.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.BaseActivity;
import com.jmgzs.carnews.util.UmengUtil;

/**
 * Created by mac on 17/6/20.
 * Description:
 */

public class MianZeActivity extends BaseActivity {
    @Override
    protected int getContent(Bundle save) {
        return R.layout.activity_mianze;
    }

    @Override
    protected void initView() {

        getView(R.id.titleInfo_img_back).setOnClickListener(this);
        TextView title = getView(R.id.titleInfo_tv_title);
        title.setText(R.string.setting_user);
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    @Override
    protected String getUmengKey() {
        return UmengUtil.U_SETTING;
    }

}
