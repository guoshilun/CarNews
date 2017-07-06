package com.jmgzs.carnews.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.jmgzs.lib_network.utils.L;

/**可监听滚动的WebView
 * Created by Wxl on 2017/6/13.
 */

public class ScrollableWebView extends WebView {

    private IWebViewScrollListener scrollListener;

    public ScrollableWebView(Context context) {
        super(context);
    }

    public ScrollableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollableWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScrollableWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ScrollableWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollListener != null){
            scrollListener.onScrollChanged(l, t, l - oldl, t - oldt);
        }
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
//        L.e("OverScroll x :"+scrollX+"\ty:"+scrollY+"\tclampedX:"+clampedX+"\tclampedY:"+clampedY);
        if (scrollListener != null){
            scrollListener.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        }
    }

    @Override
    public void flingScroll(int vx, int vy) {
        super.flingScroll(vx, vy);
    }

    public void setScrollListener(IWebViewScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    public interface IWebViewScrollListener{
        void onScrollChanged(int x, int y, int xDelta, int yDelta);

        void onOverScrolled(int x, int y, boolean isOverX, boolean isOverY);

    }

}

