package com.app.main.framework.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.main.framework.R;
import com.app.main.framework.baseutil.LibLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class LoadingDialog extends Dialog {

    private ImageView imageView;
    private TextView tvLoading;
    private GifImageView gifImageView;
    private GifDrawable gifDrawable;
    private Context mContext;


    public LoadingDialog(@NonNull @NotNull Context context) {
        super(context);
        this.mContext = context;
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        getWindow().setDimAmount(0);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        imageView = findViewById(R.id.ivLoad);
        tvLoading = findViewById(R.id.tvLoading);
        gifImageView = (GifImageView) findViewById(R.id.givLoading);
        setCancelable(false);
    }

    public void reset() {
        try {
            gifDrawable = new GifDrawable(mContext.getResources(), R.drawable.loading);
            gifDrawable.setLoopCount(0);
            gifImageView.setImageDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (gifDrawable != null) {
            gifDrawable.stop();
            gifImageView.setImageResource(R.drawable.loading_static);
            gifDrawable = null;
        }
    }

    public void setGif() {
        if (imageView != null)
            Glide.with(LibLoader.getCurrentActivity())
                    .asGif()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .load(R.drawable.loading)
                    .into(imageView);
        setLoadingTextVisible(true);
    }

    public void setStatic() {
        if (imageView != null)
            imageView.setImageResource(R.drawable.loading_static);
    }

    private void setLoadingTextVisible(boolean visible) {
        if (tvLoading != null)
            tvLoading.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
