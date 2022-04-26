package com.yctc.zhiting.fragment;

import static com.yctc.zhiting.config.Constant.CurrentHome;
import static com.yctc.zhiting.config.Constant.wifiInfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.AndroidUtil;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseFragment;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.app.main.framework.imageutil.GlideUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.AboutActivity;
import com.yctc.zhiting.activity.CaptureNewActivity;
import com.yctc.zhiting.activity.CommonWebActivity;
import com.yctc.zhiting.activity.ExperienceCenterActivity;
import com.yctc.zhiting.activity.FeedbackListActivity;
import com.yctc.zhiting.activity.FindSAGuideActivity;
import com.yctc.zhiting.activity.HomeCompanyActivity;
import com.yctc.zhiting.activity.LoginActivity;
import com.yctc.zhiting.activity.MainActivity;
import com.yctc.zhiting.activity.SupportBrand2Activity;
import com.yctc.zhiting.activity.ThirdPartyActivity;
import com.yctc.zhiting.activity.UserInfoActivity;
import com.yctc.zhiting.adapter.MineFunctionAdapter;
import com.yctc.zhiting.bean.MineFunctionBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.LoginBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.UpdateUserPost;
import com.yctc.zhiting.entity.mine.UploadFileBean;
import com.yctc.zhiting.entity.mine.UserInfoBean;
import com.yctc.zhiting.event.DeviceDataEvent;
import com.yctc.zhiting.event.MineUserInfoEvent;
import com.yctc.zhiting.event.NicknameEvent;
import com.yctc.zhiting.event.UpdateHeadImgEvent;
import com.yctc.zhiting.event.UpdateHeadImgSuccessEvent;
import com.yctc.zhiting.event.UpdateProfessionStatusEvent;
import com.yctc.zhiting.event.UpdateSaUserNameEvent;
import com.yctc.zhiting.fragment.contract.MeFragmentContract;
import com.yctc.zhiting.fragment.presenter.MeFragmentPresenter;
import com.yctc.zhiting.listener.IMinView;
import com.yctc.zhiting.utils.AllRequestUtil;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.FileUtils;
import com.yctc.zhiting.utils.GpsUtil;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UserUtils;
import com.yctc.zhiting.widget.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
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
    @BindView(R.id.ciAvatar)
    CircleImageView ciAvatar;
    @BindView(R.id.llLogin)
    LinearLayout llLogin;

    private int userId;
    private int mClickType;//点击条目类型 0支持品牌 1专业版
    private DBManager dbManager;
    private WeakReference<Context> mContext;
    private MineFunctionAdapter mineFunctionAdapter;
    private List<MineFunctionBean> functions = new LinkedList<>();
    private boolean isHasCrm;//是否有crm入口
    private boolean isHasScm;//是否有scm入口

    private String avatarUrl;

    private CenterAlertDialog gpsTipDialog;
    private final int GPS_REQUEST_CODE = 1001;

    @Override
    protected int getLayoutId() {
        return R.layout.fragmemt_min;
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            functions.clear();
            functions.addAll(MineFunctionBean.getData());
            mineFunctionAdapter.setNewData(functions);
            if (UserUtils.isLogin()) {
                if (CurrentHome != null && CurrentHome.getId() > 0) {
                    mPresenter.getExtensions();
                }
            } else {
                if (CurrentHome != null && CurrentHome.getArea_id() > 0 && (HomeUtil.isSAEnvironment() || HomeUtil.isInLAN )) {
                    mPresenter.getExtensions();
                }
            }

            if (UserUtils.isLogin()) {
                mPresenter.getSCUserInfo(UserUtils.getCloudUserId());
            }
        }
    }

    @Override
    protected void initUI() {
        mineFunctionAdapter = new MineFunctionAdapter();
        onHiddenChanged(false);

        rvFunction.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFunction.setAdapter(mineFunctionAdapter);

        mineFunctionAdapter.setOnItemClickListener((adapter, view, position) -> {
            MineFunctionBean mineFunctionBean = mineFunctionAdapter.getItem(position);
            switch (mineFunctionBean) {
                case HOME_COMPANY://家庭/公司
                    switchToActivity(HomeCompanyActivity.class);
                    break;
                case CRM_SYSTEM://crm
                    professionSupportAccess(2);
                    break;

                case SCM_SYSTEM:  // 供应链
                    LogUtil.e("供应链");
                    professionSupportAccess(3);
                    break;

                case SUPPORT_BRAND://支持品牌
                    professionSupportAccess(0);
                    break;

                case THIRD_PARTY://第三方平台
//                    Bundle thirdPartyBundle = new Bundle();
//                    thirdPartyBundle.putInt(IntentConstant.WEB_URL_TYPE, 1);
//                    switchToActivity(CommonWebActivity.class, thirdPartyBundle);
                    switchToActivity(ThirdPartyActivity.class);
                    break;

                case LANGUAGE://语言
                    break;

                case PROFESSIONAL://专业版
                    professionSupportAccess(1);
                    break;

                case EXPERIENCE_CENTER://体验中心
                    switchToActivity(ExperienceCenterActivity.class);
                    break;

                case ABOUT_US://关于我们
                    switchToActivity(AboutActivity.class);
                    break;

                case FEEDBACK: // 问题反馈
                    switchToActivity(FeedbackListActivity.class);
                    break;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( GpsUtil.isEnabled(getContext()) && requestCode == GPS_REQUEST_CODE) {
            switchToActivity(CaptureNewActivity.class);
        }
    }

    private void professionSupportAccess(int type) {
        LogUtil.e("供应链1");
        mClickType = type;
        //如果当前家庭为空，设置默认的家庭
        setDefaultHome();

        if (UserUtils.isLogin()) {
            LogUtil.e("供应链2");
            mPresenter.checkSaToken(CurrentHome.getId());
        } else if (HomeUtil.isSAEnvironment()) {
            LogUtil.e("供应链3");
            getSATokenSuccess(null);
        } else {
            ToastUtil.show(UiUtil.getString(R.string.mine_invite_login_tip));
        }
    }

    private void setDefaultHome() {
        if (CurrentHome != null) return;
        List<HomeCompanyBean> homeList = dbManager.queryHomeCompanyList();
        if (CollectionUtil.isNotEmpty(homeList)) {
            CurrentHome = homeList.get(0);
            if (wifiInfo != null) {
                for (HomeCompanyBean home : homeList) {
                    if (HomeUtil.isBssidEqual(home)) {//当前sa环境
                        CurrentHome = home;
                        break;
                    }
                }
            }
        }
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
                    avatarUrl = userInfoBean.getIconUrl();
                    SpUtil.put(SpConstant.AVATAR, avatarUrl);
                    GlideUtil.load(userInfoBean.getIconUrl()).userHead().into(ciAvatar);
                });
            }
        });
    }

    @OnClick(R.id.llInfo)
    void onClickUser() {
        Intent intent = new Intent(mActivity, UserInfoActivity.class);
        intent.putExtra(IntentConstant.NICKNAME, tvName.getText().toString());
        intent.putExtra(IntentConstant.ID, userId);
        intent.putExtra(IntentConstant.AVATAR_URL, avatarUrl);
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
        checkFineLocationTask(() -> {
            if (AndroidUtil.isGE9() && !GpsUtil.isEnabled(getContext()) && wifiInfo!=null) {
                showGpsTipDialog();
            } else {
                switchToActivity(CaptureNewActivity.class);
            }
        });
    }

    /**
     * 修改用户名
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NicknameEvent event) {
        tvName.setText(event.getNickname());
    }

    /**
     * 去修改头像
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateHeadImgEvent event) {
        File file = DiskLruCacheWrapper.create(Glide.getPhotoCacheDir(getActivity()), 250 * 1024 * 1024).get(new GlideUrl(avatarUrl));
        if (file != null && file.isFile()) {
            String path = file.getAbsolutePath();
            String hash = "";
            byte[] fileData = FileUtils.hashV2(path);
            if (fileData != null) {
                hash = FileUtils.toHex(fileData);
                if (!TextUtils.isEmpty(hash)) {
                    List<NameValuePair> uploadSAData = new ArrayList<>();
                    uploadSAData.add(new NameValuePair(Constant.FILE_UPLOAD, path, true));
                    uploadSAData.add(new NameValuePair(Constant.FILE_HASH, hash));
                    uploadSAData.add(new NameValuePair(Constant.FILE_TYPE, Constant.IMG));
                    mPresenter.uploadAvatar(uploadSAData);
                }
            }
        }
    }

    /**
     * 修改头像成功
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateHeadImgSuccessEvent event) {
        avatarUrl = event.getHeadImgUrl();
        SpUtil.put(SpConstant.AVATAR, avatarUrl);
        GlideUtil.load(event.getHeadImgUrl()).userHead().into(ciAvatar);
    }

    /**
     * 登录成功之后操作
     *
     * @param mineUserInfoEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MineUserInfoEvent mineUserInfoEvent) {
        setCloudName(mineUserInfoEvent.isUpdateSAUserName());
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
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateProfessionStatusEvent event) {
        setProfessionStatus();
    }

    /**
     * 修改sa用户昵称
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateSaUserNameEvent event) {
        updateSaUserName();
    }

    /**
     * 修改sa的用户昵称
     */
    private void updateSaUserName() {
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
            dbManager.updateUser(1, UserUtils.getCloudUserName(), UserUtils.getCloudHeadImage());
        });
    }

    /**
     * 云端昵称
     */
    private void setCloudName(boolean resetName) {
        if (resetName) {
            tvName.setText(UserUtils.getCloudUserName());
            avatarUrl = UserUtils.getCloudHeadImage();
            SpUtil.put(SpConstant.AVATAR, avatarUrl);
            GlideUtil.load(UserUtils.getCloudHeadImage()).userHead().into(ciAvatar);
        }
        llLogin.setVisibility(UserUtils.isLogin() ? View.GONE : View.VISIBLE);
    }

    /**
     * 选中tab
     */
    @Override
    public void selectTab() {
    }

    /**
     * 修改昵称成功
     */
    @Override
    public void updateNameSuccess() {
    }

    /**
     * 修改昵称失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void updateNameFail(int errorCode, String msg) {
    }

    @Override
    public void getSATokenSuccess(FindSATokenBean findSATokenBean) {
        if (findSATokenBean != null) {
            String saToken = findSATokenBean.getSa_token();
            if (!TextUtils.isEmpty(saToken)) {
                HomeUtil.tokenIsInvalid = false;
                CurrentHome.setSa_user_token(saToken);
                HttpConfig.addHeader(CurrentHome.getSa_user_token());
                UiUtil.starThread(() -> dbManager.updateSATokenByLocalId(CurrentHome.getLocalId(), saToken));
            }
        }

        if (mClickType == 0) {
            switchToActivity(SupportBrand2Activity.class);
        } else if (mClickType == 1) {
            Bundle proBundle = new Bundle();
            proBundle.putInt(IntentConstant.WEB_URL_TYPE, 0);
            switchToActivity(CommonWebActivity.class, proBundle);
        } else if (mClickType == 2) {
            Bundle crmSystemBundle = new Bundle();
            crmSystemBundle.putInt(IntentConstant.WEB_URL_TYPE, 2);
            switchToActivity(CommonWebActivity.class, crmSystemBundle);
        } else if (mClickType == 3) {
            Bundle supplyChainBundle = new Bundle();
            supplyChainBundle.putInt(IntentConstant.WEB_URL_TYPE, 3);
            switchToActivity(CommonWebActivity.class, supplyChainBundle);
        }
    }

    @Override
    public void getSATokenFail(int errorCode, String msg) {
        if (errorCode == 2011 || errorCode == 2010) {//凭证获取失败，状态码2011，无权限
            HomeUtil.tokenIsInvalid = true;
            EventBus.getDefault().post(new DeviceDataEvent(null));
        } else if (2008 == errorCode) {
            ToastUtil.show(UiUtil.getString(R.string.mine_login_tip));
        }

        if (errorCode == 2011) {
            String title = UiUtil.getString(R.string.common_tips);
            String tip = UiUtil.getString(R.string.invalid_token_find_back);
            String cancelStr = UiUtil.getString(R.string.common_cancel);
            String confirmStr = UiUtil.getString(R.string.how_to_find);

            CenterAlertDialog dialog = CenterAlertDialog.newInstance(title, tip, cancelStr, confirmStr);
            dialog.setConfirmListener(del -> {
                switchToActivity(FindSAGuideActivity.class);
                dialog.dismiss();
            });
            dialog.show(this);
        }
    }

    @Override
    public void getExtensionsSuccess(List<String> list) {
        isHasCrm = false;
        isHasScm = false;
        if (CollectionUtil.isNotEmpty(list)) {
            for (String name : list) {
                if (name.equalsIgnoreCase("crm")) {
                    isHasCrm = true;
                    setProfessionStatus();
                } else if (name.equalsIgnoreCase("scm")) {
                    isHasScm = true;
                    setProfessionStatus();
                }
            }
        }
    }

    @Override
    public void getExtensionsFail(int errorCode, String msg) {
    }

    @Override
    public void checkTokenSuccess(HomeCompanyBean homeCompanyBean) {
        if (mClickType == 0) {
            switchToActivity(SupportBrand2Activity.class);
        } else if (mClickType == 1) {
            Bundle proBundle = new Bundle();
            proBundle.putInt(IntentConstant.WEB_URL_TYPE, 0);
            switchToActivity(CommonWebActivity.class, proBundle);
        } else if (mClickType == 2) {
            Bundle crmSystemBundle = new Bundle();
            crmSystemBundle.putInt(IntentConstant.WEB_URL_TYPE, 2);
            switchToActivity(CommonWebActivity.class, crmSystemBundle);
        } else if (mClickType == 3) {
            Bundle supplyChainBundle = new Bundle();
            supplyChainBundle.putInt(IntentConstant.WEB_URL_TYPE, 3);
            switchToActivity(CommonWebActivity.class, supplyChainBundle);
        }
    }

    @Override
    public void checkTokenFail(int errorCode, String msg) {
        getSAToken();
    }

    /**
     * 上传头像成功
     *
     * @param uploadFileBean
     */
    @Override
    public void uploadAvatarSuccess(UploadFileBean uploadFileBean) {
        if (uploadFileBean != null) {
            UpdateUserPost updateUserPost = new UpdateUserPost();
            updateUserPost.setAvatar_id(uploadFileBean.getFile_id());
            updateUserPost.setAvatar_url(uploadFileBean.getFile_url());
            String body = new Gson().toJson(updateUserPost);
            mPresenter.updateMember(HomeUtil.getUserId(), body);
        }
    }

    /**
     * 上传头像失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void uploadAvatarFail(int errorCode, String msg) {
        LogUtil.e("上传头像失败============" + msg);
    }

    /**
     * 获取用户信息成功
     *
     * @param loginBean
     */
    @Override
    public void getSCUserInfoSuccess(LoginBean loginBean) {
        if (loginBean != null) {
            MemberDetailBean userInfo = loginBean.getUser_info();
            if (userInfo != null) {
                String username = userInfo.getNickname();
                String headImgUrl = userInfo.getAvatar_url();
                String originalName = tvName.getText().toString();
                tvName.setText(username);
                if (TextUtils.isEmpty(headImgUrl)) headImgUrl = "";
                if (TextUtils.isEmpty(avatarUrl)) avatarUrl = "";

                if (!headImgUrl.equals(avatarUrl)) {
                    avatarUrl = headImgUrl;
                    SpUtil.put(SpConstant.AVATAR, avatarUrl);
                    Glide.with(this).load(avatarUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    ciAvatar.setImageDrawable(resource);
                                }
                            });
                }
                if (!username.equals(originalName) || !headImgUrl.equals(avatarUrl)) {
                    dbManager.updateUser(1, username, headImgUrl);
                }
            }
        }
    }

    /**
     * 获取用户信息失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getSCUserInfoFail(int errorCode, String msg) {

    }

    private void getSAToken() {
        NameValuePair nameValuePair = new NameValuePair("area_id", String.valueOf(CurrentHome.getId()));
        List<NameValuePair> requestData = new ArrayList<>();
        requestData.add(nameValuePair);
        mPresenter.getSAToken(CurrentHome.getCloud_user_id(), requestData);//sc的用户id, sc上的家庭id
    }

    /**
     * 更新是否可点击状态
     */
    private void setProfessionStatus() {
        if (CollectionUtil.isNotEmpty(functions)) { // 数据不为空
            if (isHasCrm) {  // 如果是公司且绑了sa（是否需要crm系统入口）
                if (!functions.contains(MineFunctionBean.CRM_SYSTEM)) {  // 如果不包含crm入口
                    functions.add(1, MineFunctionBean.CRM_SYSTEM);
                }
            } else {
                if (functions.contains(MineFunctionBean.CRM_SYSTEM)) { // 如果包含crm入口
                    functions.remove(MineFunctionBean.CRM_SYSTEM);
                }
            }
            if (isHasScm) {
                if (!functions.contains(MineFunctionBean.SCM_SYSTEM)) {
                    functions.add(2, MineFunctionBean.SCM_SYSTEM);
                }
            } else {
                if (functions.contains(MineFunctionBean.SCM_SYSTEM)) {
                    functions.remove(MineFunctionBean.SCM_SYSTEM);
                }
            }
            if (UserUtils.isLogin()) { // 如果登录了
                if (!functions.contains(MineFunctionBean.FEEDBACK)) {  // 如果不包含反馈，则添加进去
                    functions.add(functions.size()-1, MineFunctionBean.FEEDBACK);
                }
            } else {  // 没有登录
                if (functions.contains(MineFunctionBean.FEEDBACK)) { // 如果包含反馈，则移除
                    functions.remove(MineFunctionBean.FEEDBACK);
                }
            }
            mineFunctionAdapter.notifyDataSetChanged();
        }
    }

    /**
     * gps弹窗
     */
    public void showGpsTipDialog() {
        if (gpsTipDialog == null) {
            String title = UiUtil.getString(R.string.common_tips);
            String tip = UiUtil.getString(R.string.main_need_open_location);
            String cancelStr = UiUtil.getString(R.string.cancel);
            String confirmStr = UiUtil.getString(R.string.confirm);
            gpsTipDialog = CenterAlertDialog.newInstance(title, tip, cancelStr, confirmStr);
            gpsTipDialog.setConfirmListener(del -> {
                Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(gpsIntent, GPS_REQUEST_CODE);
                gpsTipDialog.dismiss();
            });

            gpsTipDialog.setCancelListener(new CenterAlertDialog.OnCancelListener() {
                @Override
                public void onCancel() {
                    switchToActivity(CaptureNewActivity.class);
                }
            });
        }
        if (!gpsTipDialog.isShowing()) {
            gpsTipDialog.show(this);
        }
    }
}
