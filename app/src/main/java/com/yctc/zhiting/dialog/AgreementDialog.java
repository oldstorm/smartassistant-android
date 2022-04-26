package com.yctc.zhiting.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.app.main.framework.httputil.Util;
import com.yctc.zhiting.R;

public class AgreementDialog extends CommonBaseDialog implements View.OnClickListener {

    private TextView tvAgreement;
    private TextView tvPolicy;
    private TextView tvDisagree;
    private TextView tvAgree;
    private TextView tvRead;
    private ImageView ivSelected;


    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {
        setCancelable(false);
        tvAgreement = view.findViewById(R.id.tvAgreement);
        tvPolicy = view.findViewById(R.id.tvPolicy);
        tvDisagree = view.findViewById(R.id.tvDisagree);
        tvAgree = view.findViewById(R.id.tvAgree);
        tvRead = view.findViewById(R.id.tvRead);
        ivSelected = view.findViewById(R.id.ivSelected);
        tvAgreement.setOnClickListener(this::onClick);
        tvPolicy.setOnClickListener(this::onClick);
        tvDisagree.setOnClickListener(this::onClick);
        tvAgree.setOnClickListener(this::onClick);
        ivSelected.setOnClickListener(this::onClick);
        tvRead.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tvAgreement){  // 用户协议
            if(onOperateListener!=null){
                onOperateListener.onAgreement();
            }
        }else if (viewId == R.id.tvPolicy){ // 隐私政策
            if(onOperateListener!=null){
                onOperateListener.onPolicy();
            }
        }else if (viewId == R.id.tvDisagree){ // 不同意
            if(onOperateListener!=null){
                onOperateListener.onDisagree();
            }
        }else if (viewId == R.id.tvAgree){  // 同意
            if (!ivSelected.isSelected()) {
                ToastUtil.show(UiUtil.getString(R.string.login_please_check_user_agreement));
                return;
            }
            if(onOperateListener!=null){
                onOperateListener.onAgree();
            }
        }else if (viewId == R.id.ivSelected || viewId == R.id.tvRead) { // 已阅读
            ivSelected.setSelected(!ivSelected.isSelected());
        }
    }

    private OnOperateListener onOperateListener;

    public OnOperateListener getOnOperateListener() {
        return onOperateListener;
    }

    public void setOnOperateListener(OnOperateListener onOperateListener) {
        this.onOperateListener = onOperateListener;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_agreement;
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

    public interface OnOperateListener{
        void onAgreement();
        void onPolicy();
        void onDisagree();
        void onAgree();
    }

}
