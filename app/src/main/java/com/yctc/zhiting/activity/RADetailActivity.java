package com.yctc.zhiting.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.RADetailContract;
import com.yctc.zhiting.activity.presenter.RADetailPresenter;
import com.yctc.zhiting.adapter.EquipmentAdapter;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.dialog.EditBottomDialog;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RADetailBean;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.SpacesItemDecoration;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 房间/区域详情
 */
public class RADetailActivity extends MVPBaseActivity<RADetailContract.View, RADetailPresenter> implements RADetailContract.View {

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvDevice)
    TextView tvDevice;
    @BindView(R.id.rvEquipment)
    RecyclerView rvEquipment;

    /**
     * 1.初始
     * 2.删除
     * 3.修改
     */
    private int kind;
    private long id;  // 家庭/公司id
    private int raId;  // 房间/区域id
    private int userId;
    private boolean updateP = false;
    private boolean isBindSa; // 是否绑定sa

    private String name; // 房间区域名称
    private String updateName;// 修改的名称
    private DBManager dbManager;
    private EquipmentAdapter equipmentAdapter;
    private List<LocationBean> roomAreas;
    private WeakReference<Context> mContext;
    private CenterAlertDialog centerAlertDialog;
    private boolean needRefresh; // 回到房间列表是否需要刷新

    @Override
    protected void onResume() {
        super.onResume();
        kind = 1;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_r_a_detail;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.mine_room_area));
        mContext = new WeakReference<>(this);
        dbManager = DBManager.getInstance(mContext.get());

        id = getIntent().getLongExtra(IntentConstant.ID, -1);
        raId = getIntent().getIntExtra(IntentConstant.RA_ID, -1);
        userId = getIntent().getIntExtra(IntentConstant.USER_ID, -1);
        name = getIntent().getStringExtra(IntentConstant.RA_NAME);
        isBindSa = getIntent().getBooleanExtra(IntentConstant.IS_BIND_SA, false);
        if (!isBindSa) {  // 没有绑sa，显示删除
            setRemove();
        }
        roomAreas = (List<LocationBean>) getIntent().getSerializableExtra(IntentConstant.RA_List);
        tvName.setText(name);
        equipmentAdapter = new EquipmentAdapter();

        equipmentAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            bundle.putInt(IntentConstant.ID, equipmentAdapter.getItem(position).getId());
            bundle.putBoolean(IntentConstant.IS_SA, equipmentAdapter.getItem(position).isIs_sa());
            bundle.putString(IntentConstant.NAME, equipmentAdapter.getItem(position).getName());
            bundle.putString(IntentConstant.LOGO_URL, equipmentAdapter.getItem(position).getLogo_url());
            bundle.putInt(IntentConstant.RA_ID, raId);
            bundle.putString(IntentConstant.PLUGIN_URL, equipmentAdapter.getItem(position).getPlugin_url());
            bundle.putString(IntentConstant.RA_NAME, name);
            switchToActivity(DeviceDetailActivity.class, bundle);
        });

        HashMap<String, Integer> spaceValue = new HashMap<>();
        spaceValue.put(SpacesItemDecoration.LEFT_SPACE, 16);
        spaceValue.put(SpacesItemDecoration.TOP_SPACE, 16);
        spaceValue.put(SpacesItemDecoration.RIGHT_SPACE, 16);
        spaceValue.put(SpacesItemDecoration.BOTTOM_SPACE, 16);
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(spaceValue);
        rvEquipment.addItemDecoration(spacesItemDecoration);
        rvEquipment.setLayoutManager(new GridLayoutManager(this, 2));
        rvEquipment.setAdapter(equipmentAdapter);
        if (isBindSa) {
            mPresenter.getDetail(raId);
            mPresenter.getPermissions(userId);
        }
    }

    /**
     * 修改名称
     */
    @OnClick(R.id.rlName)
    void onClickName() {
        kind = 3;
        if (isBindSa) { // 已绑sa，服务器
            if (updateP) {  // 有修改权限
                updateRoomName();
            }
        } else {  // 否则本地
            updateRoomName();
        }
    }

    /**
     * 修改房间
     */
    private void updateRoomName() {
        EditBottomDialog editBottomDialog = EditBottomDialog.newInstance(getResources().getString(R.string.mine_room_name), getResources().getString(R.string.mine_input_room_name), tvName.getText().toString(), 1);
        editBottomDialog.setClickSaveListener(content -> {
            if (!content.equals(name)) {  // 输入名称和原来的名称不一样才修改
                for (LocationBean roomAreaBean : roomAreas) {
                    if (content.equals(roomAreaBean.getName())) {  // 判断名称是否存在
                        ToastUtil.show(getResources().getString(R.string.mine_room_duplicate));
                        return;
                    }
                }
                if (isBindSa) {  // 已绑sa，服务器
                    updateName = content;
                    mPresenter.updateName(raId, content);
                    editBottomDialog.dismiss();
                } else { // 否则本地
                    UiUtil.starThread(() -> {
                        int count = dbManager.updateLocation(id, raId, content);
                        UiUtil.runInMainThread(() -> {
                            if (count > 0) {
                                tvName.setText(content);
                                needRefresh = true;
                                ToastUtil.show(UiUtil.getString(R.string.mine_save_success));
                            } else {
                                ToastUtil.show(UiUtil.getString(R.string.mine_save_fail));
                            }
                            editBottomDialog.dismiss();
                        });
                    });
                }
            } else {
                editBottomDialog.dismiss();
            }
        });
        editBottomDialog.show(this);
    }

    /**
     * 删除房间
     */
    private void removeRoom() {
        centerAlertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.mine_remove_room_ask), getResources().getString(R.string.mine_remove_room_tip), true);
        centerAlertDialog.setConfirmListener(del -> {
            if (isBindSa) {  // 已绑sa， 服务器
                mPresenter.delRoom(raId);
            } else {  // 否则，本地
                UiUtil.starThread(() -> {
                    int count = dbManager.removeLocation(raId);
                    UiUtil.runInMainThread(() -> {
                        closeDialog();
                        if (count > 0) {
                            ToastUtil.show(getResources().getString(R.string.mine_remove_success));
                            finishResult();
                        } else {
                            ToastUtil.show(getResources().getString(R.string.mine_remove_fail));
                        }
                    });
                });
            }
        });
        centerAlertDialog.show(this);
    }

    private void closeDialog() {
        if (centerAlertDialog != null) {
            centerAlertDialog.dismiss();
        }
    }

    /**
     * 设置删除操作
     */
    private void setRemove() {
        setTitleRightText(getResources().getString(R.string.mine_remove));
        getRightTitleText().setTextColor(UiUtil.getColor(R.color.color_3F4663));
        getRightTitleText().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        getRightTitleText().setOnClickListener(v -> {
            kind = 2;
            removeRoom();
        });
    }

    /**
     * 详情数据
     *
     * @param raDetailBean
     */
    @Override
    public void getDataSuccess(RADetailBean raDetailBean) {
        if (raDetailBean != null) {
            tvName.setText(raDetailBean.getName());
            if (CollectionUtil.isNotEmpty(raDetailBean.getDevices())) {
                tvDevice.setVisibility(View.VISIBLE);
                rvEquipment.setVisibility(View.VISIBLE);
                equipmentAdapter.setNewData(raDetailBean.getDevices());
            }
        }
    }

    /**
     * 修改名称
     */
    @Override
    public void updateNameSuccess() {
        ToastUtil.show(getResources().getString(R.string.mine_update_success));
        tvName.setText(updateName);
        needRefresh = true;
    }

    /**
     * 删除房间
     */
    @Override
    public void delRoomSuccess() {
        ToastUtil.show(getResources().getString(R.string.mine_remove_success));
        closeDialog();
        finishResult();
    }

    @Override
    public void onBackPressed() {
        if (needRefresh) {
            finishResult();
        }else {
            super.onBackPressed();
        }
    }

    private void finishResult(){
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 请求失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void requestFail(int errorCode, String msg) {
        if (kind == 2) {
            closeDialog();
        }
        ToastUtil.show(msg);
    }

    @Override
    public void getPermissionsSuccess(PermissionBean permissionBean) {
        if (permissionBean.getPermissions().isDelete_location()) {
            setRemove();
        }
        updateP = permissionBean.getPermissions().isUpdate_location_name();
    }
}