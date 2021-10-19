package com.app.main.framework.imageutil;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.main.framework.R;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.widget.ScaleImageView;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class TransformationUtils extends SimpleTarget<Bitmap> {
    private ScaleImageView target;

    public TransformationUtils(ScaleImageView target) {
        this.target = target;
    }

    @Override
    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
        target.setImageBitmap(resource);
        //获取原图的宽高
        int width = resource.getWidth();
        int height = resource.getHeight();
        int toWidth = UiUtil.getScreenWidth() / 2 - UiUtil.getDimens(R.dimen.dp_18);
        target.setInitSize(toWidth, (int) (height*((float)toWidth/width)));
    }
}
