package com.yctc.zhiting.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseFragment;
import com.app.main.framework.httputil.NameValuePair;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.BrandDetailActivity;
import com.yctc.zhiting.adapter.SupportBrandAdapter;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.entity.mine.BrandsBean;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.entity.mine.PluginsBean;
import com.yctc.zhiting.fragment.contract.SBSystemContract;
import com.yctc.zhiting.fragment.presenter.SBSystemPresenter;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 支持品牌系统碎片
 */
public class SBSystemFragment extends MVPBaseFragment<SBSystemContract.View, SBSystemPresenter> implements SBSystemContract.View{

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


    private SupportBrandAdapter supportBrandAdapter;


    public static SBSystemFragment getInstance(){
        return new SBSystemFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sb_system;
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
        supportBrandAdapter = new SupportBrandAdapter(false);
        rvBrand.setLayoutManager(new LinearLayoutManager(getContext()));
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
            operatePluginByHttp(view, position);
        });
        getData(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            getData(true);
        }
    }

    /**
     * 获取数据
     */
    public void getData(boolean showLoading) {
        if (mPresenter!=null)
        mPresenter.getBrandList(requestData, showLoading);
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

    @Override
    public void getBrandListFail(int errorCode, String msg) {
        ToastUtil.show(msg);
        setFinish();
    }


    @Override
    public void addOrUpdatePluginsSuccess(OperatePluginBean operatePluginBean, int position) {
        BrandsBean brandsBean = supportBrandAdapter.getItem(position);
        brandsBean.setUpdating(false);
        brandsBean.setIs_added(true);
        brandsBean.setIs_newest(true);
        supportBrandAdapter.notifyItemChanged(position);
    }

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
