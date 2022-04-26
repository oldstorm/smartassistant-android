package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.mine.AndroidAppVersionBean;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * APP更新对话框
 */
public class AppUpdateDialog extends CommonBaseDialog {
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.tvUpdate)
    TextView tvUpdate;
    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.tvVersion)
    TextView tvVersion;

    private AndroidAppVersionBean mVersionBean;

    public static AppUpdateDialog newInstance(AndroidAppVersionBean appVersionBean) {
        Bundle args = new Bundle();
        args.putSerializable(SpConstant.KEY_BEAN, appVersionBean);
        AppUpdateDialog fragment = new AppUpdateDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_app_update;
    }

    @Override
    protected int obtainWidth() {
        return dp2px(300);
    }

    @Override
    protected int obtainHeight() {
        return LinearLayout.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected boolean getCancelable() {
        return false;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        mVersionBean = (AndroidAppVersionBean) arguments.getSerializable(SpConstant.KEY_BEAN);
    }

    @Override
    protected void initView(View view) {
        tvVersion.setText(String.format(UiUtil.getString(R.string.app_update_version),mVersionBean.getMax_app_version()));
        tvContent.setText(mVersionBean.getRemark());
        if (mVersionBean.getUpdate_type() == Constant.UpdateType.FORCE) {//强制更新
            tvCancel.setText(UiUtil.getString(R.string.app_no_update_exit));
        }
    }

    /**
     * 设置下载进度
     */
    public void setProgress(String text) {
        if (tvUpdate != null)
            tvUpdate.setText(text);
    }

    @OnClick({R.id.tvCancel, R.id.tvUpdate})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tvUpdate: // 更新
                tvUpdate.setEnabled(false);
                if (updateListener != null) {
                    updateListener.onUpdate();
                }
                //dismiss();
                break;

            case R.id.tvCancel: // 关闭
                dismiss();
                if (mVersionBean.getUpdate_type() == Constant.UpdateType.FORCE) {
                    LibLoader.exitAPP();
                } else {
                    if (updateListener != null) {
                        updateListener.onCancel();
                    }
                }
                break;
        }
    }

    private OnUpdateListener updateListener;

    public AppUpdateDialog setUpdateListener(OnUpdateListener updateListener) {
        this.updateListener = updateListener;
        return this;
    }

    public void setRefreshUI() {
        tvUpdate.setEnabled(true);
        tvUpdate.setText(UiUtil.getString(R.string.app_once_update));
    }

    public interface OnUpdateListener {
        void onUpdate();

        void onCancel();
    }
}