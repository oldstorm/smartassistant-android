package com.yctc.zhiting.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.BrandDetailContract;
import com.yctc.zhiting.activity.contract.PluginDetailContract;
import com.yctc.zhiting.activity.presenter.BrandDetailPresenter;
import com.yctc.zhiting.activity.presenter.PluginDetailPresenter;
import com.yctc.zhiting.adapter.SupportedDeviceAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.entity.home.ScanResultBean;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.entity.mine.PluginsBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.entity.scene.PluginOperateBean;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.websocket.IWebSocketListener;
import com.yctc.zhiting.websocket.WSocketManager;
import com.yctc.zhiting.widget.RingProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * 插件详情
 */
public class PluginDetailActivity extends MVPBaseActivity<PluginDetailContract.View, PluginDetailPresenter> implements  PluginDetailContract.View {

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvVersion)
    TextView tvVersion;
    @BindView(R.id.tvDesc)
    TextView tvDesc;
    @BindView(R.id.tvUpdate)
    TextView tvUpdate;
    @BindView(R.id.tvAdd)
    TextView tvAdd;
    @BindView(R.id.tvDel)
    TextView tvDel;
    @BindView(R.id.ringProgressBar)
    RingProgressBar ringProgressBar;

    @BindView(R.id.tvDevice)
    TextView tvDevice;
    @BindView(R.id.rvDevice)
    RecyclerView rvDevice;
    private SupportedDeviceAdapter supportedDeviceAdapter;

    private boolean updating; // 是否在更新中

    private String id; // 插件id

    private IWebSocketListener mIWebSocketListener;

    private PluginsBean pluginsBean;
    private Map<Long, PluginOperateBean> map = new HashMap<>();

    private int type;
    private String brand = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_plugin_detail;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.mine_mine_plugin_detail));
        initRv();
//        initWebSocket();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        pluginsBean = (PluginsBean) intent.getSerializableExtra(IntentConstant.BEAN);
//        setData();
        mPresenter.getDetail(pluginsBean.getId());  // 插件状态要与插件列表保持一致，不调接口
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    // 更新状态
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PluginsBean plugin){
        if (pluginsBean.getId().equals(plugin.getId())){
            pluginsBean.setUpdating(pluginsBean.isUpdating());
            pluginsBean.setIs_added(plugin.isIs_added());
            pluginsBean.setIs_newest(plugin.isIs_newest());
            if (!plugin.isUpdating()) {
                ringProgressBar.setRotating(false);
                ringProgressBar.setVisibility(View.GONE);
                tvAdd.setVisibility(plugin.isIs_added() ? View.GONE : View.VISIBLE);
                tvUpdate.setVisibility(plugin.isIs_added() && !plugin.isIs_newest() ? View.VISIBLE : View.GONE);
                tvDel.setVisibility(plugin.isIs_added() ? View.VISIBLE : View.GONE);
            }else {
                setRingProgressBar();
            }

        }
    }


    /**
     * 设置数据
     */
    private void setData(){
        if (pluginsBean != null){
            tvName.setText(pluginsBean.getName());
            tvVersion.setText( getResources().getString(R.string.brand_versionCode) + pluginsBean.getVersion());
            tvDesc.setText(pluginsBean.getInfo());
            boolean isNew = pluginsBean.isIs_newest();
            boolean isAdded = pluginsBean.isIs_added();
            tvDel.setVisibility(isAdded ? View.VISIBLE : View.GONE);
            tvUpdate.setVisibility(isAdded && !isNew ? View.VISIBLE : View.GONE);
            tvAdd.setVisibility(isAdded ? View.GONE : View.VISIBLE);
            updating = pluginsBean.isUpdating();
            if (CollectionUtil.isNotEmpty(pluginsBean.getSupport_devices())){
                tvDevice.setVisibility(View.VISIBLE);
                supportedDeviceAdapter.setNewData(pluginsBean.getSupport_devices());
            }
            if (updating){
                setRingProgressBar();
            }else {
                tvAdd.setVisibility(pluginsBean.isIs_added() ? View.GONE : View.VISIBLE);
                tvDel.setVisibility(pluginsBean.isIs_added() ? View.VISIBLE : View.GONE);
                tvUpdate.setVisibility(pluginsBean.isIs_added() && !pluginsBean.isIs_newest() ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * 设置loading可见
     */
    private void setRingProgressBar(){
        ringProgressBar.setVisibility(View.VISIBLE);
        tvUpdate.setVisibility(View.GONE);
        tvDel.setVisibility(View.GONE);
        tvAdd.setVisibility(View.GONE);
        ringProgressBar.setRotating(true);
        ringProgressBar.setProgress(30);
    }

    /**
     * 支持设备列表
     */
    private void initRv(){
        supportedDeviceAdapter = new SupportedDeviceAdapter();
        rvDevice.setAdapter(supportedDeviceAdapter);
        rvDevice.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * WebSocket初始化、添加监听
     */
    private void initWebSocket() {
        mIWebSocketListener = new IWebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                if (!TextUtils.isEmpty(text)) {
                    ScanResultBean scanBean = GsonConverter.getGson().fromJson(text, ScanResultBean.class);
                    if (scanBean != null && scanBean.isSuccess()) {
                        PluginOperateBean pluginOperateBean = map.get(scanBean.getId());
                        if (pluginOperateBean!=null){
                            if (pluginOperateBean.getService().equals(Constant.INSTALL)){
                               pluginsBean.setIs_added(true);
                               pluginsBean.setIs_newest(true);
                            }else if (pluginOperateBean.getService().equals(Constant.UPDATE)){
                                pluginsBean.setIs_newest(true);
                            }
                            pluginsBean.setUpdating(false);
                            EventBus.getDefault().post(pluginsBean);
                        }
                    }
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            }
        };
        WSocketManager.getInstance().addWebSocketListener(mIWebSocketListener);
    }



    /************************** 点击事件 *************************/
    @OnClick({R.id.tvDel, R.id.tvUpdate, R.id.tvAdd})
    void onClick(View view){
        PluginOperateBean pluginOperateBean = new PluginOperateBean(SupportBrandActivity.pId, Constant.PLUGIN, new PluginOperateBean.ServiceDataBean(pluginsBean.getId()));
        switch (view.getId()){
            case R.id.tvDel:  // 删除
                CenterAlertDialog centerAlertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.mine_mine_plugin_del_tips_1), null);
                centerAlertDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm(boolean del) {
//                        operatePlugin(Constant.REMOVE);
//                        pluginsBean.setIs_newest(false);
//                        pluginsBean.setIs_added(false);
//                        EventBus.getDefault().post(pluginsBean);
                        List<String> pluginIds = new ArrayList<>();
                        pluginIds.add(pluginsBean.getId());
                        AddOrUpdatePluginRequest addOrUpdatePluginRequest = new AddOrUpdatePluginRequest(pluginIds);
                        mPresenter.removePlugins(addOrUpdatePluginRequest, brand, 0);
                        setRingProgressBar();
                        centerAlertDialog.dismiss();
                    }
                });

                centerAlertDialog.show(this);
                break;

            case R.id.tvUpdate:  // 更新
//                operatePlugin(Constant.UPDATE);

            case R.id.tvAdd:  // 添加
//                operatePlugin(Constant.INSTALL);
                List<String> pluginIds = new ArrayList<>();
                pluginIds.add(pluginsBean.getId());
                AddOrUpdatePluginRequest addOrUpdatePluginRequest = new AddOrUpdatePluginRequest(pluginIds);
                mPresenter.addOrUpdatePlugins(addOrUpdatePluginRequest, brand, 0);
                setRingProgressBar();
                break;

        }

    }


    private void operatePlugin(String service){
        PluginOperateBean pluginOperateBean = new PluginOperateBean(SupportBrandActivity.pId, Constant.PLUGIN, new PluginOperateBean.ServiceDataBean(pluginsBean.getId()));
        pluginOperateBean.setService(service);
        operatePlugins(pluginOperateBean);
        setRingProgressBar();
        map.put(SupportBrandActivity.pId, pluginOperateBean);
        SupportBrandActivity.pId++;
    }

    /**
     * 操作插件
     * @param pluginOperateBean
     */
    private void operatePlugins(PluginOperateBean pluginOperateBean){
        String pluginJson = GsonConverter.getGson().toJson(pluginOperateBean);
        UiUtil.post(() -> WSocketManager.getInstance().sendMessage(pluginJson));
    }

    /**
     * 获取数据成功
     * @param pluginDetailBean
     */
    @Override
    public void getDetailSuccess(PluginDetailBean pluginDetailBean) {
        if (pluginDetailBean!=null){
            PluginsBean pluginsBean = pluginDetailBean.getPlugin();
            if (pluginsBean != null){
                tvName.setText(pluginsBean.getName());
                tvVersion.setText( getResources().getString(R.string.brand_versionCode) + pluginsBean.getVersion());
                tvDesc.setText(pluginsBean.getInfo());
                brand = pluginsBean.getBrand();
                boolean isNew = pluginsBean.isIs_newest();
                boolean isAdded = pluginsBean.isIs_added();
                tvDel.setVisibility(isAdded ? View.VISIBLE : View.GONE);
                tvUpdate.setVisibility(isAdded && !isNew ? View.VISIBLE : View.GONE);
                tvAdd.setVisibility(isAdded ? View.GONE : View.VISIBLE);
                if (CollectionUtil.isNotEmpty(pluginsBean.getSupport_devices())){
                    tvDevice.setVisibility(View.VISIBLE);
                    supportedDeviceAdapter.setNewData(pluginsBean.getSupport_devices());
                }
            }
        }
    }

    /**
     * 获取数据失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getDetailFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public void addOrUpdatePluginsSuccess(OperatePluginBean operatePluginBean, int position) {
        pluginsBean.setIs_added(true);
        pluginsBean.setIs_newest(true);
        pluginsBean.setUpdating(false);
        EventBus.getDefault().post(pluginsBean);
    }

    @Override
    public void addOrUpdatePluginsFail(int errorCode, String msg, int position) {
        pluginsBean.setUpdating(false);
        EventBus.getDefault().post(pluginsBean);
        ToastUtil.show(msg);
    }

    @Override
    public void removePluginsSuccess(OperatePluginBean operatePluginBean, int position) {
        pluginsBean.setUpdating(false);
        pluginsBean.setIs_added(false);
        pluginsBean.setIs_newest(false);
        EventBus.getDefault().post(pluginsBean);
        ToastUtil.show(UiUtil.getString(R.string.mine_remove_success));
    }

    @Override
    public void removePluginsFail(int errorCode, String msg, int position) {
        pluginsBean.setUpdating(false);
        EventBus.getDefault().post(pluginsBean);
        ToastUtil.show(msg);
    }

}