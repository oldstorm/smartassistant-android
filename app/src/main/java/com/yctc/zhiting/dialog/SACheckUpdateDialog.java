package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.BindView;
import butterknife.OnClick;

public class SACheckUpdateDialog extends CommonBaseDialog {

    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.rbUpdate)
    ProgressBar rbUpdate;
    @BindView(R.id.tvUpdate)
    TextView tvUpdate;
    @BindView(R.id.llUpdate)
    LinearLayout llUpdate;

    private String latestVersion;

    public static SACheckUpdateDialog newInstance(String latest_version) {
        Bundle args = new Bundle();
        args.putString("version", latest_version);
        SACheckUpdateDialog fragment = new SACheckUpdateDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_sa_check_update;
    }

    @Override
    protected int obtainWidth() {
        return UiUtil.dip2px(240);
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
    protected void initArgs(Bundle arguments) {
        latestVersion = arguments.getString("version");
    }

    @Override
    protected void initView(View view) {
        tvContent.setText(String.format(UiUtil.getString(R.string.home_check_update_content), latestVersion));
    }

    @OnClick({R.id.ivClose, R.id.llUpdate})
    void onClick(View view){
        int viewId = view.getId();
        switch (viewId){
            case R.id.llUpdate: // 更新
                if (updateListener!=null){
                    updateListener.onUpdate();
                }
                break;

            case R.id.ivClose: // 关闭
                dismiss();
                break;
        }
    }

    /**
     * 设置更新状态
     * @param updating
     */
    public void setUpdateStatus(boolean updating){
        tvUpdate.setVisibility(updating ? View.GONE : View.VISIBLE);
        rbUpdate.setVisibility(updating ? View.VISIBLE : View.GONE);
        llUpdate.setEnabled(!updating);
    }

    private OnUpdateListener updateListener;

    public void setUpdateListener(OnUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public interface OnUpdateListener{
        void onUpdate();
    }
}
