package com.jmgzs.carnews.ui.view;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;

import com.jmgzs.carnews.R;
import com.jmgzs.carnews.base.App;

/**
 * Created by Wxl on 2017/6/23.
 */

public class ScaleClickUtils {

    public static void click(View v, final View.OnClickListener listener){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.clearAnimation();
                Animation clickAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.anim_scale_click);
                v.setAnimation(clickAnim);
                clickAnim.startNow();
                if (listener != null){
                    listener.onClick(v);
                }
            }
        });
    }

    public static void check(CompoundButton v, final CompoundButton.OnCheckedChangeListener listener){
        v.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.clearAnimation();
                Animation clickAnim = AnimationUtils.loadAnimation(buttonView.getContext(), R.anim.anim_scale_click);
                buttonView.setAnimation(clickAnim);
                clickAnim.startNow();
                if (listener != null){
                    listener.onCheckedChanged(buttonView, isChecked);
                }
            }
        });
    }

    public static void startScaleAnim(View v){
        v.clearAnimation();
        Animation clickAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.anim_scale_click);
        v.setAnimation(clickAnim);
        clickAnim.startNow();
    }

    public static void startScaleSmallAnim(final View v, final Runnable runnable){
        v.clearAnimation();
        Animation smallAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.anim_scale_small);
        smallAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (runnable != null){
                    runnable.run();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.setAnimation(smallAnim);
        smallAnim.startNow();
    }

    public static void startScaleBigAnim(final View v, final Runnable runnable){
        v.clearAnimation();
        Animation bigAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.anim_scale_big);
        bigAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (runnable != null) {
                    runnable.run();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.setAnimation(bigAnim);
        bigAnim.startNow();
    }
}
