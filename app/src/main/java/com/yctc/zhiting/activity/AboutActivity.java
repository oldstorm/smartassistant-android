package com.yctc.zhiting.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.app.main.framework.baseutil.AndroidUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.updateapp.AppUpdateHelper;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.AboutContract;
import com.yctc.zhiting.activity.presenter.AboutPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.AppUpdateDialog;
import com.yctc.zhiting.entity.mine.AndroidAppVersionBean;
import com.yctc.zhiting.utils.IntentConstant;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 关于
 */
public class AboutActivity extends MVPBaseActivity<AboutContract.View, AboutPresenter> implements AboutContract.View {

    @BindView(R.id.tvVersion)
    TextView tvVersion;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.about));
        tvVersion.setText(UiUtil.getString(R.string.version_code) + AndroidUtil.getAppVersion());
    }

    @OnClick({R.id.tvAgreement, R.id.tvPolicy, R.id.tvUpdateApp})
    void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.tvAgreement) {
            Bundle bundle = new Bundle();
            bundle.putString(IntentConstant.TITLE, UiUtil.getString(R.string.user_agreement));
            bundle.putString(IntentConstant.URL, Constant.AGREEMENT_URL);
            switchToActivity(NormalWebActivity.class, bundle);
        } else if (viewId == R.id.tvPolicy) {
            Bundle bundle = new Bundle();
            bundle.putString(IntentConstant.TITLE, UiUtil.getString(R.string.privacy_policy));
            bundle.putString(IntentConstant.URL, Constant.POLICY_URL);
            switchToActivity(NormalWebActivity.class, bundle);
        } else if (viewId == R.id.tvUpdateApp) {
            mPresenter.checkAppVersionInfo();
        }
    }

    AppUpdateDialog mUpdateDialog;

    @Override
    public void getAppVersionInfoSuccess(AndroidAppVersionBean appVersionBean) {
        if (appVersionBean != null && appVersionBean.getUpdate_type() == Constant.UpdateType.NONE) {
            ToastUtil.show(UiUtil.getString(R.string.app_latest_version));
        } else {
            mUpdateDialog = AppUpdateDialog.newInstance(appVersionBean);
            mUpdateDialog.setUpdateListener(new AppUpdateDialog.OnUpdateListener() {
                @Override
                public void onUpdate() {
                    checkPermission(appVersionBean.getLink());
                }

                @Override
                public void onCancel() {

                }
            });
            mUpdateDialog.show(this);
        }
    }

    /**
     * 检测权限
     */
    private void checkPermission(String apkUrl) {
        updateApk(apkUrl);
//        checkInstallApkTask(new OnPermissionListener() {
//            @Override
//            public void onSuccess() {
//                AboutActivity.this.updateApk(apkUrl);
//            }
//        });
    }

    /**
     * 下载并安装apk
     */
    private void updateApk(String apkUrl) {
        //apkUrl = "https://baicaiyouxuan.oss-cn-shenzhen.aliyuncs.com/baicaiyouxuan.apk";
        if (!TextUtils.isEmpty(apkUrl))
            apkUrl = apkUrl.substring(0, apkUrl.indexOf(".apk") + 4);
        AppUpdateHelper helper = new AppUpdateHelper(AboutActivity.this, apkUrl);
        helper.setonDownLoadListener(new AppUpdateHelper.onDownLoadListener() {
            @Override
            public void pending() {
                ToastUtil.show(UiUtil.getString(R.string.app_update_now));
            }

            @Override
            public void progress(int currentBytes, int totalBytes) {
                if (mUpdateDialog != null) {
                    int progress = (int) ((currentBytes / (totalBytes * 1.0)) * 100);
                    mUpdateDialog.setProgress("下载中" + progress + "%...");
                }
            }

            @Override
            public void completed(String apkPath) {
                AndroidUtil.installApk(AboutActivity.this, apkPath);
            }

            @Override
            public void error() {

            }
        });
        helper.start();
    }

    @Override
    public void getAppVersionInfoFailed(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}