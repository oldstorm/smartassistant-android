package com.yctc.zhiting.activity;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.HomeCompanyContract;
import com.yctc.zhiting.activity.presenter.HomeCompanyPresenter;
import com.yctc.zhiting.adapter.HomeCompanyAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.AddHomeOrCompanyDialog;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.event.RefreshHomeList;
import com.yctc.zhiting.utils.AllRequestUtil;
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
    @BindView(R.id.rvCompany)
    RecyclerView rvCompany;
    @BindView(R.id.tvTodo)
    TextView tvTodo;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;
    @BindView(R.id.tvHome)
    TextView tvHome;
    @BindView(R.id.tvCompany)
    TextView tvCompany;
    @BindView(R.id.layout_null)
    View viewNull;
    @BindView(R.id.llAdd)
    LinearLayout llAdd;

    private DBManager dbManager;
    private WeakReference<Context> mContext;
    private List<HomeCompanyBean> mHomeList = new ArrayList<>();
    private HomeCompanyAdapter homeCompanyAdapter;
    private HomeCompanyAdapter mCompanyAdapter;
    private boolean isFirst = true;

    private AddHomeOrCompanyDialog mAddHomeOrCompanyDialog;

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
        setTitleCenter(this.getResources().getString(R.string.mine_home_company_2));
        tvEmpty.setText(getResources().getString(R.string.mine_home_empty));
        tvTodo.setVisibility(View.INVISIBLE);
        mContext = new WeakReference<>(this);
        dbManager = DBManager.getInstance(mContext.get());

        homeCompanyAdapter = new HomeCompanyAdapter();
        rvHC.setLayoutManager(new LinearLayoutManager(this));
        rvHC.setAdapter(homeCompanyAdapter);

        refreshLayout.setOnRefreshListener(refreshLayout -> loadData());
        homeCompanyAdapter.setOnItemClickListener((adapter, view, position) -> {
            HomeCompanyBean homeBean = homeCompanyAdapter.getItem(position);
            toDetail(homeBean);
        });

        initRvCompany();
        initAddHomeOrCompanyDialog();
    }

    /**
     * 去到家庭详情
     * @param homeBean
     */
    private void toDetail(HomeCompanyBean homeBean){
        if (!UserUtils.isLogin() && homeBean.getId() > 0) {
            if (HomeUtil.isBssidEqual(homeBean) && homeBean.isIs_bind_sa()) {
                bundle.putSerializable(IntentConstant.BEAN, homeBean);
                switchToActivity(HCDetailActivity.class, bundle);
            } else {
                if (homeBean.isIs_bind_sa() && TextUtils.isEmpty(homeBean.getBSSID()) && !TextUtils.isEmpty(homeBean.getSa_lan_address())) {  // 已绑sa，需判断地址是否在sa环境
                    AllRequestUtil.checkUrl(homeBean.getSa_lan_address(), new AllRequestUtil.onCheckUrlListener() {
                        @Override
                        public void onSuccess() {
                            LogUtil.e("checkUrl===onSuccess");
                            WifiInfo wifiInfo = Constant.wifiInfo;
                            homeBean.setBSSID(wifiInfo.getBSSID());
                            dbManager.updateHomeMacAddress(homeBean.getLocalId(), wifiInfo.getBSSID());
                            bundle.putSerializable(IntentConstant.BEAN, homeBean);
                            switchToActivity(HCDetailActivity.class, bundle);
                        }

                        @Override
                        public void onError() {
                            switchToActivity(LoginActivity.class);
                        }
                    });
                } else {
                    switchToActivity(LoginActivity.class);
                }
            }
        } else {
            bundle.putSerializable(IntentConstant.BEAN, homeBean);
            switchToActivity(HCDetailActivity.class, bundle);
        }
    }

    /**
     * 初始化公司列表
     */
    private void initRvCompany(){
        rvCompany.setLayoutManager(new LinearLayoutManager(this));
        mCompanyAdapter = new HomeCompanyAdapter();
        rvCompany.setAdapter(mCompanyAdapter);
        mCompanyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                HomeCompanyBean homeBean = mCompanyAdapter.getItem(position);
                toDetail(homeBean);
            }
        });
    }

    /**
     * 添加家庭/公司弹窗
     */
    private void initAddHomeOrCompanyDialog() {
        mAddHomeOrCompanyDialog = AddHomeOrCompanyDialog.getInstance();
        mAddHomeOrCompanyDialog.setClickListener(new AddHomeOrCompanyDialog.OnClickListener() {
            @Override
            public void onHome() {
                switchToActivity(AddHCActivity.class);
                mAddHomeOrCompanyDialog.dismiss();
            }

            @Override
            public void onCompany() {
                switchToActivity(AddCompanyActivity.class);
                mAddHomeOrCompanyDialog.dismiss();
            }
        });
    }

    @OnClick(R.id.llAdd)
    void onClickAdd() {
        if (mAddHomeOrCompanyDialog != null && !mAddHomeOrCompanyDialog.isShowing()) {
            mAddHomeOrCompanyDialog.show(this);
        }
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
                    List<HomeCompanyBean> homeList = new ArrayList<>();
                    List<HomeCompanyBean> companyList = new ArrayList<>();
                    for (HomeCompanyBean homeCompanyBean : mHomeList){
                        if (homeCompanyBean.getArea_type() == 2){
                            companyList.add(homeCompanyBean);
                        }else {
                            homeList.add(homeCompanyBean);
                        }
                    }
                    homeCompanyAdapter.setNewData(homeList);
                    mCompanyAdapter.setNewData(companyList);
                    tvHome.setVisibility(CollectionUtil.isNotEmpty(homeList) ? View.VISIBLE : View.GONE);
                    rvHC.setVisibility(CollectionUtil.isNotEmpty(homeList) ? View.VISIBLE : View.GONE);
                    tvCompany.setVisibility(CollectionUtil.isNotEmpty(companyList) ? View.VISIBLE : View.GONE);
                    rvCompany.setVisibility(CollectionUtil.isNotEmpty(companyList) ? View.VISIBLE : View.GONE);
                }
                refreshLayout.finishRefresh();
            });
            refreshLayout.finishRefresh();
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
//                    List<HomeCompanyBean> userHomeCompanyList = dbManager.queryHomeCompanyListByCloudUserId(cloudUserId);
                    List<HomeCompanyBean> userHomeCompanyList = dbManager.queryHomeCompanyList();
                    List<Long> cloudIdList = new ArrayList<>();
                    List<Long> areaIdList = new ArrayList<>();
                    for (HomeCompanyBean hcb : userHomeCompanyList) {
                        cloudIdList.add(hcb.getId());
                        areaIdList.add(hcb.getArea_id());
                    }
                    // 用于存储从服务获取的数据
                    List<Long> serverIdList = new ArrayList<>();
                    for (HomeCompanyBean area : mHomeList) {
                        serverIdList.add(area.getId());
                        if (cloudIdList.contains(area.getId()) || areaIdList.contains(area.getId())) {
                            dbManager.updateHomeCompanyByCloudId(area);
                        } else {
                            if (area.isIs_bind_sa()) {
                                area.setArea_id(area.getId());
                            }
                            dbManager.insertCloudHomeCompany(area);
                        }
                    }

                    // 移除sc已删除的数据
                    for (Long id : cloudIdList) {
                        if (!serverIdList.contains(id) && id > 0) {  // 如果云端数据不在，删除本地
                            dbManager.removeFamilyByCloudId(id);
                        }
                    }
                    mHomeList = dbManager.queryHomeCompanyList();
                    List<HomeCompanyBean> homeList = new ArrayList<>();
                    List<HomeCompanyBean> companyList = new ArrayList<>();
                    for (HomeCompanyBean homeCompanyBean : mHomeList){
                        if (homeCompanyBean.getArea_type() == 2){
                            companyList.add(homeCompanyBean);
                        }else {
                            homeList.add(homeCompanyBean);
                        }
                    }
                    UiUtil.runInMainThread(() -> {
                        homeCompanyAdapter.setNewData(homeList);
                        mCompanyAdapter.setNewData(companyList);
                        tvHome.setVisibility(CollectionUtil.isNotEmpty(homeList) ? View.VISIBLE : View.GONE);
                        rvHC.setVisibility(CollectionUtil.isNotEmpty(homeList) ? View.VISIBLE : View.GONE);
                        tvCompany.setVisibility(CollectionUtil.isNotEmpty(companyList) ? View.VISIBLE : View.GONE);
                        rvCompany.setVisibility(CollectionUtil.isNotEmpty(companyList) ? View.VISIBLE : View.GONE);
                    });
                }
                EventBus.getDefault().post(new RefreshHomeList());
            });
        }
        if (CollectionUtil.isEmpty(mHomeList)) {
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

    /**
     * 添加云端家庭成功
     *
     * @param idBean
     */
    @Override
    public void addHomeCompanySuccess(IdBean idBean) {
        loadData();
    }

    /**
     * 添加云端家庭失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void addHomeCompanyFail(int errorCode, String msg) {

    }
}