package com.yctc.zhiting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.imageutil.GlideUtil;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.util.SmartUtil;
import com.yctc.zhiting.R;

import org.jetbrains.annotations.NotNull;

/**
 * 头部刷新
 */
public class ZhiTingRefreshHeader extends LinearLayout implements RefreshHeader {

    private ImageView imageView;
    private boolean play;

    public ZhiTingRefreshHeader(Context context) {
        this(context, null);
    }

    public ZhiTingRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZhiTingRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setGravity(Gravity.CENTER);
        imageView = new ImageView(context);
        addView(imageView, UiUtil.getDimens(R.dimen.dp_35), UiUtil.getDimens(R.dimen.dp_35));
        setMinimumHeight(SmartUtil.dp2px(60));
    }

    @Override
    public void onStartAnimator(@NonNull @NotNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
    }

    @Override
    public int onFinish(@NonNull @NotNull RefreshLayout refreshLayout, boolean success) {
        play = false;
        return 0;
    }

    @Override
    public void onStateChanged(@NonNull @NotNull RefreshLayout refreshLayout, @NonNull @NotNull RefreshState oldState, @NonNull @NotNull RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh:
                GlideUtil.load(R.drawable.loading_static).into(imageView);
                break;

            case Refreshing:

                break;
            case ReleaseToRefresh:
                if (!play) {
                    GlideUtil.controlGif(imageView, R.drawable.loading, true);
                    play = true;
                }
                break;
        }
    }

    @NonNull
    @NotNull
    @Override
    public View getView() {
        return this;//真实的视图就是自己，不能返回null
    }

    @NonNull
    @NotNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;//指定为平移，不能null
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull @NotNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull @NotNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }
}
