package com.app.main.framework.imageutil;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.UiUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

/**
 * 加载图片
 */
public class GlideUtil {

    public static GlideConfing load(String path) {
        return new GlideConfing<>(Glide.with(UiUtil.getContext()).asDrawable().load(path));
    }

    public static GlideConfing load(File file) {
        return new GlideConfing<>(Glide.with(UiUtil.getContext()).asDrawable().load(file));
    }

    public static GlideConfing load(Uri uri) {
        return new GlideConfing<>(Glide.with(UiUtil.getContext()).asDrawable().load(uri));
    }

    public static GlideConfing load(int rid) {
        return new GlideConfing<>(Glide.with(UiUtil.getContext()).asDrawable().load(rid));
    }

    public static GlideConfing loadGif(String path) {
        return new GlideConfing<>(Glide.with(UiUtil.getContext()).asGif().load(path));
    }

    public static GlideConfing loadGif(File file) {
        return new GlideConfing<>(Glide.with(UiUtil.getContext()).asGif().load(file));
    }

    public static GlideConfing loadGif(Uri uri) {
        return new GlideConfing<>(Glide.with(UiUtil.getContext()).asGif().load(uri));
    }

    public static GlideConfing loadGif(int rid) {
        return new GlideConfing<>(Glide.with(UiUtil.getContext()).asGif().load(rid));
    }


    public static void controlGif(ImageView imageView, @DrawableRes int resource, boolean isPlayGif){
        Glide.with(LibLoader.getCurrentActivity())
                .load(resource)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        if (resource instanceof GifDrawable) {
                            if (isPlayGif) { //是否播放GIf的控制
                                imageView.setImageDrawable(resource);
                                ((GifDrawable) resource).start(); //开始播放GIF
                            } else {
                                //不播放GIF，只展示GIF第一帧
                                imageView.setImageBitmap(((GifDrawable) resource).getFirstFrame());
                            }
                        } else {
                            //非GIF情况
                            imageView.setImageDrawable(resource);
                        }
                    }
                });
    }
}
