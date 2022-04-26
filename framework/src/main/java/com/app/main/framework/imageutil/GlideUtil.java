package com.app.main.framework.imageutil;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.BaseApplication;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

/**
 * 加载图片
 */
public class GlideUtil {

    public static GlideConfing load(String path) {
        return new GlideConfing<>(Glide.with(UiUtil.getContext()).asDrawable().diskCacheStrategy(DiskCacheStrategy.ALL).load(path));
    }

    public static GlideConfing loadHeadImg(String path) {
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

    public static GlideConfing loadRound(String url, int round) {
        return new GlideConfing(Glide.with(UiUtil.getContext()).load(url).apply(RequestOptions.bitmapTransform(new RoundedCorners(round))));
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

    /**
     * Glide缓存图片（要在子线程执行
     * @param url
     */
    public static String getImagePathFromCache(String url) {
        FutureTarget<File> future = Glide.with(BaseApplication.getContext())
                .load(url)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        try {
            File cacheFile = future.get();
            String newPath = cacheFile.getParent() + "/" + cacheFile.getName().replace(".0", "") + ".jpg";
            LogUtil.e("全路径："+cacheFile.getAbsolutePath());
            LogUtil.e("父路径："+cacheFile.getParentFile().getAbsolutePath());
            LogUtil.e("文件名："+cacheFile.getName());
            File file = new File(newPath);
            if (file.exists()) {
                file.delete();
            }
            copyFile(cacheFile.getPath(), newPath);
            return newPath;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void copyFile(String oldPath, String newPath) {
        try {
            int byteRead;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ( (byteRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            LogUtil.e("复制文件操作出错");
            e.printStackTrace();
        }
    }
}
