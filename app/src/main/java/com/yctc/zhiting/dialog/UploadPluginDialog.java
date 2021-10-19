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

/**
 * 上次插件弹窗
 */
public class UploadPluginDialog extends CommonBaseDialog {

    @BindView(R.id.tvUpload)
    TextView tvUpload;
    @BindView(R.id.tvTips)
    TextView tvTips;
    @BindView(R.id.tvSuccess)
    TextView tvSuccess;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;

    private boolean first = true;

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_upload_plugin;
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

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {

    }

    /**
     * 上传
     */
    @OnClick(R.id.tvUpload)
    void onClickUpload(){
        // todo 具体功能再修改
        if (first){
            tvTips.setText(getResources().getString(R.string.mine_mine_upload_plugin_tips_2));
            first = false;
        }else {
            tvUpload.setVisibility(View.GONE);
            tvTips.setVisibility(View.GONE);
            tvSuccess.setVisibility(View.VISIBLE);
            tvConfirm.setEnabled(true);
        }
    }

    /**
     * 确定
     */
    @OnClick(R.id.tvConfirm)
    void onClickConfirm(){
        dismiss();
    }
}
