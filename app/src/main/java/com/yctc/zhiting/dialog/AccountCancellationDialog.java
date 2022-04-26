package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;
import com.yctc.zhiting.utils.UserUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 注销验证码弹窗
 */
public class AccountCancellationDialog extends CommonBaseDialog {

    @BindView(R.id.tvTips)
    TextView tvTips;
    @BindView(R.id.etCode)
    EditText etCode;
    @BindView(R.id.tvCode)
    TextView tvCode;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.rbConfirm)
    ProgressBar rbConfirm;

    private CountDownTimer mCountDownTimer;

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {
        setCancelable(false);
        tvTips.setText(String.format(UiUtil.getString(R.string.mine_account_cancellation_tips), UserUtils.getPhone()));
        initDownTimer();
    }

    private void initDownTimer() {
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCode.setEnabled(false);
                tvCode.setText(String.format(getResources().getString(R.string.login_get_it_again_in_sixty_seconds), String.valueOf(millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                tvCode.setEnabled(true);
                tvCode.setText(getResources().getString(R.string.login_get_verification_code));
            }
        };
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_account_cancellation;
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

    @Override
    public void onDestroy() {
        if (etCode!=null)
        etCode.setText("");
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    @OnClick({R.id.tvCode, R.id.tvConfirm, R.id.tvCancel})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tvCode:  // 获取验证码
                mCountDownTimer.start();
                if (confirmListener != null) {
                    confirmListener.getCode();
                }
                break;

            case R.id.tvConfirm:  // 确定注销
                String code = etCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.show(UiUtil.getString(R.string.mine_input_verification_code_hint));
                    return;
                }
                if (confirmListener != null) {
                    confirmListener.onConfirm(code);
                }
                break;

            case R.id.tvCancel: // 取消
                dismiss();
                etCode.setText("");
                break;
        }
    }

    /**
     * 设置是否加载中状态
     * @param isLoading
     */
    public void setLoading(boolean isLoading) {
        rbConfirm.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        tvConfirm.setVisibility(!isLoading ? View.VISIBLE : View.GONE);
    }

    private OnConfirmListener confirmListener;

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface OnConfirmListener {
        void getCode();  // 获取验证码
        void onConfirm(String code);  // 确定
    }
}
