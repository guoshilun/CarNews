package com.jmgzs.carnews.ui.view;

import android.webkit.WebView;

import com.jmgzs.lib_network.utils.L;

/**顶部条手势滚动控制器
 * Created by Wxl on 2017/6/13.
 */

public class TitleBarScrollController implements ScrollableWebView.IWebViewScrollListener, ScrollControlFrameLayout.IScrollEndListener{

    private transient boolean isToTop = true, isToBottom;
    private transient WebView wv;

    public TitleBarScrollController(WebView wv){
        this.wv = wv;
    }

    @Override
    public void onScrollChanged(int x, int y, int xDelta, int yDelta) {
//        L.e("滚动的x:"+x+"\t y:"+y+"\txDelta:"+xDelta+"\tyDelta:"+yDelta+"scrollX:"+wv.getScrollX()+"scrollY:"+wv.getScrollY());
        if (y <= 0){
            isToTop = true;
        }else{
            isToTop = false;
        }
    }

    @Override
    public void onOverScrolled(int x, int y, boolean isOverX, boolean isOverY) {
//        L.e("OverScroll的x:"+x+"\t y:"+y+"\tisOverX:"+isOverX+"\tisOverY:"+isOverY);
        if (y <= 0){
            isToTop = true;
        }else{
            isToTop = false;
        }
    }

    @Override
    public boolean isScrollVerticalEnd(boolean isTop) {
        if (isTop){
//            L.e("是否到顶:"+isToTop);
            return isToTop;
        }else{
            return isToBottom;
        }
    }

    @Override
    public void reset() {
        isToTop = true;
        isToBottom = false;
    }
}
