package com.yctc.zhiting.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;
import com.yctc.zhiting.widget.colorpicker.ColorCircleView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 颜色选择器
 */
public class ColorPickerDialog extends CommonBaseDialog {

    @BindView(R.id.ccv)
    ColorCircleView colorCircleView;

    private int mColor;
    private float mHue = 0;
    private float mSaturation = 100;
    private String rgb;
    private String mColorStr;

    @Override
    protected void initArgs(Bundle arguments) {
        rgb = arguments.getString("rgb");
    }

    @Override
    protected void initView(View view) {
        int color = 0xFFFFFF;
        if (rgb!=null){
            color = Color.parseColor(rgb);
        }
        colorCircleView.setColor(color, false);
        colorCircleView.setColorPickerListener(new ColorCircleView.OnColorPickerListener() {
            @Override
            public void onPicker(String colorStr, int color, float hue, float saturation) {
                mColorStr = colorStr;
                mColor = color;
                mHue = hue;
                mSaturation = saturation;
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_color_picker;
    }

    @Override
    protected int obtainWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int obtainHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.BOTTOM;
    }

    @OnClick({R.id.ivClose, R.id.tvTodo})
    void onClick(View view){
        int viewId = view.getId();
        switch (viewId){
            case R.id.ivClose:
                dismiss();
                break;

            case R.id.tvTodo:
                if (confirmListener!=null){
                    confirmListener.onConfirm(mColorStr, mColor, mHue, mSaturation);
                }
                break;
        }
    }

    private OnConfirmListener confirmListener;

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface OnConfirmListener{
        void onConfirm(String colorStr, int color, float hue, float saturation);
    }
}
