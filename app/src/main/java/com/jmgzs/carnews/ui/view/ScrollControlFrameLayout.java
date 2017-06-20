package com.jmgzs.carnews.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.jmgzs.lib_network.utils.L;

/**
 * 可以控制child滚动帧布局
 * Created by Wxl on 2017/6/13.
 */

public class ScrollControlFrameLayout extends FrameLayout {

    private IScrollEndListener scrollEndListener;
    private float mStartX, mStartY, mX, mY;
    private View alphaView;
    private View scrollView;
    private int alphaViewHeight;
    private boolean isIntercepted = false;
    private float interceptedStartY;

    public ScrollControlFrameLayout(@NonNull Context context) {
        super(context);
    }

    public ScrollControlFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollControlFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollEndListener(IScrollEndListener scrollEndListener) {
        this.scrollEndListener = scrollEndListener;
    }

    public void setAlphaView(View alphaView) {
        this.alphaView = alphaView;
        alphaViewHeight = alphaView.getMeasuredHeight();
        L.e("alphaView的高度：" + alphaViewHeight);
    }

    public void setScrollView(View scrollView) {
        this.scrollView = scrollView;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScrollControlFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            mStartX = event.getX();
            mStartY = event.getY();
            mX = mStartX;
            mY = mStartY;
        } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            requestDisallowInterceptTouchEvent(true);
            float yDelta = event.getY() - mY;
            L.e("x:" + event.getX() + "\ty:" + event.getY());

            if (yDelta < 0) {//上滑手势↑，页面向下滚动
                L.e("开始上滑");
                if (scrollView != null) {
                    LayoutParams lps = (LayoutParams) scrollView.getLayoutParams();
                    if (lps.topMargin <= 0) {
                        L.e("开始滚动");
                        if (isIntercepted) {
                            isIntercepted = false;
                        }
                    } else {
                        isIntercepted = true;
                        interceptedStartY += yDelta;
                        int oldMarginTop = (int) (yDelta / 2f) + lps.topMargin;
                        if (oldMarginTop < 0) {
                            oldMarginTop = 0;
                        }
                        L.e("不滚动");
                        LayoutParams newLps = new LayoutParams(lps.width, lps.height);
                        newLps.setMargins(lps.leftMargin, oldMarginTop, lps.rightMargin, lps.bottomMargin);
                        scrollView.setLayoutParams(newLps);
                        this.invalidate();
                        L.e("scrollview margin:" + oldMarginTop);
                    }
                }
                //透明度
                if (alphaView != null && alphaView.getAlpha() != 0) {
                    alphaView.setAlpha(yDelta / 100 + alphaView.getAlpha());
                    if (alphaView.getAlpha() <= 0) {
                        alphaView.setAlpha(0);
                        alphaView.setVisibility(View.GONE);
                    }
                    //位移
                    if (alphaView.getPaddingTop() > -alphaViewHeight) {
                        int oldPaddingTop = (int) (yDelta / 4) + alphaView.getPaddingTop();
                        if (oldPaddingTop < -alphaViewHeight) {
                            oldPaddingTop = -alphaViewHeight;
                        }
                        L.e("减少alphaView的padding" + oldPaddingTop);
                        alphaView.setPadding(alphaView.getPaddingLeft(), oldPaddingTop, alphaView.getPaddingRight(), alphaView.getPaddingBottom());
                    }
                }
            } else {//下滑手势↓，页面向上滚动
                L.e("开始下滑");
                //透明度
                if (alphaView != null) {
                    if (alphaView.getVisibility() != View.VISIBLE) {
                        alphaView.setVisibility(View.VISIBLE);
                    } else {
                        L.e("当前alphaView可见");
                    }
                    if (alphaView.getAlpha() < 1) {
                        alphaView.setAlpha(alphaView.getAlpha() + yDelta / 100);
                        if (alphaView.getAlpha() > 1) {
                            alphaView.setAlpha(1);
                        }
                    } else {
                        alphaView.setAlpha(1);
                    }
                    L.e("alphaView的alpha:" + alphaView.getAlpha());
                    //位移
                    if (alphaView.getPaddingTop() < 0) {
                        int oldPaddingTop = (int) (yDelta / 4 + alphaView.getPaddingTop());
                        if (oldPaddingTop > 0) {
                            oldPaddingTop = 0;
                        }
                        L.e("增加alphaView的padding:" + oldPaddingTop + " Visible:" + alphaView.getVisibility());
                        alphaView.setPadding(alphaView.getPaddingLeft(), oldPaddingTop, alphaView.getPaddingRight(), alphaView.getPaddingBottom());
                    }
                }
            }
            if (scrollEndListener != null) {
                if (scrollEndListener.isScrollVerticalEnd(true)) {//scrollview到顶
                    L.e("最后下滑");
                    if (scrollView != null) {
                        if (yDelta > 0) {
                            LayoutParams lps = (LayoutParams) scrollView.getLayoutParams();
                            int oldMarginTop = (int) (yDelta / 2f) + lps.topMargin;
                            if (oldMarginTop >= alphaViewHeight) {//顶部条也已经完全显示
                                oldMarginTop = alphaViewHeight;
                            }
                            LayoutParams newLps = new LayoutParams(lps.width, lps.height);
                            newLps.setMargins(lps.leftMargin, oldMarginTop, lps.rightMargin, lps.bottomMargin);
                            scrollView.setLayoutParams(newLps);
                            invalidate();
                            L.e("滚动View的margin:" + oldMarginTop);
                            if (interceptedStartY > 0) {
                                interceptedStartY -= yDelta;
                                if (interceptedStartY < 0) {
                                    interceptedStartY = 0;
                                }
                            }
                        }

                    }
                } else if (scrollEndListener.isScrollVerticalEnd(false)) {//scrollview到底
                }
            }
            this.invalidate();
            mY = event.getY();
        } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            mStartX = 0;
            mStartY = 0;
            mX = 0;
            mY = 0;
            interceptedStartY = 0;
        } else {
        }
        event.offsetLocation(0, -interceptedStartY);
        return super.dispatchTouchEvent(event);
    }

    public interface IScrollEndListener {
        boolean isScrollVerticalEnd(boolean isTop);
    }
}
