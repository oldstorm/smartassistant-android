package com.yctc.zhiting.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SoftwareUpgradeContract;
import com.yctc.zhiting.activity.presenter.SoftwareUpgradePresenter;
import com.yctc.zhiting.dialog.CheckUpdateDialog;
import com.yctc.zhiting.dialog.SACheckUpdateDialog;
import com.yctc.zhiting.entity.mine.CurrentVersionBean;
import com.yctc.zhiting.entity.mine.SoftWareVersionBean;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 固件、软件升级
 */
public class SoftwareUpgradeActivity extends MVPBaseActivity<SoftwareUpgradeContract.View, SoftwareUpgradePresenter> implements SoftwareUpgradeContract.View {

    @BindView(R.id.ivTitleBarMore)
    ImageView ivTitleBarMore;
    @BindView(R.id.tvTitleBarText)
    TextView tvTitleBarText;
    @BindView(R.id.tvVersion)
    TextView tvVersion;
    private CheckUpdateDialog dialog;

    private SACheckUpdateDialog mSACheckUpdateDialog;
    private String version;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_software_upgrade;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        int type = intent.getIntExtra("type", -1);
        int titleId = R.string.home_firmware_upgrade;
        if (type == 1) {
            titleId = R.string.home_software_upgrade;
            mPresenter.getCurrentVersion();
        }
        tvTitleBarText.setText(titleId);
    }


    @OnClick({R.id.ivTitleBarLeft, R.id.tvCheckUpdate})
    public void onClick(View view) {
        if (view.getId() == R.id.ivTitleBarLeft) {
            finish();
        } else if (view.getId() == R.id.tvCheckUpdate) {
            mPresenter.getCheckSoftWareVersion();
        }
    }

    @Override
    public void onCheckSoftWareVersionSuccess(SoftWareVersionBean bean) {
        if (bean.getVersion().equals(bean.getLatest_version())) {
            ToastUtil.show(UiUtil.getString(R.string.home_version_is_latest));
        } else {
//            if (dialog == null) {
//                dialog = CheckUpdateDialog.newInstance(bean.getLatest_version());
//                dialog.setDialogListener(() -> {
//                    SoftWareVersionBean request = new SoftWareVersionBean();
//                    request.setVersion(bean.getLatest_version());
//                    mPresenter.postSoftWareUpgrade(request);
//                });
//            }
//            if (!dialog.isShowing())
//                dialog.show(this);
            if (mSACheckUpdateDialog==null){
                mSACheckUpdateDialog = SACheckUpdateDialog.newInstance(bean.getLatest_version());
                mSACheckUpdateDialog.setUpdateListener(new SACheckUpdateDialog.OnUpdateListener() {
                    @Override
                    public void onUpdate() {
                        SoftWareVersionBean request = new SoftWareVersionBean();
                        request.setVersion(bean.getLatest_version());
                        version = bean.getLatest_version();
                        mSACheckUpdateDialog.setUpdateStatus(true);
                        mPresenter.postSoftWareUpgrade(request);
                    }
                });
            }
            if (!mSACheckUpdateDialog.isShowing()){
                mSACheckUpdateDialog.show(this);
            }
        }
    }

    @Override
    public void onCheckSoftWareVersionFailed(int code, String msg) {
        if (TextUtils.isEmpty(msg)) {
            ToastUtil.show(UiUtil.getString(R.string.home_get_update_info_failed));
        } else {
            ToastUtil.show(msg);
        }
    }

    @Override
    public void onSoftWareUpgradeSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.home_update_success));
        tvVersion.setText(String.format(UiUtil.getString(R.string.home_current_version), version));
        mSACheckUpdateDialog.setUpdateStatus(false);
        mSACheckUpdateDialog.dismiss();
    }

    @Override
    public void onSoftWareUpgradeFailed(int code, String msg) {
        ToastUtil.show(UiUtil.getString(R.string.home_update_failed));
        mSACheckUpdateDialog.setUpdateStatus(false);
    }

    @Override
    public void onCurrentVersionSuccess(CurrentVersionBean data) {
        tvVersion.setText(String.format(UiUtil.getString(R.string.home_current_version), data.getVersion()));
    }

    @Override
    public void onCurrentVersionFailed(int code, String msg) {
        ToastUtil.show(msg);
    }
}