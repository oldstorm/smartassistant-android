package com.app.main.framework.view.titlebar;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.AttrRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.main.framework.R;
import com.app.main.framework.baseutil.UiUtil;

public class TitleBarManager extends FrameLayout implements BaseTitleBar {
    private RelativeLayout rlTitleBarRoot;
    private ImageView ivTitleBarLeft;
    private TextView tvTitleBarText;
    private ImageView ivTitleBarMore;
    private TextView tvTitleBarMoreText;
    private TextView tvTitleBarLeft;

    public TitleBarManager(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public TitleBarManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TitleBarManager(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public String getTitleStr(){
        if (tvTitleBarText != null)
            return tvTitleBarText.getText().toString();
        return null;
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.layout_base_titlebar, this);
        tvTitleBarLeft = findViewById(R.id.tvTitleBarLeftText);
        rlTitleBarRoot = findViewById(R.id.rlTitleBarRoot);
        ivTitleBarLeft = findViewById(R.id.ivTitleBarLeft);
        tvTitleBarText = findViewById(R.id.tvTitleBarText);
        ivTitleBarMore = findViewById(R.id.ivTitleBarMore);
        tvTitleBarMoreText = findViewById(R.id.tvTitleBarMoreText);
        int id = getId();
        if (id < 1) {
            setId(R.id.id_base_title_bar);
        }
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public BaseTitleBar setBgColor(int color) {
        rlTitleBarRoot.setBackgroundColor(UiUtil.getColor(color));
        return this;
    }

    @Override
    public BaseTitleBar setTitle(@StringRes int StringResId) {
        tvTitleBarText.setText(StringResId);
        return this;
    }

    @Override
    public BaseTitleBar setTitle(String text) {
        tvTitleBarText.setText(text);
        return this;
    }

    @Override
    public BaseTitleBar setTitleIcon(@DrawableRes int drawableId) {
        tvTitleBarText.setCompoundDrawables(null, null, UiUtil.getDrawable(drawableId), null);
        return this;
    }

    @Override
    public BaseTitleBar setTitleTextColor(int color) {
        tvTitleBarText.setTextColor(color);
        return this;
    }

    @Override
    public BaseTitleBar showTitleBarMoreView() {
        if (ivTitleBarMore.getVisibility() != VISIBLE) {
            ivTitleBarMore.setVisibility(VISIBLE);
        }
        return this;
    }

    @Override
    public BaseTitleBar showTitleBarMoreText() {
        if (tvTitleBarMoreText.getVisibility() != VISIBLE)
            tvTitleBarMoreText.setVisibility(VISIBLE);
        return this;
    }

    @Override
    public BaseTitleBar setTitleBarMoreText(String text) {
        if (tvTitleBarMoreText.getVisibility() == GONE)
            tvTitleBarMoreText.setVisibility(VISIBLE);
        tvTitleBarMoreText.setText(text);
        return this;
    }

    @Override
    public BaseTitleBar setTitleBarMoreText(int StringResId) {
        if (tvTitleBarMoreText.getVisibility() == GONE)
            tvTitleBarMoreText.setVisibility(VISIBLE);
        tvTitleBarMoreText.setText(UiUtil.getString(StringResId));
        return this;
    }

    @Override
    public BaseTitleBar setTitleBarMoreTextColor(int color) {
        tvTitleBarMoreText.setTextColor(UiUtil.getColor(color));
        return this;
    }

    @Override
    public BaseTitleBar hideTitleBarMoreView() {
        if (ivTitleBarMore.getVisibility() != GONE) {
            ivTitleBarMore.setVisibility(GONE);
        }
        return this;
    }

    @Override
    public BaseTitleBar setTitleBarMoreClickListener(OnClickListener listener) {
        ivTitleBarMore.setOnClickListener(listener);
        return this;
    }

    @Override
    public BaseTitleBar setTitleBarMoreIcon(Drawable drawable) {
        ivTitleBarMore.setImageDrawable(drawable);
        ivTitleBarMore.setVisibility(VISIBLE);
        return this;
    }

    @Override
    public BaseTitleBar setTitleBarMoreTextClickListener(OnClickListener listener) {
        tvTitleBarMoreText.setOnClickListener(listener);
        return this;
    }

    @Override
    public BaseTitleBar showTitleBarLeftView() {
        if (ivTitleBarLeft.getVisibility() != VISIBLE) {
            ivTitleBarLeft.setVisibility(VISIBLE);
        }
        return this;
    }

    @Override
    public BaseTitleBar hideTitleBarLeftVeiw() {
        if (ivTitleBarLeft.getVisibility() != GONE) {
            ivTitleBarLeft.setVisibility(GONE);
        }
        return this;
    }

    @Override
    public BaseTitleBar setTitleBarLeftClickListener(OnClickListener listener) {
        ivTitleBarLeft.setOnClickListener(listener);
        return this;
    }

    /**
     * 左边文字的点击
     * @param listener
     * @return
     */
    @Override
    public BaseTitleBar setTitleBarTextLeftClickListener(OnClickListener listener) {
        tvTitleBarLeft.setOnClickListener(listener);
        return this;
    }

    /**
     * 设置左边的文字
     *
     * @param text
     * @return
     */
    @Override
    public BaseTitleBar setTitleBarLeftText(String text) {
        hideTitleBarLeftVeiw();
        if (tvTitleBarLeft != null) {
            tvTitleBarLeft.setVisibility(VISIBLE);
            tvTitleBarLeft.setText(text);
        }
        return this;
    }

    @Override
    public BaseTitleBar setTitleBarMoreEnable(boolean enable) {
        if (tvTitleBarMoreText != null) {
            tvTitleBarMoreText.setEnabled(enable);
        }
        return this;
    }

    @Override
    public TextView getTvTitleBarMoreText(){
        return tvTitleBarMoreText;
    }
}
