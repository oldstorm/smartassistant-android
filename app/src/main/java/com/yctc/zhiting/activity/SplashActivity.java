package com.yctc.zhiting.activity;

import static com.yctc.zhiting.config.Constant.CurrentHome;
import static com.yctc.zhiting.config.Constant.wifiInfo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.app.main.framework.baseutil.AndroidUtil;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.updateapp.AppUpdateHelper;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SplashContract;
import com.yctc.zhiting.activity.presenter.SplashPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.AgreementDialog;
import com.yctc.zhiting.dialog.AgreementTipDialog;
import com.yctc.zhiting.dialog.AppUpdateDialog;
import com.yctc.zhiting.entity.mine.AndroidAppVersionBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.UserInfoBean;
import com.yctc.zhiting.event.UpdateProfessionStatusEvent;
import com.yctc.zhiting.fragment.HomeFragment;
import com.yctc.zhiting.receiver.WifiReceiver;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UsernameUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;

/**
 * 启动页
 */
public class SplashActivity extends MVPBaseActivity<SplashContract.View, SplashPresenter> implements SplashContract.View {

    private WeakReference<Context> mContext;
    private DBManager dbManager;

    /**
     * 1 授权登录
     */
    private String type;
    /**
     * 第三方应用需要的权限
     */
    private String needPermissions;
    /**
     * 第三方app的名称
     */
    private String appName;

    private AgreementDialog mAgreementDialog;
    private AgreementTipDialog mAgreementTipDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected boolean isSetStateBar() {
        return false;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if (!this.isTaskRoot()) { // 当前类不是该Task的根部，那么之前启动
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) { // 当前类是从桌面启动的
                    super.onCreate(savedInstanceState);
                    finish(); // finish掉该类，直接打开该Task中现存的Activity
                    return;
                }
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initUI() {
        super.initUI();
        SpUtil.init(this);
        registerWifiReceiver();
        initAgreementDialog();
        initAgreementTipDialog();
        mContext = new WeakReference<>(this);
        dbManager = DBManager.getInstance(mContext.get());
        //updateApk("");
    }

    /**
     * 初始化用户协议弹窗
     */
    private void initAgreementDialog() {
        mAgreementDialog = new AgreementDialog();
        mAgreementDialog.setOnOperateListener(new AgreementDialog.OnOperateListener() {
            @Override
            public void onAgreement() {
                Bundle bundle = new Bundle();
                bundle.putString(IntentConstant.TITLE, UiUtil.getString(R.string.user_agreement));
                bundle.putString(IntentConstant.URL, Constant.AGREEMENT_URL);
                switchToActivity(NormalWebActivity.class, bundle);
            }

            @Override
            public void onPolicy() {
                Bundle bundle = new Bundle();
                bundle.putString(IntentConstant.TITLE, UiUtil.getString(R.string.privacy_policy));
                bundle.putString(IntentConstant.URL, Constant.POLICY_URL);
                switchToActivity(NormalWebActivity.class, bundle);
            }

            @Override
            public void onDisagree() {
                mAgreementDialog.dismiss();
                if (mAgreementTipDialog != null && !mAgreementTipDialog.isShowing()) {
                    mAgreementTipDialog.show(SplashActivity.this);
                }
            }

            @Override
            public void onAgree() {
                mAgreementDialog.dismiss();
                SpUtil.put(Constant.AGREED, true);
                //afterAgreed(getIntent());
                mPresenter.checkAppVersionInfo();
            }
        });
    }

    /**
     * 询问是否仍不同意
     */
    private void initAgreementTipDialog() {
        mAgreementTipDialog = AgreementTipDialog.getInstance();
        mAgreementTipDialog.setOnOperateListener(new AgreementTipDialog.OnOperateListener() {
            @Override
            public void onDisagree() {
                mAgreementTipDialog.dismiss();
                finish();
            }

            @Override
            public void onRead() {
                mAgreementTipDialog.dismiss();
                if (mAgreementDialog != null && !mAgreementDialog.isShowing()) {
                    mAgreementDialog.show(SplashActivity.this);
                }
            }
        });
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        boolean agreed = SpUtil.getBoolean(Constant.AGREED);
        if (agreed) {
            mPresenter.checkAppVersionInfo();
            //afterAgreed(intent);
        } else {
            mAgreementDialog.show(this);
        }
    }

    /**
     * 同意过用户协议和隐私政策
     *
     * @param intent
     */
    private void afterAgreed(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            type = uri.getQueryParameter("type");
        }
        needPermissions = intent.getStringExtra("needPermissions");
        appName = intent.getStringExtra("appName");
        if (type != null && type.equals("1") && CurrentHome != null) {  // 如果是授权过来且当前家庭不为空，直接调整授权界面
            toMain();
        } else { // 否则，正常流程
            checkUser();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterWifiReceiver();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d("TAG", "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        checkUser();
    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {
        checkUser();
    }

    @Override
    protected void hasPermissionTodo() {
        super.hasPermissionTodo();
        checkUser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            checkUser();
        }
    }

    /**
     * 检查是否存在用户，不存在则创建
     */
    private void checkUser() {
        UiUtil.starThread(() -> {
            UserInfoBean userInfoBean = dbManager.getUser();
            if (userInfoBean != null) {  // 已经创建用户
                createHome();
            } else {
                UserInfoBean uib = new UserInfoBean(1, UsernameUtil.getUsername());
                long count = dbManager.insertUser(uib);
                UiUtil.runInMainThread(() -> {
                    if (count > 0) {
                        createHome();
                    } else {
                        ToastUtil.show(UiUtil.getString(R.string.main_create_user_fail));
                    }
                });
            }
        });
    }

    /**
     * 去到主界面/授权界面
     */
    private void toMain1() {
    }

    private void toMain() {
        UiUtil.starThread(() -> {
            List<HomeCompanyBean> homeList = dbManager.queryHomeCompanyList();
            if (CollectionUtil.isNotEmpty(homeList)) {
                CurrentHome = homeList.get(0);
                UiUtil.runInMainThread(() -> {
                    if (HomeFragment.homeLocalId > 0) { // 之前打开过，没退出，按Home键之前的那个家庭
                        for (HomeCompanyBean home : homeList) {
                            if (home.getLocalId() == HomeFragment.homeLocalId) {
                                CurrentHome = home;
                                break;
                            }
                        }
                    } else {
                        if (wifiInfo != null) {
                            for (HomeCompanyBean home : homeList) {
                                if (home.getBSSID() != null && home.getBSSID().
                                        equalsIgnoreCase(wifiInfo.getBSSID()) && home.isIs_bind_sa()) { // 当前sa环境
                                    CurrentHome = home;
                                    break;
                                }

                            }
                        }
                    }

                    for (HomeCompanyBean homeCompanyBean : homeList) {
                        if (homeCompanyBean.getLocalId() == CurrentHome.getLocalId()) {
                            homeCompanyBean.setSelected(true);
                        } else {
                            homeCompanyBean.setSelected(false);
                        }
                    }
                    UiUtil.postDelayed(() -> {
                        Bundle bundle = new Bundle();
                        bundle.putString(IntentConstant.TYPE, type);
                        bundle.putString(IntentConstant.NEED_PERMISSION, needPermissions);
                        bundle.putString(IntentConstant.APP_NAME, appName);
                        bundle.putSerializable(IntentConstant.BEAN_LIST, (Serializable) homeList);
                        boolean guided = SpUtil.getBoolean(Constant.GUIDED);
                        if (guided) { // 已经走过引导页
                            // 如果type不为空且为1的情况下到授权界面，否则直接到主界面
                            switchToActivity(type != null && type.equals("1") ? AuthorizeActivity.class : MainActivity.class, bundle);
                        } else {
                            switchToActivity(GuideActivity.class, bundle);
                        }
                        finish();
                    }, 1500);
                });
            }
        });
    }

    /**
     * Wifi 状态接收器
     */
    private final WifiReceiver mWifiReceiver = new WifiReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                EventBus.getDefault().post(new UpdateProfessionStatusEvent());
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    wifiInfo = null;
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        wifiInfo = wifiManager.getConnectionInfo();
                    }
                }
            }
        }
    };

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
     * 创建家庭
     */
    private void createHome() {
        UiUtil.starThread(() -> {
            List<HomeCompanyBean> homes = dbManager.queryHomeCompanyList();
            if (CollectionUtil.isNotEmpty(homes)) { // 存在家庭
                UiUtil.runInMainThread(() -> toMain());
            } else {  // 不存在家庭则创建一个
                HomeCompanyBean homeCompanyBean = new HomeCompanyBean(1, getResources().getString(R.string.main_my_home));
                homeCompanyBean.setIs_bind_sa(false);
                homeCompanyBean.setSa_user_token(null);
                homeCompanyBean.setSa_lan_address(null);
                homeCompanyBean.setUser_id(1);
                homeCompanyBean.setArea_type(Constant.HOME_MODE);
                long count = dbManager.insertHomeCompany(homeCompanyBean, null, false);
                UiUtil.runInMainThread(() -> {
                    if (count > 0) {
                        toMain();
                    } else {
                        ToastUtil.show(UiUtil.getString(R.string.main_create_home_fail));
                    }
                });
            }
        });
    }

    AppUpdateDialog mUpdateDialog;

    /**
     * 更新app版本类型回调
     */
    @Override
    public void getAppVersionInfoSuccess(AndroidAppVersionBean appVersionBean) {
        LogUtil.e(TAG+"getAppVersionInfoSuccess="+ GsonConverter.getGson().toJson(appVersionBean));
        if (appVersionBean != null && appVersionBean.getUpdate_type() == Constant.UpdateType.NONE) {
            afterAgreed(getIntent());
        } else {
            mUpdateDialog = AppUpdateDialog.newInstance(appVersionBean);
            mUpdateDialog.setUpdateListener(new AppUpdateDialog.OnUpdateListener() {
                @Override
                public void onUpdate() {
                    checkPermission(appVersionBean.getLink());
                }

                @Override
                public void onCancel() {
                    afterAgreed(getIntent());
                }
            }).show(this);
        }
    }

    /**
     * 检测权限
     */
    private void checkPermission(String apkUrl) {
        updateApk(apkUrl);
//        checkInstallApkTask(() -> {
//            updateApk(apkUrl);
//        });
    }

    /**
     * 下载并安装apk
     */
    private void updateApk(String apkUrl) {
        //apkUrl = "https://baicaiyouxuan.oss-cn-shenzhen.aliyuncs.com/baicaiyouxuan.apk";
        //apkUrl = "http://192.168.22.169:8082/file/d9/c1/d9c1ee9cade1b1d9098b4baf649f995cf0e162d12540584fada5b38cc45f0d80.V2.0.0-zhiting-release-03-02-12-13.apk";
        if(!TextUtils.isEmpty(apkUrl))
            apkUrl = apkUrl.substring(0, apkUrl.indexOf(".apk")+4);
        AppUpdateHelper helper = new AppUpdateHelper(SplashActivity.this, apkUrl);
        helper.setonDownLoadListener(new AppUpdateHelper.onDownLoadListener() {
            @Override
            public void pending() {
                ToastUtil.show(UiUtil.getString(R.string.app_update_now));
            }

            @Override
            public void progress(int currentBytes, int totalBytes) {
                if (mUpdateDialog != null) {
                    int progress = (int) ((currentBytes / (totalBytes * 1.0)) * 100);
                    mUpdateDialog.setProgress("下载中(" + progress + "%)...");
                }
            }

            @Override
            public void completed(String apkPath) {
                if (mUpdateDialog != null) {
                    mUpdateDialog.setRefreshUI();
                }
                AndroidUtil.installApk(SplashActivity.this, apkPath);
            }

            @Override
            public void error() {
                ToastUtil.show(UiUtil.getString(R.string.app_update_error));
                if (mUpdateDialog != null) {
                    mUpdateDialog.setRefreshUI();
                }
            }
        });
        helper.start();
    }

    @Override
    public void getAppVersionInfoFailed(int errorCode, String msg) {
        ToastUtil.show(msg);
        afterAgreed(getIntent());
    }
}
