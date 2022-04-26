package com.yctc.zhiting.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.imageutil.GlideUtil;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.CompanyMemberDetailContract;
import com.yctc.zhiting.activity.presenter.CompanyMemberDetailPresenter;
import com.yctc.zhiting.bean.ListBottomBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.dialog.ListBottomDialog;
import com.yctc.zhiting.dialog.SelectDepartmentDialog;
import com.yctc.zhiting.entity.DepartmentListBean;
import com.yctc.zhiting.entity.mine.DepartmentInfoBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RolesBean;
import com.yctc.zhiting.entity.mine.UpdateUserPost;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 公司成员详情
 */
public class CompanyMemberDetailActivity extends MVPBaseActivity<CompanyMemberDetailContract.View, CompanyMemberDetailPresenter> implements CompanyMemberDetailContract.View {

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvRoleNote)
    TextView tvRoleNote;
    @BindView(R.id.tvRole)
    TextView tvRole;
    @BindView(R.id.tvDepartment)
    TextView tvDepartment;
    @BindView(R.id.tvDel)
    TextView tvDel;
    @BindView(R.id.tvDepartmentNote)
    TextView tvDepartmentNote;
    @BindView(R.id.ivRole)
    ImageView ivRole;
    @BindView(R.id.ivDepartment)
    ImageView ivDepartment;
    @BindView(R.id.ciAvatar)
    ImageView ciAvatar;

    private int id;
    private boolean isCreator = true;// 是不是创建者
    private boolean hasChangeRole = false; // 改变了角色
    private boolean hasDelMember = false; // 删除成员
    private boolean isOwner = true; // 是不是拥有者
    private List<ListBottomBean> data = new ArrayList<>();
    private List<MemberDetailBean.RoleInfosBean> role_infos;
    private List<MemberDetailBean.RoleInfosBean> roleData = new ArrayList<>();
    private List<LocationBean> departmentInfos;
    private List<LocationBean> departmentData = new ArrayList<>();

    private CenterAlertDialog centerAlertDialog;
    private CenterAlertDialog quitAlertDialog;
    private boolean needRefresh;
    private List<Integer> ids = new ArrayList<>();
    private List<Integer> departmentIds = new ArrayList<>();
    /**
     * 1. 详情
     * 2. 角色
     * 3. 删除
     */
    private int kind;

    private boolean hasChange;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_company_member_detail;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void initUI() {
        super.initUI();
        initQuitDialog();
        tvRole.post(new Runnable() {
            @Override
            public void run() {
                tvRole.setMaxWidth(UiUtil.getScreenWidth() - UiUtil.dip2px(50) - tvRoleNote.getWidth() - ivRole.getWidth());
                tvDepartment.setMaxWidth(UiUtil.getScreenWidth() - UiUtil.dip2px(50) - tvDepartmentNote.getWidth() - ivDepartment.getWidth());
            }
        });
        initQuitDialog();

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        id = intent.getIntExtra(IntentConstant.ID, 0);
        getMemberDetail();
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
     * 返回
     */
    private void back(){
        if (hasChange) { // 数据改变未保存
            if (quitAlertDialog != null && !quitAlertDialog.isShowing()) {
                quitAlertDialog.show(this);
            }
        } else {
            finishResult();
        }
    }

    @OnClick({R.id.ivBack, R.id.llRole, R.id.clDepartment, R.id.tvSave})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {

            case R.id.ivBack: // 返回
                back();
                break;

            case R.id.llRole:  // 角色
                kind = 2;
                if (!isOwner && hasChangeRole) {
                    if (CollectionUtil.isEmpty(data)) {
                        mPresenter.getRoleList();
                    } else {
                        showRoleDialog();
                    }
                }
                break;

            case R.id.clDepartment: // 部门
                kind = 2;
                if (hasChangeRole) {
                    if (CollectionUtil.isEmpty(departmentData)) {
                        mPresenter.getDepartments();
                    } else {
                        showDepartmentDialog();
                    }
                }
                break;

            case R.id.tvSave: // 保存
                UpdateUserPost updateUserPost = new UpdateUserPost();
                updateUserPost.setRole_ids(ids);
                updateUserPost.setDepartment_ids(departmentIds);
                String body = new Gson().toJson(updateUserPost);
                mPresenter.updateMember(id, body);
                break;
        }
    }

    /**
     * 角色选择弹出
     */
    private void showRoleDialog() {
        String title = getResources().getString(R.string.mine_select_role);
        String strTodo = getResources().getString(R.string.common_confirm);
        ListBottomDialog listBottomDialog = ListBottomDialog.newInstance(title, null, strTodo, true, data);
        listBottomDialog.setClickTodoListener(data -> {
            roleData.clear();
            ids.clear();
            for (int i = 0; i < data.size(); i++) {
                roleData.add(new MemberDetailBean.RoleInfosBean(data.get(i).getId(), data.get(i).getName()));
                ids.add(data.get(i).getId());
            }
            setUserRole(roleData);
            hasChange = true;
            listBottomDialog.dismiss();
        });
        listBottomDialog.show(this);
    }

    /**
     * 部门列表弹窗
     */
    private void showDepartmentDialog() {
        SelectDepartmentDialog selectDepartmentDialog = SelectDepartmentDialog.getInstance(departmentData);
        selectDepartmentDialog.setDepartmentListener(new SelectDepartmentDialog.OnDepartmentListener() {
            @Override
            public void onDepartment(List<LocationBean> locationBeans) {
                departmentIds.clear();
                for (LocationBean locationBean : locationBeans) {
                    departmentIds.add(locationBean.getId());
                }
                setDepartmentText(locationBeans);
                hasChange = true;
                selectDepartmentDialog.dismiss();
            }
        });
        selectDepartmentDialog.show(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            getMemberDetail();
        }
    }

    /**
     * 删除
     */
    @OnClick(R.id.tvDel)
    void onClickDel() {
        if (isOwner) {  // 转移拥有者
            switchToActivityForResult(TransferOwnerActivity.class, 100);
        } else {
            kind = 3;
            if (hasDelMember) {
                centerAlertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.mine_member_del_confirm), null, true);
                centerAlertDialog.setConfirmListener((del) -> {
                    mPresenter.delMember(id);
                });
                centerAlertDialog.show(this);
            }
        }
    }

    /**
     * 设置角色
     *
     * @param roles
     */
    private void setUserRole(List<MemberDetailBean.RoleInfosBean> roles) {
        StringBuffer stringBuffer = new StringBuffer();
        if (CollectionUtil.isNotEmpty(roles)) {
            for (int i = 0; i < roles.size(); i++) {
                stringBuffer.append(roles.get(i).getName());
                if (i < roles.size() - 1) {
                    stringBuffer.append("、");
                }
            }
        }
        tvRole.setText(stringBuffer.toString());
    }

    /**
     * 设置部门文案
     *
     * @param departmentList
     */
    private void setDepartmentText(List<LocationBean> departmentList) {
        String department = UiUtil.getString(R.string.mine_not_divided);
        if (CollectionUtil.isNotEmpty(departmentList)) {
            StringBuffer departmentStringBuffer = new StringBuffer();
            for (int i = 0; i < departmentList.size(); i++) {
                departmentStringBuffer.append(departmentList.get(i).getName());
                if (i < departmentList.size() - 1) {
                    departmentStringBuffer.append("、");
                }
            }
            department = departmentStringBuffer.toString();
        }
        tvDepartment.setText(department);
    }

    /**
     * 详情数据
     *
     * @param memberDetailBean
     */
    @Override
    public void getDataSuccess(MemberDetailBean memberDetailBean) {
        if (memberDetailBean != null) {
            tvName.setText(memberDetailBean.getNickname());
            isCreator = memberDetailBean.isIs_creator();
            isOwner = memberDetailBean.isIs_owner();
            if (CollectionUtil.isNotEmpty(memberDetailBean.getRole_infos())) {
                role_infos = memberDetailBean.getRole_infos();
                setUserRole(role_infos);
            }

            if (CollectionUtil.isNotEmpty(memberDetailBean.getDepartment_infos())) {
                departmentInfos = memberDetailBean.getDepartment_infos();
            }
            setDepartmentText(memberDetailBean.getDepartment_infos());
            GlideUtil.load(memberDetailBean.getAvatar_url()).userHead().into(ciAvatar);
            if (memberDetailBean.isIs_owner()) {
                tvRole.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            }
            if (memberDetailBean.isIs_self()) { // 自己
                if (memberDetailBean.isIs_owner()) { // 拥有者
                    tvDel.setVisibility(View.VISIBLE);
                    tvDel.setText(getResources().getString(R.string.mine_transfer_owner));
                } else {
                    tvDel.setVisibility(View.GONE);
                }
            } else {
                // 有删除成员权限和改成员不是拥有者，删除按钮可见
                tvDel.setVisibility(hasDelMember && !memberDetailBean.isIs_owner() ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * 获取用户权限
     *
     * @param permissionBean
     */
    @Override
    public void getPermissionsSuccess(PermissionBean permissionBean) {
        if (permissionBean != null) {
            hasChangeRole = permissionBean.getPermissions().isUpdate_area_member_role();
            hasDelMember = permissionBean.getPermissions().isDelete_area_member();
            mPresenter.getMemberDetail(id);
        }
    }

    /**
     * 获取数据
     */
    private void getMemberDetail() {
        mPresenter.getPermissions(Constant.CurrentHome.getUser_id());
    }

    /**
     * 角色列表
     *
     * @param rolesBean
     */
    @Override
    public void getRoleListSuccess(RolesBean rolesBean) {
        if (rolesBean != null) {
            if (CollectionUtil.isNotEmpty(rolesBean.getRoles())) {
                for (RolesBean.RoleBean roleBean : rolesBean.getRoles()) {
                    if (roleBean.getId() != -1) {
                        data.add(new ListBottomBean(roleBean.getId(), roleBean.getName()));
                    }
                }
                // 设置当前已有的角色
                if (CollectionUtil.isNotEmpty(role_infos)) {
                    for (ListBottomBean listBottomBean : data) {
                        for (MemberDetailBean.RoleInfosBean roleInfosBean : role_infos) {
                            if (listBottomBean.getId() == roleInfosBean.getId()) {
                                listBottomBean.setSelected(true);
                            }
                        }
                    }
                }
                showRoleDialog();
            }
        }
    }

    /**
     * 修改成员
     */
    @Override
    public void updateSuccess() {
        needRefresh = true;
        hasChange = false;
        ToastUtil.show(UiUtil.getString(R.string.mine_save_success));
        finishResult();
    }

    /**
     * 失败操作
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void requestFail(int errorCode, String msg) {
        if (kind == 3) {
            closeDialog();
        }
        ToastUtil.show(msg);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    /**
     * 删除成员
     */
    @Override
    public void delMemberSuccess() {
        ToastUtil.show(getResources().getString(R.string.mine_remove_success));
        closeDialog();
        needRefresh = true;
        finishResult();
    }

    @Override
    public void getDepartmentsSuccess(DepartmentListBean departmentListBean) {
        if (departmentListBean != null) {
            List<LocationBean> departments = departmentListBean.getDepartments();
            if (CollectionUtil.isNotEmpty(departments)) {
                departmentData = departments;
            }
            // 设置所在部门未选中状态
            if (CollectionUtil.isNotEmpty(departmentInfos)) {
                for (LocationBean locationBean : departmentData) {
                    for (LocationBean selLocationBean : departmentInfos) {
                        if (locationBean.getId() == selLocationBean.getId()) {
                            locationBean.setCheck(true);
                        }
                    }
                }
            }
            showDepartmentDialog();
        }
    }

    @Override
    public void getDepartmentsFail(int errorCode, String msg) {

    }

    private void finishResult() {
        if (needRefresh)
            setResult(RESULT_OK);
        finish();
    }

    private void closeDialog() {
        if (centerAlertDialog != null) {
            centerAlertDialog.dismiss();
        }
    }
}