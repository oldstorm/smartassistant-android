package com.yctc.zhiting.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseFragment;
import com.google.gson.Gson;
import com.yctc.zhiting.activity.AboutActivity;
import com.yctc.zhiting.activity.AgreementAndPolicyActivity;
import com.yctc.zhiting.activity.CaptureNewActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.CommonWebActivity;
import com.yctc.zhiting.activity.HomeCompanyActivity;
import com.yctc.zhiting.activity.LoginActivity;
import com.yctc.zhiting.activity.SupportBrand2Activity;
import com.yctc.zhiting.activity.UserInfoActivity;
import com.yctc.zhiting.adapter.MineFunctionAdapter;
import com.yctc.zhiting.bean.MineFunctionBean;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.entity.mine.UpdateUserPost;
import com.yctc.zhiting.entity.mine.UserInfoBean;
import com.yctc.zhiting.event.MineUserInfoEvent;
import com.yctc.zhiting.event.NicknameEvent;
import com.yctc.zhiting.event.UpdateProfessionStatusEvent;
import com.yctc.zhiting.event.UpdateSaUserNameEvent;
import com.yctc.zhiting.fragment.contract.MeFragmentContract;
import com.yctc.zhiting.fragment.presenter.MeFragmentPresenter;
import com.yctc.zhiting.listener.IMinView;
import com.yctc.zhiting.utils.AllRequestUtil;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UserUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页-我的
 */
public class MinFragment extends MVPBaseFragment<MeFragmentContract.View, MeFragmentPresenter> implements IMinView,
        MeFragmentContract.View {

    @BindView(R.id.rvFunction)
    RecyclerView rvFunction;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.llLogin)
    LinearLayout llLogin;

    private int userId;
    private DBManager dbManager;
    private WeakReference<Context> mContext;
    private MineFunctionAdapter mineFunctionAdapter;
    private List<MineFunctionBean> functions = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragmemt_min;
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected void initUI() {
        functions.add(new MineFunctionBean(R.drawable.icon_mine_home, getContext().getResources().getString(R.string.mine_home_company), true));
        functions.add(new MineFunctionBean(R.drawable.icon_mine_brand, getContext().getResources().getString(R.string.mine_brand), getIsBindSA()));
        functions.add(new MineFunctionBean(R.drawable.icon_mine_third_party, getContext().getResources().getString(R.string.mine_third_party), true));
        functions.add(new MineFunctionBean(R.drawable.icon_mine_language, getContext().getResources().getString(R.string.mine_language), false));
        functions.add(new MineFunctionBean(R.drawable.icon_mine_professional, UiUtil.getString(R.string.mine_professional), getIsBindSA()));
        functions.add(new MineFunctionBean(R.drawable.icon_about_us, UiUtil.getString(R.string.mine_about_us), true));
        functions.add(new MineFunctionBean(R.drawable.icon_user_agreement, UiUtil.getString(R.string.mine_user_agreement_and_privacy_policy), true));

        mineFunctionAdapter = new MineFunctionAdapter();
        mineFunctionAdapter.setNewData(functions);
        rvFunction.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFunction.setAdapter(mineFunctionAdapter);
        mineFunctionAdapter.setOnItemClickListener((adapter, view, position) -> {
            switch (position) {
                case 0:  // 家庭/公司
                    switchToActivity(HomeCompanyActivity.class);
                    break;
                case 1: // 支持品牌
                    if (getIsBindSA())
                        switchToActivity(SupportBrand2Activity.class);
                    break;
                case 2:
                    Bundle thirdPartyBundle = new Bundle();
                    thirdPartyBundle.putInt(IntentConstant.WEB_URL_TYPE, 1);
                    switchToActivity(CommonWebActivity.class, thirdPartyBundle);
                    break;
                case 3:
                    break;
                case 4:
                    if (getIsBindSA()) {
                        Bundle proBundle = new Bundle();
                        proBundle.putInt(IntentConstant.WEB_URL_TYPE, 0);
                        switchToActivity(CommonWebActivity.class, proBundle);
                    }
                    break;

                case 5:  // 关于我们
//                    Bundle bundle = new Bundle();
//                    bundle.putString(IntentConstant.TITLE, UiUtil.getString(R.string.mine_about_us));
//                    bundle.putString(IntentConstant.URL, Constant.ABOUT_US_URL);
//                    switchToActivity(AboutActivity.class, bundle);
                    switchToActivity(AboutActivity.class);
                    break;

                case 6:  // 用户协议和隐私政策
                    switchToActivity(AgreementAndPolicyActivity.class);
                    break;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mContext = new WeakReference<>(getActivity());
        dbManager = DBManager.getInstance(mContext.get());
        if (UserUtils.isLogin()) {
            setCloudName(true);
        } else {
            getUserInfo();
        }
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        UiUtil.starThread(() -> {
            UserInfoBean userInfoBean = dbManager.getUser();
            if (userInfoBean != null) {
                UiUtil.runInMainThread(() -> {
                    userId = userInfoBean.getUserId();
                    AllRequestUtil.nickName = userInfoBean.getNickname();
                    tvName.setText(userInfoBean.getNickname());
                });
            }
        });
    }

    @OnClick(R.id.llInfo)
    void onClickUser() {
        Intent intent = new Intent(getActivity(), UserInfoActivity.class);
        intent.putExtra(IntentConstant.NICKNAME, tvName.getText().toString());
        intent.putExtra(IntentConstant.ID, userId);
        startActivity(intent);
    }

    /**
     * 登录
     */
    @OnClick(R.id.tvLogin)
    void onClickLogin() {
        switchToActivity(LoginActivity.class);
    }

    /**
     * 扫描二维码
     */
    @OnClick(R.id.ivScan)
    void onClickScan() {
        switchToActivity(CaptureNewActivity.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NicknameEvent event) {
        tvName.setText(event.getNickname());
    }

    /**
     * 登录成功之后操作
     *
     * @param mineUserInfoEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MineUserInfoEvent mineUserInfoEvent) {
        setCloudName(mineUserInfoEvent.isUpdateSAUserName());
        if (CollectionUtil.isNotEmpty(functions)){
            functions.get(4).setEnable(getIsBindSA());
            if (mineFunctionAdapter!=null){
                mineFunctionAdapter.notifyDataSetChanged();
            }
        }
        if (TextUtils.isEmpty(UserUtils.getCloudUserName())) {
            getUserInfo();
        }
        if (mineUserInfoEvent.isUpdateSAUserName()) {
            updateUserInfo();
            updateSaUserName();
        }
    }

    /**
     * 更是专业版点击状态
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateProfessionStatusEvent event) {
        setProfessionStatus();
    }


    /**
     * 修改sa用户昵称
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateSaUserNameEvent event) {
        updateSaUserName();
    }

    /**
     * 修改sa的用户昵称
     */
    private void updateSaUserName(){
        if (HomeUtil.isHomeIdThanZero()) {
            UpdateUserPost updateUserPost = new UpdateUserPost();
            updateUserPost.setNickname(UserUtils.getCloudUserName());
            String body = new Gson().toJson(updateUserPost);
            mPresenter.updateMember(HomeUtil.getUserId(), body);
        }
    }

    /**
     * 修改用户昵称
     */
    private void updateUserInfo() {
        UiUtil.starThread(() -> {
            dbManager.updateUser(1, UserUtils.getCloudUserName());
        });
    }

    /**
     * 云端昵称
     */
    private void setCloudName(boolean resetName) {
        LogUtil.e("setCloudName="+UserUtils.getCloudUserName());
        if (resetName)
        tvName.setText(UserUtils.getCloudUserName());
        llLogin.setVisibility(UserUtils.isLogin() ? View.GONE : View.VISIBLE);
    }

    /**
     * 更新是否可点击状态
     */
    private void setProfessionStatus(){
        if (functions != null && functions.size() > 0) {
            boolean showProfessional = getIsBindSA();
            functions.get(1).setEnable(showProfessional);
            functions.get(4).setEnable( showProfessional);
            mineFunctionAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 选中tab
     */
    @Override
    public void selectTab() {
        setProfessionStatus();
    }

    /**
     * 是否绑定SA
     *
     * @return
     */
    private boolean getIsBindSA() {
        return HomeUtil.isSAEnvironment();
    }

    /**
     * 修改昵称成功
     */
    @Override
    public void updateNameSuccess() {

    }

    /**
     * 修改昵称失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void updateNameFail(int errorCode, String msg) {

    }
}
