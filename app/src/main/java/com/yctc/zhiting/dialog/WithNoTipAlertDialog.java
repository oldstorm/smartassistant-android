package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.BindView;
import butterknife.OnClick;

public class WithNoTipAlertDialog extends CommonBaseDialog {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvTip)
    TextView tvTip;
    @BindView(R.id.tvNotTip)
    TextView tvNotTip;

    private String mTitle;  // 标题
    private String mTip;  // 提示

    public static WithNoTipAlertDialog getInstance(String title, String tip) {
        WithNoTipAlertDialog withNoTipAlertDialog = new WithNoTipAlertDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("tip", tip);
        withNoTipAlertDialog.setArguments(bundle);
        return withNoTipAlertDialog;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        mTitle = arguments.getString("title");
        mTip = arguments.getString("tip");
    }

    @Override
    protected void initView(View view) {
        tvTitle.setText(mTitle);
        tvTip.setText(mTip);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_with_no_tip_alert;
    }

    @Override
    protected int obtainWidth() {
        return dp2px(300);
    }

    @Override
    protected int obtainHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.CENTER;
    }

    @OnClick({R.id.tvNotTip, R.id.tvCancel, R.id.tvConfirm})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tvNotTip:  // 不再提示
                tvNotTip.setSelected(!tvNotTip.isSelected());
                break;

            case R.id.tvCancel:  // 取消
                dismiss();
                break;


            case R.id.tvConfirm:  // 确定
                if (confirmListener != null) {
                    confirmListener.onConfirm(tvNotTip.isSelected());
                }
                break;
        }
    }

    private OnConfirmListener confirmListener;

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface OnConfirmListener {
        void onConfirm(boolean notTipAgain);
    }
}
