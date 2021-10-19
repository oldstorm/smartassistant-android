package com.yctc.zhiting.activity;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.HomeCompanyContract;
import com.yctc.zhiting.activity.presenter.HomeCompanyPresenter;
import com.yctc.zhiting.adapter.HomeCompanyAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.event.RefreshHomeList;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UserUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 家庭/公司
 */
public class HomeCompanyActivity extends MVPBaseActivity<HomeCompanyContract.View, HomeCompanyPresenter> implements HomeCompanyContract.View {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rvHC)
    RecyclerView rvHC;
    @BindView(R.id.tvTodo)
    TextView tvTodo;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;
    @BindView(R.id.layout_null)
    View viewNull;
    @BindView(R.id.llAdd)
    LinearLayout llAdd;

    private DBManager dbManager;
    private WeakReference<Context> mContext;
    private List<HomeCompanyBean> mHomeList = new ArrayList<>();
    private HomeCompanyAdapter homeCompanyAdapter;
    private boolean isFirst = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home_company;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(this.getResources().getString(R.string.mine_home_company));
        tvEmpty.setText(getResources().getString(R.string.mine_home_empty));
        tvTodo.setVisibility(View.INVISIBLE);
        mContext = new WeakReference<>(this);
        dbManager = DBManager.getInstance(mContext.get());

        homeCompanyAdapter = new HomeCompanyAdapter();
        rvHC.setLayoutManager(new LinearLayoutManager(this));
        rvHC.setAdapter(homeCompanyAdapter);

        refreshLayout.setOnRefreshListener(refreshLayout -> loadData());
        homeCompanyAdapter.setOnItemClickListener((adapter, view, position) -> {
            HomeCompanyBean homeBean = mHomeList.get(position);
//            if (homeBean.isIs_bind_sa() && Constant.wifiInfo != null) {
//                bundle.putSerializable(IntentConstant.BEAN, homeBean);
//                switchToActivity(HCDetailActivity.class, bundle);
//            } else if (homeBean.getId() > 0 && !UserUtils.isLogin()) {
//                switchToActivity(LoginActivity.class);
//            } else {
//                bundle.putSerializable(IntentConstant.BEAN, homeBean);
//                switchToActivity(HCDetailActivity.class, bundle);
//            }
            if (!UserUtils.isLogin() && homeBean.getId() > 0) {
                if (HomeUtil.isHomeSAEnvironment(homeBean) && homeBean.isIs_bind_sa()){
                    bundle.putSerializable(IntentConstant.BEAN, homeBean);
                    switchToActivity(HCDetailActivity.class, bundle);
                }else {
                    switchToActivity(LoginActivity.class);
                }
            } else {
                bundle.putSerializable(IntentConstant.BEAN, homeBean);
                switchToActivity(HCDetailActivity.class, bundle);
            }
        });
    }

    @OnClick(R.id.llAdd)
    void onClickAdd() {
        switchToActivity(AddHCActivity.class);
    }

    @OnClick(R.id.tvTodo)
    void onClickTodo() {

    }

    /**
     * 从数据库查询数据
     */
    private void loadData() {
        if (UserUtils.isLogin()) {//有登录从服务器获取
            mPresenter.getHomeCompanyList(isFirst);
            isFirst = false;
        } else {//没登录从本地获取
            loadLocalData();
        }
    }

    /**
     * 加载本地数据
     */
    private void loadLocalData() {
        UiUtil.starThread(() -> {
            mHomeList = dbManager.queryHomeCompanyList();
            UiUtil.post(() -> {
                if (mHomeList.isEmpty()) {
                    setNullView(true);
                } else {
                    setNullView(false);
                    homeCompanyAdapter.setNewData(mHomeList);
                }
            });
        });
    }

    private void setNullView(boolean visible) {
        viewNull.setVisibility(visible ? View.VISIBLE : View.GONE);
        rvHC.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    @Override
    public void getHomeCompanyListSuccess(HomeCompanyListBean data) {
        refreshLayout.finishRefresh();
        if (data != null) {
            setNullView(false);
            mHomeList.clear();
            mHomeList.addAll(data.getAreas());
            //删除本地云端家庭数据
            UiUtil.starThread(() -> {
                int cloudUserId = UserUtils.getCloudUserId();
                dbManager.removeFamilyNotPresentUserFamily(cloudUserId);
                if (CollectionUtil.isNotEmpty(mHomeList)) {
                    for (HomeCompanyBean homeBean : mHomeList) {
                        homeBean.setCloud_user_id(cloudUserId);
                    }
                    List<HomeCompanyBean> userHomeCompanyList = dbManager.queryHomeCompanyListByCloudUserId(cloudUserId);
                    List<Integer> cloudIdList = new ArrayList<>();
                    for (HomeCompanyBean hcb : userHomeCompanyList) {
                        cloudIdList.add(hcb.getId());
                    }
                    for (HomeCompanyBean area : mHomeList) {
                        if (cloudIdList.contains(area.getId())) {
                            dbManager.updateHomeCompanyByCloudId(area.getId(), area.getName());
                        } else {
                            dbManager.insertCloudHomeCompany(area);
                        }
                    }
                    UiUtil.runInMainThread(() -> homeCompanyAdapter.setNewData(mHomeList));
                }
                EventBus.getDefault().post(new RefreshHomeList());
            });
        } else {
            loadLocalData();
        }
    }

    @Override
    public void getHomeCompanyListFailed(String errorMessage) {
        refreshLayout.finishRefresh();
        ToastUtil.show(errorMessage);
        loadLocalData();
    }

    @Override
    public void getPermissionsSuccess(PermissionBean permissionBean) {
        //llAdd.setVisibility(permissionBean.getPermissions().isAdd_area() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void getFail(int errorCode, String msg) {
        refreshLayout.finishRefresh();
    }
}