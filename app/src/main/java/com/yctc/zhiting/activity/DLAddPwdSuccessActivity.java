package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DLAddPwdSuccessContract;
import com.yctc.zhiting.activity.presenter.DLAddPwdSuccessPresenter;
import com.yctc.zhiting.dialog.WithNoTipAlertDialog;

import butterknife.OnClick;

/**
 * 门锁添加密码成功
 */
public class DLAddPwdSuccessActivity extends MVPBaseActivity<DLAddPwdSuccessContract.View, DLAddPwdSuccessPresenter> implements DLAddPwdSuccessContract.View {

    private WithNoTipAlertDialog mWithNoTipAlertDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dladd_pwd_success;
    }

    @Override
    protected void initUI() {
        super.initUI();
        SpUtil.init(this);
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @OnClick({R.id.ivBack, R.id.tvCopy, R.id.tvSave, R.id.tvFinish})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.ivBack: // 返回
                exit();
                break;

            case R.id.tvCopy:  // 复制

                break;


            case R.id.tvSave:  // 保存

                break;

            case R.id.tvFinish: // 完成

                break;
        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        SpUtil.put(SpConstant.ADD_PWD_NOT_TOP_AGAIN, false);
        if (!SpUtil.getBoolean(SpConstant.ADD_PWD_NOT_TOP_AGAIN)) {
            showWithNoTipAlertDialog();
        } else {
            finish();
        }
    }

    /**
     * 退出提示弹窗
     */
    private void showWithNoTipAlertDialog() {
        if (mWithNoTipAlertDialog == null) {
            mWithNoTipAlertDialog = WithNoTipAlertDialog.getInstance(UiUtil.getString(R.string.common_tips), UiUtil.getString(R.string.home_dl_exit_add_pwd_success_tip));
            mWithNoTipAlertDialog.setConfirmListener(new WithNoTipAlertDialog.OnConfirmListener() {
                @Override
                public void onConfirm(boolean notTipAgain) {
                    SpUtil.put(SpConstant.ADD_PWD_NOT_TOP_AGAIN, notTipAgain);
                    finish();
                }
            });
        }
        if (mWithNoTipAlertDialog != null && !mWithNoTipAlertDialog.isShowing()) {
            mWithNoTipAlertDialog.show(this);
        }
    }
}