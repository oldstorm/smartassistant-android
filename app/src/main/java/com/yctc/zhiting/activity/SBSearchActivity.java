package com.yctc.zhiting.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.fileutil.BaseFileUtil;
import com.app.main.framework.httputil.NameValuePair;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SBSearchContract;
import com.yctc.zhiting.activity.presenter.SBSearchPresenter;
import com.yctc.zhiting.adapter.SbCreateAdapter;
import com.yctc.zhiting.adapter.SupportBrandAdapter;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.entity.CreatePluginListBean;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.entity.mine.BrandsBean;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.entity.mine.PluginsBean;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 支持品牌搜索
 */
public class SBSearchActivity extends MVPBaseActivity<SBSearchContract.View, SBSearchPresenter> implements SBSearchContract.View {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rvBrand)
    RecyclerView rvBrand;
    @BindView(R.id.layoutNull)
    View layoutNull;
    @BindView(R.id.ivEmpty)
    ImageView ivEmpty;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;
    @BindView(R.id.etKey)
    EditText etKey;


    private int type; // 表示搜索系统（0）还是搜索创作（1）
    private SupportBrandAdapter supportBrandAdapter;
    private SbCreateAdapter mSbCreateAdapter;
    private boolean feedback;
    private List<BrandsBean> brandData;
    private List<CreatePluginListBean.PluginsBean> createPluginData;
    private final int DETAIL_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.sb_search_bottom_in, R.anim.sb_search_bottom_out);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.sb_search_top_in, R.anim.sb_search_top_out);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sb_search;
    }

    @Override
    protected void initUI() {
        super.initUI();
        ivEmpty.setImageResource(R.drawable.icon_empty_data);
        tvEmpty.setText(getResources().getString(R.string.common_no_content));
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData(false);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

            }
        });
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        type = intent.getIntExtra(IntentConstant.TYPE, 0);
        rvBrand.setLayoutManager(new LinearLayoutManager(this));
        supportBrandAdapter = new SupportBrandAdapter(false);
        mSbCreateAdapter = new SbCreateAdapter();
        rvBrand.setAdapter(type == 0 ? supportBrandAdapter : mSbCreateAdapter);
        setKeyListener();

        supportBrandAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(IntentConstant.BEAN, supportBrandAdapter.getItem(position));
                switchToActivity(BrandDetailActivity.class, bundle);
            }
        });

        supportBrandAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            operatePluginByHttp(view, position);
        });

        mSbCreateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CreatePluginListBean.PluginsBean pluginsBean = mSbCreateAdapter.getItem(position);
                if (pluginsBean.getBuild_status() == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(IntentConstant.BEAN, pluginsBean);
                    switchToActivityForResult(CreatePluginDetailActivity.class, bundle, DETAIL_REQUEST_CODE);
                }
            }
        });

        mSbCreateAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int viewId = view.getId();
                CreatePluginListBean.PluginsBean pluginsBean = mSbCreateAdapter.getItem(position);
                if (viewId == R.id.tvDel){ // 删除
                    if (pluginsBean.getBuild_status() == 1){
                        CenterAlertDialog alertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.home_not_in_lan_tips), getResources().getString(R.string.home_cancel), getResources().getString(R.string.confirm), false);
                        alertDialog.setConfirmListener(del -> {
                            alertDialog.dismiss();
                            removePlugin(pluginsBean, position);

                        });
                    }
                    removePlugin(pluginsBean, position);
                }
            }
        });
        getData(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DETAIL_REQUEST_CODE && resultCode == RESULT_OK){
            feedback = true;
            getData(true);
        }
    }

    /**
     * 删除插件
     * @param pluginsBean
     * @param position
     */
    private void removePlugin(CreatePluginListBean.PluginsBean pluginsBean, int position){
        pluginsBean.setLoading(true);
        mSbCreateAdapter.notifyItemChanged(position);
        mPresenter.removePlugin(pluginsBean.getId(), position);
        setNullView(CollectionUtil.isEmpty(mSbCreateAdapter.getData()));
    }

    @OnClick(R.id.tvCancel)
    void onClickCancel(){
        finishAct();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAct();
    }

    private void finishAct(){
        if (feedback){
            setResult(RESULT_OK);
        }
        finish();
    }

    /**
     * 搜索
     */
    private void setKeyListener() {
        etKey.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (TextUtils.isEmpty(etKey.getText().toString().trim())){
                    if (type == 0){
                        supportBrandAdapter.setNewData(brandData);
                    }else {
                        mSbCreateAdapter.setNewData(createPluginData);
                    }
                    setNullView(CollectionUtil.isEmpty(type == 0 ? brandData : createPluginData));
                }else {
                    dataFilter(etKey.getText().toString().trim());
                }
                return true;
            }
            return false;
        });
    }

    /**
     * 搜索数据
     */
    private void dataFilter(String key){
        if (type == 0){
            List<BrandsBean> data = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(brandData)) {
                for (BrandsBean brandsBean : brandData) {
                    String name = brandsBean.getName();
                    if (brandsBean.getName().toLowerCase().contains(key.toLowerCase())){
                        data.add(brandsBean);
                    }
                }
                supportBrandAdapter.setNewData(data);
                setNullView(CollectionUtil.isEmpty(data));
            }
        }else {
            List<CreatePluginListBean.PluginsBean> data = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(createPluginData)){
                for (CreatePluginListBean.PluginsBean pluginsBean : createPluginData){
                    if (pluginsBean.getName().toLowerCase().contains(key.toLowerCase())){
                        data.add(pluginsBean);
                    }
                }
                mSbCreateAdapter.setNewData(data);
                setNullView(CollectionUtil.isEmpty(data));
            }
        }
    }


    /**
     * 搜索
     * @param showLoading
     */
    private void getData(boolean showLoading){
        List<NameValuePair> requestData = new ArrayList<>();
        if (type == 0){
            String key = etKey.getText().toString().trim();
            if (!TextUtils.isEmpty(key))
            requestData.add(new NameValuePair("name", key));
            mPresenter.getBrandList(requestData, showLoading);
        }else {
            requestData.add(new NameValuePair("list_type", String.valueOf(1)));
            mPresenter.getCreateList(requestData, showLoading);
        }
    }

    /**
     * 通过http操作插件
     * @param view
     * @param position
     */
    private void operatePluginByHttp(View view, int position){
        BrandsBean brandsBean = supportBrandAdapter.getItem(position);
        if (view.getId() == R.id.tvUpdate) {
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


    @Override
    public void getBrandListSuccess(BrandListBean brandListBean) {
        setFinish();
        if (brandListBean != null) {
            if (CollectionUtil.isNotEmpty(brandListBean.getBrands())) {
                setNullView(false);
                brandData = brandListBean.getBrands();
                supportBrandAdapter.setNewData(brandListBean.getBrands());
            } else {
                setNullView(true);
            }
        } else {
            setNullView(true);
        }
    }

    @Override
    public void getBrandListFail(int errorCode, String msg) {
        setFinish();
        ToastUtil.show(msg);
    }

    @Override
    public void addOrUpdatePluginsSuccess(OperatePluginBean operatePluginBean, int position) {
        BrandsBean brandsBean = supportBrandAdapter.getItem(position);
        brandsBean.setUpdating(false);
        brandsBean.setIs_added(true);
        brandsBean.setIs_newest(true);
        supportBrandAdapter.notifyItemChanged(position);
        feedback = true;
    }

    @Override
    public void addOrUpdatePluginsFail(int errorCode, String msg, int position) {
        BrandsBean brandsBean = supportBrandAdapter.getItem(position);
        brandsBean.setUpdating(false);
        supportBrandAdapter.notifyItemChanged(position);
    }

    /**
     * 创作列表成功
     * @param createPluginListBean
     */
    @Override
    public void getCreateListSuccess(CreatePluginListBean createPluginListBean) {
        refreshLayout.finishRefresh();
        if (createPluginListBean!=null){
            List<CreatePluginListBean.PluginsBean> plugins = createPluginListBean.getPlugins();
            createPluginData = createPluginListBean.getPlugins();
            if (CollectionUtil.isNotEmpty(plugins)){
                mSbCreateAdapter.setNewData(plugins);
                setNullView(false);
            }else {
                setNullView(true);
            }
        }else {
            setNullView(true);
        }
    }

    /**
     * 创作列表失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getCreateListFail(int errorCode, String msg) {
        refreshLayout.finishRefresh();
        ToastUtil.show(msg);
    }

    /**
     * 删除插件成功
     * @param position
     */
    @Override
    public void removePluginSuccess(int position) {
        mSbCreateAdapter.getData().remove(position);
        mSbCreateAdapter.notifyItemRemoved(position);
        setNullView(CollectionUtil.isEmpty(mSbCreateAdapter.getData()));
    }

    /**
     * 删除插件失败
     * @param errorCode
     * @param msg
     * @param position
     */
    @Override
    public void removePluginFail(int errorCode, String msg, int position) {
        CreatePluginListBean.PluginsBean pluginsBean = mSbCreateAdapter.getItem(position);
        pluginsBean.setLoading(false);
        mSbCreateAdapter.notifyItemChanged(position);
    }

    private void setFinish() {
        refreshLayout.finishRefresh();
    }

    /**
     * 有无无数据
     */
    private void setNullView(boolean visible) {
        refreshLayout.setEnableRefresh(!visible);
        rvBrand.setVisibility(visible ? View.GONE : View.VISIBLE);
        layoutNull.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}