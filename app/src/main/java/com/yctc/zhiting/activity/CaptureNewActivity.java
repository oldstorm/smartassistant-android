package com.yctc.zhiting.activity;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.camera.view.PreviewView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.ScreenUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.Header;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.google.zxing.Result;
import com.king.zxing.CameraScan;
import com.king.zxing.DefaultCameraScan;
import com.king.zxing.ViewfinderView;
import com.king.zxing.util.LogUtils;
import com.king.zxing.util.PermissionUtils;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.ScanContract;
import com.yctc.zhiting.activity.presenter.ScanPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.ScanFailDialog;
import com.yctc.zhiting.entity.GenerateCodeJson;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.InvitationCheckBean;
import com.yctc.zhiting.entity.mine.UserInfoBean;
import com.yctc.zhiting.event.HomeEvent;
import com.yctc.zhiting.event.HomeSelectedEvent;
import com.yctc.zhiting.request.BindCloudRequest;
import com.yctc.zhiting.utils.AllRequestUtil;
import com.yctc.zhiting.utils.UserUtils;
import com.yctc.zhiting.utils.statusbarutil.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.yctc.zhiting.config.Constant.wifiInfo;

public class CaptureNewActivity extends MVPBaseActivity<ScanContract.View, ScanPresenter> implements CameraScan.OnScanResultCallback, ScanContract.View {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 0X86;
    protected PreviewView previewView;
    protected ViewfinderView viewfinderView;
    private CameraScan mCameraScan;
    private ImageView ivBack;
    private DBManager dbManager;
    private WeakReference<Context> mContext;
    private GenerateCodeJson mQRCodeBean;//扫描出来二维码对象
    private HomeCompanyBean homeCompanyBean;
    private ScanFailDialog scanFailDialog;

    private String saToken;
    private String nickname;

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected boolean isSetStateBar() {
        return false;
    }

    /**
     * 布局id
     *
     * @return
     */
    @Override
    public int getLayoutId() {
        return R.layout.activity_capture_new;
    }

    /**
     * 初始化
     */
    @Override
    public void initUI() {
        StatusBarUtil.setStatusBarDarkTheme(this, false);
        previewView = findViewById(getPreviewViewId());
        ivBack = findViewById(R.id.ivBack);
        ScreenUtil.fitNotchScreen(this, ivBack);
        int viewfinderViewId = getViewfinderViewId();
        if (viewfinderViewId != 0) {
            viewfinderView = findViewById(viewfinderViewId);
        }
        initCameraScan();
        startCamera();

        ivBack.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mContext = new WeakReference<>(this);
        dbManager = DBManager.getInstance(mContext.get());

        scanFailDialog = new ScanFailDialog();
        scanFailDialog.setClickCloseListener(() -> {
            scanFailDialog.dismiss();
            resetScan();
        });
        getUserInfo();
    }

    /**
     * 重置扫码
     */
    private void resetScan(){
        mCameraScan.setAnalyzeImage(true);
        mCameraScan.startCamera();
        viewfinderView.setScan(true);
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        UiUtil.starThread(() -> {
            UserInfoBean userInfoBean = dbManager.getUser();
            if (userInfoBean != null) {
                nickname = userInfoBean.getNickname();
            }
        });
    }

    /**
     * R
     * 初始化CameraScan
     */
    public void initCameraScan() {
        mCameraScan = new DefaultCameraScan(this, previewView);
        mCameraScan.setOnScanResultCallback(this);
        mCameraScan.setPlayBeep(true).setVibrate(true);
    }

    /**
     * 启动相机预览
     */
    public void startCamera() {
        if (mCameraScan != null) {
            if (PermissionUtils.checkPermission(this, Manifest.permission.CAMERA)) {
                mCameraScan.startCamera();
            } else {
                LogUtils.d("checkPermissionResult != PERMISSION_GRANTED");
                PermissionUtils.requestPermission(this, Manifest.permission.CAMERA, CAMERA_PERMISSION_REQUEST_CODE);
            }
        }
    }

    /**
     * 释放相机
     */
    private void releaseCamera() {
        if (mCameraScan != null) {
            mCameraScan.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            requestCameraPermissionResult(permissions, grantResults);
        }
    }

    /**
     * 请求Camera权限回调结果
     *
     * @param permissions
     * @param grantResults
     */
    public void requestCameraPermissionResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PermissionUtils.requestPermissionsResult(Manifest.permission.CAMERA, permissions, grantResults)) {
            startCamera();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        releaseCamera();
        StatusBarUtil.setStatusBarDarkTheme(this, true);
        super.onDestroy();
    }

    /**
     * 返回true时会自动初始化{@link #setContentView(int)}，返回为false是需自己去初始化{@link #setContentView(int)}
     *
     * @param layoutId
     * @return 默认返回true
     */
    public boolean isContentView(@LayoutRes int layoutId) {
        return true;
    }


    /**
     * {@link #viewfinderView} 的 ID
     *
     * @return 默认返回{@code R.id.viewfinderView}, 如果不需要扫码框可以返回0
     */
    public int getViewfinderViewId() {
        return R.id.viewfinderView;
    }


    /**
     * 预览界面{@link #previewView} 的ID
     *
     * @return
     */
    public int getPreviewViewId() {
        return R.id.previewView;
    }

    /**
     * Get {@link CameraScan}
     *
     * @return {@link #mCameraScan}
     */
    public CameraScan getCameraScan() {
        return mCameraScan;
    }

    /**
     * 接收扫码结果回调
     *
     * @param result 扫码结果
     * @return 返回true表示拦截，将不自动执行后续逻辑，为false表示不拦截，默认不拦截
     */
    @Override
    public boolean onScanResultCallback(Result result) {
        String scanResult = result.getText();
        mCameraScan.setAnalyzeImage(false);
        mCameraScan.stopCamera();
        viewfinderView.setScan(false);
        LogUtil.e("扫描结果=" + scanResult);
        if (scanResult.contains("qr_code")) {
            checkToken(scanResult);
        } else {
            showTipsDialog();
        }
        return true;
    }

    /**
     * 检查sa的token是否存在
     *
     * @param scanResult
     */
    private void checkToken(String scanResult) {
        UiUtil.starThread(() -> {
            mQRCodeBean = GsonConverter.getGson().fromJson(scanResult, GenerateCodeJson.class);
            //只是判断是否新加入
            saToken = dbManager.getSaTokenByUrl(mQRCodeBean.getUrl());
            mQRCodeBean.setSaToken(saToken);
            String body = "{\"qr_code\":\"" + mQRCodeBean.getQr_code() + "\", \"nickname\":\"" + nickname + "\"}";
            mPresenter.invitationCheck(body, mQRCodeBean);
        });
    }

    @Override
    public void invitationCheckSuccess(InvitationCheckBean invitationCheckBean) {
        UiUtil.starThread(() -> {
            Cursor cursor = dbManager.getLastHomeCompany();
            homeCompanyBean = new HomeCompanyBean(mQRCodeBean.getArea_name());
            homeCompanyBean.setSa_user_token(invitationCheckBean.getUser_info().getToken());
            homeCompanyBean.setUser_id(invitationCheckBean.getUser_info().getUser_id());
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToLast();
                int id = cursor.getInt(cursor.getColumnIndex("h_id"));
                if (!TextUtils.isEmpty(saToken))
                    homeCompanyBean.setLocalId(id + 1);
            } else {
                homeCompanyBean.setLocalId(1);
            }
            if (UserUtils.isLogin())
                homeCompanyBean.setId(mQRCodeBean.getArea_id());
            homeCompanyBean.setUser_id(invitationCheckBean.getUser_info().getUser_id());
            homeCompanyBean.setName(mQRCodeBean.getArea_name());
            homeCompanyBean.setSa_user_token(invitationCheckBean.getUser_info().getToken());
            homeCompanyBean.setIs_bind_sa(true);
            homeCompanyBean.setSa_lan_address(mQRCodeBean.getUrl());
            homeCompanyBean.setCloud_user_id(UserUtils.getCloudUserId());
            if (wifiInfo != null) {
                homeCompanyBean.setSs_id(wifiInfo.getSSID());
                homeCompanyBean.setMac_address(wifiInfo.getBSSID());
            }
            Constant.CurrentHome = homeCompanyBean;
            if (TextUtils.isEmpty(saToken)) {  // 没有加入过，加入数据库
                insertHome(homeCompanyBean);
            } else {  // 加入过，更新数据库
                dbManager.updateHomeCompanyBySaToken(homeCompanyBean);
                UiUtil.runInMainThread(() -> toMain());
            }
            AllRequestUtil.createHomeBindSC(homeCompanyBean);
        });
    }

    /**
     * 插入家庭
     */
    private void insertHome(HomeCompanyBean hcb) {
        long id = dbManager.insertHomeCompany(hcb, null);
        homeCompanyBean.setLocalId(id);
        UiUtil.runInMainThread(() -> {
            toMain();
        });
    }

    /**
     * 回到主界面
     */
    private void toMain() {
        if (homeCompanyBean != null) {
            EventBus.getDefault().post(new HomeEvent(TextUtils.isEmpty(saToken), homeCompanyBean));
            EventBus.getDefault().post(new HomeSelectedEvent(mQRCodeBean.getArea_name()));
            ToastUtil.show(String.format(UiUtil.getString(R.string.home_welcome_add_home), mQRCodeBean.getArea_name()));
        }
        switchToActivity(MainActivity.class);
        finish();
    }

    @Override
    public void invitationCheckFail(int errorCode, String msg) {
        if (errorCode == -1) {
            showTipsDialog();
        }else {
            ToastUtil.show(msg);
            resetScan();
        }
    }

    /**
     * 显示弹窗
     */
    private void showTipsDialog() {
        if (scanFailDialog != null && !scanFailDialog.isShowing()) {
            scanFailDialog.show(this);
        }
    }

    @Override
    public void createHomeSCSuccess(int cloudId) {
        //更新云id
        int cloudUserId = UserUtils.getCloudUserId();
        dbManager.updateHomeCompanyCloudId(homeCompanyBean.getLocalId(), cloudId, cloudUserId);
        homeCompanyBean.setId(cloudId);
        homeCompanyBean.setCloud_user_id(cloudUserId);
        Constant.CurrentHome = homeCompanyBean;

        BindCloudRequest request = new BindCloudRequest();
        request.setCloud_area_id(cloudId);
        request.setCloud_user_id(UserUtils.getCloudUserId());
        HttpConfig.addHeader(homeCompanyBean.getSa_user_token());

        mPresenter.bindCloudSC(request);
    }

    @Override
    public void createHomeSCFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}