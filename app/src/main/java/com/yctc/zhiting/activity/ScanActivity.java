//package com.yctc.zhiting.activity;
//
//import androidx.annotation.NonNull;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.os.Handler;
//import android.os.Looper;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//
//import com.app.main.framework.baseutil.UiUtil;
//import com.app.main.framework.baseutil.toast.ToastUtil;
//import com.app.main.framework.baseview.MVPBaseActivity;
//import com.app.main.framework.gsonutils.GsonConverter;
//import com.app.main.framework.httputil.HTTPCaller;
//import com.app.main.framework.httputil.RequestDataCallback;
//import com.app.main.framework.httputil.comfig.HttpConfig;
//import com.google.gson.Gson;
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.DecodeHintType;
//import com.google.zxing.Result;
//import com.google.zxing.client.android.BeepManager;
//import com.google.zxing.client.android.FinishListener;
//import com.google.zxing.client.android.ViewfinderView;
//import com.google.zxing.client.android.camera.CameraManager;
//import com.yctc.zhiting.R;
//import com.yctc.zhiting.activity.contract.ScanContract;
//import com.yctc.zhiting.activity.presenter.ScanPresenter;
//import com.yctc.zhiting.config.Constant;
//import com.yctc.zhiting.config.HttpUrlConfig;
//import com.yctc.zhiting.db.DBManager;
//import com.yctc.zhiting.db.DBThread;
//import com.yctc.zhiting.dialog.ScanFailDialog;
//import com.yctc.zhiting.entity.GenerateCodeJson;
//import com.yctc.zhiting.entity.home.SynPost;
//import com.yctc.zhiting.entity.mine.HomeCompanyBean;
//import com.yctc.zhiting.entity.mine.IdBean;
//import com.yctc.zhiting.entity.mine.InvitationCheckBean;
//import com.yctc.zhiting.entity.mine.UserInfoBean;
//import com.yctc.zhiting.event.HomeEvent;
//import com.yctc.zhiting.event.HomeSelectedEvent;
//import com.yctc.zhiting.request.BindCloudRequest;
//import com.yctc.zhiting.utils.ScanActivityHandler;
//import com.yctc.zhiting.utils.UserUtils;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.io.IOException;
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Map;
//
//import butterknife.OnClick;
//
//
///**
// * 扫描界面
// */
//public class ScanActivity extends MVPBaseActivity<ScanContract.View, ScanPresenter> implements SurfaceHolder.Callback, ScanContract.View {
//
//    private static final String TAG = ScanActivity.class.getSimpleName();
//
//    private boolean hasSurface;
//
//    private CameraManager cameraManager;
//    private ScanActivityHandler handler;
//    private Collection<BarcodeFormat> decodeFormats;
//    private Map<DecodeHintType, ?> decodeHints;
//    private String characterSet;
//    private ViewfinderView viewfinderView;
//    private BeepManager beepManager;
//    private ScanFailDialog scanFailDialog;
//
//    private WeakReference<Context> mContext;
//    private DBManager dbManager;
//    private Handler mainThreadHandler;
//    private GenerateCodeJson generateCodeJson;
//    private HomeCompanyBean homeCompanyBean;
//    private String saToken = null;
//    private String nickname;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_scan;
//    }
//
//    @Override
//    protected void initData() {
//        super.initData();
//
//        mContext = new WeakReference<>(this);
//        dbManager = DBManager.getInstance(mContext.get());
//        mainThreadHandler = new Handler(Looper.getMainLooper());
//
//        scanFailDialog = new ScanFailDialog();
//        scanFailDialog.setClickCloseListener(() -> {
//            scanFailDialog.dismiss();
//            handler.restartPreviewAndDecode();
//        });
//        hasSurface = false;
//        beepManager = new BeepManager(this);
//        getUserInfo();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        cameraManager = new CameraManager(getApplication());
//        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
//        viewfinderView.setCameraManager(cameraManager);
//        viewfinderView.setText(getResources().getString(R.string.scan_tips));
//        handler = null;
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        beepManager.updatePrefs();
//        decodeFormats = null;
//        characterSet = null;
//
//        SurfaceView surfaceView = (SurfaceView) findViewById(com.example.zxingutil.R.id.preview_view);
//        SurfaceHolder surfaceHolder = surfaceView.getHolder();
//        if (hasSurface) {
//            initCamera(surfaceHolder);
//        } else {
//            surfaceHolder.addCallback(this);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        if (handler != null) {
//            handler.quitSynchronously();
//            handler = null;
//        }
//        beepManager.close();
//        cameraManager.closeDriver();
//        if (!hasSurface) {
//            SurfaceView surfaceView = (SurfaceView) findViewById(com.example.zxingutil.R.id.preview_view);
//            SurfaceHolder surfaceHolder = surfaceView.getHolder();
//            surfaceHolder.removeCallback(this);
//        }
//        super.onPause();
//    }
//
//    @Override
//    protected boolean isSetStateBar() {
//        return false;
//    }
//
//    @Override
//    protected boolean isLoadTitleBar() {
//        return false;
//    }
//
//    @OnClick(R.id.ivBack)
//    void onClickBack() {
//        finish();
//    }
//
//    @Override
//    public void surfaceCreated(@NonNull SurfaceHolder holder) {
//        if (holder == null) {
//            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
//        }
//        if (!hasSurface) {
//            hasSurface = true;
//            initCamera(holder);
//        }
//    }
//
//    @Override
//    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
//
//    }
//
//    @Override
//    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
//        hasSurface = false;
//    }
//
//    @Override
//    public boolean bindEventBus() {
//        return true;
//    }
//
//    private void initCamera(SurfaceHolder surfaceHolder) {
//        if (surfaceHolder == null) {
//            throw new IllegalStateException("No SurfaceHolder provided");
//        }
//
//        if (cameraManager.isOpen()) {
//            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
//            return;
//        }
//        try {
//            cameraManager.openDriver(surfaceHolder);
//            if (handler == null) {
//                handler = new ScanActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
//            }
//        } catch (IOException ioe) {
//            Log.w(TAG, ioe);
//            displayFrameworkBugMessageAndExit();
//        } catch (RuntimeException e) {
//            Log.w(TAG, "Unexpected error initializing camera", e);
//            displayFrameworkBugMessageAndExit();
//        }
//    }
//
//    public ViewfinderView getViewfinderView() {
//        return viewfinderView;
//    }
//
//    public CameraManager getCameraManager() {
//        return cameraManager;
//    }
//
//    public void drawViewfinder() {
//        viewfinderView.drawViewfinder();
//    }
//
//    public Handler getHandler() {
//        return handler;
//    }
//
//    private void displayFrameworkBugMessageAndExit() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(getString(com.example.zxingutil.R.string.app_name));
//        builder.setMessage(getString(com.example.zxingutil.R.string.msg_camera_framework_bug));
//        builder.setPositiveButton(com.example.zxingutil.R.string.button_ok, new FinishListener(this));
//        builder.setOnCancelListener(new FinishListener(this));
//        builder.show();
//    }
//
//    /**
//     * A valid barcode has been found, so give an indication of success and show the results.
//     *
//     * @param rawResult   The contents of the barcode.
//     * @param scaleFactor amount by which thumbnail was scaled
//     * @param barcode     A greyscale bitmap of the camera data which was decoded.
//     */
//    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
//        boolean fromLiveScan = barcode != null;
//        if (fromLiveScan) {
//            beepManager.playBeepSoundAndVibrate();
//        }
//
//        String scanResult = rawResult.getText();
//        System.out.println("扫描结果：" + scanResult);
//        //String body = "{\"qr_code\":\""+scanResult+"\", \"nickname\":\""+"aa"+"\"}";
//        if (scanResult.contains("qr_code")) {  // 是json串
//            GsonConverter.getGson();
//            generateCodeJson = new Gson().fromJson(scanResult, GenerateCodeJson.class);
//            checkToken(generateCodeJson);
//        } else {
//            showTipsDialog();
//        }
//    }
//
//    @Override
//    public void invitationCheckSuccess(InvitationCheckBean invitationCheckBean) {
//        new DBThread(() -> {
//            Cursor cursor = dbManager.getLastHomeCompany();
//            homeCompanyBean = new HomeCompanyBean(generateCodeJson.getArea_name());
//            homeCompanyBean.setSa_user_token(invitationCheckBean.getUser_info().getToken());
//            homeCompanyBean.setUser_id(invitationCheckBean.getUser_info().getUser_id());
//            if (cursor != null && cursor.getCount() > 0) {
//                cursor.moveToLast();
//                int id = cursor.getInt(cursor.getColumnIndex("h_id"));
//                if (!TextUtils.isEmpty(saToken))
//                    homeCompanyBean.setLocalId(id + 1);
//            } else {
//                homeCompanyBean.setLocalId(1);
//            }
//
//            homeCompanyBean.setUser_id(invitationCheckBean.getUser_info().getUser_id());
//            homeCompanyBean.setName(generateCodeJson.getArea_name());
//            homeCompanyBean.setSa_user_token(invitationCheckBean.getUser_info().getToken());
//            homeCompanyBean.setIs_bind_sa(true);
//            homeCompanyBean.setSa_lan_address(generateCodeJson.getUrl());
//            if (TextUtils.isEmpty(saToken)) {  // 没有加入过，加入数据库
//                insertHome(homeCompanyBean);
//            } else {  // 加入过，更新数据库
//                new DBThread(() -> {
//                    dbManager.updateHomeCompanyBySaToken(homeCompanyBean);
//                    mainThreadHandler.post(() -> {
//                        Constant.CurrentHome = homeCompanyBean;
//                        toMain();
//                    });
//                }).start();
//            }
//            createHomeSC();
//        }).start();
//    }
//
//    /**
//     * 创建云端家庭
//     */
//    private void createHomeSC() {
//        if (generateCodeJson.getArea_id() == 0)//没有绑定云端家庭
//            UiUtil.runInMainThread(() -> mPresenter.createHomeSC(generateCodeJson.getArea_name()));
//    }
//
//    @Override
//    public void invitationCheckFail(int errorCode, String msg) {
//        showTipsDialog();
//    }
//
//    /**
//     * 创建家庭成功返回id绑定云端
//     *
//     * @param cloudId
//     */
//    @Override
//    public void createHomeSCSuccess(int cloudId) {
//        //更新云id
//        int cloudUserId = UserUtils.getCloudUserId();
//        dbManager.updateHomeCompanyCloudId(homeCompanyBean.getLocalId(), cloudId, cloudUserId);
//        homeCompanyBean.setId(cloudId);
//        homeCompanyBean.setCloud_user_id(cloudUserId);
//        Constant.CurrentHome = homeCompanyBean;
//
//        BindCloudRequest request = new BindCloudRequest();
//        request.setCloud_area_id(cloudId);
//        request.setCloud_user_id(UserUtils.getCloudUserId());
//        HttpConfig.addHeader(homeCompanyBean.getSa_user_token());
//
//        mPresenter.bindCloudSC(request);
//    }
//
//    @Override
//    public void createHomeSCFail(int errorCode, String msg) {
//        ToastUtil.show(msg);
//    }
//
//    /**
//     * 显示弹窗
//     */
//    private void showTipsDialog() {
//        if (scanFailDialog != null && !scanFailDialog.isShowing()) {
//            scanFailDialog.show(this);
//        }
//    }
//
//    /**
//     * 获取用户信息
//     */
//    private void getUserInfo() {
//        new DBThread(() -> {
//            UserInfoBean userInfoBean = dbManager.getUser();
//            if (userInfoBean != null) {
//                mainThreadHandler.post(() -> {
//                    nickname = userInfoBean.getNickname();
//                });
//            }
//        }).start();
//    }
//
//    /**
//     * 插入家庭
//     */
//    private void insertHome(HomeCompanyBean homeCompanyBean) {
//        dbManager.insertHomeCompany(homeCompanyBean, null);
//        mainThreadHandler.post(() -> {
//            Constant.CurrentHome = homeCompanyBean;
//            toMain();
//        });
//    }
//
//    /**
//     * 检查sa的token是否存在
//     *
//     * @param qrCode
//     */
//    private void checkToken(GenerateCodeJson qrCode) {
//        UiUtil.starThread(() -> {
//            saToken = dbManager.getSaTokenByUrl(qrCode.getUrl());
//            mainThreadHandler.post(() -> {
//                if (!TextUtils.isEmpty(saToken)) {
//                    HttpConfig.addHeader(saToken);
//                }
//                String body = "{\"qr_code\":\"" + generateCodeJson.getQr_code() + "\", \"nickname\":\"" + nickname + "\"}";
//                mPresenter.invitationCheck(body, qrCode.getArea_id());
//            });
//        });
//    }
//
//    private void toMain() {
//        if (homeCompanyBean != null) {
//            EventBus.getDefault().post(new HomeEvent(TextUtils.isEmpty(saToken), homeCompanyBean));
//            EventBus.getDefault().post(new HomeSelectedEvent(generateCodeJson.getArea_name()));
//            ToastUtil.show(String.format(UiUtil.getString(R.string.home_welcome_add_home), generateCodeJson.getArea_name()));
//        }
//        switchToActivity(MainActivity.class);
//        finish();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//}