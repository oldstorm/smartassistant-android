package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.BindView;

public class FindCertificateDialog extends CommonBaseDialog implements View.OnClickListener {

    @BindView(R.id.cbAllowFind)
    CheckBox cbAllowFind;
    @BindView(R.id.cbNoAllowFind)
    CheckBox cbNoAllowFind;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.clAllow)
    ConstraintLayout clAllow;
    @BindView(R.id.clNoAllow)
    ConstraintLayout clNoAllow;

    public static FindCertificateDialog newInstance() {
        Bundle args = new Bundle();
        FindCertificateDialog fragment = new FindCertificateDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean getCancelable() {
        return false;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_find_certificate;
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

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvCancel) {
            dismiss();
        } else if (view.getId() == R.id.tvConfirm) {
            if (cbAllowFind.isChecked()) {
                mListener.onConfirm(true);
                dismiss();
            } else if (cbNoAllowFind.isChecked()) {
                mListener.onConfirm(false);
                dismiss();
            }
        } else if (view.getId() == R.id.clAllow) {
            cbAllowFind.setChecked(!cbAllowFind.isChecked());
            cbNoAllowFind.setChecked(false);
        } else if (view.getId() == R.id.clNoAllow) {
            cbAllowFind.setChecked(false);
            cbNoAllowFind.setChecked(!cbNoAllowFind.isChecked());
        }
    }

    @Override
    protected void initView(View view) {
        clAllow.setOnClickListener(this);
        clNoAllow.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
    }

    private OnConfirmListener mListener;

    public interface OnConfirmListener {
        void onConfirm(boolean isAllow);
    }

    public FindCertificateDialog setDialogListener(OnConfirmListener mListener) {
        this.mListener = mListener;
        return this;
    }
}
