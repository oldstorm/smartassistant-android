package com.yctc.zhiting.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.NetworkUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.TempChannelUtil;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.app.main.framework.httputil.cookie.PersistentCookieStore;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.AuthorizeContract;
import com.yctc.zhiting.activity.presenter.AuthorizePresenter;
import com.yctc.zhiting.adapter.AuthHCAdapter;
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
import com.yctc.zhiting.request.ScopeTokenRequest;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UserUtils;


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
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.rvHC)
    RecyclerView rvHC;
    @BindView(R.id.llConfirm)
    LinearLayout llConfirm;
    @BindView(R.id.rbConfirm)
    ProgressBar rbConfirm;

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
    private HomeCompanyBean nowHome;

    private CenterAlertDialog mCenterAlertDialog;  // 请找回凭证提示弹窗
    private HomeCompanyBean mTempHomeBean;

    private AuthHCAdapter mAuthHCAdapter; // 家庭列表
    private int index = 0;  // 绑了SA的数据索引
    private List<HomeCompanyBean> selectedData = new ArrayList<>();  // 绑了SA的数据
    private List<AuthBackBean> authDataList = new ArrayList<>();
    private boolean allLoadAuth;

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
//        initSelectDialog();

    }

    @Override
    protected void initUI() {
        super.initUI();
        SpUtil.init(this);
        initCenterAlertDialog();
        setConfirmEnabled(false, false);
        rvScopes.setLayoutManager(new LinearLayoutManager(this));
        scopesAdapter = new ScopesAdapter();
        rvScopes.setAdapter(scopesAdapter);
        initRvHC();
    }

    /**
     * 初始化家庭列表
     */
    private void initRvHC() {
        mAuthHCAdapter = new AuthHCAdapter();
        rvHC.setLayoutManager(new LinearLayoutManager(this));
        rvHC.setAdapter(mAuthHCAdapter);
        mAuthHCAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int viewId = view.getId();
                HomeCompanyBean homeCompanyBean = mAuthHCAdapter.getItem(position);
                if (viewId == R.id.tvRetry) {
                    if (allLoadAuth) {
                        getScopeToken(homeCompanyBean);
                    }
                }
            }
        });
    }

    private void setConfirmEnabled(boolean enabled, boolean showLoading) {
        if (showLoading) {
            rbConfirm.setVisibility(enabled ? View.GONE : View.VISIBLE);
        }
        llConfirm.setEnabled(enabled);
        llConfirm.setAlpha(enabled ? 1f : 0.5f);
    }

    @Override
    protected void initData() {
        super.initData();
        mContext = new WeakReference<>(getActivity());
        dbManager = DBManager.getInstance(mContext.get());
        mainThreadHandler = new Handler(Looper.getMainLooper());
        nowHome = Constant.CurrentHome;
        String homeBeanJson = GsonConverter.getGson().toJson(CurrentHome);
        mTempHomeBean = GsonConverter.getGson().fromJson(homeBeanJson, HomeCompanyBean.class);
        if (!TextUtils.isEmpty(nowHome.getSa_lan_address())) {
            HttpUrlConfig.baseSAUrl = nowHome.getSa_lan_address();
            HttpUrlConfig.apiSAUrl = HttpUrlConfig.baseSAUrl + HttpUrlConfig.API;
        }
        getUserInfo();
        getHCData();
        permissions = needPermissions.split(",");
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }


    /**
     * 提示找回凭证弹窗
     */
    private void initCenterAlertDialog() {
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
    @OnClick(R.id.llConfirm)
    void onClickConfirm() {
        Intent intent = new Intent();
        nowHome.setSc_lan_address(HttpUrlConfig.baseSCUrl);
        nowHome.setSAEnvironment(HomeUtil.isBssidEqual(nowHome));
        intent.setAction("zt.com.yctc.zhiting.sign");
        String authInfo = new Gson().toJson(authDataList);
        String userJson = SpUtil.get(Constant.CLOUD_USER);
        intent.putExtra("backInfo", authInfo);
        intent.putExtra("userInfo", userJson);
        sendBroadcast(intent);
        LibLoader.finishAllActivity();
    }

    /**
     * 取消
     */
    @OnClick(R.id.tvCancel)
    void onClickCancel() {
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
            List<String> scopes = new ArrayList<>();
            for (ScopesBean.ScopeBean scopeBean : scopesBean.getScopes()) {
                scopes.add(scopeBean.getName());
            }
            ScopeTokenRequest scopeTokenRequest = new ScopeTokenRequest(scopes);
            // 获取token接口
            mPresenter.getScopeToken(scopeTokenRequest.toString());
        }
    }

    @Override
    public void getScopesFail(int errorCode, String msg) {
//        ToastUtil.show(msg);
        findSAToken(errorCode, msg);
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
                nowHome.setSc_lan_address(HttpUrlConfig.baseSCUrl);
                nowHome.setSAEnvironment(HomeUtil.isBssidEqual(nowHome));
                AuthBackBean authBackBean = new AuthBackBean(nowHome.getUser_id(), userName, nowHome, stBean);
                if (UserUtils.isLogin()) {
                    authBackBean.setCloudUserId(UserUtils.getCloudUserId());
                    authBackBean.setCountryCode(SpUtil.get(SpConstant.AREA_CODE));
                    authBackBean.setPhone(SpUtil.get(SpConstant.PHONE_NUM));
                    authBackBean.setCookies(PersistentCookieStore.getInstance().get(HttpUrl.parse(HttpUrlConfig.getLogin())));
                } else {
                    authBackBean.setCookies(new ArrayList<>());
                }
                authDataList.add(authBackBean);
                setHCStatus(1);
            } else {
                setHCStatus(2);
            }
        } else {
            setHCStatus(2);
        }
        dealNextHome();
    }

    @Override
    public void getScopeTokenFail(int errorCode, String msg) {
        findSAToken(errorCode, msg);
    }

    /**
     * 找回凭证
     *
     * @param errorCode
     * @param msg
     */
    private void findSAToken(int errorCode, String msg) {
        if ((errorCode == 5012 || errorCode == 5027) && nowHome.getId() > 0) {
            if (UserUtils.isLogin()) {
                NameValuePair nameValuePair = new NameValuePair("area_id", String.valueOf(nowHome.getId()));
                List<NameValuePair> requestData = new ArrayList<>();
                requestData.add(nameValuePair);
                mPresenter.getSAToken(nowHome.getCloud_user_id(), requestData);  // sc的用户id, sc上的家庭id
            } else {
                setHCStatus(2);
                if (allLoadAuth) {
                    authFail();
                } else {
                    dealNextHome();
                }
            }
        } else {
            setHCStatus(2);
            if (allLoadAuth) {
                authFail();
            } else {
                dealNextHome();
            }
        }
    }

    private void authFail() {
        ToastUtil.show(UiUtil.getString(R.string.mine_authorize_fail));
    }

    /**
     * 找回凭证成功
     *
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
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getSATokenFail(int errorCode, String msg) {
        setHCStatus(2);
        if (allLoadAuth) {
            authFail();
        } else {
            dealNextHome();
        }
    }

    private void setHCStatus(int status) {
        selectedData.get(index).setAuthStatus(status);
        mAuthHCAdapter.notifyItemChanged(index);
    }

    /**
     * 处理下一个家庭
     */
    private void dealNextHome() {
        if (!allLoadAuth)
        if (index == selectedData.size() - 1) {
            allLoadAuth = true;
            if (CollectionUtil.isNotEmpty(authDataList)) {
                setConfirmEnabled(true, false);
            } else {
                ToastUtil.show(UiUtil.getString(R.string.mine_authorize_fail));
            }
        } else {
            index++;
            getScopeToken(selectedData.get(index));
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
                });
            }
        });
    }

    /**
     * 家庭列表数据
     */
    private void getHCData() {
        UiUtil.starThread(new Runnable() {
            @Override
            public void run() {
                List<HomeCompanyBean> hcList = dbManager.queryHomeCompanyList();

                for (HomeCompanyBean homeCompanyBean : hcList) {
                    if (UserUtils.isLogin()) { // 如果登录了
                        if (homeCompanyBean.isIs_bind_sa()) { // 有实体SA
                            selectedData.add(homeCompanyBean);
                        }
                    } else { // 没登录
                        if (homeCompanyBean.isIs_bind_sa() && HomeUtil.isBssidEqual(homeCompanyBean)) { // 有实体SA且在SA环境下
                            selectedData.add(homeCompanyBean);
                        }
                    }
                }
                UiUtil.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        mAuthHCAdapter.setNewData(selectedData);

                        if (CollectionUtil.isNotEmpty(selectedData)) {
                            getScopeToken(selectedData.get(0));
                        }
                    }
                });
            }
        });
    }

    private void getScopeToken(HomeCompanyBean homeCompanyBean) {
        nowHome = homeCompanyBean;
        String saLanAddress = nowHome.getSa_lan_address();
        HttpUrlConfig.baseSAUrl = TextUtils.isEmpty(saLanAddress) ? "" : saLanAddress;
        TempChannelUtil.baseSAUrl = saLanAddress;
        LogUtil.e("SA地址===========" + TempChannelUtil.baseSAUrl);
        CurrentHome = nowHome;
        if (!TextUtils.isEmpty(nowHome.getSa_lan_address())) {
            HttpUrlConfig.baseSAUrl = nowHome.getSa_lan_address();
            HttpUrlConfig.apiSAUrl = HttpUrlConfig.baseSAUrl + HttpUrlConfig.API;
        }
        HttpConfig.clearHeader();
        SpUtil.put(SpConstant.SA_TOKEN, nowHome.getSa_user_token());
        SpUtil.put(SpConstant.IS_BIND_SA, nowHome.isIs_bind_sa());

        HttpConfig.addAreaIdHeader(HttpConfig.AREA_ID, String.valueOf(nowHome.getArea_id()));
        HttpConfig.addAreaIdHeader(HttpConfig.TOKEN_KEY, nowHome.getSa_user_token());
        mPresenter.getScopeList();
    }
}