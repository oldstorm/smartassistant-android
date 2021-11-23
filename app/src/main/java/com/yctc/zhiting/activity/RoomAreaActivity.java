package com.yctc.zhiting.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
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
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.RoomAreaContract;
import com.yctc.zhiting.activity.presenter.RoomAreaPresenter;
import com.yctc.zhiting.adapter.RoomAreaAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.EditBottomDialog;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RoomListBean;
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
 * 房间/区域管理
 */
public class RoomAreaActivity extends MVPBaseActivity<RoomAreaContract.View, RoomAreaPresenter> implements RoomAreaContract.View {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rvRA)
    RecyclerView rvRA;
    @BindView(R.id.tvTodo)
    TextView tvTodo;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;
    @BindView(R.id.layout_null)
    View viewNull;
    @BindView(R.id.llAdd)
    LinearLayout llAdd;

    private final int ROOM_DETAIL_ACT_REQUEST_CODE = 100;

    private long id; // 家庭/公司id
    private int userId;
    private boolean isEdit;
    private boolean isBindSa; // 是否绑定sa
    private boolean isAdd_location;  // 是否有添加房间权限
    private boolean isPermissionSeeRoom;//是否有权限查看房间

    private Handler mainThreadHandler;
    private DBManager dbManager;
    private RoomAreaAdapter roomAreaAdapter;
    private ItemTouchHelper itemTouchHelper;
    private EditBottomDialog editBottomDialog;
    private WeakReference<Context> mContext;
    private boolean needRefreshHC; // 是否需要刷新家庭详情

    @Override
    protected int getLayoutId() {
        return R.layout.activity_room_area;
    }


    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.mine_room_area_manage));

        id = getIntent().getLongExtra(IntentConstant.ID, -1);
        isBindSa = getIntent().getBooleanExtra(IntentConstant.IS_BIND_SA, false);
        isBindSa = isBindSa || id > 0;
        userId = getIntent().getIntExtra(IntentConstant.USER_ID, -1);
        mContext = new WeakReference<>(this);
        dbManager = DBManager.getInstance(mContext.get());
        mainThreadHandler = new Handler(Looper.getMainLooper());

        roomAreaAdapter = new RoomAreaAdapter();
        rvRA.setLayoutManager(new LinearLayoutManager(this));
        rvRA.setAdapter(roomAreaAdapter);
        roomAreaAdapter.setOnItemClickListener((adapter, view, position) -> {
            LocationBean roomAreaBean = roomAreaAdapter.getItem(position);
            if (!isEdit) {  // 非编辑
                if (isBindSa) {
                    if (!isPermissionSeeRoom) {  // 没有权限
                        ToastUtil.show(UiUtil.getString(R.string.common_no_permission));
                        return;
                    }
                }
                toRoomDetail(roomAreaBean);
            }
        });

        itemTouchHelper = new ItemTouchHelper(new MyItemTouchHelper(roomAreaAdapter, false));
        refreshLayout.setOnRefreshListener(refreshLayout -> refresh(false));
        refresh(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ROOM_DETAIL_ACT_REQUEST_CODE){
            needRefreshHC = true;
            refresh(true);
        }
    }

    /**
     * 去到房间详情
     *
     * @param roomAreaBean
     */
    private void toRoomDetail(LocationBean roomAreaBean) {
        Intent intent = new Intent(RoomAreaActivity.this, RADetailActivity.class);
        intent.putExtra(IntentConstant.ID, id);
        intent.putExtra(IntentConstant.RA_ID, roomAreaBean.getLocationId() > 0 ? roomAreaBean.getLocationId() : roomAreaBean.getId());
        intent.putExtra(IntentConstant.RA_NAME, roomAreaBean.getName());
        intent.putExtra(IntentConstant.RA_List, (Serializable) roomAreaAdapter.getData());
        intent.putExtra(IntentConstant.IS_BIND_SA, isBindSa);
        intent.putExtra(IntentConstant.USER_ID, userId);
        startActivityForResult(intent, ROOM_DETAIL_ACT_REQUEST_CODE);
    }

    /**
     * 刷新数据
     */
    private void refresh(boolean showLoading) {
        if (UserUtils.isLogin()) { // 登录了云端
            if (isBindSa) {
                mPresenter.getPermissions(Constant.CurrentHome.getUser_id());
            }
            mPresenter.getRoomList(showLoading);
        } else {  // 没登录云端
            if (isBindSa) {  // 已经绑定sa从服务器获取
                mPresenter.getPermissions(userId);
                mPresenter.getRoomList(showLoading);
            } else { // 否则，从本地获取
                loadData();
            }
        }
    }

    /**
     * 添加
     */
    @OnClick({R.id.tvTodo, R.id.llAdd})
    public void onClickAdd() {
        editBottomDialog = EditBottomDialog.newInstance(getResources().getString(R.string.mine_room_name), getResources().getString(R.string.mine_input_room_name), null, 1);
        editBottomDialog.setClickSaveListener(content -> {
            if (CollectionUtil.isNotEmpty(roomAreaAdapter.getData())) {
                for (LocationBean roomAreaBean : roomAreaAdapter.getData()) {
                    if (content.equals(roomAreaBean.getName())) {
                        ToastUtil.show(UiUtil.getString(R.string.mine_room_duplicate));
                        return;
                    }
                }
            }
            if (UserUtils.isLogin()) {  // 登录sc情况
                mPresenter.addRoom(content);
            } else {  // 没登录sc情况
                if (isBindSa) {  // 绑定sa从接口获取
                    mPresenter.addRoom(content);
                    editBottomDialog.dismiss();
                } else { // 否则，从本地获取
                    UiUtil.starThread(() -> {
                        LocationBean locationBean = new LocationBean();
                        locationBean.setName(content);
                        locationBean.setSort(1);
                        locationBean.setArea_id(id);
                        dbManager.insertLocation(locationBean);

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
                    roomAreaAdapter.setNewData(list);
                    roomAreaAdapter.notifyDataSetChanged();
                }
                refreshLayout.finishRefresh();
            });
        });
    }

    private void setNullView(boolean visible) {
        viewNull.setVisibility(visible ? View.VISIBLE : View.GONE);
        rvRA.setVisibility(visible ? View.GONE : View.VISIBLE);
        if (isBindSa) { // 绑了sa
            if (isAdd_location) { // 有添加房间的权限
                llAdd.setVisibility(visible ? View.GONE : View.VISIBLE);
            } else {  // 没有添加房间的权限
                llAdd.setVisibility(View.GONE);
            }
        } else {
            llAdd.setVisibility(View.VISIBLE);
        }

        if (!visible) {
            setEdit(!isBindSa);
        }
    }

    /**
     * 重置编辑文本
     */
    private void resetEdit() {
        getRightTitleText().setText(!isEdit ? getResources().getString(R.string.common_edit) : getResources().getString(R.string.common_finish));
        getRightTitleText().setSelected(isEdit);
        itemTouchHelper.attachToRecyclerView(isEdit ? rvRA : null);
        refreshLayout.setEnableRefresh(!isEdit);
        roomAreaAdapter.setEdit(isEdit);
    }

    /**
     * 切换位置
     */
    public void resetPos() {
        UiUtil.starThread(() -> {
            dbManager.updateLocationList(id, roomAreaAdapter.getData());
            UiUtil.runInMainThread(() -> resetEdit());
        });
    }

    /**
     * 房间/区域列表数据
     *
     * @param roomListBean
     */
    @Override
    public void getRoomListSuccess(RoomListBean roomListBean) {
        if (roomListBean != null) {
            if (CollectionUtil.isNotEmpty(roomListBean.getLocations())) {
                setNullView(false);
                roomAreaAdapter.setNewData(roomListBean.getLocations());
            } else {
                setNullView(true);
            }
        } else {
            loadData();
        }
        refreshLayout.finishRefresh();
    }

    /**
     * 添加房间成功
     */
    @Override
    public void addRoomSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.common_add_success));
        if (editBottomDialog != null && editBottomDialog.isShowing()) {
            editBottomDialog.dismiss();
        }
        needRefreshHC = true;
        mPresenter.getRoomList(false);
    }

    @Override
    public void orderRoomSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.common_sort_success));
        resetEdit();
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

    /**
     * 设置编辑可见
     */
    private void setEdit(boolean isCanEdit) {
        if (!isCanEdit) return;
        setTitleRightText(getResources().getString(R.string.common_edit));
        getRightTitleText().setTextColor(UiUtil.getColor(R.color.color_3F4663));
        getRightTitleText().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        isEdit = getRightTitleText().isSelected();
        // 重新排序
        getRightTitleText().setOnClickListener(v -> {
            isEdit = !isEdit;
            if (isEdit) {
                resetEdit();
            } else {
                List<Integer> locationId = new ArrayList<>();
                for (int i = 0; i < roomAreaAdapter.getData().size(); i++) {
                    roomAreaAdapter.getData().get(i).setSort(i);
                    roomAreaAdapter.notifyDataSetChanged();
                    locationId.add(roomAreaAdapter.getData().get(i).getId());
                }
                if (isBindSa) {  // 已绑sa，服务器
                    String body = "{\"locations_id\":" + locationId.toString() + "}";
                    mPresenter.orderRoom(body);
                } else {  // 否则，本地
                    resetPos();
                }
            }
        });
    }

    /**
     * 权限
     *
     * @param permissionBean
     */
    @Override
    public void getPermissionsSuccess(PermissionBean permissionBean) {
        if (permissionBean != null) {
            PermissionBean.PermissionsBean permission = permissionBean.getPermissions();
            if (permission != null) {
                isPermissionSeeRoom = permission.isGet_location();
                setEdit(permission.isUpdate_location_order());
            }
            isAdd_location = permission.isAdd_location();
            tvTodo.setVisibility(permission.isAdd_location() ? View.VISIBLE : View.INVISIBLE);
            llAdd.setVisibility(permission.isAdd_location() ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void requestFail(int errorCode, String msg) {
        ToastUtil.show(msg);
        refreshLayout.finishRefresh();
    }
}