package com.yctc.zhiting.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.AndroidUtil;
import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.fileutil.BaseFileUtil;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.DownloadFailListener;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.Header;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SetDevicePositionContract;
import com.yctc.zhiting.activity.presenter.SetDevicePositionPresenter;
import com.yctc.zhiting.adapter.PositionAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.EditBottomDialog;
import com.yctc.zhiting.entity.DeviceDetailEntity;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.mine.AreasBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.PluginsBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.event.DeviceRefreshEvent;
import com.yctc.zhiting.request.AddRoomRequest;
import com.yctc.zhiting.request.ModifyDeviceRequest;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.ZipUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.lizhangqu.coreprogress.ProgressUIListener;

/**
 * 设置设备位置
 */
public class SetDevicePositionActivity extends MVPBaseActivity<SetDevicePositionContract.View, SetDevicePositionPresenter> implements SetDevicePositionContract.View {

    @BindView(R.id.etDeviceName)
    EditText etDeviceName;
    @BindView(R.id.tvAddArea)
    TextView tvAddArea;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private PositionAdapter mPositionAdapter;
    private List<LocationBean> mLocationList = new ArrayList<>();
    private int mDeviceId;//设备id
    private int mLocationId;//房间id
    private String pluginId; // 插件id
    private String mDeviceName;//设备名称
    private String control;//相对路径

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_device_posotion;
    }

    @Override
    protected void initUI() {
        super.initUI();
        //HttpConfig.addHeader("MTYyMjAxNjYzOHxOd3dBTkZSQ1ZFVktTMVJOVlV4VVNrRlJRVkV5UlU0MlYxYzFRVE5JV2twVlJVOUNTMVF5TjA5UE5EZERUVkJXTmpOR1EwUldRVUU9fNhz67_KPiTN6Ab74Yx-eP2L8Xhhz5TsE0cJkRySfD8N");
        setTitleCenter(UiUtil.getString(R.string.home_setting));
        initRecyclerView();
        mPresenter.getAreaList();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mDeviceId = intent.getIntExtra(IntentConstant.ID, 0);
        mLocationId = intent.getIntExtra(IntentConstant.RA_ID, 0);
        control = intent.getStringExtra(IntentConstant.CONTROL);
        pluginId = intent.getStringExtra(IntentConstant.PLUGIN_ID);
        mDeviceName = intent.getStringExtra(IntentConstant.NAME);
//        etDeviceName.setText(mDeviceName);
        mPresenter.getDeviceDetailRestructure(mDeviceId);
    }

    private void initRecyclerView() {
        mPositionAdapter = new PositionAdapter(mLocationList);
        recyclerView.setAdapter(mPositionAdapter);

        mPositionAdapter.setOnItemClickListener((adapter, view, position) -> {
            for (int i = 0; i < mLocationList.size(); i++)
                if (i != position) mLocationList.get(i).setCheck(false);
            LocationBean bean = mPositionAdapter.getItem(position);
            bean.setCheck(!bean.isCheck());
            adapter.notifyDataSetChanged();
            mLocationId = bean.isCheck() ? bean.getId() : 0;
        });
    }

    @OnClick({R.id.tvComplete, R.id.tvAddArea})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvComplete:
                complete();
                break;
            case R.id.tvAddArea:
                showAddAreaDialog();
                break;
        }
    }

    /**
     * 添加房间、区域对话框
     */
    private void showAddAreaDialog() {
        String title = UiUtil.getString(R.string.mine_room_name);
        String tip = UiUtil.getString(R.string.mine_input_room_name);

        EditBottomDialog editBottomDialog = EditBottomDialog.newInstance(title, tip, null, 1);
        editBottomDialog.setClickSaveListener(content -> {
            AndroidUtil.hideSoftInput(LibLoader.getCurrentActivity());
            mPresenter.addAreaRoom(new AddRoomRequest(content));
        }).show(this);
    }

    /**
     * 完成
     */
    private void complete() {
        mDeviceName = etDeviceName.getText().toString();
        if (TextUtils.isEmpty(mDeviceName)) {
            ToastUtil.show(UiUtil.getString(R.string.home_input_device_name_tip));
            return;
        }
        if (mDeviceName.length() > 20) {
            ToastUtil.show(UiUtil.getString(R.string.home_device_name_lenght_tip));
            return;
        }
        LogUtil.e("complete============完成");
        Request request = new ModifyDeviceRequest(mDeviceName, mLocationId);
        mPresenter.updateDeviceName(String.valueOf(mDeviceId), request);
    }

    @Override
    public void onAreasSuccess(AreasBean data) {
        if (data != null && data.getLocations() != null && data.getLocations().size() > 0) {
            mLocationList = data.getLocations();
            if (mLocationId > 0) {
                for (int i = 0; i < mLocationList.size(); i++) {
                    if (mLocationList.get(i).getLocationId() == mLocationId) {
                        mLocationList.get(i).setCheck(true);
                        break;
                    }
                }
            }
            mPositionAdapter.setNewData(mLocationList);
        }
    }

    @Override
    public void onAreasFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public void onAddAreaRoomSuccess(AreasBean data) {
        mPresenter.getAreaList();
    }

    @Override
    public void onAddAreaRoomFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public void onUpdateDeviceNameSuccess() {
        EventBus.getDefault().post(new DeviceRefreshEvent());
        mPresenter.getPluginDetail(pluginId);
    }

    @Override
    public void onUpdateDeviceNameFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 插件详情成功
     *
     * @param pluginDetailBean
     */
    @Override
    public void getPluginDetailSuccess(PluginDetailBean pluginDetailBean) {

        if (pluginDetailBean != null) {
            PluginsBean pluginsBean = pluginDetailBean.getPlugin();
            if (pluginsBean != null) {
                String pluginId = pluginsBean.getId();
                String pluginFilePath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/plugins/" + pluginId;

                String downloadUrl = pluginsBean.getDownload_url();
                String cachePluginJson = SpUtil.get(pluginId);
                PluginsBean cachePlugin = GsonConverter.getGson().fromJson(cachePluginJson, PluginsBean.class);
                String cacheVersion = "";
                if (cachePlugin != null) {
                    cacheVersion = cachePlugin.getVersion();
                }
                String version = pluginsBean.getVersion();
                if (BaseFileUtil.isFileExist(pluginFilePath) &&
                        !TextUtils.isEmpty(cacheVersion) && !TextUtils.isEmpty(version) && cacheVersion.equals(version)) {  // 如果缓存存在且版本相同
                    String urlPath = "file://" + pluginFilePath + "/" + control;
                    toDeviceDetail(urlPath);
                } else {
                    if (!TextUtils.isEmpty(downloadUrl)) {
                        String suffix = downloadUrl.substring(downloadUrl.lastIndexOf(".") + 1);
                        BaseFileUtil.deleteFolderFile(pluginFilePath, true);
                        String fileZipPath = pluginFilePath + "." + suffix;
                        File file = new File(fileZipPath);
                        BaseFileUtil.deleteFile(file);
                        List<Header> headers = new ArrayList<>();
                        headers.add(new Header("Accept-Encoding", "identity"));
                        headers.add(new Header(HttpConfig.TOKEN_KEY, HomeUtil.getSaToken()));
                        String path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/plugins/";
                        String finalPath = path;
                        String finalPluginFilePath = pluginFilePath;
                        String tip = UiUtil.getString(R.string.home_download_plugin_package_fail);
                        HTTPCaller.getInstance().downloadFile(downloadUrl, path, pluginId, headers.toArray(new Header[headers.size()]), UiUtil.getString(R.string.home_download_plugin_package_fail), new ProgressUIListener() {
                            @Override
                            public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                                LogUtil.e("进度：" + percent);
                                if (percent == 1) {
                                    LogUtil.e("下载完成");
                                    try {
                                        ZipUtils.decompressFile(new File(fileZipPath), finalPath, true);
                                        String pluginsBeanToJson = GsonConverter.getGson().toJson(pluginsBean);
                                        SpUtil.put(pluginId, pluginsBeanToJson);
                                        String urlPath = "file://" + finalPluginFilePath + "/" + control;
                                        toDeviceDetail(urlPath);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        ToastUtil.show(UiUtil.getString(R.string.unzip_file_exception));
                                    }
                                }
                            }
                        }, new DownloadFailListener() {
                            @Override
                            public void downloadFailed() {
                                UiUtil.runInMainThread(() -> ToastUtil.show(tip));
                            }
                        });
                    }
                }
            }
        }
    }

    private void toDeviceDetail(String urlPath) {
        DeviceMultipleBean deviceMultipleBean = new DeviceMultipleBean();
        deviceMultipleBean.setId(mDeviceId);
        deviceMultipleBean.setIs_sa(false);
        deviceMultipleBean.setName(mDeviceName);
        deviceMultipleBean.setLocation_id(mLocationId);
        bundle.putInt(IntentConstant.FROM, 1);
        bundle.putString(IntentConstant.PLUGIN_PATH, urlPath);
        bundle.putSerializable(IntentConstant.BEAN, deviceMultipleBean);
        switchToActivity(DeviceDetailActivity.class, bundle);
        finish();
    }

    /**
     * 插件详情失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getPluginDetailFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 设备详情成功 重构
     *
     * @param deviceDetailResponseEntity
     */
    @Override
    public void getDeviceDetailRestructureSuccess(DeviceDetailResponseEntity deviceDetailResponseEntity) {
        if (deviceDetailResponseEntity != null) {
            DeviceDetailEntity deviceDetailEntity = deviceDetailResponseEntity.getDevice_info();
            if (deviceDetailEntity != null) {
                etDeviceName.setText(deviceDetailEntity.getName());
                DeviceDetailEntity.PluginBean pluginBean = deviceDetailEntity.getPlugin();
                if (pluginBean != null) {
                    pluginId = pluginBean.getId();
                    if (TextUtils.isEmpty(control)) {
                        control = pluginBean.getControl();
                    }
                }
            }
        }
    }

    @Override
    public void getDeviceDetailRestructureFail(int errorCode, String msg) {

    }


}