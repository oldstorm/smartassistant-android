package com.yctc.zhiting.activity;



import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.AuthorizeContract;
import com.yctc.zhiting.activity.presenter.AuthorizePresenter;
import com.yctc.zhiting.adapter.ScopesAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.db.DBThread;
import com.yctc.zhiting.entity.AuthBackBean;
import com.yctc.zhiting.entity.ScopeTokenBean;
import com.yctc.zhiting.entity.ScopesBean;
import com.yctc.zhiting.entity.mine.UserInfoBean;
import com.yctc.zhiting.request.ScopeTokenRequest;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
    private int userId;
    private String userName;
    private String[] permissions;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_authorize;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        needPermissions = intent.getStringExtra(IntentConstant.NEED_PERMISSION);
        appName = intent.getStringExtra(IntentConstant.APP_NAME);
        tvJoin.setText(UiUtil.getString(R.string.main_welcome_join)+appName);
    }

    @Override
    protected void initUI() {
        super.initUI();
        rvScopes.setLayoutManager(new LinearLayoutManager(this));
        scopesAdapter = new ScopesAdapter();
        rvScopes.setAdapter(scopesAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        mContext = new WeakReference<>(getActivity());
        dbManager = DBManager.getInstance(mContext.get());
        mainThreadHandler = new Handler(Looper.getMainLooper());
        getUserInfo();
//        if (!TextUtils.isEmpty(Constant.CurrentHome.getSa_user_token())) {
//            UiUtil.postDelayed(() -> {
//                mPresenter.getScopeList();
//            }, 500);
//        }
         permissions = needPermissions.split(",");
         List<ScopesBean.ScopeBean> data = new ArrayList<>();
         if (permissions.length>0){
             for (int i=0; i<permissions.length; i++){
                 if (permissions[i].equals(Constant.USER)){
                     data.add(new ScopesBean.ScopeBean(Constant.USER, getResources().getString(R.string.main_get_login_status)));
                 }else if (permissions[i].equals(Constant.AREA)){
                     data.add(new ScopesBean.ScopeBean(Constant.AREA, getResources().getString(R.string.main_get_family_info)));
                 }
             }
         }
        scopesAdapter.setNewData(data);
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switchToActivity(MainActivity.class);
    }

    @OnClick(R.id.tvConfirm)
    void onClickConfirm(){
        if (TextUtils.isEmpty(Constant.CurrentHome.getSa_user_token())){
            ToastUtil.show(UiUtil.getString(R.string.main_home_is_not_bind_with_sa));
        }else {
            List<String> scopes = new ArrayList<>();
            for (String permission : permissions){
                scopes.add(permission);
            }
            ScopeTokenRequest scopeTokenRequest = new ScopeTokenRequest(scopes);
            mPresenter.getScopeToken(scopeTokenRequest.toString());
        }
    }

    /**
     * 获取 SCOPE 列表成功
     * @param scopesBean
     */
    @Override
    public void getScopesSuccess(ScopesBean scopesBean) {
        if (scopesBean!=null){
            if (CollectionUtil.isNotEmpty(scopesBean.getScopes())){
                List<ScopesBean.ScopeBean> data = new ArrayList<>();
                if (!TextUtils.isEmpty(needPermissions)) {

                    for (ScopesBean.ScopeBean scopeBean : scopesBean.getScopes()) {
                        for (String permission : permissions){
                            if (scopeBean.getName().equals(permission)){
                                data.add(scopeBean);
                            }
                        }
                    }
                    scopesAdapter.setNewData(data);
                }
            }
        }
    }

    @Override
    public void getScopesFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 获取 SCOPE Token成功
     * @param scopeTokenBean
     */
    @Override
    public void getScopeTokenSuccess(ScopeTokenBean scopeTokenBean) {
        if (scopeTokenBean!=null){
            ScopeTokenBean.STBean  stBean = scopeTokenBean.getScope_token();
            if (stBean!=null){
                Intent intent = new Intent();
                AuthBackBean authBackBean = new AuthBackBean(Constant.CurrentHome.getUser_id(), userName, Constant.CurrentHome, stBean);
                intent.setAction("zt.com.yctc.zhiting.sign");
                intent.putExtra("backInfo", authBackBean.toString());
                sendBroadcast(intent);
                LibLoader.finishAllActivity();
            }else {
                ToastUtil.show(UiUtil.getString(R.string.main_login_fail));
            }
        }else {
            ToastUtil.show(UiUtil.getString(R.string.main_login_fail));
        }
    }

    @Override
    public void getScopeTokenFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        new DBThread(new Runnable() {
            @Override
            public void run() {
                UserInfoBean userInfoBean = dbManager.getUser();
                if (userInfoBean != null) {
                    mainThreadHandler.post(() -> {
                        userId = userInfoBean.getUserId();
                        userName = userInfoBean.getNickname();
                        String nickName = userInfoBean.getNickname();
                        String name = nickName.substring(0,1)+"*****"+userInfoBean.getNickname().substring(nickName.length());
                        tvName.setText(name);
                        tvTips.setText(String.format(UiUtil.getString(R.string.main_agree), name));
                        if (!TextUtils.isEmpty(Constant.CurrentHome.getSa_user_token())) {
                            mPresenter.getScopeList();
                        }
                    });
                }
            }
        }).start();
    }
}