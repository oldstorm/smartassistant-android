package com.yctc.zhiting.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DepartmentListContract;
import com.yctc.zhiting.activity.contract.DepartmentSettingsContract;
import com.yctc.zhiting.activity.presenter.DepartmentListPresenter;
import com.yctc.zhiting.activity.presenter.DepartmentSettingsPresenter;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.entity.mine.DepartmentDetail;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.UserBean;
import com.yctc.zhiting.request.UpdateDepartmentRequest;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.widget.CircleImageView;
import com.yctc.zhiting.widget.DepartmentManagerDialog;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 部门设置
 */
public class DepartmentSettingsActivity extends MVPBaseActivity<DepartmentSettingsContract.View, DepartmentSettingsPresenter> implements DepartmentSettingsContract.View {

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvSave)
    TextView tvSave;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.llManager)
    LinearLayout llManager;
    @BindView(R.id.ciAvatar)
    CircleImageView ciAvatar;


    private int userId;  // 用户id
    private int departmentId; // 部门id
    private boolean isBindSa;//是否绑定sa
    private String departmentName; // 部门名称

    private CenterAlertDialog quitAlertDialog;
    private List<UserBean> members;

    private int managerId; // 部门主管id
    private int originalManagerId; // 部门主管id
    private DepartmentManagerDialog mDepartmentManagerDialog; // 部门主管选择弹窗
    private List<LocationBean> departmentList;

    private WeakReference<Context> mContext;
    private DBManager dbManager;

    private CenterAlertDialog removeAlertDialog;
    private boolean needRefresh; // 回到房间列表是否需要刷新
    private final int DEPARTMENT_ACT_REQUEST_CODE = 100;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_department_settings;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void initUI() {
        super.initUI();

        mContext = new WeakReference<>(this);
        dbManager = DBManager.getInstance(mContext.get());

        departmentId = getIntent().getIntExtra(IntentConstant.DEPARTMENT_ID, 0);
        userId = getIntent().getIntExtra(IntentConstant.USER_ID, 0);
        isBindSa = getIntent().getBooleanExtra(IntentConstant.IS_BIND_SA, false);
        departmentName = getIntent().getStringExtra(IntentConstant.NAME);
        departmentList = (List<LocationBean>) getIntent().getSerializableExtra(IntentConstant.RA_List);
        etName.setText(departmentName);
        initQuitDialog();
        initRemoveAlertDialog();
        setSaveEnabled();
        getData();
    }

    private void getData(){
        if (isBindSa){
            mPresenter.getPermissions(userId);
            mPresenter.getDepartmentDetail(departmentId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == DEPARTMENT_ACT_REQUEST_CODE) {
            getData();
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void finishResult(boolean del){
        Intent intent = new Intent();
        intent.putExtra(IntentConstant.BOOL, del);
        intent.putExtra(IntentConstant.NAME, departmentName);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 退出提示
     */
    private void initQuitDialog() {
        quitAlertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.mine_not_save_tip), null, false);
        quitAlertDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
            @Override
            public void onConfirm(boolean del) {
                finish();
            }
        });
    }

    /**
     * 初始化删除弹窗
     */
    private void initRemoveAlertDialog(){
        removeAlertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.mine_remove_department_ask), getResources().getString(R.string.mine_remove_department_tip),
                getResources().getString(R.string.mine_mine_cancel), getResources().getString(R.string.common_confirm),true);
        removeAlertDialog.setConfirmListener(del -> {
            if (isBindSa) {  // 已绑sa， 服务器
                mPresenter.delDepartment(departmentId);
            } else {  // 否则，本地
                UiUtil.starThread(() -> {
                    int count = dbManager.removeLocation(departmentId);
                    UiUtil.runInMainThread(() -> {
                        closeDialog();
                        if (count > 0) {
                            ToastUtil.show(getResources().getString(R.string.mine_remove_success));
                            finishResult(true);
                        } else {
                            ToastUtil.show(getResources().getString(R.string.mine_remove_fail));
                        }
                    });
                });
            }
        });
    }

    /**
     * 关闭弹窗
     */
    private void closeDialog() {
        if (removeAlertDialog != null) {
            removeAlertDialog.dismiss();
        }
    }

    /**
     * 设置保存按钮
     */
    private void setSaveEnabled() {
        String name = etName.getText().toString().trim();
        tvSave.setEnabled(!TextUtils.isEmpty(name));
    }

    @OnTextChanged(R.id.etName)
    void nameTextChange() {
        setSaveEnabled();
    }

    /**
     * 返回
     */
    private void back(){
        String name = etName.getText().toString().trim();
        if (!name.equals(departmentName) || managerId != originalManagerId) {
            if (quitAlertDialog != null && !quitAlertDialog.isShowing()) {
                quitAlertDialog.show(this);
            }
        } else {
            if (needRefresh) {
                finishResult(false);
            }else {
                finish();
            }
        }
    }

    @OnClick({R.id.ivBack, R.id.llManager, R.id.tvRemove, R.id.tvSave})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.ivBack:  // 返回
                back();
                break;

            case R.id.llManager:  // 部门主管
                if (mDepartmentManagerDialog != null && !mDepartmentManagerDialog.isShowing()) {
                    mDepartmentManagerDialog.show(this);
                }
                break;

            case R.id.tvRemove:  // 删除部门
                if (removeAlertDialog!=null && !removeAlertDialog.isShowing()){
                    removeAlertDialog.show(this);
                }
                break;

            case R.id.tvSave:  // 保存
                save();
                break;
        }
    }

    /**
     * 保存
     */
    private void save(){
        String name = etName.getText().toString().trim();
        if (name.equals(departmentName) && managerId == originalManagerId){
            ToastUtil.show(UiUtil.getString(R.string.mine_save_success));
            finish();
        }else {
            if (!name.equals(departmentName)){
                if (CollectionUtil.isNotEmpty(departmentList)) {
                    for (LocationBean roomAreaBean : departmentList) {
                        if (name.equals(roomAreaBean.getName())) {  // 判断名称是否存在
                            ToastUtil.show(getResources().getString(R.string.mine_department_duplicate));
                            return;
                        }
                    }
                }
            }
            if (isBindSa) {  // 已绑sa，服务器
                UpdateDepartmentRequest updateDepartmentRequest = new UpdateDepartmentRequest(name, managerId);
                String json = updateDepartmentRequest.toString();
                mPresenter.updateDepartment(departmentId, json);
            } else { // 否则本地
                UiUtil.starThread(() -> {
                    int count = dbManager.updateLocation(0, departmentId, name);
                    UiUtil.runInMainThread(() -> {
                        if (count > 0) {
                            tvName.setText(name);
                            needRefresh = true;
                            departmentName = name;
                            ToastUtil.show(UiUtil.getString(R.string.mine_save_success));
                            finishResult(false);
                        } else {
                            ToastUtil.show(UiUtil.getString(R.string.mine_save_fail));
                        }
                    });
                });
            }
        }
    }

    /**
     * 详情成功
     *
     * @param departmentDetail
     */
    @Override
    public void getDepartmentDetailSuccess(DepartmentDetail departmentDetail) {
        if (departmentDetail != null) {
            members = departmentDetail.getUsers();
            llManager.setVisibility(View.VISIBLE);
            if (members != null) {
                UserBean ub = null;
                for (UserBean userBean : members) {
                    if (userBean.isIs_manager()) {
                        ub = userBean;
                        userBean.setSelected(true);
                        break;
                    }
                }

                originalManagerId = ub == null ? 0 : ub.getUser_id();
                resetData(ub);
            }
            initDepartmentManagerDialog();
        }
    }

    private void resetData(UserBean userBean){
        ciAvatar.setVisibility(userBean == null ? View.GONE : View.VISIBLE);
        tvName.setText(userBean == null ? "" : userBean.getNickname());
        managerId = userBean == null ? 0 : userBean.getUser_id();
    }

    /**
     * 初始化部门主管选择弹窗
     */
    private void initDepartmentManagerDialog() {
        mDepartmentManagerDialog = DepartmentManagerDialog.getInstance(members);
        mDepartmentManagerDialog.setConfirmListener(new DepartmentManagerDialog.OnConfirmListener() {
            @Override
            public void onConfirm(UserBean userBean) {
                resetData(userBean);
                needRefresh = true;
                mDepartmentManagerDialog.dismiss();
            }
        });
    }

    /**
     * 详情失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getDepartmentDetailFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 权限成功
     *
     * @param permissionBean
     */
    @Override
    public void getPermissionsSuccess(PermissionBean permissionBean) {

    }

    /**
     * 权限失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getPermissionFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 修改部门成功
     */
    @Override
    public void updateDepartmentSuccess() {
        departmentName = etName.getText().toString().trim();
        originalManagerId = managerId;
        needRefresh = true;
        ToastUtil.show(UiUtil.getString(R.string.mine_save_success));
        finishResult(false);
    }

    /**
     * 修改部门失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void updateDepartmentFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 删除部门成功
     */
    @Override
    public void delDepartmentSuccess() {
        ToastUtil.show(getResources().getString(R.string.mine_remove_success));
        closeDialog();
        finishResult(true);
    }

    /**
     * 删除部门失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void delDepartmentFail(int errorCode, String msg) {
        ToastUtil.show(msg);
        closeDialog();
    }
}