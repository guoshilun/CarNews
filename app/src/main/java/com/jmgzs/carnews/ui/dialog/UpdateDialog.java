package com.jmgzs.carnews.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.bean.UpdateInfo;
import com.jmgzs.carnews.util.T;

import io.reactivex.annotations.NonNull;


/**
 * Created by mac on 17/6/27.
 * Description:
 */

public class UpdateDialog extends BaseDialog {

    private Button updateBtn;
    private Button cancelBtn;
    private TextView updateMsgTv;

    public UpdateDialog(Context ct) {
        super(ct);
    }

    @Override
    protected void create(Bundle b) {
        setContentView(R.layout.dialog_update);
        setCanceledOnTouchOutside(false);
        mWidthScale = 0.75f;

    }

    @Override
    protected void initView() {
        updateMsgTv = getView(R.id.update_content);
        updateBtn = getView(R.id.update_id_ok);
        cancelBtn = getView(R.id.update_id_cancel);

        updateBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update_id_ok:
                if (mOnDialogClickListener != null) {
                    mOnDialogClickListener.onClick(this, 0);
                    dismiss();
                }
                break;
            case R.id.update_id_cancel:
                if (!force)
                    dismiss();
                else T.toastS("本次有重大更新,需升级后才能继续使用。");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
            return true;
        else
            return super.dispatchKeyEvent(event);
    }

    private boolean force = false;

    public void setData(String msg, boolean force) {
        updateMsgTv.setText(msg);
        this.force = force;
    }
}
