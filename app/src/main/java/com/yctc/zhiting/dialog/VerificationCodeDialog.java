package com.yctc.zhiting.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.BindView;

public class VerificationCodeDialog extends CommonBaseDialog implements View.OnClickListener {

    @BindView(R.id.tvCode)
    TextView tvCode;
    @BindView(R.id.tvCopy)
    TextView tvCopy;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    private String code = "";

    public static VerificationCodeDialog newInstance(String code) {
        Bundle args = new Bundle();
        args.putString("code", code);
        VerificationCodeDialog fragment = new VerificationCodeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean getCancelable() {
        return false;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_verification_code;
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
    protected void initArgs(Bundle arguments) {
        code = arguments.getString("code");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvCopy) {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(null, code);
            clipboard.setPrimaryClip(clipData);
            ToastUtil.show(getActivity().getString(R.string.mine_copy_success));
        } else {
            dismiss();
        }
    }

    @Override
    protected void initView(View view) {
        tvCopy.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvCode.setText(code);
    }

    private OnConfirmListener mListener;

    public interface OnConfirmListener {
        void onConfirm(boolean isAllow);
    }

    public VerificationCodeDialog setDialogListener(OnConfirmListener mListener) {
        this.mListener = mListener;
        return this;
    }
}
