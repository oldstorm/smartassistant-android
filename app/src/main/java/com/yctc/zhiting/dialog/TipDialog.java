package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.BindView;
import butterknife.OnClick;

public class TipDialog extends CommonBaseDialog {

    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.tvLeft)
    TextView tvLeft;
    @BindView(R.id.tvRight)
    TextView tvRight;

    public static final String TIP_TXT = "tipTxt";
    public static final String LEFT_TXT = "leftTxt";
    public static final String RIGHT_TXT = "rightTxt";
    private String tipTxt;  // 提示文案
    private String leftTxt; // 左边按钮文本
    private String rightTxt;  // 右边按钮文本

    public static TipDialog getInstance(String tipTxt, String leftTxt, String rightTxt) {
        TipDialog tipDialog = new TipDialog();
        Bundle arguments = new Bundle();
        arguments.putString(TIP_TXT, tipTxt);
        arguments.putString(LEFT_TXT, leftTxt);
        arguments.putString(RIGHT_TXT, rightTxt);
        tipDialog.setArguments(arguments);
        return tipDialog;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        tipTxt = arguments.getString(TIP_TXT);
        leftTxt = arguments.getString(LEFT_TXT);
        rightTxt = arguments.getString(RIGHT_TXT);
    }

    @Override
    protected void initView(View view) {
        tvContent.setText(tipTxt);
        tvLeft.setText(leftTxt);
        tvRight.setText(rightTxt);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_tips;
    }

    @Override
    protected int obtainWidth() {
        return dp2px(300);
    }

    @Override
    protected int obtainHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.CENTER;
    }

    @OnClick({R.id.tvLeft, R.id.tvRight})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tvLeft:
                if (operateListener != null) {
                    operateListener.onClickLeft();
                }
                break;

            case R.id.tvRight:
                if (operateListener != null) {
                    operateListener.onClickRight();
                }
                break;
        }
    }

    private OnOperateListener operateListener;

    public void setOperateListener(OnOperateListener operateListener) {
        this.operateListener = operateListener;
    }

    public interface OnOperateListener {
        void onClickLeft();

        void onClickRight();
    }
}
