package com.jmgzs.carnews.ui.view;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;

import com.jmgzs.carnews.R;

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
        AnimationSet set = new AnimationSet(false);
        Animation smallAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.anim_scale_small);
        Animation alphaAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.anim_alpha_dismiss);
        set.addAnimation(smallAnim);
        set.addAnimation(alphaAnim);
        set.setFillBefore(true);
        set.setAnimationListener(new Animation.AnimationListener() {
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
        v.setAnimation(set);
        set.startNow();
    }

    public static void startScaleBigAnim(final View v, final Runnable runnable){
        v.clearAnimation();
        AnimationSet set = new AnimationSet(false);
        Animation bigAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.anim_scale_big);
        Animation alphaAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.anim_alpha_show_before);
        set.addAnimation(bigAnim);
        set.addAnimation(alphaAnim);
        set.setFillBefore(true);
        set.setAnimationListener(new Animation.AnimationListener() {
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
        v.setAnimation(set);
        set.startNow();
    }
}
