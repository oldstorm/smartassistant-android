package com.yctc.zhiting.activity;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DepartmentListContract;
import com.yctc.zhiting.activity.presenter.DepartmentListPresenter;
import com.yctc.zhiting.adapter.DepartmentListAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.EditBottomDialog;
import com.yctc.zhiting.entity.DepartmentListBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.MyItemTouchHelper;
import com.yctc.zhiting.utils.UserUtils;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 公司部门列表
 */
public class DepartmentListActivity extends MVPBaseActivity<DepartmentListContract.View, DepartmentListPresenter> implements DepartmentListContract.View {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rvDepartment)
    RecyclerView rvDepartment;
    @BindView(R.id.tvCompany)
    TextView tvCompany;
    @BindView(R.id.tvTodo)
    TextView tvTodo;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;
    @BindView(R.id.layout_null)
    View viewNull;
    @BindView(R.id.llAdd)
    LinearLayout llAdd;

    private ItemTouchHelper itemTouchHelper;
    private DepartmentListAdapter mDepartmentListAdapter;

    private long id; // 家庭/公司id
    private int userId;
    private boolean isEdit;
    private boolean isBindSa; // 是否绑定sa
    private boolean add_department;  // 是否有添加房间权限
    private boolean get_department;//是否有权限查看房间
    private boolean update_department_order; // 调整部门顺序权限

    private EditBottomDialog editBottomDialog;
    private DBManager dbManager;
    private WeakReference<Context> mContext;
    private boolean needRefreshHC; // 是否需要刷新家庭详情
    private final int DEPARTMENT_DETAIL_ACT_REQUEST_CODE = 100;
    private long cloudId; // 云端家庭id
    private String companyName; // 公司名称
    private HomeCompanyBean mHomeBean; // 家庭

    private boolean isReset;  // 是否编辑过item位置

    @Override
    protected int getLayoutId() {
        return R.layout.activity_department_list;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.mine_department));
        mContext = new WeakReference<>(this);
        dbManager = DBManager.getInstance(mContext.get());
        id = getIntent().getLongExtra(IntentConstant.ID, -1);
        userId = getIntent().getIntExtra(IntentConstant.USER_ID, -1);
        cloudId = getIntent().getLongExtra(IntentConstant.CLOUD_ID, -1);
        mHomeBean = (HomeCompanyBean) getIntent().getSerializableExtra(IntentConstant.BEAN);
        companyName = getIntent().getStringExtra(IntentConstant.NAME);
        isBindSa = getIntent().getBooleanExtra(IntentConstant.IS_BIND_SA, false);
        isBindSa = isBindSa || cloudId > 0;
        tvCompany.setText(companyName);
        tvTodo.setText(UiUtil.getString(R.string.mine_add_department));
        initRv();
        refreshLayout.setOnRefreshListener(refreshLayout -> refresh(false));
        refresh(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == DEPARTMENT_DETAIL_ACT_REQUEST_CODE){
            needRefreshHC = true;
            refresh(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (needRefreshHC){
            setResult(RESULT_OK);
            finish();
        }else {
            super.onBackPressed();
        }
    }

    private void initRv() {
        rvDepartment.setLayoutManager(new LinearLayoutManager(this));
        mDepartmentListAdapter = new DepartmentListAdapter();
        rvDepartment.setAdapter(mDepartmentListAdapter);
        mDepartmentListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LocationBean departmentBean = mDepartmentListAdapter.getItem(position);
                if (!isEdit) {  // 非编辑
                    if (isBindSa) {
                        if (!get_department) {  // 没有权限
                            ToastUtil.show(UiUtil.getString(R.string.common_no_permission));
                            return;
                        }
                    }
                    toDepartmentDetail(departmentBean, position);
                }
            }
        });
        setEdit(true);
        MyItemTouchHelper myItemTouchHelper = new MyItemTouchHelper(mDepartmentListAdapter, false);
        myItemTouchHelper.setMovedListener(new MyItemTouchHelper.MovedListener() {
            @Override
            public void onMoved() {
                isReset = true;
            }
        });
        itemTouchHelper = new ItemTouchHelper(myItemTouchHelper);
    }

    /**
     * 刷新数据
     */
    private void refresh(boolean showLoading) {
        if (UserUtils.isLogin()) { // 登录了云端
            if (isBindSa) {
                mPresenter.getPermissions(Constant.CurrentHome.getUser_id());
            }
            mPresenter.getDepartmentList(showLoading);
        } else {  // 没登录云端
            if (isBindSa) {  // 已经绑定sa从服务器获取
                mPresenter.getPermissions(userId);
                mPresenter.getDepartmentList(showLoading);
            } else { // 否则，从本地获取
                loadData();
            }
        }
    }

    /**
     * 去到房间详情
     *
     * @param departmentBean
     */
    private void toDepartmentDetail(LocationBean departmentBean, int position) {
        Intent intent = new Intent(DepartmentListActivity.this, DepartmentDetailActivity.class);
        intent.putExtra(IntentConstant.ID, id);
        intent.putExtra(IntentConstant.RA_ID, departmentBean.getLocationId() > 0 ? departmentBean.getLocationId() : departmentBean.getId());
        intent.putExtra(IntentConstant.RA_NAME, departmentBean.getName());
        intent.putExtra(IntentConstant.RA_List, (Serializable) mDepartmentListAdapter.getData());
        intent.putExtra(IntentConstant.POSITION, position);
        intent.putExtra(IntentConstant.IS_BIND_SA, isBindSa);
        intent.putExtra(IntentConstant.USER_ID, userId);
        intent.putExtra(IntentConstant.DEPARTMENT_ID, departmentBean.getId());
        intent.putExtra(IntentConstant.NAME, companyName);
        intent.putExtra(IntentConstant.BEAN, mHomeBean);
        startActivityForResult(intent, DEPARTMENT_DETAIL_ACT_REQUEST_CODE);
    }

    /**
     * 重置编辑文本
     */
    private void resetEdit(boolean showToast) {
        if (showToast)
            ToastUtil.show(UiUtil.getString(R.string.mine_edit_success));
        getRightTitleText().setText(!isEdit ? getResources().getString(R.string.common_edit) : getResources().getString(R.string.common_finish));
        getRightTitleText().setSelected(isEdit);
        llAdd.setVisibility(isEdit ? View.GONE : View.VISIBLE);
        itemTouchHelper.attachToRecyclerView(isEdit ? rvDepartment : null);
        refreshLayout.setEnableRefresh(!isEdit);
        mDepartmentListAdapter.setEdit(isEdit);
        isReset = false;
    }

    /**
     * 设置编辑可见
     */
    private void setEdit(boolean isCanEdit) {
        if (isCanEdit) {
            getRightTitleText().setVisibility(View.VISIBLE);
            setTitleRightText(getResources().getString(R.string.common_edit));
            getRightTitleText().setTextColor(UiUtil.getColor(R.color.color_3F4663));
            getRightTitleText().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            isEdit = getRightTitleText().isSelected();
            // 重新排序
            getRightTitleText().setOnClickListener(v -> {
                isEdit = !isEdit;
                if (isEdit) {
                    resetEdit(false);
                } else {
                    if (isReset) {
                        List<Integer> locationId = new ArrayList<>();
                        for (int i = 0; i < mDepartmentListAdapter.getData().size(); i++) {
                            mDepartmentListAdapter.getData().get(i).setSort(i);
                            mDepartmentListAdapter.notifyDataSetChanged();
                            locationId.add(mDepartmentListAdapter.getData().get(i).getId());
                        }
                        resetEdit(false);
                        if (isBindSa) {  // 已绑sa，服务器
                            String body = "{\"departments_id\":" + locationId.toString() + "}";
                            mPresenter.orderDepartment(body);
                        } else {  // 否则，本地
                            resetPos();
                        }
                    } else {
                        resetEdit(false);
                    }
                }
            });
        }else {
            getRightTitleText().setVisibility(View.GONE);
        }
    }

    /**
     * 切换位置
     */
    public void resetPos() {
        UiUtil.starThread(() -> {
            dbManager.updateLocationList(id, mDepartmentListAdapter.getData());
            UiUtil.runInMainThread(() -> resetEdit(true));
        });
    }

    /**
     * 添加部门
     */
    @OnClick({R.id.tvTodo, R.id.llAdd})
    void onClickAdd() {
        editBottomDialog = EditBottomDialog.newInstance(getResources().getString(R.string.mine_department_name), getResources().getString(R.string.mine_input_department_name), null, 1);
        editBottomDialog.setClickSaveListener(content -> {
            if (CollectionUtil.isNotEmpty(mDepartmentListAdapter.getData())) {
                for (LocationBean roomAreaBean : mDepartmentListAdapter.getData()) {
                    if (content.equals(roomAreaBean.getName())) {
                        ToastUtil.show(UiUtil.getString(R.string.mine_department_duplicate));
                        return;
                    }
                }
            }
            if (UserUtils.isLogin()) {  // 登录sc情况
                mPresenter.addDepartment(content);
            } else {  // 没登录sc情况
                if (isBindSa) {  // 绑定sa从接口获取
                    mPresenter.addDepartment(content);
                    editBottomDialog.dismiss();
                } else { // 否则，从本地获取
                    UiUtil.starThread(() -> {
                        LocationBean locationBean = new LocationBean();
                        locationBean.setName(content);
                        locationBean.setSort(1);
                        locationBean.setArea_id(id);
                        dbManager.insertLocation(locationBean);
                        needRefreshHC = true;
                        UiUtil.runInMainThread(() -> {
                            loadData();
                            editBottomDialog.dismiss();
                        });
                    });
                }
            }
        });
        editBottomDialog.show(this);
    }

    /**
     * 从数据库查询数据
     */
    private void loadData() {
        UiUtil.starThread(() -> {
            List<LocationBean> list = dbManager.queryLocationList(id);
            UiUtil.runInMainThread(() -> {
                if (list.isEmpty()) {
                    setNullView(true);
                } else {
                    setNullView(false);
                    mDepartmentListAdapter.setNewData(list);
                    mDepartmentListAdapter.notifyDataSetChanged();
                }
                refreshLayout.finishRefresh();
            });
        });
    }

    private void setNullView(boolean visible) {
        viewNull.setVisibility(visible ? View.VISIBLE : View.GONE);
        rvDepartment.setVisibility(visible ? View.GONE : View.VISIBLE);
        if (isBindSa) { // 绑了sa
            if (add_department) { // 有添加房间的权限
                llAdd.setVisibility(visible ? View.GONE : View.VISIBLE);
            } else {  // 没有添加房间的权限
                llAdd.setVisibility(View.GONE);
            }
        } else {
            llAdd.setVisibility(View.VISIBLE);
        }
        if (visible){
            setEdit(false);
        }else {
            if (isBindSa){
                setEdit(update_department_order);
            }else {
                setEdit(true);
            }
        }
    }

    /**
     * 部门列表成功
     * @param departmentListBean
     */
    @Override
    public void getDepartmentListSuccess(DepartmentListBean departmentListBean) {
        if (departmentListBean != null) {
            if (CollectionUtil.isNotEmpty(departmentListBean.getDepartments())) {
                setNullView(false);
                mDepartmentListAdapter.setNewData(departmentListBean.getDepartments());
            } else {
                setNullView(true);
            }
        } else {
            loadData();
        }
        refreshLayout.finishRefresh();
    }

    /**
     * 部门列表失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getDepartmentListFail(int errorCode, String msg) {
        ToastUtil.show(msg);
        refreshLayout.finishRefresh();
    }

    /**
     * 添加部门成功
     */
    @Override
    public void addDepartmentSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.common_add_success));
        if (editBottomDialog != null && editBottomDialog.isShowing()) {
            editBottomDialog.dismiss();
        }
        needRefreshHC = true;
        mPresenter.getDepartmentList(false);
    }

    /**
     * 添加部门失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void addDepartmentFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 排序部门成功
     */
    @Override
    public void orderDepartmentSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.mine_edit_success));
    }

    /**
     * 排序部门失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void orderDepartmentFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 获取权限成功
     * @param permissionBean
     */
    @Override
    public void getPermissionsSuccess(PermissionBean permissionBean) {
        if (permissionBean != null) {
            PermissionBean.PermissionsBean permission = permissionBean.getPermissions();
            if (permission != null) {
                get_department = permission.isGet_department();
                update_department_order = permission.isUpdate_department_order();
                setEdit(permission.isUpdate_department_order());
            }
            add_department = permission.isAdd_department();
            tvTodo.setVisibility(add_department ? View.VISIBLE : View.INVISIBLE);
            llAdd.setVisibility(add_department ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 获取权限失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getPermissionsFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}