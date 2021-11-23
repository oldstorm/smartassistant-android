package com.yctc.zhiting.activity;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.httputil.cookie.PersistentCookieStore;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.UserInfoContract;
import com.yctc.zhiting.activity.presenter.UserInfoPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.dialog.EditBottomDialog;
import com.yctc.zhiting.entity.mine.UpdateUserPost;
import com.yctc.zhiting.event.MineUserInfoEvent;
import com.yctc.zhiting.event.NicknameEvent;
import com.yctc.zhiting.utils.AllRequestUtil;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UserUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 个人资料
 */
public class UserInfoActivity extends MVPBaseActivity<UserInfoContract.View, UserInfoPresenter> implements UserInfoContract.View {

    @BindView(R.id.tvNickname)
    TextView tvNickname;
    @BindView(R.id.tvLogout)
    TextView tvLogout;
    @BindView(R.id.rlPhone)
    RelativeLayout rlPhone;
    @BindView(R.id.tvPhone)
    TextView tvPhone;

    private WeakReference<Context> mContext;
    private DBManager dbManager;

    private int userId;
    private String nickname;

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(this.getResources().getString(R.string.mine_user_info));
    }

    @Override
    protected void initData() {
        super.initData();
        mContext = new WeakReference<>(this);
        dbManager = DBManager.getInstance(mContext.get());
        nickname = getIntent().getStringExtra(IntentConstant.NICKNAME);
        userId = getIntent().getIntExtra(IntentConstant.ID, 0);
        tvLogout.setVisibility(UserUtils.isLogin() ? View.VISIBLE : View.GONE);
        rlPhone.setVisibility(UserUtils.isLogin() ? View.VISIBLE : View.GONE);
        tvPhone.setText(UserUtils.getPhone());
        tvNickname.setText(nickname);
    }

    /**
     * 修改昵称
     */
    @OnClick(R.id.rlNickname)
    void onClickNickname() {
        String title = getResources().getString(R.string.mine_modify_nickname);
        String hint = getResources().getString(R.string.mine_input_nickname);
        String contentStr = tvNickname.getText().toString();

        EditBottomDialog editBottomDialog = EditBottomDialog.newInstance(title, hint, contentStr, 0);
        editBottomDialog.setClickSaveListener(content -> {
            nickname = content;
            UpdateUserPost updateUserPost = new UpdateUserPost();
            updateUserPost.setNickname(content);
            String body = new Gson().toJson(updateUserPost);
            if (UserUtils.isLogin()) {  // 登录的情况
                mPresenter.updateMemberSC(UserUtils.getCloudUserId(), body);  // 更新sa昵称
                if (HomeUtil.isHomeIdThanZero()) {  // 当前家庭有绑sa,sc转sa
                    mPresenter.updateMember(HomeUtil.getUserId(), body);
                }
            } else if (!TextUtils.isEmpty(Constant.CurrentHome.getSa_user_token())) {  // 没登录
                mPresenter.updateMember(Constant.CurrentHome.getUser_id(), body);
            } else {  // 没登录，没sa
                updateUserInfo(content);
            }
        });
        editBottomDialog.show(this);
    }

    /**
     * 退出登录
     */
    @OnClick(R.id.tvLogout)
    void onClickLogout() {
        CenterAlertDialog centerAlertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.login_logout_tips), null);
        centerAlertDialog.setConfirmListener((del) -> {
            mPresenter.logout();
            centerAlertDialog.dismiss();
        });
        centerAlertDialog.show(this);
    }

    /**
     * 修改用户昵称
     */
    private void updateUserInfo(String nickname) {
        UiUtil.starThread(() -> {
            long count = dbManager.updateUser(userId, nickname);
            UiUtil.runInMainThread(() -> {
                updateSuccessful();
            });
        });
    }

    /**
     * 修改成功
     */
    private void updateSuccessful() {
        ToastUtil.show(UiUtil.getString(R.string.mine_save_success));
        EventBus.getDefault().post(new NicknameEvent(nickname));
        AllRequestUtil.nickName = nickname;
        tvNickname.setText(nickname);
    }

    /**
     * 修改用户信息
     */
    @Override
    public void updateSuccess() {
        if (UserUtils.isLogin()) {
//            UserUtils.setCloudUserName(nickname);
//            LogUtil.e("updateSuccess1="+nickname);
//            LogUtil.e("updateSuccess2="+UserUtils.getCloudUserName());
//            updateSuccessful();
        } else {
            updateUserInfo(nickname);
        }
    }

    @Override
    public void updateFail(int errorCode, String msg) {

    }

    @Override
    public void updateScSuccess() {
        UserUtils.setCloudUserName(nickname);
//        LogUtil.e("updateSuccess1="+nickname);
//        LogUtil.e("updateSuccess2="+UserUtils.getCloudUserName());
//        updateSuccessful();
        updateUserInfo(nickname);
    }

    @Override
    public void updateScFail(int errorCode, String msg) {

    }

    /**
     * 退出登录成功
     */
    @Override
    public void logoutSuccess() {
        UserUtils.saveUser(null);
        PersistentCookieStore.getInstance().removeAll();
        EventBus.getDefault().post(new MineUserInfoEvent(false));
        finish();
    }

    /**
     * 请求失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void requestFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}