package com.app.main.framework.view.defaultview;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.app.main.framework.entity.BaseEntity;

public class NoDataBean extends BaseEntity {
    private int imageResourceId = -1;
    private int textResourceId = -1;
    private boolean isReflash = true;
    private int height = ViewGroup.LayoutParams.MATCH_PARENT;
    private View.OnClickListener reflashClickListener;

    public NoDataBean() {
    }

    public NoDataBean(int imageResourceId, int textResourceId, boolean isReflash, int height) {
        this.imageResourceId = imageResourceId;
        this.textResourceId = textResourceId;
        this.isReflash = isReflash;
        this.height = height;
    }

    public NoDataBean(int imageResourceId, int textResourceId, int height) {
        this.imageResourceId = imageResourceId;
        this.textResourceId = textResourceId;
        this.isReflash = false;
        this.height = height;
    }

    public NoDataBean(int imageResourceId, int textResourceId) {
        this.imageResourceId = imageResourceId;
        this.textResourceId = textResourceId;
    }

    public NoDataBean(int imageResourceId, int textResourceId, View.OnClickListener reflashClickListener) {
        this.imageResourceId = imageResourceId;
        this.textResourceId = textResourceId;
        this.reflashClickListener = reflashClickListener;
    }

    public NoDataBean(int imageResourceId, int textResourceId, boolean isReflash) {
        this.imageResourceId = imageResourceId;
        this.textResourceId = textResourceId;
        this.isReflash = isReflash;
    }

    public NoDataBean(int imageResourceId, int textResourceId, boolean isReflash, View.OnClickListener reflashClickListener) {
        this.imageResourceId = imageResourceId;
        this.textResourceId = textResourceId;
        this.isReflash = isReflash;
        this.reflashClickListener = reflashClickListener;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public @DrawableRes
    int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(@DrawableRes int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public @StringRes
    int getTextResourceId() {
        return textResourceId;
    }

    public void setTextResourceId(@StringRes int textResourceId) {
        this.textResourceId = textResourceId;
    }

    public boolean isReflash() {
        return isReflash;
    }

    public void setReflash(boolean reflash) {
        isReflash = reflash;
    }

    public View.OnClickListener getReflashClickListener() {
        return reflashClickListener;
    }

    public void setReflashClickListener(View.OnClickListener reflashClickListener) {
        this.reflashClickListener = reflashClickListener;
    }
}
