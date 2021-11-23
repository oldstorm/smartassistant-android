package com.yctc.zhiting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.imageutil.GlideUtil;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.BrandDetailContract;
import com.yctc.zhiting.activity.presenter.BrandDetailPresenter;
import com.yctc.zhiting.adapter.PluginAdapter;
import com.yctc.zhiting.adapter.SupportedDeviceAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.entity.home.ScanResultBean;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.entity.mine.BrandDetailBean;
import com.yctc.zhiting.entity.mine.BrandsBean;
import com.yctc.zhiting.entity.mine.PluginsBean;
import com.yctc.zhiting.entity.mine.SupportDevicesBean;
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
 * 品牌详情
 */
public class BrandDetailActivity extends MVPBaseActivity<BrandDetailContract.View, BrandDetailPresenter> implements  BrandDetailContract.View {

    @BindView(R.id.ivCover)
    ImageView ivCover;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvUpdate)
    TextView tvUpdate;
    @BindView(R.id.tvAdded)
    TextView tvAdded;
    @BindView(R.id.ringProgressBar)
    RingProgressBar ringProgressBar;
    @BindView(R.id.rvPlugin)
    RecyclerView rvPlugin;
    @BindView(R.id.rvDevice)
    RecyclerView rvDevice;
    @BindView(R.id.tvDevice)
    TextView tvDevice;

    private PluginAdapter pluginAdapter;
    private SupportedDeviceAdapter supportedDeviceAdapter;
    private String name;
    private IWebSocketListener mIWebSocketListener;
    private BrandsBean brandsBean;
    private Map<Long, PluginOperateBean> map = new HashMap<>();  // 存储操作插件

    @Override
    protected int getLayoutId() {
        return R.layout.activity_brand_detail;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.mine_mine_device_detail));
        initRvPlugin();
        initRvDevice();
//        initWebSocket();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);

        brandsBean = (BrandsBean) intent.getSerializableExtra(IntentConstant.BEAN);
        name = brandsBean.getName();
//        setData(brandsBean); // 展示数据
        mPresenter.getDetail(name);   //插件状态要与品牌列表保持一致，不调接口
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    // 更新状态
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PluginsBean pluginsBean){
        for (PluginsBean plugin : pluginAdapter.getData()){
            if (pluginsBean.getId().equals(plugin.getId())){
                plugin.setUpdating(pluginsBean.isUpdating());
                plugin.setIs_added(pluginsBean.isIs_added());
                plugin.setIs_newest(pluginsBean.isIs_newest());
                pluginAdapter.notifyItemChanged(pluginAdapter.getData().indexOf(plugin));
                setStatus();
                break;
            }
        }
    }

    /**
     * 插件列表
     */
    private void initRvPlugin(){
        rvPlugin.setFocusable(false);
        pluginAdapter = new PluginAdapter();
        rvPlugin.setAdapter(pluginAdapter);
        rvPlugin.setLayoutManager(new LinearLayoutManager(this));

        pluginAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(IntentConstant.BEAN, pluginAdapter.getItem(position));
            switchToActivity(PluginDetailActivity.class, bundle);
        });

        pluginAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            PluginsBean pluginsBean = pluginAdapter.getItem(position);
            if (view.getId() == R.id.tvDel){ // 删除
                // 需要判断是否安装该插件已安装提示  确定删除该插件吗？删除后CC、YY不能使用
                // 为安装提示   确定删除该插件吗？
                CenterAlertDialog centerAlertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.mine_mine_plugin_del_tips_1), null);
                centerAlertDialog.setConfirmListener((del) -> {
//                    removePluginByWebSocket(centerAlertDialog, pluginsBean, position);
                    List<String> pluginIds = new ArrayList<>();
                    pluginIds.add(pluginsBean.getId());
                    pluginsBean.setUpdating(true);
                    pluginAdapter.notifyItemChanged(position);
                    AddOrUpdatePluginRequest addOrUpdatePluginRequest = new AddOrUpdatePluginRequest(pluginIds);
                    mPresenter.removePlugins(addOrUpdatePluginRequest, name, position);
                    centerAlertDialog.dismiss();
                });

                centerAlertDialog.show(BrandDetailActivity.this);
            }else if (view.getId() == R.id.tvUpdate || view.getId() == R.id.tvAdd){  // 添加/更新
//                updatePluginByWebSocket(pluginsBean, position);
                List<String> pluginIds = new ArrayList<>();
                pluginIds.add(pluginsBean.getId());
                pluginsBean.setUpdating(true);
                pluginAdapter.notifyItemChanged(position);
                operatePluginByHttp(pluginIds, position);
            }
//            else if (view.getId() == R.id.tvAdd){  // 添加
//                addPluginByWebSocket(pluginsBean, position);
//            }
        });
    }

    /**
     * WebSocket更新插件
     * @param pluginsBean
     * @param position
     */
    @Deprecated
    private void updatePluginByWebSocket(PluginsBean pluginsBean, int position){
        PluginOperateBean pluginOperateBean = new PluginOperateBean(SupportBrandActivity.pId, Constant.PLUGIN, Constant.UPDATE, new PluginOperateBean.ServiceDataBean(pluginsBean.getId()));
        map.put(SupportBrandActivity.pId, pluginOperateBean);
        SupportBrandActivity.pId++;
        operatePlugins(pluginOperateBean);
        pluginsBean.setUpdating(true);
        pluginAdapter.notifyItemChanged(position);
    }

    /**
     * WebSocket添加插件
     * @param pluginsBean
     * @param position
     */
    @Deprecated
    private void addPluginByWebSocket(PluginsBean pluginsBean, int position){
        PluginOperateBean pluginOperateBean = new PluginOperateBean(SupportBrandActivity.pId, Constant.PLUGIN, Constant.INSTALL, new PluginOperateBean.ServiceDataBean(pluginsBean.getId()));
        map.put(SupportBrandActivity.pId, pluginOperateBean);
        SupportBrandActivity.pId++;
        operatePlugins(pluginOperateBean);
        pluginsBean.setUpdating(true);
        pluginAdapter.notifyItemChanged(position);
    }

    /**
     * 通过websokcet删除插件
     * @param pluginsBean
     * @param position
     */
    @Deprecated
    private void removePluginByWebSocket(CenterAlertDialog centerAlertDialog, PluginsBean pluginsBean, int position){
        PluginOperateBean pluginOperateBean = new PluginOperateBean(SupportBrandActivity.pId, Constant.PLUGIN, Constant.REMOVE, new PluginOperateBean.ServiceDataBean(pluginsBean.getId()));
        SupportBrandActivity.pId++;
        operatePlugins(pluginOperateBean);
        centerAlertDialog.dismiss();
        pluginsBean.setIs_added(false);
        pluginsBean.setIs_newest(false);
        pluginsBean.setUpdating(false);
        centerAlertDialog.dismiss();
        pluginAdapter.notifyItemChanged(position);
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
     * 设备列表
     */
    private void initRvDevice(){
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
                            // 遍历插件
                            for (PluginsBean pluginsBean : pluginAdapter.getData()){
                                if (pluginsBean.getId().equals(pluginOperateBean.getService_data().getPlugin_id())){
                                    String service = pluginOperateBean.getService();
                                    if (service.equals(Constant.INSTALL)){  // 添加
                                        pluginsBean.setIs_added(true);
                                        pluginsBean.setIs_newest(true);
                                    }else if (service.equals(Constant.UPDATE)){  // 更新
                                        pluginsBean.setIs_newest(true);
                                    }else if (service.equals(Constant.REMOVE)){  // 删除
                                        pluginsBean.setIs_added(false);
                                        pluginsBean.setIs_newest(false);
                                    }
                                    pluginsBean.setUpdating(false);
                                    EventBus.getDefault().post(pluginsBean);
//                                    pluginAdapter.notifyItemChanged(pluginAdapter.getData().indexOf(pluginsBean));
                                }
                            }
                            if (isAllAdded()){
                                setStatus();
                            }
                        }
                        map.remove(scanBean.getId());
                    }
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                t.printStackTrace();
            }
        };
        WSocketManager.getInstance().addWebSocketListener(mIWebSocketListener);
    }

    @OnClick(R.id.tvUpdate)
    void onClickUpdate(){
        setRingProgressBar();
//        operatePluginByWebSocket();
        addOrUpdateAllPlugin();

    }

    @Override
    protected void onDestroy() {
        WSocketManager.getInstance().removeWebSocketListener(mIWebSocketListener);
        super.onDestroy();
    }

    /**
     * 添加更新全部插件
     */
    private void addOrUpdateAllPlugin(){
        List<String> pluginIds = new ArrayList<>();
        for (PluginsBean pluginsBean : pluginAdapter.getData()){
            pluginIds.add(pluginsBean.getId());
            pluginsBean.setUpdating(true);
            pluginAdapter.notifyItemChanged(pluginAdapter.getData().indexOf(pluginsBean));
        }
        operatePluginByHttp(pluginIds, -1);
    }

    /**
     * 通过http操作插件
     */
    private void operatePluginByHttp(List<String> pluginIds, int position){
        AddOrUpdatePluginRequest addOrUpdatePluginRequest = new AddOrUpdatePluginRequest(pluginIds);
        mPresenter.addOrUpdatePlugins(addOrUpdatePluginRequest, name, position);

    }

    /**
     * websocket操作插件
     */
    @Deprecated
    private void operatePluginByWebSocket(){
        for (PluginsBean pluginsBean : pluginAdapter.getData()){
            PluginOperateBean pluginOperateBean = new PluginOperateBean(SupportBrandActivity.pId, Constant.PLUGIN, Constant.INSTALL, new PluginOperateBean.ServiceDataBean(pluginsBean.getId()));
            map.put(SupportBrandActivity.pId, pluginOperateBean);
            SupportBrandActivity.pId++;
            operatePlugins(pluginOperateBean);
            pluginsBean.setUpdating(true);
            pluginAdapter.notifyItemChanged(pluginAdapter.getData().indexOf(pluginsBean));
        }
    }

    /**
     * 显示数据
     * @param brandsBean
     */
    private void setData(BrandsBean brandsBean){
        if (brandsBean!=null) {
            tvName.setText(brandsBean.getName());
            GlideUtil.load(brandsBean.getLogo_url()).into(ivCover);
            boolean isNews = brandsBean.isIs_newest();
            boolean isAdded = brandsBean.isIs_added();

            List<SupportDevicesBean> deviceData = new ArrayList<>();
            List<PluginsBean> plugins = brandsBean.getPlugins();
            if (CollectionUtil.isNotEmpty(plugins)) {  // 插件数据
                pluginAdapter.setNewData(plugins);
                for (PluginsBean pluginsBean : plugins){
                    if (CollectionUtil.isNotEmpty(pluginsBean.getSupport_devices())){
                        deviceData.addAll(pluginsBean.getSupport_devices());
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(deviceData)) {  // 支持设备数据
//                tvDevice.setVisibility(View.VISIBLE);
//                supportedDeviceAdapter.setNewData(deviceData);
            }
            setStatus();
        }
    }

    private void setStatus(){
        // 暂时不显示
//        if (isUpdating()){
//            setRingProgressBar();
//            tvUpdate.setVisibility(View.GONE);
//            tvAdded.setVisibility( View.GONE);
//        }else {
//            ringProgressBar.setVisibility(View.GONE);
//            if (isAllAdded()) {
//                tvUpdate.setText(getResources().getString(R.string.mine_mine_all_update));
//                tvUpdate.setVisibility(isAllNewest() ? View.GONE : View.VISIBLE);
//                tvAdded.setVisibility(isAllNewest() ? View.VISIBLE : View.GONE);
//            } else {
//                tvUpdate.setText(getResources().getString(R.string.brand_all_add));
//                tvUpdate.setVisibility(View.VISIBLE);
//            }
//        }
    }

    /**
     * 设置loading可见
     */
    private void setRingProgressBar(){
        ringProgressBar.setVisibility(View.VISIBLE);
        tvUpdate.setVisibility(View.GONE);
        ringProgressBar.setRotating(true);
        ringProgressBar.setProgress(30);
    }

    /**
     * 所有插件是否添加
     * @return
     */
    private boolean isAllAdded(){
        for (PluginsBean pluginsBean : pluginAdapter.getData()){
            if (!pluginsBean.isIs_added()){
                return false;
            }
        }
        return true;
    }

    /**
     * 所有插件是否最新
     * @return
     */
    private boolean isAllNewest(){
        for (PluginsBean pluginsBean : pluginAdapter.getData()){
            if (!pluginsBean.isIs_newest()){
                return false;
            }
        }
        return true;
    }

    /**
     * 是否数据在更新
     * @return
     */
    private boolean isUpdating(){
        for (PluginsBean pluginsBean : pluginAdapter.getData()){
            if (pluginsBean.isUpdating()){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取详情成功
     * @param brandDetailBean
     */
    @Override
    public void getDetailSuccess(BrandDetailBean brandDetailBean) {
        if (brandDetailBean!=null){

            BrandsBean brandsBean = brandDetailBean.getBrand();
            if (brandsBean!=null) {
                tvName.setText(brandsBean.getName());
                GlideUtil.load(brandsBean.getLogo_url()).into(ivCover);
                boolean isNews = brandsBean.isIs_newest();
                boolean isAdded = brandsBean.isIs_added();
//                if (isAdded) { // 已添加
//                    tvAdded.setVisibility(isNews ? View.VISIBLE : View.GONE);  // 是最新，已添加可见
//                    tvUpdate.setText(getResources().getString(R.string.mine_mine_all_update));
//                    tvUpdate.setVisibility(isNews ? View.GONE : View.VISIBLE);// 不是最新，更新可见
//                } else {
//                    tvUpdate.setText(getResources().getString(R.string.mine_add));
//                    tvUpdate.setVisibility(View.VISIBLE);
//                }
                if (CollectionUtil.isNotEmpty(brandsBean.getPlugins())) {  // 插件数据
                    pluginAdapter.setNewData(brandsBean.getPlugins());
                }
                if (CollectionUtil.isNotEmpty(brandsBean.getSupport_devices())) {  // 支持设备数据
                    tvDevice.setVisibility(View.VISIBLE);
                    supportedDeviceAdapter.setNewData(brandsBean.getSupport_devices());
                }
            }
        }
    }

    /**
     * 获取详情失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getDetailFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }


    /**
     * 添加更新插件成功
     * @param operatePluginBean
     * @param position
     */
    @Override
    public void addOrUpdatePluginsSuccess(OperatePluginBean operatePluginBean, int position) {
        brandsBean.setIs_added(true);
        brandsBean.setIs_newest(true);
        if (operatePluginBean !=null) {
            List<String> successPluginIds = operatePluginBean.getSuccess_plugins();
            if (position < 0) {  // 全部添加/更新
                for (PluginsBean pluginsBean : pluginAdapter.getData()) {
                    pluginsBean.setUpdating(false);
                    if (successPluginIds!=null) {
                        for (String successPluginId : successPluginIds) {
                            if (pluginsBean.getId().equals(successPluginId)) {
                                pluginsBean.setIs_newest(true);
                                pluginsBean.setIs_added(true);
                                break;
                            }
                        }
                    }
                }
                pluginAdapter.notifyDataSetChanged();
            } else {  // 单个添加/更新
                PluginsBean pluginsBean = pluginAdapter.getItem(position);
                pluginsBean.setUpdating(false);
                pluginsBean.setIs_added(true);
                pluginsBean.setIs_newest(true);
                pluginAdapter.notifyItemChanged(position);
                EventBus.getDefault().post(pluginsBean);
            }
            setStatus();
        }
    }

    /**
     * 添加更新插件失败
     * @param errorCode
     * @param msg
     * @param position
     */
    @Override
    public void addOrUpdatePluginsFail(int errorCode, String msg, int position) {
        if (position < 0){  // 全部添加/更新
            for (PluginsBean pluginsBean : pluginAdapter.getData()) {
                pluginsBean.setUpdating(false);
            }
            pluginAdapter.notifyDataSetChanged();
        }else { // 单个添加/更新
            PluginsBean pluginsBean = pluginAdapter.getItem(position);
            pluginsBean.setUpdating(false);
            pluginAdapter.notifyItemChanged(position);
            EventBus.getDefault().post(pluginsBean);
        }
        setStatus();
        ToastUtil.show(msg);

    }

    /**
     * 删除插件成功
     * @param operatePluginBean
     * @param position
     */
    @Override
    public void removePluginsSuccess(OperatePluginBean operatePluginBean, int position) {
        PluginsBean pluginsBean = pluginAdapter.getItem(position);
        pluginsBean.setUpdating(false);
        pluginsBean.setIs_newest(false);
        pluginsBean.setIs_added(false);
        pluginAdapter.notifyItemChanged(position);
        EventBus.getDefault().post(pluginsBean);
        ToastUtil.show(UiUtil.getString(R.string.mine_remove_success));
    }

    /**
     * 删除插件失败
     * @param errorCode
     * @param msg
     * @param position
     */
    @Override
    public void removePluginsFail(int errorCode, String msg, int position) {
        PluginsBean pluginsBean = pluginAdapter.getItem(position);
        pluginsBean.setUpdating(false);
        pluginAdapter.notifyItemChanged(position);
        EventBus.getDefault().post(pluginsBean);
        ToastUtil.show(msg);
    }
}