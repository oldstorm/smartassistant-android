package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;
import com.yctc.zhiting.config.Constant;

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
    @BindView(R.id.tvClear)
    TextView tvClear;

    private boolean first = true;
    private String mFilePath = "";

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
//        if (first){
//            tvTips.setText(getResources().getString(R.string.mine_mine_upload_plugin_tips_2));
//            first = false;
//        }else {
//            tvUpload.setVisibility(View.GONE);
//            tvTips.setVisibility(View.GONE);
//            tvSuccess.setVisibility(View.VISIBLE);
//            tvConfirm.setEnabled(true);
//        }

        if (clickUploadListener!=null){
            clickUploadListener.onUpload();
        }
    }

    @OnClick(R.id.tvClear)
    void onClick(){
        resetUploadStatus(0, "");
        mFilePath = "";
    }

    public void resetUploadStatus(int status){
        switch (status){
            case 1:  // 上传中
                tvUpload.setEnabled(false);
                tvUpload.setText(UiUtil.getString(R.string.mine_uploading));
                break;

            case 2: // 上传成功
                tvUpload.setVisibility(View.GONE);
                tvTips.setVisibility(View.GONE);
                tvSuccess.setVisibility(View.VISIBLE);
                tvConfirm.setEnabled(true);
                break;

            case 3:  // 上传失败
                tvUpload.setText(UiUtil.getString(R.string.mine_mine_upload_plugin));
                tvTips.setText(UiUtil.getString(R.string.mine_mine_upload_plugin_tips_2));
                tvUpload.setEnabled(true);
                break;
        }
    }

    public void resetUploadStatus(int status, String msg){
        switch (status){

            case 0: // 原始状态
                tvUpload.setVisibility(View.VISIBLE);
                tvClear.setVisibility(View.GONE);
                tvTips.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                tvTips.setText(UiUtil.getString(R.string.mine_mine_upload_plugin_tips_1));
                tvConfirm.setText(UiUtil.getString(R.string.common_confirm));
                tvConfirm.setEnabled(false);
                break;

            case 1:  // 选择文件后
                int pos = msg.lastIndexOf("/");
                mFilePath = msg;
                String fileName = msg.substring(pos+1);
                tvTips.setText(fileName);
                tvTips.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tvUpload.setVisibility(View.GONE);
                tvClear.setVisibility(View.VISIBLE);
                tvConfirm.setEnabled(true);
                break;

            case 2: // 上传中
                tvUpload.setVisibility(View.GONE);
                tvClear.setVisibility(View.GONE);
                tvConfirm.setText(UiUtil.getString(R.string.mine_uploading_now));
                tvConfirm.setEnabled(false);
                break;

            case 3:  // 上传失败
                tvUpload.setText(UiUtil.getString(R.string.mine_mine_upload_plugin));
                tvTips.setText(UiUtil.getString(R.string.mine_uploading_fail)+msg);
                tvTips.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                tvClear.setVisibility(View.GONE);
                tvUpload.setVisibility(View.VISIBLE);
                tvUpload.setEnabled(true);
                tvConfirm.setText(UiUtil.getString(R.string.common_confirm));
                break;
        }
    }

    /**
     * 确定
     */
    @OnClick(R.id.tvConfirm)
    void onClickConfirm(){
        String text = tvTips.getText().toString().trim();
        if (text.endsWith(Constant.DOT_ZIP)){
            if (clickUploadListener!=null){
                resetUploadStatus(2, "");
                clickUploadListener.onConfirm(mFilePath);
            }
        }else {
            resetUploadStatus(3, UiUtil.getString(R.string.mine_format_incorrect));
        }
    }

    private OnClickUploadListener clickUploadListener;

    public OnClickUploadListener getClickUploadListener() {
        return clickUploadListener;
    }

    public void setClickUploadListener(OnClickUploadListener clickUploadListener) {
        this.clickUploadListener = clickUploadListener;
    }

    public interface OnClickUploadListener{
        void onUpload();
        void onConfirm(String filePath);
        void onClear();
    }
}
