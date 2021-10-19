package com.yctc.zhiting.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

public class AnimationUtil {

    /**
     * 旋转动画
     * @param view
     * @param duration
     * @param startImg
     * @param endImg
     */
    public static void rotationAnim(final ImageView view, long duration, @DrawableRes int startImg, @DrawableRes int endImg){
        // 第二个参数"rotation"表明要执行旋转
        // 0f -> 360f，从旋转360度，也可以是负值，负值即为逆时针旋转，正值是顺时针旋转。
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "rotation", 360f, 0f);

        // 动画的持续时间，执行多久？
        anim.setDuration(duration);

        // 回调监听
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setImageResource(startImg);
                view.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setImageResource(endImg);
                view.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        // 正式开始启动执行动画
        anim.start();
    }

}
