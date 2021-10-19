package com.yctc.zhiting.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseFragment;
import com.google.gson.Gson;
import com.yctc.zhiting.activity.CaptureNewActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.CommonWebActivity;
import com.yctc.zhiting.activity.HomeCompanyActivity;
import com.yctc.zhiting.activity.LoginActivity;
import com.yctc.zhiting.activity.SupportBrandActivity;
import com.yctc.zhiting.activity.UserInfoActivity;
import com.yctc.zhiting.adapter.MineFunctionAdapter;
import com.yctc.zhiting.bean.MineFunctionBean;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.db.DBThread;
import com.yctc.zhiting.entity.mine.UpdateUserPost;
import com.yctc.zhiting.entity.mine.UserInfoBean;
import com.yctc.zhiting.event.MineUserInfoEvent;
import com.yctc.zhiting.event.NicknameEvent;
import com.yctc.zhiting.event.UpdateProfessionStatusEvent;
import com.yctc.zhiting.event.UpdateSaUserNameEvent;
import com.yctc.zhiting.fragment.contract.MeFragmentContract;
import com.yctc.zhiting.fragment.presenter.MeFragmentPresenter;
import com.yctc.zhiting.listener.IMinView;
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
    private Handler mainThreadHandler;
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
        functions.add(new MineFunctionBean(R.drawable.icon_mine_third_party, getContext().getResources().getString(R.string.mine_third_party), false));
        functions.add(new MineFunctionBean(R.drawable.icon_mine_language, getContext().getResources().getString(R.string.mine_language), false));
        functions.add(new MineFunctionBean(R.drawable.icon_mine_professional, UiUtil.getString(R.string.mine_professional), getIsBindSA()));

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
                        switchToActivity(SupportBrandActivity.class);
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    if (getIsBindSA())
                        switchToActivity(CommonWebActivity.class);
                    break;
            }
        });
    }


    @Override
    protected void initData() {
        super.initData();
        mContext = new WeakReference<>(getActivity());
        dbManager = DBManager.getInstance(mContext.get());
        mainThreadHandler = new Handler(Looper.getMainLooper());
        if (UserUtils.isLogin()) {
            setCloudName();
        } else {
            getUserInfo();
        }
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        new DBThread(() -> {
            UserInfoBean userInfoBean = dbManager.getUser();
            if (userInfoBean != null) {
                mainThreadHandler.post(() -> {
                    tvName.setText(userInfoBean.getNickname());
                    userId = userInfoBean.getUserId();
                });
            }
        }).start();
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
        //switchToActivity(ScanActivity.class);
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
        setCloudName();
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
        if (HomeUtil.isBindSA()) {
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
            long count = dbManager.updateUser(1, UserUtils.getCloudUserName());
        });
    }

    /**
     * 云端昵称
     */
    private void setCloudName() {
        LogUtil.e("setCloudName="+UserUtils.getCloudUserName());
        tvName.setText(UserUtils.getCloudUserName());
        llLogin.setVisibility(UserUtils.isLogin() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
//        boolean isBindSa = false;
//        if (WSocketManager.isConnecting && Constant.CurrentHome != null && !TextUtils.isEmpty(Constant.CurrentHome.getSa_user_token())) {
//            isBindSa = true;
//        }
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
