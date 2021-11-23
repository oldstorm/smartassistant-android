package com.app.main.framework.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


import com.app.main.framework.R;


public class CertificateDialog extends CommonBaseDialog{

    private TextView tvTitle;
    private TextView tvCancel;
    private TextView tvConfirm;
    private String title; // 标题

    public static CertificateDialog newInstance(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        CertificateDialog fragment = new CertificateDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_certificate;
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
        title = arguments.getString("title");
    }

    @Override
    protected void initView(View view) {
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvConfirm = view.findViewById(R.id.tvConfirm);
        tvTitle.setText(title);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmListener!=null){
                    confirmListener.onConfirm();
                }
                dismiss();
            }
        });
    }

    private OnConfirmListener confirmListener;

    public OnConfirmListener getConfirmListener() {
        return confirmListener;
    }

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface OnConfirmListener{
        void onConfirm();
    }
}
