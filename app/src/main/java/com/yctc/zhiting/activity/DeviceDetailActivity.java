package com.yctc.zhiting.activity;



import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.imageutil.GlideUtil;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DeviceDetailContract;
import com.yctc.zhiting.activity.presenter.DeviceDetailPresenter;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.entity.DeviceDetailEntity;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.event.FinishDeviceDetailEvent;
import com.yctc.zhiting.utils.IntentConstant;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设备详情
 */
public class DeviceDetailActivity extends MVPBaseActivity<DeviceDetailContract.View, DeviceDetailPresenter> implements  DeviceDetailContract.View {

    private int id;
    private int locationId;
    private boolean is_sa;
    private String name;
    private String logoUrl;
    private String plugin_url;
    private String raName;

    @BindView(R.id.ivSetting)
    ImageView ivSetting;
    @BindView(R.id.ivCover)
    ImageView ivCover;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_detail;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void initUI() {
        super.initUI();
//        mPresenter.getPermissions(Constant.CurrentHome.getUser_id());
        WebSettings webSettings = webView.getSettings();

        //设置支持javaScript脚步语言
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
//        webView.setBackgroundColor(0); // 设置背景色
//        webView.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
        //限制在WebView中打开网页，而不用默认浏览器
        //重写WebViewClient中的shouldOverrideUrlLoading方法，来验证安卓拦截url
        //webView.setWebViewClient(new WebViewClient());
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                LogUtil.e("url_address", uri.toString());
                //返回true取消当前加载，否则返回false.
                webView.loadUrl(uri.toString());
                return true;
            }
        }
        );

        webView.setWebChromeClient(new MyWebChromeClient());
    }

    @Override
    protected void initData() {
        super.initData();
        id = getIntent().getIntExtra(IntentConstant.ID, 0);
        locationId = getIntent().getIntExtra(IntentConstant.RA_ID, 0);
        is_sa = getIntent().getBooleanExtra(IntentConstant.IS_SA, false);
        name = getIntent().getStringExtra(IntentConstant.NAME);
        logoUrl = getIntent().getStringExtra(IntentConstant.LOGO_URL);
        plugin_url = getIntent().getStringExtra(IntentConstant.PLUGIN_URL);
        raName = getIntent().getStringExtra(IntentConstant.RA_NAME);

        ivCover.setVisibility(is_sa ? View.VISIBLE : View.GONE);
        tvName.setVisibility(is_sa ? View.VISIBLE : View.GONE);
        webView.setVisibility(is_sa ? View.GONE : View.VISIBLE);
        progressbar.setVisibility(is_sa ? View.GONE : View.VISIBLE);
        GlideUtil.load(logoUrl).into(ivCover);
        tvName.setText(name);
//        if (!is_sa){
//            webView.loadUrl(plugin_url);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mPresenter.getDeviceDetail(id);
        mPresenter.getDeviceDetailRestructure(id);
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(DeviceNameEvent event) {
//        name = event.getName();
//        tvName.setText(name);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(LocationEvent event) {
//        locationId = event.getId();
//        raName = event.getName();
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FinishDeviceDetailEvent event) {
        finish();
    }

    /**
     * 返回
     */
    @OnClick(R.id.ivBack)
    void onClickBack(){
        onBackPressed();
    }

    /**
     * 设置
     */
    @OnClick(R.id.ivSetting)
    void onClickSetting(){
        Bundle bundle = new Bundle();
        bundle.putInt(IntentConstant.ID, id);
        bundle.putString(IntentConstant.NAME, name);
        bundle.putInt(IntentConstant.RA_ID, locationId);
        bundle.putString(IntentConstant.RA_NAME, raName);
        bundle.putString(IntentConstant.LOGO_URL, logoUrl);
        bundle.putBoolean(IntentConstant.IS_SA, is_sa);
        switchToActivity(DeviceSettingActivity.class, bundle);
    }

    /**
     * 获取权限成功
     * @param permissionBean
     */
    @Override
    public void getPermissionsSuccess(PermissionBean permissionBean) {
        if (permissionBean!=null){
            boolean showSetting = permissionBean.getPermissions().isUpdate_device() || permissionBean.getPermissions().isDelete_device();
            ivSetting.setVisibility(showSetting ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 获取详情成功
     * @param deviceDetailBean
     */
    @Override
    public void getDeviceDetailSuccess(DeviceDetailBean deviceDetailBean) {
        if (deviceDetailBean!=null){
            DeviceDetailBean.DeviceInfoBean deviceInfoBean = deviceDetailBean.getDevice_info();
            if (deviceDetailBean!=null) {
                DeviceDetailBean.DeviceInfoBean.LocationBean locationBean = deviceInfoBean.getLocation();
                name = deviceInfoBean.getName();
                tvName.setText(deviceInfoBean.getName());
                GlideUtil.load(deviceInfoBean.getLogo_url()).into(ivCover);
                if (locationBean!=null){
                    locationId = locationBean.getId();
                    raName = locationBean.getName();
                }
                DeviceDetailBean.DeviceInfoBean.PluginBean pluginBean = deviceInfoBean.getPlugin();
                if (pluginBean!=null){
                    if (!is_sa && !TextUtils.isEmpty(pluginBean.getUrl())){
                        webView.loadUrl(pluginBean.getUrl());
                    }
                }
                DeviceDetailBean.DeviceInfoBean.PermissionsBean permissionsBean = deviceInfoBean.getPermissions();
                if (permissionsBean!=null){
                    ivSetting.setVisibility((permissionsBean.isUpdate_device() || permissionsBean.isDelete_device()) ? View.VISIBLE : View.GONE);
//                  tvDel.setVisibility(permissionsBean.isDelete_device() ? View.VISIBLE : View.GONE);
                }
            }
        }
    }


    /**
     * 获取详情成功 重构
     * @param deviceDetailResponseEntity
     */
    @Override
    public void getDeviceDetailRestructureSuccess(DeviceDetailResponseEntity deviceDetailResponseEntity) {
        if (deviceDetailResponseEntity!=null){
            DeviceDetailEntity deviceDetailEntity = deviceDetailResponseEntity.getDevice_info();
            if (deviceDetailEntity!=null){
                name = deviceDetailEntity.getName();
                tvName.setText(name);
                GlideUtil.load(deviceDetailEntity.getLogo_url()).into(ivCover);
                DeviceDetailEntity.AreaAndLocationBean locationBean = deviceDetailEntity.getLocation();
                if (locationBean!=null){
                    locationId = locationBean.getId();
                    raName = locationBean.getName();
                }
                DeviceDetailEntity.PluginBean pluginBean = deviceDetailEntity.getPlugin();
                if (pluginBean!=null){
                    if (!is_sa && !TextUtils.isEmpty(pluginBean.getUrl())){
                        webView.loadUrl(pluginBean.getUrl());
                    }
                }
                DeviceDetailEntity.PermissionsBean permissionsBean = deviceDetailEntity.getPermissions();
                if (permissionsBean!=null){
                    ivSetting.setVisibility((permissionsBean.isUpdate_device() || permissionsBean.isDelete_device()) ? View.VISIBLE : View.GONE);
                }
            }
        }
    }

    @Override
    public void getFail(int errorCode, String msg) {
        ToastUtil.show(msg);
        if (!is_sa){
            webView.loadUrl(plugin_url);
        }
    }

    class MyWebChromeClient extends WebChromeClient{
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressbar.setProgress(newProgress);
            if (newProgress == 100){
                progressbar.setVisibility(View.GONE);
            }
        }
    }
}