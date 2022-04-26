package com.yctc.zhiting.activity;

import androidx.annotation.NonNull;
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
import com.app.main.framework.httputil.NameValuePair;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.ThirdPartyContract;
import com.yctc.zhiting.activity.presenter.ThirdPartyPresenter;
import com.yctc.zhiting.adapter.ThirdPartyAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.mine.ThirdPartyBean;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 第三方平台
 */
public class ThirdPartyActivity extends MVPBaseActivity<ThirdPartyContract.View, ThirdPartyPresenter> implements ThirdPartyContract.View {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rvData)
    RecyclerView rvData;
    @BindView(R.id.viewEmpty)
    View viewEmpty;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;

    private ThirdPartyAdapter mThirdPartyAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_third_party;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.mine_third_party));
        tvEmpty.setText(UiUtil.getString(R.string.no_data_hint));
        initRv();
        getData(true);
    }

    /**
     * 初始化列表
     */
    private void initRv() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData(false);
            }
        });
        rvData.setLayoutManager(new LinearLayoutManager(this));
        mThirdPartyAdapter = new ThirdPartyAdapter();
        rvData.setAdapter(mThirdPartyAdapter);
        mThirdPartyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ThirdPartyBean.AppsBean appsBean = mThirdPartyAdapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(IntentConstant.BEAN, appsBean);
                switchToActivityForResult(ThirdPartyWebActivity.class, bundle, 100);
            }
        });
    }

    /**
     * 获取数据
     *
     * @param showLoading
     */
    private void getData(boolean showLoading) {
        List<NameValuePair> requestData = new ArrayList<>();
        long areaId = Constant.CurrentHome.getId() > 0 ?  Constant.CurrentHome.getId() : Constant.CurrentHome.getArea_id();
        requestData.add(new NameValuePair(Constant.AREA_ID, String.valueOf(areaId)));
        boolean sc = true;
        if (TextUtils.isEmpty(Constant.CurrentHome.getSa_id())) {  // 本地家庭
            sc = true;
        } else {  // 有实体SA或虚拟SA
            if (!HomeUtil.isSAEnvironment() && !UserUtils.isLogin()) {  // 没有在SA环境且没有登录
                sc = true;
            } else {
                sc = false;
            }
        }
        mPresenter.getThirdPartyList(sc, requestData, showLoading);
    }

    /**
     * 设置空视图
     *
     * @param show
     */
    private void setNullView(boolean show) {
        viewEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        rvData.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /**
     * 解除绑定之后需要刷新数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                getData(true);
            }
        }
    }

    /**
     * 第三方云绑定列表成功
     *
     * @param thirdPartyBean
     */
    @Override
    public void getThirdPartyListSuccess(ThirdPartyBean thirdPartyBean) {
        refreshLayout.finishRefresh();
        if (thirdPartyBean != null) {
            List<ThirdPartyBean.AppsBean> appsBeanList = thirdPartyBean.getApps();
            if (CollectionUtil.isNotEmpty(appsBeanList)) {
                setNullView(false);
                mThirdPartyAdapter.setNewData(appsBeanList);
            } else {
                setNullView(true);
            }
        }
    }

    /**
     * 第三方云绑定列表失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getThirdPartyListFail(int errorCode, String msg) {
        refreshLayout.finishRefresh();
        ToastUtil.show(msg);
    }
}