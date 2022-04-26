package com.yctc.zhiting.activity;

import static com.yctc.zhiting.config.Constant.CurrentHome;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.main.framework.baseutil.AndroidUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SettingContract;
import com.yctc.zhiting.activity.presenter.SettingPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.ModifyPwdDialog;
import com.yctc.zhiting.dialog.UpdateUsernameDialog;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.UpdateUserPost;
import com.yctc.zhiting.event.FinishWebActEvent;
import com.yctc.zhiting.receiver.WifiReceiver;
import com.yctc.zhiting.utils.HomeUtil;
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
    @BindView(R.id.clIP)
    ConstraintLayout clIP;
    @BindView(R.id.tvIP)
    TextView tvIP;

    private boolean set; // 是否设置过用户名和密码

    private UpdateUsernameDialog mUpdateUsernameDialog;
    private String mUpdateName;
    private ModifyPwdDialog modifyPwdDialog;

    /**
     * Wifi 状态接收器
     */
    private final WifiReceiver mWifiReceiver = new WifiReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    if (clIP != null) {
                        clIP.setVisibility(View.GONE);
                    }
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    showIP();
                }
            }
        }
    };

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
        registerWifiReceiver();
        showIP();
    }

    /**
     * Wifi 状态监听注册
     */
    private void registerWifiReceiver() {
        if (mWifiReceiver == null) return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mWifiReceiver, filter);
    }

    /**
     * Wifi 状态监听注销
     */
    public void unRegisterWifiReceiver() {
        if (mWifiReceiver == null) return;
        unregisterReceiver(mWifiReceiver);
    }

    /**
     * 显示ip地址
     */
    private void showIP() {
        if (HomeUtil.isSAEnvironment()) {
            if (clIP.getVisibility() == View.GONE) {
                String ipStr = Constant.CurrentHome.getSa_lan_address();
                if (!TextUtils.isEmpty(ipStr)) {
                    tvIP.setText(ipStr);
                    clIP.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CurrentHome != null)
            mPresenter.getUserInfo(CurrentHome.getUser_id());
    }

    /**
     * 初始化修改用户名弹窗
     */
    private void showUpdateUsernameDialog() {
        if (mUpdateUsernameDialog == null) {
            mUpdateUsernameDialog = new UpdateUsernameDialog();
            mUpdateUsernameDialog.setSaveListener(username -> {
                mUpdateName = username;
                UpdateUserPost updateUserPost = new UpdateUserPost();
                updateUserPost.setAccount_name(username);
                String body = new Gson().toJson(updateUserPost);
                mUpdateUsernameDialog.dismiss();

                if (CurrentHome != null)
                    mPresenter.updateMember(CurrentHome.getUser_id(), body);
            });
        }
        String un = tvUsername.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString("username", un);
        mUpdateUsernameDialog.setArguments(bundle);
        mUpdateUsernameDialog.show(this);
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatusBarUtil.setStatusBarDarkTheme(this, true);
        unRegisterWifiReceiver();
    }

    @OnClick({R.id.rlUsername, R.id.rlPassword, R.id.llClose, R.id.tvCopy})
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.rlUsername) { // 用户名
            if (set) { // 已设置
                showUpdateUsernameDialog(); // 弹窗修改用户名
            } else {  // 未设置，去设置用户名和密码界面
                switchToActivity(SetUPActivity.class);
            }
        } else if (viewId == R.id.rlPassword) {
            if (set) { // 已设置密码，修改密码弹窗
                showModifyPwdDialog();
            } else { // 没设置过密码，去设置用户名和密码界面
                switchToActivity(SetUPActivity.class);
            }
        } else if (viewId == R.id.llClose) { // 关闭
            EventBus.getDefault().post(new FinishWebActEvent());
            finish();
        } else if (viewId == R.id.tvCopy) { // 复制
            String ip = tvIP.getText().toString().trim();
            AndroidUtil.copyText(ip);
        }
    }

    /**
     * 修改密码弹窗
     */
    private void showModifyPwdDialog() {
        if (modifyPwdDialog == null) {
            modifyPwdDialog = new ModifyPwdDialog();
            modifyPwdDialog.setConfirmListener((oldPwd, newPwd) -> {
                UpdateUserPost updateUserPost = new UpdateUserPost();
                updateUserPost.setOld_password(oldPwd);
                updateUserPost.setPassword(newPwd);
                String body = new Gson().toJson(updateUserPost);
                if (CurrentHome != null)
                    mPresenter.updateMember(CurrentHome.getUser_id(), body);
            });
        }
        modifyPwdDialog.show(this);
    }


    /**
     * 用户信息成功
     *
     * @param settingUserInfoBean
     */
    @Override
    public void getUserInfoSuccess(MemberDetailBean settingUserInfoBean) {
        if (settingUserInfoBean != null) {
            set = settingUserInfoBean.isIs_set_password();
            if (settingUserInfoBean.isIs_set_password()) {
                tvUsername.setText(settingUserInfoBean.getAccount_name());
                tvPassword.setText(getResources().getString(R.string.mine_set));
            } else {
                tvUsername.setText(getResources().getString(R.string.mine_unsetting));
                tvPassword.setText(getResources().getString(R.string.mine_unsetting));
            }
        }
    }

    /**
     * 用户信息失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getUserInfoFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 修改成员成功
     */
    @Override
    public void updateMemberSuccess() {
        if (!TextUtils.isEmpty(mUpdateName)) {
            tvUsername.setText(mUpdateName);
            mUpdateName = "";
        }
        if (modifyPwdDialog != null && modifyPwdDialog.isShowing()) {
            modifyPwdDialog.resetText();
            modifyPwdDialog.dismiss();
        }
        ToastUtil.show(UiUtil.getString(R.string.mine_update_success));
    }

    /**
     * 修改成员失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void updateMemberFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}