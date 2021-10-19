package com.yctc.zhiting.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.MemberDetailContract;
import com.yctc.zhiting.activity.presenter.MemberDetailPresenter;
import com.yctc.zhiting.bean.ListBottomBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.dialog.ListBottomDialog;
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
 * 成员详情
 */
public class MemberDetailActivity extends MVPBaseActivity<MemberDetailContract.View, MemberDetailPresenter> implements MemberDetailContract.View {

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvRoleNote)
    TextView tvRoleNote;
    @BindView(R.id.tvRole)
    TextView tvRole;
    @BindView(R.id.tvDel)
    TextView tvDel;

    private int id;
    private boolean isCreator = true;// 是不是创建者
    private boolean hasChangeRole = false;
    private boolean hasDelMember = false;
    private boolean isOwner = true; // 是不是拥有者
    private List<ListBottomBean> data = new ArrayList<>();
    private List<MemberDetailBean.RoleInfosBean> role_infos;
    private List<MemberDetailBean.RoleInfosBean> roleData = new ArrayList<>();

    private CenterAlertDialog centerAlertDialog;

    /**
     * 1. 详情
     * 2. 角色
     * 3. 删除
     */
    private int kind;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_member_detail;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.mine_member_detail));
        tvRole.post(new Runnable() {
            @Override
            public void run() {
                tvRole.setMaxWidth(UiUtil.getScreenWidth()-UiUtil.getDimens(R.dimen.dp_40)-tvRoleNote.getWidth());
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        id = getIntent().getIntExtra(IntentConstant.ID, -1);
        getMemberDetail();

    }

    /**
     * 角色
     */
    @OnClick(R.id.llRole)
    void onClickRole() {
        kind = 2;
        if (!isOwner && hasChangeRole) {
            if (CollectionUtil.isEmpty(data)) {
                mPresenter.getRoleList();
            } else {
                showRoleDialog();
            }
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
            List<Integer> ids = new ArrayList<>();
            roleData.clear();
            for (int i = 0; i < data.size(); i++) {
                roleData.add(new MemberDetailBean.RoleInfosBean(data.get(i).getId(), data.get(i).getName()));
                ids.add(data.get(i).getId());
            }
            UpdateUserPost updateUserPost = new UpdateUserPost();
            updateUserPost.setRole_ids(ids);
            String body = new Gson().toJson(updateUserPost);
            mPresenter.updateMember(id, body);
            listBottomDialog.dismiss();
        });
        listBottomDialog.show(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100){
            getMemberDetail();
        }
    }

    /**
     * 删除
     */
    @OnClick(R.id.tvDel)
    void onClickDel() {
        if (isOwner){  // 转移拥有者
            switchToActivityForResult(TransferOwnerActivity.class, 100);
        }else {
            kind = 3;
            if (hasDelMember) {
                centerAlertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.mine_member_del_confirm), null, true);
                centerAlertDialog.setConfirmListener(() -> {
                    mPresenter.delMember(id);
                });
                centerAlertDialog.show(this);
            }
        }
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
            StringBuffer stringBuffer = new StringBuffer();
            if (CollectionUtil.isNotEmpty(memberDetailBean.getRole_infos())) {
                role_infos = memberDetailBean.getRole_infos();
                for (int i = 0; i < memberDetailBean.getRole_infos().size(); i++) {
                    stringBuffer.append(memberDetailBean.getRole_infos().get(i).getName());
                    if (i < memberDetailBean.getRole_infos().size() - 1) {
                        stringBuffer.append("、");
                    }
                }
            }

            if (memberDetailBean.isIs_self()){ // 自己
                if (memberDetailBean.isIs_owner()){ // 拥有者
                    tvDel.setVisibility(View.VISIBLE);
                    tvDel.setText(getResources().getString(R.string.mine_transfer_owner));
                }else {
                    tvDel.setVisibility(View.GONE);
                }
            }else {
                // 有删除成员权限和改成员不是拥有者，删除按钮可见
                tvDel.setVisibility(hasDelMember && !memberDetailBean.isIs_owner() ? View.VISIBLE : View.GONE);
            }
            tvRole.setText(stringBuffer.toString());
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
//            tvDel.setVisibility(permissionBean.getPermissions().isDelete_area_member() ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 获取数据
     */
    private void getMemberDetail(){
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
                    if (roleBean.getId()!=-1) {
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
        role_infos.clear();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < roleData.size(); i++) {
            role_infos.add(roleData.get(i));
            sb.append(roleData.get(i).getName());
            if (i < roleData.size() - 1) {
                sb.append("、");
            }
        }
        // 设置当前已有的角色
        if (CollectionUtil.isNotEmpty(role_infos)) {
            for (ListBottomBean listBottomBean : data) {
                for (MemberDetailBean.RoleInfosBean roleInfosBean : role_infos) {
                    if (listBottomBean.getId() == roleInfosBean.getId()) {
                        listBottomBean.setSelected(true);
                    } else {
                        listBottomBean.setSelected(false);
                    }
                }
            }
        }
        tvRole.setText(sb.toString());
    }

    /**
     * 失败操作
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void requestFail(int errorCode, String msg) {
        if (errorCode == 5021) {
            showRoleDialog();
        } else {
            if (kind == 3){
                closeDialog();
            }
            ToastUtil.show(msg);
        }
    }

    /**
     * 删除成员
     */
    @Override
    public void delMemberSuccess() {
        ToastUtil.show(getResources().getString(R.string.mine_remove_success));
        closeDialog();
        finish();
    }

    private void closeDialog(){
        if (centerAlertDialog!=null){
            centerAlertDialog.dismiss();
        }
    }
}