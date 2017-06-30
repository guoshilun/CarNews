package com.jmgzs.carnews.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.ui.WebViewActivity;
import com.jmgzs.lib_network.utils.L;

/**插屏广告对话框
 * Created by Wxl on 2017/6/28.
 */

public class AdvDialog extends BaseDialog {

    private IOnAdvLoadListener mListener;
    private WebView mWv;
    private Context context;

    public AdvDialog(Context context) {
        super(context);
        this.context = context;
    }

    public AdvDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public void setListener(IOnAdvLoadListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void create(Bundle b) {
        setContentView(R.layout.dialog_insert_adv);
        setCanceledOnTouchOutside(false);
        mWidthScale = 0.75f;
    }

    @Override
    protected void initView() {
        mWv = getView(R.id.dialogInsertAdv_wv);
        mWv.getSettings().setJavaScriptEnabled(true);
        mWv.getSettings().setUseWideViewPort(false);
        mWv.getSettings().setLoadsImagesAutomatically(true);
        mWv.setWebChromeClient(new WebChromeClient() {

        });
        mWv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                L.e("url:" + url);
                if (url.startsWith("file")) {
                    return false;
                }
                if (url.startsWith("http")){
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra(WebViewActivity.INTENT_URL, url);
                    context.startActivity(intent);
                }
                return true;
            }
        });
    }

    public void setWidthHeight(int width, int height){
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = width;
        lp.height = height;
    }


    @Override
    public void show() {
        super.show();
        if (mListener != null){
            mListener.onAdvLoad(mWv);
        }
    }

    public interface IOnAdvLoadListener{
        void onAdvLoad(WebView wv);
    }
}
