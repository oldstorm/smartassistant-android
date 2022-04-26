package com.yctc.zhiting.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.BindView;
import butterknife.OnClick;

public class CompanyQrCodeDialog extends CommonBaseDialog {

    @BindView(R.id.ivClose)
    ImageView ivClose;
    @BindView(R.id.llDepartment)
    LinearLayout llDepartment;
    @BindView(R.id.llRole)
    LinearLayout llRole;
    @BindView(R.id.tvDepartment)
    TextView tvDepartment;
    @BindView(R.id.tvRole)
    TextView tvRole;
    @BindView(R.id.tvTodo)
    TextView tvTodo;

    public static CompanyQrCodeDialog newInstance() {
        CompanyQrCodeDialog fragment = new CompanyQrCodeDialog();
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_company_qrcode;
    }

    @Override
    protected int obtainWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int obtainHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.BOTTOM;
    }

    @OnClick({R.id.llDepartment, R.id.llRole, R.id.tvTodo, R.id.ivClose})
    public void onClick(View view) {
        if (view.getId() == R.id.ivClose) {
            dismiss();
        } else if (view.getId() == R.id.llDepartment) {
            mListener.onSelectDepartmentDialog();
        } else if (view.getId() == R.id.llRole) {
            mListener.onSelectRoleDialog();
        } else if (view.getId() == R.id.tvTodo) {
            mListener.onGenerateQrCode();
        }
    }

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {
        tvTodo.setEnabled(false);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mListener.onDismiss();
    }

    public void setRoles(String roles) {
        tvRole.setText(roles);
    }

    public void setDepartment(String department) {
        tvDepartment.setText(department);
    }

    public void setCreateQrCodeEnable(boolean enable) {
        tvTodo.setEnabled(enable);
    }

    public void setListener(OnCompanyQrListener mListener) {
        this.mListener = mListener;
    }

    public OnCompanyQrListener mListener;

    public interface OnCompanyQrListener {
        void onSelectDepartmentDialog();

        void onSelectRoleDialog();

        void onGenerateQrCode();

        void onDismiss();
    }
}
