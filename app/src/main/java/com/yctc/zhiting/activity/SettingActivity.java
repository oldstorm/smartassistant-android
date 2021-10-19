package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SettingContract;
import com.yctc.zhiting.activity.presenter.SettingPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.event.FinishWebActEvent;
import com.yctc.zhiting.utils.statusbarutil.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置
 */
public class SettingActivity extends MVPBaseActivity<SettingContract.View, SettingPresenter> implements SettingContract.View {

    @BindView(R.id.tvUsername)
    TextView tvUsername;
    @BindView(R.id.tvPassword)
    TextView tvPassword;

    private boolean set; // 是否设置过用户名和密码

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected boolean isSetStateBar() {
        return false;
    }

    @Override
    protected void initUI() {
        super.initUI();
        StatusBarUtil.setStatusBarDarkTheme(this, false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getUserInfo(Constant.CurrentHome.getUser_id());
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatusBarUtil.setStatusBarDarkTheme(this, true);
    }



    @OnClick({R.id.rlUsername, R.id.rlPassword, R.id.llClose})
    public void onClick(View view) {
        if (view.getId() == R.id.rlUsername || view.getId() == R.id.rlPassword) {
           if (!set){
                switchToActivity(SetUPActivity.class);
           }
        } else if (view.getId() == R.id.llClose) {
            EventBus.getDefault().post(new FinishWebActEvent());
            finish();
        }
    }


    @Override
    public void getUserInfoSuccess(MemberDetailBean settingUserInfoBean) {
        if (settingUserInfoBean!=null){
            set = settingUserInfoBean.isIs_set_password();
            if (settingUserInfoBean.isIs_set_password()){
                tvUsername.setText(settingUserInfoBean.getAccount_name());
                tvPassword.setText(getResources().getString(R.string.mine_set));
            }else {
                tvUsername.setText(getResources().getString(R.string.mine_unsetting));
                tvPassword.setText(getResources().getString(R.string.mine_unsetting));
            }
        }
    }

    @Override
    public void getUserInfoFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}