package com.jmgzs.carnews.ui.tab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.jmgzs.carnews.R;
import com.jmgzs.lib_network.utils.L;

public class TabItem extends View {

    private int mTextSize = 16;
    private int mTextColorSelect = 0x696969;
    private int mTextColorNormal = 0x0159a1;
    private Paint mTextPaintNormal;
    private Paint mTextPaintSelect;
    private int mViewHeight, mViewWidth;
    private String mTextValue;
    private Bitmap mIconNormal;
    private Bitmap mIconSelect;
    private Rect mBoundText;
    private Paint mIconPaintSelect;
    private Paint mIconPaintNormal;
    private Paint mDotPaint;
    private int mDotColor = 0xFFFF7359;
    private float mDotRadius = 10;
    private boolean mDrawDot = false;
    private int mDotCount = 0;

    public TabItem(Context context) {
        this(context, null);
    }

    public TabItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        initView();
    }

    private void initView() {
        mBoundText = new Rect();
    }

    private void measureText() {
        if (!TextUtils.isEmpty(mTextValue)) {
            getTextPaintNormal().getTextBounds(mTextValue, 0, mTextValue.length(), mBoundText);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0, height = 0;

        measureText();
        int contentWidth = Math.max(mBoundText.width(), mIconNormal == null ? 0 : mIconNormal.getWidth());
        int desiredWidth = getPaddingLeft() + getPaddingRight() + contentWidth;
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                width = Math.min(widthSize, desiredWidth);
                break;
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                width = desiredWidth;
                break;
        }
        int contentHeight = mBoundText.height() + (mIconNormal == null ? 0 : mIconNormal.getHeight());
        int desiredHeight = getPaddingTop() + getPaddingBottom() + contentHeight;
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                height = Math.min(heightSize, desiredHeight);
                break;
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                height = contentHeight;
                break;
        }
        setMeasuredDimension(width, height);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBitmap(canvas);
        drawText(canvas);
        drawDot(canvas);
    }

    private void drawBitmap(Canvas canvas) {
        int w = mIconNormal == null ? 0 : mIconNormal.getWidth();
        int h = mIconNormal == null ? 0 : mIconNormal.getHeight();
        int left = (mViewWidth - w) / 2;
        int top = (mViewHeight - h - mBoundText.height()) / 2;

        if (mIconNormal != null) {
            canvas.drawBitmap(mIconNormal, left, top, getIconPaintNormal());
        }

        if (mIconSelect != null) {
            canvas.drawBitmap(mIconSelect, left, top, getIconPaintSelect());
        }
    }

    private void drawText(Canvas canvas) {
        if (!TextUtils.isEmpty(mTextValue)) {
            float x = (mViewWidth - mBoundText.width()) / 2.0f;
            float y = (mViewHeight + (mIconNormal == null ? 0 : mIconNormal.getHeight()) + mBoundText.height()) / 2.0F;
            if (isSelected()) {
                getTextPaintSelect().setAlpha(0xff);
                getTextPaintNormal().setAlpha(0);
            canvas.drawText(mTextValue, x, y, getTextPaintSelect());
            } else {
                getTextPaintSelect().setAlpha(0);
                getTextPaintNormal().setAlpha(0xff);
            canvas.drawText(mTextValue, x, y, getTextPaintNormal());
            }
            L.i(isSelected() + ",normal:" + getTextPaintNormal().getColor() + "-alpha---" + getTextPaintNormal().getAlpha() + ",select:" + getTextPaintSelect().getAlpha() + "," + getTextPaintSelect().getColor());
        }
    }

    private void colorListDrawable() {
        ColorStateList colorStateList = getResources().getColorStateList(R.color.title_text_selector);


    }

    private void drawDot(Canvas canvas) {
        if (mDrawDot && mDotCount > 0) {
            float x = mDotRadius * 1.5f;
            int y = mViewHeight / 2;
            canvas.drawCircle(x, y, mDotRadius, getDotPaint());
        }
    }

    public void setDot(int dotColor, float dotRadius) {
        this.mDotColor = dotColor;
        this.mDotRadius = dotRadius;
        this.mDrawDot = true;
        getDotPaint().setColor(dotColor);
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
        getTextPaintNormal().setTextSize(textSize);
        getTextPaintSelect().setTextSize(textSize);
    }

    public void setTextColorSelect(int mTextColorSelect) {
        this.mTextColorSelect = mTextColorSelect;
        getTextPaintSelect().setColor(mTextColorSelect);
        getTextPaintSelect().setAlpha(0);
    }

    public void setTextColorNormal(int mTextColorNormal) {
        this.mTextColorNormal = mTextColorNormal;
        getTextPaintNormal().setColor(mTextColorNormal);
        getTextPaintNormal().setAlpha(0xff);
    }

    public void setTextValue(String TextValue) {
        this.mTextValue = TextValue;
    }

    public void setIconText(int iconResIdForNormal, int iconResIdForSelect, String TextValue) {
        try {
            this.mIconSelect = BitmapFactory.decodeResource(getResources(), iconResIdForSelect);
        } catch (Exception e) {
            // ignore
        }

        try {
            this.mIconNormal = BitmapFactory.decodeResource(getResources(), iconResIdForNormal);
        } catch (Exception e) {
            // ignore
        }

        this.mTextValue = TextValue;
        invalidate();
    }

    public void setTabAlpha(float alpha) {
        int paintAlpha = (int) (alpha * 255);
        getIconPaintSelect().setAlpha(paintAlpha);
        getIconPaintNormal().setAlpha(255 - paintAlpha);
        getTextPaintSelect().setAlpha(paintAlpha);
        getTextPaintNormal().setAlpha(255 - paintAlpha);
        getDotPaint().setAlpha(255 - paintAlpha);
        invalidate();
    }

    private Paint getIconPaintSelect() {
        if (mIconPaintSelect == null) {
            mIconPaintSelect = new Paint(Paint.ANTI_ALIAS_FLAG);
            mIconPaintSelect.setAlpha(0);
        }

        return mIconPaintSelect;
    }

    private Paint getIconPaintNormal() {
        if (mIconPaintNormal == null) {
            mIconPaintNormal = new Paint(Paint.ANTI_ALIAS_FLAG);
            mIconPaintNormal.setAlpha(0xff);
        }

        return mIconPaintNormal;
    }

    private Paint getTextPaintSelect() {
        if (mTextPaintSelect == null) {
            mTextPaintSelect = new Paint();
            mTextPaintSelect.setAntiAlias(true);
            mTextPaintSelect.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, getResources().getDisplayMetrics()));
            mTextPaintSelect.setColor(mTextColorSelect);
            mTextPaintSelect.setAntiAlias(true);
            mTextPaintSelect.setAlpha(0);
        }
        return mTextPaintSelect;
    }

    private Paint getTextPaintNormal() {
        if (mTextPaintNormal == null) {
            mTextPaintNormal = new Paint();
            mTextPaintNormal.setAntiAlias(true);
            mTextPaintNormal.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, getResources().getDisplayMetrics()));
            mTextPaintNormal.setColor(mTextColorNormal);
            mTextPaintNormal.setAntiAlias(true);
            mTextPaintNormal.setAlpha(0xff);
        }

        return mTextPaintNormal;
    }

    private Paint getDotPaint() {
        if (mDotPaint == null) {
            mDotPaint = new Paint();
            mDotPaint.setAntiAlias(true);
            mDotPaint.setColor(mDotColor);
            mDotPaint.setAlpha(0xFF);
        }

        return mDotPaint;
    }

    public void setDotCount(int count) {
        this.mDotCount = count;
        invalidate();
    }

}
