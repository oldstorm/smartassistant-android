package com.yctc.zhiting.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.ScreenUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.entity.ChannelEntity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.Header;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.TempChannelUtil;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.google.zxing.Result;
import com.king.zxing.CameraScan;
import com.king.zxing.DefaultCameraScan;
import com.king.zxing.ViewfinderView;
import com.king.zxing.util.LogUtils;
import com.king.zxing.util.PermissionUtils;
import com.king.zxing.util.QRCodeParseUtils;
import com.yalantis.ucrop.util.FileUtils;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.ScanContract;
import com.yctc.zhiting.activity.presenter.ScanPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.dialog.ScanFailDialog;
import com.yctc.zhiting.entity.GenerateCodeJson;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.InvitationCheckBean;
import com.yctc.zhiting.entity.mine.UserInfoBean;
import com.yctc.zhiting.event.HomeEvent;
import com.yctc.zhiting.event.HomeSelectedEvent;
import com.yctc.zhiting.fragment.HomeFragment2;
import com.yctc.zhiting.request.BindCloudRequest;
import com.yctc.zhiting.utils.AllRequestUtil;
import com.yctc.zhiting.utils.JwtUtil;
import com.yctc.zhiting.utils.StringUtil;
import com.yctc.zhiting.utils.UserUtils;
import com.yctc.zhiting.utils.jwt.JwtBean;
import com.yctc.zhiting.utils.statusbarutil.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.yctc.zhiting.config.Constant.CurrentHome;
import static com.yctc.zhiting.config.Constant.wifiInfo;

public class CaptureNewActivity extends MVPBaseActivity<ScanContract.View, ScanPresenter> implements CameraScan.OnScanResultCallback, ScanContract.View {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 0X86;
    private static final int WRITE_PERMISSION_REQUEST_CODE = 0X87;
    private static final int OPEN_PHOTO_ALBUM_REQUEST_CODE = 0X87;
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
    private boolean insertWifiInfo; // 是否插入WiFi信息
    private ChannelEntity channelEntity;  // 临时通道实体类
    private CenterAlertDialog alertDialog;
    private String mTempChannelUrl;//临时通道地址
    private String saId; // sa设备的id
    private int area_type;
    private ImageView ivOpenPhotoAlbum;

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
        ivOpenPhotoAlbum = findViewById(R.id.ivOpenPhotoAlbum);
        ivBack = findViewById(R.id.ivBack);
        ScreenUtil.fitNotchScreen(this, ivBack);
        initAlertDialog();
        int viewfinderViewId = getViewfinderViewId();
        if (viewfinderViewId != 0) {
            viewfinderView = findViewById(viewfinderViewId);
        }
        initCameraScan();
        startCamera();

        ivBack.setOnClickListener(v -> {
            finish();
        });

        ivOpenPhotoAlbum.setOnClickListener(view -> checkReadWritePermission());
    }

    private void checkReadWritePermission() {
        if (PermissionUtils.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openPhotoAlbum();
        } else {
            LogUtils.d("checkPermissionResult != PERMISSION_GRANTED");
            PermissionUtils.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, WRITE_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * 打开相册
     */
    private void openPhotoAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }

        startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), OPEN_PHOTO_ALBUM_REQUEST_CODE);
    }

    /**
     * 不在局域网提示弹窗
     */
    private void initAlertDialog() {
        alertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.home_not_in_lan_tips), getResources().getString(R.string.home_cancel), getResources().getString(R.string.home_go_to_login), false);
        alertDialog.setConfirmListener(del -> {
            alertDialog.dismiss();
            switchToActivity(LoginActivity.class);
            finish();
        });
        alertDialog.setDismissListener(() -> resetScan(false));
    }

    @Override
    protected void initData() {
        super.initData();
        mContext = new WeakReference<>(this);
        dbManager = DBManager.getInstance(mContext.get());

        scanFailDialog = new ScanFailDialog();
        scanFailDialog.setClickCloseListener(() -> {
            scanFailDialog.dismiss();
            resetScan(false);
        });
        getUserInfo();
    }

    /**
     * 重置扫码
     */
    private void resetScan(boolean needDelay) {
        if (needDelay) {
            UiUtil.postDelayed(() -> resetCamera(), 2000);
        } else {
            resetCamera();
        }
    }

    private void resetCamera() {
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
        } else if (requestCode == WRITE_PERMISSION_REQUEST_CODE) {
            requestWritePermissionResult(permissions, grantResults);
        }
    }

    /**
     * 获取读取权限
     *
     * @param permissions
     * @param grantResults
     */
    private void requestWritePermissionResult(String[] permissions, int[] grantResults) {
        if (PermissionUtils.requestPermissionsResult(Manifest.permission.READ_EXTERNAL_STORAGE, permissions, grantResults)) {
            openPhotoAlbum();
        } else {
            finish();
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
        if (result != null && result.getText() != null) {
            String scanResult = result.getText();
            handleScanResult(scanResult);
        }
        return true;
    }

    private void handleScanResult(String scanResult) {
        if (TextUtils.isEmpty(scanResult)) return;
        mCameraScan.setAnalyzeImage(false);
        mCameraScan.stopCamera();
        viewfinderView.setScan(false);
        LogUtil.e("扫描结果=" + scanResult);
        if (scanResult.contains("qr_code") && scanResult.contains("url")) {  // 二维码中包含 qr_code
            checkToken(scanResult);
        } else { // 二维码中不包含 qr_code，则说明扫码失败
            showTipsDialog();
        }
    }

    /**
     * 检查sa的token是否存在
     *
     * @param scanResult
     */
    private void checkToken(String scanResult) {
        UiUtil.starThread(() -> {
            mQRCodeBean = GsonConverter.getGson().fromJson(scanResult, GenerateCodeJson.class);

            String qrCode = mQRCodeBean.getQr_code();
            String body = "{\"qr_code\":\"" + qrCode + "\", \"nickname\":\"" + nickname + "\"}";
            JwtBean jwtBean = JwtUtil.decodeJwt(qrCode);
            long areaId = 0;
            if (jwtBean != null) {
                JwtBean.JwtBody jwtBody = jwtBean.getJwtBody();
                areaId = jwtBody.getArea_id();
                saId = jwtBody.getSa_id();
                area_type = jwtBody.getArea_type();
                mQRCodeBean.setSaId(jwtBody.getSa_id());
            } else {
                ToastUtil.show(getResources().getString(R.string.home_jwt_decode_fail));
                resetScan(false);
                return;
            }
            if (saId != null) {
                saToken = dbManager.getSaTokenBySAID(saId);
            }
            if (areaId > 0) {
                mQRCodeBean.setArea_id(areaId);
            }
            channelEntity = null;
            showLoadingView();
            AllRequestUtil.checkUrl500(mQRCodeBean.getUrl(), new AllRequestUtil.onCheckUrlListener() {// 检查地址是否可以连接
                @Override
                public void onSuccess() {  // 可以连接上
                    LogUtil.e("checkUrl===onSuccess");
                    //只是判断是否新加入
                    hideLoadingView();
                    mQRCodeBean.setSaToken(saToken);
                    insertWifiInfo = true;
                    inviteCheck(body, mQRCodeBean.getUrl());
                }

                @Override
                public void onError() {  // 连接失败
                    LogUtil.e("checkUrl===onError");
                    hideLoadingView();
                    requestTempChannel(saId, body);
                }
            });
        });
    }

    /**
     * 请求临时通道
     *
     * @param saId
     * @param body
     */
    private void requestTempChannel(String saId, String body) {
        if (UserUtils.isLogin()) { // 用户已登录sc
            List<Header> headers = new ArrayList<>();
            headers.add(new Header(HttpConfig.SA_ID, saId));
            List<NameValuePair> requestData = new ArrayList<>();
            requestData.add(new NameValuePair("scheme", Constant.HTTPS));
            String url = TempChannelUtil.baseSCUrl + "/datatunnel";

            HTTPCaller.getInstance().getChannel(ChannelEntity.class, url, headers.toArray(new Header[headers.size()]), requestData,
                    new RequestDataCallback<ChannelEntity>() {
                        @Override
                        public void onSuccess(ChannelEntity obj) {  // 获取临时通道成功
                            super.onSuccess(obj);
                            channelEntity = obj;
                            insertWifiInfo = false;
                            if (obj != null) {
                                Log.e("CaptureNewActivity=", "checkTemporaryUrl=onSuccess=");
                                inviteCheck(body, Constant.HTTPS_HEAD + obj.getHost());
                            }
                        }

                        @Override
                        public void onFailed(int errorCode, String errorMessage) {  // 获取临时通道失败
                            super.onFailed(errorCode, errorMessage);
                            hideLoadingView();
                            Log.e("CaptureNewActivity=", "checkTemporaryUrl=onFailed");
                            resetScan(false);
                            ToastUtil.show(getResources().getString(R.string.home_temp_channel_fail));
                        }
                    }, false);
        } else {  // 用户未登录sc，去到登录界面
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.show(CaptureNewActivity.this);
            }
        }
    }

    /**
     * 校验邀请码
     *
     * @param body
     * @param tempChannelUrl
     */
    private void inviteCheck(String body, String tempChannelUrl) {
        if (mQRCodeBean != null) {
            mTempChannelUrl = tempChannelUrl;
            mPresenter.invitationCheck(body, mQRCodeBean, tempChannelUrl);
        }
    }

    @Override
    public void invitationCheckSuccess(InvitationCheckBean invitationCheckBean) {
        UiUtil.starThread(() -> {
            homeCompanyBean = new HomeCompanyBean(mQRCodeBean.getArea_name());
            homeCompanyBean.setIs_bind_sa(true);
            homeCompanyBean.setName(mQRCodeBean.getArea_name());
            homeCompanyBean.setSa_lan_address(mQRCodeBean.getUrl());
            homeCompanyBean.setCloud_user_id(UserUtils.getCloudUserId());
            homeCompanyBean.setUser_id(invitationCheckBean.getUser_info().getUser_id());
            homeCompanyBean.setSa_user_token(invitationCheckBean.getUser_info().getToken());
            homeCompanyBean.setSa_id(saId);
            homeCompanyBean.setArea_type(area_type);
            IdBean idBean = invitationCheckBean.getArea_info();
            long areaId = 0;
            if (idBean != null) {
                areaId = idBean.getId();
                homeCompanyBean.setArea_id(areaId);
            }
            if (wifiInfo != null && channelEntity == null) {  // wifi信息不为空且没有走临时通道时
                homeCompanyBean.setSs_id(wifiInfo.getSSID());
                homeCompanyBean.setBSSID(StringUtil.getBssid());
            }
            HttpConfig.addAreaIdHeader(HttpConfig.AREA_ID, String.valueOf(homeCompanyBean.getArea_id()));
            HttpConfig.addAreaIdHeader(HttpConfig.TOKEN_KEY, homeCompanyBean.getSa_user_token());

            Constant.CurrentHome = homeCompanyBean;
            if (channelEntity != null) {
                SpUtil.put(SpConstant.SA_TOKEN, homeCompanyBean.getSa_user_token());
                TempChannelUtil.saveTempChannelUrl(channelEntity);
            }

            HomeCompanyBean checkHome = dbManager.queryHomeCompanyByAreaId(areaId); // 根据areaId查找家庭
            if (checkHome == null) {  // 没有加入过，加入数据库
                AllRequestUtil.bindCloudWithoutCreateHome(homeCompanyBean, channelEntity, homeCompanyBean.getSa_lan_address(), homeId -> {
                    CurrentHome.setId(homeId);
                    homeCompanyBean.setId(homeId);
                    insertHome(homeCompanyBean);
                });
            } else {// 加入过，更新数据库
                CurrentHome.setId(checkHome.getId());
                homeCompanyBean.setId(checkHome.getId());
                dbManager.updateHomeCompanyByAreaId(homeCompanyBean);
                HomeFragment2.homeLocalId = checkHome.getLocalId();
                UiUtil.runInMainThread(() -> toMain());
            }
            //AllRequestUtil.createHomeBindSC(homeCompanyBean, channelEntity);
        });
    }



    /**
     * 插入家庭
     */
    private void insertHome(HomeCompanyBean hcb) {
        long id = dbManager.insertHomeCompany(hcb, null, insertWifiInfo);
        homeCompanyBean.setLocalId(id);
        HomeFragment2.homeLocalId = id;
        UiUtil.runInMainThread(() -> {
            toMain();
        });
    }

    /**
     * 回到主界面
     */
    private void toMain() {
        if (homeCompanyBean != null) {
            if (!TextUtils.isEmpty(homeCompanyBean.getSa_lan_address())) {
                HttpUrlConfig.baseSAUrl = homeCompanyBean.getSa_lan_address();
            } else if (!TextUtils.isEmpty(mTempChannelUrl)) {
                HttpUrlConfig.baseSAUrl = mTempChannelUrl;
            }
            HttpUrlConfig.apiSAUrl = HttpUrlConfig.baseSAUrl + HttpUrlConfig.API;
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
        } else {
            ToastUtil.show(msg);
            resetScan(true);
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

    /**
     * 更新云id
     *
     * @param idBean
     */
    @Override
    public void createHomeSCSuccess(IdBean idBean) {
        if (idBean != null) {
            int cloudUserId = UserUtils.getCloudUserId();
            long cloudId = idBean.getId();
            IdBean.CloudSaUserInfo cloudSaUserInfo = idBean.getCloud_sa_user_info();
            int userId = homeCompanyBean.getUser_id();
            String token = homeCompanyBean.getSa_user_token();
            if (cloudSaUserInfo != null && !homeCompanyBean.isIs_bind_sa()) {
                userId = cloudSaUserInfo.getId();
                token = cloudSaUserInfo.getToken();
            }
            dbManager.updateHomeCompanyCloudId(homeCompanyBean.getLocalId(), cloudId, cloudUserId, userId, token);
            homeCompanyBean.setId(cloudId);
            homeCompanyBean.setCloud_user_id(cloudUserId);
            Constant.CurrentHome = homeCompanyBean;

            BindCloudRequest request = new BindCloudRequest();
            request.setCloud_area_id(String.valueOf(cloudId));
            request.setCloud_user_id(UserUtils.getCloudUserId());
            HttpConfig.addHeader(homeCompanyBean.getSa_user_token());

            mPresenter.bindCloudSC(request);
        }
    }

    @Override
    public void createHomeSCFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OPEN_PHOTO_ALBUM_REQUEST_CODE) {
                String imageUrl = FileUtils.getPath(this, data.getData());
                LogUtil.e(TAG + "onActivityResult=imageUrl=" + imageUrl);
                QRCodeParseUtils.parsePhoto(this, imageUrl, result -> {
                    LogUtil.e(TAG + "onActivityResult=result=" + result);
                    handleScanResult(result);
                });
            }
        }
    }
}