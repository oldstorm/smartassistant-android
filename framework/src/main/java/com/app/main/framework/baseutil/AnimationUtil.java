package com.app.main.framework.baseutil;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.DrawableRes;

public class AnimationUtil {
    public static void changeBackground(View view, @DrawableRes int resId){
        view.setBackgroundResource(resId);
        int[] location = new int[2];
        view.getLocationInWindow(location);
        Animator anim = ViewAnimationUtils.createCircularReveal(
                view,//对应的view
                location[0],// 开始缩放点x位置
                location[1],// 开始缩放点y位置
                0,//开始半径
                // 结束半径    hypot(double ,double ) 表示斜线的长度
                (float) Math.hypot(view.getWidth(), view.getHeight()));
        anim.setDuration(1000);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    public static void startViewShowHideAnim(int height,boolean show,View view) {//View是否显示的标志
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
       @SuppressLint("HandlerLeak") Handler handler = new Handler(){
           private int hideHeight = height;
           private int showHeight = 0;
           @Override
           public void handleMessage(Message msg) {
               if (show){
                    showHeight+=10;
                    if (showHeight >= height){
                        removeMessages(0);
                        return;
                    }
                    layoutParams.height = showHeight;
                    view.setLayoutParams(layoutParams);
               }else {
                   hideHeight-=10;
                   if (showHeight <= 0){
                       removeMessages(0);
                       return;
                   }
                   layoutParams.height = hideHeight;
                   view.setLayoutParams(layoutParams);
               }
               sendEmptyMessageDelayed(0,1);
           }
       };
       handler.sendEmptyMessage(0);
    }
}
