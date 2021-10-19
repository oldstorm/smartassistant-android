package com.app.main.framework.imageutil;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.app.main.framework.R;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

@SuppressLint("CheckResult")
public class GlideConfing<T> {
    private RequestBuilder<T> requestBuilder;
    private RequestOptions requestOptions;
    private int type = 3;

    public GlideConfing(RequestBuilder<T> builder) {
        requestBuilder = builder;
        requestOptions = new RequestOptions();
        requestOptions.error(R.mipmap.pic_bg).placeholder(R.mipmap.pic_bg);
        requestOptions.skipMemoryCache(false);
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
    }

    public GlideConfing storeHead() {
        type = LoadType.STORE_HEAD;
        requestOptions.error(R.mipmap.profile_massage_img).placeholder(R.mipmap.profile_massage_img);
        return this;
    }

    public GlideConfing modelHead() {
        type = LoadType.MODEL_HEAD;
        requestOptions.error(R.mipmap.profile_model_img).placeholder(R.mipmap.profile_model_img);
        return this;
    }

    public GlideConfing userHead() {
        type = LoadType.USER_HEAD;
        requestOptions.error(R.mipmap.profile_user_img).placeholder(R.mipmap.profile_user_img);
        return this;
    }

    public GlideConfing defaultIcon() {
        type = LoadType.DEFAULT;
        requestOptions.error(R.drawable.default_bg).placeholder(R.drawable.default_bg);
        return this;
    }

    public GlideConfing placeholder(int resourceId) {
        requestOptions.placeholder(resourceId);
        return this;
    }

    public GlideConfing listener(RequestListener<T> listener) {
        requestBuilder.listener(listener);
        return this;
    }

    public GlideConfing placeholder(Drawable resourceId) {
        if (resourceId != null)
            requestOptions.placeholder(resourceId);
        return this;
    }

    public GlideConfing centerCrop() {
        requestOptions.centerCrop();
        return this;
    }

    public GlideConfing override(int width) {
        requestBuilder.override(width, Target.SIZE_ORIGINAL);
        return this;
    }

    public GlideConfing override(int width, int height) {
        requestBuilder.override(width, height);
        return this;
    }

    public GlideConfing error(int resourceId) {
        requestOptions.error(resourceId);
        return this;
    }

    public GlideConfing skipMemoryCache(boolean noMemoryCach) {
        requestOptions.skipMemoryCache(noMemoryCach);
        if (!noMemoryCach)
            requestOptions.dontAnimate();
        return this;
    }

    public GlideConfing skipDiskCache(DiskCacheStrategy valuse) {
        requestOptions.diskCacheStrategy(valuse);
        return this;
    }

    public GlideConfing thumbnail(float sizeMultiplier) {
        requestBuilder.thumbnail(sizeMultiplier);
        return this;
    }

    public GlideConfing setCircleTransform() {
        requestOptions.transform(new GlideCircleTransform());
        return this;
    }

    public GlideConfing setTransfrom(Transformation<Bitmap> transformation) {
        requestOptions.transform(transformation);
        return this;
    }

    public GlideConfing setRoundCorner(int roundingRadius) {
        requestOptions.transform(new RoundedCorners(roundingRadius));
        return this;
    }

    public void into(ImageView imageView) {
        initDefaultIcon();
        requestBuilder.apply(requestOptions);
        requestBuilder.into(imageView);
    }

    private void initDefaultIcon() {
        switch (type) {
            case LoadType.DEFAULT:
                defaultIcon();
                break;
            case LoadType.MODEL_HEAD:
                modelHead();
                break;
            case LoadType.STORE_HEAD:
                storeHead();
                break;
            case LoadType.USER_HEAD:
                userHead();
                break;
        }
        type = LoadType.DEFAULT;
    }

    public void into(ImageView imageView, RequestOptions options) {
        defaultIcon();
        requestBuilder.apply(options);
        requestBuilder.into(imageView);
    }

    public void into(Target<T> target) {
        defaultIcon();
        requestBuilder.apply(requestOptions);
        requestBuilder.into(target);
    }

    public interface LoadType {
        int STORE_HEAD = 0;
        int MODEL_HEAD = 1;
        int USER_HEAD = 2;
        int DEFAULT = 3;
    }
}
