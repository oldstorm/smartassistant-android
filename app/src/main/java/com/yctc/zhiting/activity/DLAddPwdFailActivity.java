package com.yctc.zhiting.activity;


import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DLAddPwdFailContract;
import com.yctc.zhiting.activity.presenter.DLAddPwdFailPresenter;

/**
 * 门锁添密码失败
 */
public class DLAddPwdFailActivity extends MVPBaseActivity<DLAddPwdFailContract.View, DLAddPwdFailPresenter> implements DLAddPwdFailContract.View {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dl_add_pwd_fail;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.home_dl_add_password));
    }
}