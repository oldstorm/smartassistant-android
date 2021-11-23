package com.yctc.zhiting.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.httputil.Util;
import com.yctc.zhiting.R;

public class AgreementDialog extends Dialog implements View.OnClickListener {

    private TextView tvAgreement;
    private TextView tvPolicy;
    private TextView tvDisagree;
    private TextView tvAgree;

    public AgreementDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setLayout((int) (UiUtil.getScreenWidth()/1.25), UiUtil.getScreenWidth());
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(R.drawable.shape_white_c10);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_agreement);
        setCancelable(false);
        tvAgreement = findViewById(R.id.tvAgreement);
        tvPolicy = findViewById(R.id.tvPolicy);
        tvDisagree = findViewById(R.id.tvDisagree);
        tvAgree = findViewById(R.id.tvAgree);
        tvAgreement.setOnClickListener(this::onClick);
        tvPolicy.setOnClickListener(this::onClick);
        tvDisagree.setOnClickListener(this::onClick);
        tvAgree.setOnClickListener(this::onClick);
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
            if(onOperateListener!=null){
                onOperateListener.onAgree();
            }
        }
    }

    private OnOperateListener onOperateListener;

    public OnOperateListener getOnOperateListener() {
        return onOperateListener;
    }

    public void setOnOperateListener(OnOperateListener onOperateListener) {
        this.onOperateListener = onOperateListener;
    }

    public interface OnOperateListener{
        void onAgreement();
        void onPolicy();
        void onDisagree();
        void onAgree();
    }
}
