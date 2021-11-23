package com.yctc.zhiting.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.app.main.framework.httputil.cookie.PersistentCookieStore;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.AuthorizeContract;
import com.yctc.zhiting.activity.presenter.AuthorizePresenter;
import com.yctc.zhiting.adapter.ScopesAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.dialog.HomeSelectDialog;
import com.yctc.zhiting.entity.AuthBackBean;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.ScopeTokenBean;
import com.yctc.zhiting.entity.ScopesBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.UserInfoBean;
import com.yctc.zhiting.event.DeviceDataEvent;
import com.yctc.zhiting.request.ScopeTokenRequest;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UserUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.HttpUrl;

import static com.yctc.zhiting.config.Constant.CurrentHome;

/**
 * 授权界面
 */
public class AuthorizeActivity extends MVPBaseActivity<AuthorizeContract.View, AuthorizePresenter> implements AuthorizeContract.View {

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvJoin)
    TextView tvJoin;
    @BindView(R.id.rvScopes)
    RecyclerView rvScopes;
    @BindView(R.id.tvTips)
    TextView tvTips;
    @BindView(R.id.tvHome)
    TextView tvHome;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;

    /**
     * 第三方应用需要的权限
     */
    private String needPermissions;
    /**
     * 第三方app的名称
     */
    private String appName;

    private ScopesAdapter scopesAdapter;

    private Handler mainThreadHandler;
    private DBManager dbManager;
    private WeakReference<Context> mContext;
    private String userName;  // 用户名称
    private String[] permissions; // 同意授权的信息

    private List<HomeCompanyBean> mHomeList;  // 家庭列表
    private HomeSelectDialog mHomeSelectDialog; // 家庭列表选择弹窗
    private HomeCompanyBean nowHome;

    private CenterAlertDialog mCenterAlertDialog;  // 请找回凭证提示弹窗
    private HomeCompanyBean mTempHomeBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_authorize;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        needPermissions = intent.getStringExtra(IntentConstant.NEED_PERMISSION);
        appName = intent.getStringExtra(IntentConstant.APP_NAME);
        tvJoin.setText(UiUtil.getString(R.string.main_welcome_join) + appName);
        mHomeList = (List<HomeCompanyBean>) intent.getSerializableExtra(IntentConstant.BEAN_LIST);
        initSelectDialog();

    }

    @Override
    protected void initUI() {
        super.initUI();
        SpUtil.init(this);
        setConfirmEnabled(false);
        initCenterAlertDialog();
        rvScopes.setLayoutManager(new LinearLayoutManager(this));
        scopesAdapter = new ScopesAdapter();
        rvScopes.setAdapter(scopesAdapter);
    }

    private void setConfirmEnabled(boolean enabled){
        tvConfirm.setEnabled(enabled);
        tvConfirm.setAlpha(enabled ? 1f : 0.5f);
    }

    @Override
    protected void initData() {
        super.initData();
        mContext = new WeakReference<>(getActivity());
        dbManager = DBManager.getInstance(mContext.get());
        mainThreadHandler = new Handler(Looper.getMainLooper());
        nowHome = Constant.CurrentHome;
        tvHome.setText(nowHome.getName());
        String homeBeanJson = GsonConverter.getGson().toJson(CurrentHome);
        mTempHomeBean = GsonConverter.getGson().fromJson(homeBeanJson, HomeCompanyBean.class);
        getUserInfo();
        permissions = needPermissions.split(",");
        // 同意授权的信息数据
//        List<ScopesBean.ScopeBean> data = new ArrayList<>();
//        if (permissions.length > 0) {
//            for (int i = 0; i < permissions.length; i++) {
//                if (permissions[i].equals(Constant.USER)) {
//                    data.add(new ScopesBean.ScopeBean(Constant.USER, getResources().getString(R.string.main_get_login_status)));
//                } else if (permissions[i].equals(Constant.AREA)) {
//                    data.add(new ScopesBean.ScopeBean(Constant.AREA, getResources().getString(R.string.main_get_family_info)));
//                }
//            }
//        }
//        scopesAdapter.setNewData(data);
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    /**
     * 家庭列表选择弹窗
     */
    private void initSelectDialog(){
        mHomeSelectDialog = new HomeSelectDialog(mHomeList);
        mHomeSelectDialog.setClickItemListener(new HomeSelectDialog.OnClickItemListener() {
            @Override
            public void onItem(HomeCompanyBean homeCompanyBean) {
                setConfirmEnabled(false);
                scopesAdapter.setNewData(null);
                if (!homeCompanyBean.isIs_bind_sa()){
                    ToastUtil.show(UiUtil.getString(R.string.family_without_intelligent_center));
                    return;
                }
                tvHome.setText(homeCompanyBean.getName());
                nowHome = homeCompanyBean;
                CurrentHome = nowHome;
                HttpConfig.clearHeader();
                HttpConfig.addHeader(nowHome.getSa_user_token());
                HttpConfig.addAreaIdHeader(HttpConfig.AREA_ID, String.valueOf(nowHome.getId()));
                SpUtil.put(SpConstant.SA_TOKEN, nowHome.getSa_user_token());
                SpUtil.put(SpConstant.IS_BIND_SA, nowHome.isIs_bind_sa());

                HttpConfig.addAreaIdHeader(HttpConfig.AREA_ID, String.valueOf(nowHome.getArea_id()));
                HttpConfig.addAreaIdHeader(HttpConfig.TOKEN_KEY, nowHome.getSa_user_token());
                if (HomeUtil.isSAEnvironment(nowHome) || UserUtils.isLogin()) {  // 在sa环境或登录的情况下
                    mPresenter.getScopeList();
                }
                mHomeSelectDialog.dismiss();
            }
        });
    }

    /**
     * 提示找回凭证弹窗
     */
    private void initCenterAlertDialog(){
        mCenterAlertDialog = CenterAlertDialog.newInstance(UiUtil.getString(R.string.common_tips), UiUtil.getString(R.string.invalid_token_please_find_back),
                UiUtil.getString(R.string.common_cancel), UiUtil.getString(R.string.how_to_find));
        mCenterAlertDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
            @Override
            public void onConfirm(boolean del) {
                switchToActivity(FindSAGuideActivity.class);
                mCenterAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void finish() {
        CurrentHome = mTempHomeBean;
        SpUtil.put(SpConstant.SA_TOKEN, mTempHomeBean.getSa_user_token());
        HttpConfig.addHeader(CurrentHome.getSa_user_token());
        HttpConfig.addAreaIdHeader(HttpConfig.AREA_ID, String.valueOf(HomeUtil.getHomeId()));
        super.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switchToActivity(MainActivity.class);
    }

    /**
     * 确认
     */
    @OnClick(R.id.tvConfirm)
    void onClickConfirm() {
        if (!nowHome.isIs_bind_sa()) {
            ToastUtil.show(UiUtil.getString(R.string.main_home_is_not_bind_with_sa));
        } else {
            if (!HomeUtil.isSAEnvironment(nowHome) && !UserUtils.isLogin()){
                ToastUtil.show(getResources().getString(R.string.main_auth_failed));;
                return;
            }
            List<String> scopes = new ArrayList<>();
            for (String permission : permissions) {
                scopes.add(permission);
            }
            ScopeTokenRequest scopeTokenRequest = new ScopeTokenRequest(scopes);
            // 获取token接口
            mPresenter.getScopeToken(scopeTokenRequest.toString());
        }
    }

    /**
     * 选择家庭
     */
    @OnClick(R.id.tvHome)
    void onClickHome(){
        if (mHomeSelectDialog!=null && !mHomeSelectDialog.isShowing()){
            mHomeSelectDialog.show(this);
        }
    }

    /**
     * 取消
     */
    @OnClick(R.id.tvCancel)
    void onClickCancel(){
        finish();
    }

    /**
     * 获取 SCOPE 列表成功
     *
     * @param scopesBean
     */
    @Override
    public void getScopesSuccess(ScopesBean scopesBean) {
        if (scopesBean != null && CollectionUtil.isNotEmpty(scopesBean.getScopes())) {
            List<ScopesBean.ScopeBean> data = new ArrayList<>();
            if (!TextUtils.isEmpty(needPermissions)) {
                for (ScopesBean.ScopeBean scopeBean : scopesBean.getScopes()) {
                    for (String permission : permissions) {
                        if (scopeBean.getName().equals(permission)) {
                            data.add(scopeBean);
                        }
                    }
                }

            }
            scopesAdapter.setNewData(data);
            setConfirmEnabled(true);
        }
    }

    @Override
    public void getScopesFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 获取 SCOPE Token成功
     *
     * @param scopeTokenBean
     */
    @Override
    public void getScopeTokenSuccess(ScopeTokenBean scopeTokenBean) {
        if (scopeTokenBean != null) {
            ScopeTokenBean.STBean stBean = scopeTokenBean.getScope_token();
            if (stBean != null) {
                Intent intent = new Intent();
                nowHome.setSc_lan_address(HttpUrlConfig.baseSCUrl);
                nowHome.setSAEnvironment(HomeUtil.isSAEnvironment(nowHome));
                AuthBackBean authBackBean = new AuthBackBean(nowHome.getUser_id(), userName, nowHome, stBean);
                if (UserUtils.isLogin()) {
                    authBackBean.setCookies(PersistentCookieStore.getInstance().get(HttpUrl.parse(HttpUrlConfig.getLogin())));
                } else {
                    authBackBean.setCookies(new ArrayList<>());
                }
                intent.setAction("zt.com.yctc.zhiting.sign");
                intent.putExtra("backInfo", authBackBean.toString());
                sendBroadcast(intent);
                LibLoader.finishAllActivity();
            } else {
                ToastUtil.show(UiUtil.getString(R.string.main_login_fail));
            }
        } else {
            ToastUtil.show(UiUtil.getString(R.string.main_login_fail));
        }
    }

    @Override
    public void getScopeTokenFail(int errorCode, String msg) {
        if (errorCode == 5012 && nowHome.getId()>0){
            if (UserUtils.isLogin()){
                NameValuePair nameValuePair = new NameValuePair("area_id", String.valueOf(nowHome.getId()));
                List<NameValuePair> requestData = new ArrayList<>();
                requestData.add(nameValuePair);
                mPresenter.getSAToken(nowHome.getCloud_user_id(), requestData);  // sc的用户id, sc上的家庭id
            }else {
                ToastUtil.show(UiUtil.getString(R.string.exit_family_please_select_again));
            }
        }else {
            ToastUtil.show(msg);
        }
    }

    /**
     * 找回凭证成功
     * @param findSATokenBean
     */
    @Override
    public void getSATokenSuccess(FindSATokenBean findSATokenBean) {
        if (findSATokenBean != null) {
            String saToken = findSATokenBean.getSa_token();
            if (!TextUtils.isEmpty(saToken)) {
                nowHome.setSa_user_token(saToken);
                HttpConfig.addHeader(nowHome.getSa_user_token());
                mPresenter.getScopeList();
                UiUtil.starThread(() -> dbManager.updateSATokenByLocalId(nowHome.getLocalId(), saToken));
            }
        }
    }

    /**
     * 找回凭证失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getSATokenFail(int errorCode, String msg) {
        if (errorCode == 2011 || errorCode == 2010) {    //凭证获取失败，状态码2011，无权限
            if (mCenterAlertDialog!=null && !mCenterAlertDialog.isShowing()){
                mCenterAlertDialog.show(this);
            }
        } else if (errorCode == 3002) {  //状态码3002，提示被管理员移除家庭
            ToastUtil.show(UiUtil.getString(R.string.exit_family_please_select_again));
        } else {
            ToastUtil.show(msg);
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
                    userName = userInfoBean.getNickname();
                    String nickName = userInfoBean.getNickname();
                    String name = nickName.substring(0, 1) + "*****" + userInfoBean.getNickname().substring(nickName.length());
                    tvName.setText(name);
                    tvTips.setText(String.format(UiUtil.getString(R.string.main_agree), name));
                    if (!nowHome.isIs_bind_sa()){
                        ToastUtil.show(UiUtil.getString(R.string.family_without_intelligent_center));
                        return;
                    }
                    if (!TextUtils.isEmpty(Constant.CurrentHome.getSa_user_token())) { // 有satoken
                        if (HomeUtil.isSAEnvironment() || UserUtils.isLogin()) {  // 在sa环境或登录的情况下
                            HttpConfig.addHeader(nowHome.getSa_user_token());
                            mPresenter.getScopeList();
                        }
                    }
                });
            }
        });
    }
}