package com.app.main.framework.view.loading;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.main.framework.R;
import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.UiUtil;
import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

public class LoadingView {
    private FrameLayout rootView;
    private ImageView ivAnim;
    private TextView tvLoading;
    private CountDownTimer mTimer;

    private List<String> loadList = Arrays.asList(UiUtil.getString(R.string.loading1), UiUtil.getString(R.string.loading2), UiUtil.getString(R.string.loading3));
    private int position;

    public LoadingView() {
        initView();
    }

    public LoadingView(int type) {
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        rootView = (FrameLayout) UiUtil.inflate(R.layout.loading_view);
        ivAnim = rootView.findViewById(R.id.ivGifLoad);
        tvLoading = rootView.findViewById(R.id.tvLoading);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.setOnTouchListener((View v, MotionEvent event) -> {
            //消费事件
            return true;
        });
        startAnim();
    }

    private void startAnim() {
        Glide.with(LibLoader.getCurrentActivity())
                .asGif()
                .load(R.drawable.loading)
                .into(ivAnim);

//        position = 0;
//        mTimer = new CountDownTimer(30000, 500) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                position++;
//                position = position % 3;
//                tvLoading.setText(loadList.get(position));
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        };
//        mTimer.start();
    }

    public View getRootView() {
        return rootView;
    }
}
