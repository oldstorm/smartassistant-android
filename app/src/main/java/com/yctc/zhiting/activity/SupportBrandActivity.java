package com.yctc.zhiting.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.fileutil.BaseFileUtil;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.NameValuePair;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SupportBrandContract;
import com.yctc.zhiting.activity.presenter.SupportBrandPresenter;
import com.yctc.zhiting.adapter.SupportBrandAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.UploadPluginDialog;
import com.yctc.zhiting.entity.home.ScanResultBean;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.entity.mine.BrandsBean;
import com.yctc.zhiting.entity.mine.PluginsBean;
import com.yctc.zhiting.entity.scene.PluginOperateBean;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.websocket.IWebSocketListener;
import com.yctc.zhiting.websocket.WSocketManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * 支持品牌
 */
public class SupportBrandActivity extends MVPBaseActivity<SupportBrandContract.View, SupportBrandPresenter> implements SupportBrandContract.View {

    @BindView(R.id.myTitle)
    RelativeLayout rlTitle;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.tvAdd)
    TextView tvAdd;
    @BindView(R.id.etKey)
    EditText etKey;
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rvBrand)
    RecyclerView rvBrand;
    @BindView(R.id.tvKey)
    TextView tvKey;
    @BindView(R.id.tvTips)
    TextView tvTips;
    @BindView(R.id.viewLine)
    View viewLine;
    @BindView(R.id.llSearch)
    LinearLayout llSearch;
    @BindView(R.id.layoutNull)
    View layoutNull;
    @BindView(R.id.ivEmpty)
    ImageView ivEmpty;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;

    private SupportBrandAdapter supportBrandAdapter;
    private IWebSocketListener mIWebSocketListener;
    public static long pId = 1;
    private Map<Integer, List<Long>> positionMap = new HashMap<>();  // 用于存supportBrandAdapter的位置和对应 更新/添加 发送websokcet的id
    private UploadPluginDialog uploadPluginDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_support_brand;
    }

    @Override
    protected void initUI() {
        super.initUI();
        ivEmpty.setImageResource(R.drawable.icon_empty_data);
        tvEmpty.setText(getResources().getString(R.string.common_no_content));
        initUploadPluginDialog();
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData(false);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

            }
        });
        supportBrandAdapter = new SupportBrandAdapter(false);
        rvBrand.setLayoutManager(new LinearLayoutManager(this));
        rvBrand.setAdapter(supportBrandAdapter);
        supportBrandAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(IntentConstant.BEAN, supportBrandAdapter.getItem(position));
                switchToActivity(BrandDetailActivity.class, bundle);
            }
        });
        supportBrandAdapter.setOnItemChildClickListener((adapter, view, position) -> {
//            operatePluginByWebSocket(view, position);
            operatePluginByHttp(view, position);
        });
        setKeyListener();
        initWebSocket();
        getData(true);
    }

    /**
     * 通过http操作插件
     * @param view
     * @param position
     */
    private void operatePluginByHttp(View view, int position){
        BrandsBean brandsBean = supportBrandAdapter.getItem(position);
        if (view.getId() == R.id.tvUpdate) {
            List<Long> pIds = new ArrayList<>();
            List<String> plugins = new ArrayList<>();
            for (int i = 0; i < brandsBean.getPlugins().size(); i++) {
                PluginsBean pluginsBean = brandsBean.getPlugins().get(i);
                plugins.add(pluginsBean.getId());
            }
            AddOrUpdatePluginRequest addOrUpdatePluginRequest = new AddOrUpdatePluginRequest(plugins);
            brandsBean.setUpdating(true);
            mPresenter.addOrUpdatePlugins(addOrUpdatePluginRequest, brandsBean.getName(), position);
            supportBrandAdapter.notifyItemChanged(position);
        }
    }

    /**
     * 用websocket方式操作插件
     * @param view
     * @param position
     */
    @Deprecated
    private void operatePluginByWebSocket(View view, int position){
        BrandsBean brandsBean = supportBrandAdapter.getItem(position);
        if (view.getId() == R.id.tvUpdate) {
            List<Long> pIds = new ArrayList<>();
            if (brandsBean.isIs_added()) {  // 已添加，则是更新

                for (int i = 0; i < brandsBean.getPlugins().size(); i++) {
                    PluginsBean pluginsBean = brandsBean.getPlugins().get(i);
                    if (!pluginsBean.isIs_newest() && pluginsBean.isIs_added()) {
                        PluginOperateBean pluginOperateBean = new PluginOperateBean(pId, Constant.PLUGIN, Constant.UPDATE, new PluginOperateBean.ServiceDataBean(pluginsBean.getId()));
                        pluginsBean.setPid(pId);
                        pluginsBean.setUpdating(true);
                        pIds.add(pId);
                        pId++;
                        operatePlugins(pluginOperateBean);
                    }
                }

            } else {  // 为添加，则添加
                for (int i = 0; i < brandsBean.getPlugins().size(); i++) {
                    PluginsBean pluginsBean = brandsBean.getPlugins().get(i);
                    if (!pluginsBean.isIs_added()) {
                        PluginOperateBean pluginOperateBean = new PluginOperateBean(pId, Constant.PLUGIN, Constant.INSTALL, new PluginOperateBean.ServiceDataBean(pluginsBean.getId()));
                        pluginsBean.setPid(pId);
                        pluginsBean.setUpdating(true);
                        pIds.add(pId);
                        pId++;
                        operatePlugins(pluginOperateBean);
                    }
                }
            }
            positionMap.put(position, pIds);
            brandsBean.setUpdating(true);
            supportBrandAdapter.notifyItemChanged(position);
        }
    }

    /**
     * 上传插件弹窗
     */
    private void initUploadPluginDialog(){
        uploadPluginDialog = new UploadPluginDialog();
        uploadPluginDialog.setClickUploadListener(new UploadPluginDialog.OnClickUploadListener() {
            @Override
            public void onUpload() {
                selectOtherFile();
            }

            @Override
            public void onConfirm(String filePath) {

            }

            @Override
            public void onClear() {

            }
        });
    }

    /**
     * 操作插件
     *
     * @param pluginOperateBean
     */
    private void operatePlugins(PluginOperateBean pluginOperateBean) {
        String pluginJson = GsonConverter.getGson().toJson(pluginOperateBean);
        UiUtil.post(() -> WSocketManager.getInstance().sendMessage(pluginJson));
    }

    private void setKeyListener() {
        etKey.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getData(true);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    /**
     * 获取数据
     */
    private void getData(boolean showLoading) {
        List<NameValuePair> requestData = new ArrayList<>();
        String key = etKey.getText().toString().trim();
        if (!TextUtils.isEmpty(key)) {
            requestData.add(new NameValuePair("name", key));
        }
        mPresenter.getBrandList(requestData, showLoading);
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @OnClick(R.id.ivBack)
    public void onClickBack() {
        onBackPressed();
    }

    /**
     * 搜索
     */
    @OnClick(R.id.tvKey)
    public void onClickSearch() {
        setSearch(true);
    }

    /**
     * 取消
     */
    @OnClick(R.id.tvCancel)
    public void onClickCancel() {
        etKey.setText("");
        setSearch(false);
    }

    /**
     * 添加插件
     */
    @OnClick(R.id.tvAdd)
    void onClickAdd() {
        if (uploadPluginDialog!=null && !uploadPluginDialog.isShowing()) {
            uploadPluginDialog.show(this);
        }
    }

    /**
     * 打开其他文件
     */
    private void selectOtherFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String filePath = BaseFileUtil.getFilePathByUri(this, uri);
            if (!filePath.endsWith(".zip")){
                ToastUtil.show(UiUtil.getString(R.string.only_zip));
                return;
            }
            uploadPlugin(filePath);
            LogUtil.e("onActivityResult=filePath:" + filePath);
        }
    }

    /**
     * 上传插件
     * @param filePath
     */
    private void uploadPlugin(String filePath){
        NameValuePair nameValuePair = new NameValuePair("file", filePath, true);
        List<NameValuePair> requestData = new ArrayList<>();
        requestData.add(nameValuePair);
        uploadPluginDialog.resetUploadStatus(1);
        mPresenter.uploadPlugin(requestData);
    }

    /**
     * 设置搜索显示内容
     */
    private void setSearch(boolean search) {
        tvKey.setVisibility(search ? View.GONE : View.VISIBLE);
        rlTitle.setVisibility(search ? View.GONE : View.VISIBLE);
        tvTips.setVisibility(search ? View.GONE : View.VISIBLE);
        viewLine.setVisibility(search ? View.GONE : View.VISIBLE);
        etKey.setVisibility(search ? View.VISIBLE : View.GONE);
        tvCancel.setVisibility(search ? View.VISIBLE : View.GONE);
        llSearch.setPadding(0, search ? UiUtil.dip2px(10) : 0, 0, search ? UiUtil.dip2px(21) : 0);
        keyboardSwitch(etKey, search);
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
                        for (Map.Entry<Integer, List<Long>> entry : positionMap.entrySet()) {
                            Integer mapKey = entry.getKey();
                            List<Long> mapValue = entry.getValue();
                            // 遍历插件
                            for (Long value : mapValue) {
                                if (value == scanBean.getId()) {
                                    int index = mapValue.indexOf(value);
                                    BrandsBean brandsBean = supportBrandAdapter.getItem(mapKey);
                                    for (PluginsBean pluginsBean : brandsBean.getPlugins()) {
                                        if (pluginsBean.getPid() == value) {
                                            pluginsBean.setUpdating(false);
                                            pluginsBean.setIs_newest(true);
                                            pluginsBean.setIs_added(true);
                                            EventBus.getDefault().post(pluginsBean);
                                        }
                                    }
                                    mapValue.remove(index);
                                }
                            }
                            // 设置列表状态
                            if (CollectionUtil.isEmpty(mapValue)) {
                                BrandsBean brandsBean = supportBrandAdapter.getItem(mapKey);
                                brandsBean.setUpdating(false);
                                brandsBean.setIs_newest(true);
                                brandsBean.setIs_added(true);
                                supportBrandAdapter.notifyItemChanged(mapKey);
                            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pId = 1;
        WSocketManager.getInstance().removeWebSocketListener(mIWebSocketListener);
    }

    /**
     * 支持品牌列表成功
     *
     * @param brandListBean
     */
    @Override
    public void getBrandListSuccess(BrandListBean brandListBean) {
        setFinish();
        if (brandListBean != null) {
            if (CollectionUtil.isNotEmpty(brandListBean.getBrands())) {
                setNullView(false);
                supportBrandAdapter.setNewData(brandListBean.getBrands());
            } else {
                setNullView(true);
            }
        } else {
            setNullView(true);
        }
    }

    /**
     * 支持品牌列表失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getBrandListFail(int errorCode, String msg) {
        setFinish();
    }

    /**
     * 上传插件成功
     * @param object
     */
    @Override
    public void uploadPluginSuccess(Object object) {
        uploadPluginDialog.resetUploadStatus(2);
        getData(true);
    }

    /**
     * 上传插件失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void uploadPluginFail(int errorCode, String msg) {
        uploadPluginDialog.resetUploadStatus(3);
    }

    /**
     * 添加更新插件成功
     * @param operatePluginBean
     * @param position
     */
    @Override
    public void addOrUpdatePluginsSuccess(OperatePluginBean operatePluginBean, int position) {
        BrandsBean brandsBean = supportBrandAdapter.getItem(position);
        brandsBean.setUpdating(false);
        brandsBean.setIs_added(true);
        brandsBean.setIs_newest(true);
        supportBrandAdapter.notifyItemChanged(position);
    }

    /**
     * 添加更新插件失败
     * @param errorCode
     * @param msg
     * @param position
     */
    @Override
    public void addOrUpdatePluginsFail(int errorCode, String msg, int position) {
        BrandsBean brandsBean = supportBrandAdapter.getItem(position);
        brandsBean.setUpdating(false);
        supportBrandAdapter.notifyItemChanged(position);
    }

    private void setFinish() {
        refreshLayout.finishRefresh();
    }

    /**
     * 有无无数据
     */
    private void setNullView(boolean visible) {
        rvBrand.setVisibility(visible ? View.GONE : View.VISIBLE);
        layoutNull.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}