package com.yctc.zhiting.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.BaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.UserInfoBean;
import com.yctc.zhiting.event.UpdateProfessionStatusEvent;
import com.yctc.zhiting.receiver.WifiReceiver;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UsernameUtil;
import com.yctc.zhiting.websocket.WSocketManager;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.yctc.zhiting.config.Constant.CurrentHome;
import static com.yctc.zhiting.config.Constant.wifiInfo;

/**
 * 欢迎页
 */
public class SplashActivity extends BaseActivity {

    private boolean cancel;

    private WeakReference<Context> mContext;
    private DBManager dbManager;
    private Handler mainThreadHandler;

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

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        // 避免从桌面启动程序后，会重新实例化入口类的activity
//        if (!this.isTaskRoot()) { // 当前类不是该Task的根部，那么之前启动
//            Intent intent = getIntent();
//            if (intent != null) {
//                String action = intent.getAction();
//                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) { // 当前类是从桌面启动的
//                    super.onCreate(savedInstanceState);
//                    finish(); // finish掉该类，直接打开该Task中现存的Activity
//                    return;
//                }
//            }
//        }
//        super.onCreate(savedInstanceState);
//    }

    @Override
    protected void initUI() {
        super.initUI();
        registerWifiReceiver();
        mContext = new WeakReference<>(this);
        dbManager = DBManager.getInstance(mContext.get());
        mainThreadHandler = new Handler(Looper.getMainLooper());

        locationAndContactsTask();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {
            type = uri.getQueryParameter("type");
        }
        needPermissions = intent.getStringExtra("needPermissions");
        appName = intent.getStringExtra("appName");
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
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms) && !cancel) {
//            new AppSettingsDialog.Builder(this).build().show();
//        }
//        if (perms != null && perms.size() > 0 && !cancel) {
//            new AppSettingsDialog.Builder(this).build().show();
//        }
        checkUser();
    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {
        cancel = true;
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

    private void toMain() {



        UiUtil.starThread(() -> {
            List<HomeCompanyBean> homeList = dbManager.queryHomeCompanyList();
            if (CollectionUtil.isNotEmpty(homeList)){
            CurrentHome = homeList.get(0);
            UiUtil.runInMainThread(() -> {
                if (wifiInfo != null) {
                    for (HomeCompanyBean home : homeList) {
                        if (home.getMac_address() != null && home.getMac_address().
                                equalsIgnoreCase(wifiInfo.getBSSID()) && home.isIs_bind_sa()) { // 当前sa环境
                            CurrentHome = home;
                            break;
                        }
                    }
                }
                UiUtil.postDelayed(() -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(IntentConstant.TYPE, type);
                    bundle.putString(IntentConstant.NEED_PERMISSION, needPermissions);
                    bundle.putString(IntentConstant.APP_NAME, appName);
                    switchToActivity(type!=null && type.equals("1") ? AuthorizeActivity.class : MainActivity.class, bundle);
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
                long count = dbManager.insertHomeCompany(homeCompanyBean, null);
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
}
