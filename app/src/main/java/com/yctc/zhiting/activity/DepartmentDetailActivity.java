package com.yctc.zhiting.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DepartmentDetailContract;
import com.yctc.zhiting.activity.presenter.DepartmentDetailPresenter;
import com.yctc.zhiting.adapter.MemberAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.mine.DepartmentDetail;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.UserBean;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.StringUtil;
import com.yctc.zhiting.utils.UserUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 部门详情
 */
public class DepartmentDetailActivity extends MVPBaseActivity<DepartmentDetailContract.View, DepartmentDetailPresenter> implements DepartmentDetailContract.View {

    @BindView(R.id.rvMember)
    RecyclerView rvMember;
    @BindView(R.id.tvCompany)
    TextView tvCompany;
    @BindView(R.id.tvDepartment)
    TextView tvDepartment;
    @BindView(R.id.tvMember)
    TextView tvMember;
    @BindView(R.id.tvAddMember)
    TextView tvAddMember;
    @BindView(R.id.tvDepartmentSettings)
    TextView tvDepartmentSettings;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;
    @BindView(R.id.layout_null)
    View viewNull;
    @BindView(R.id.llMember)
    LinearLayout llMember;

    private MemberAdapter memberAdapter;

    private String companyName; // 公司名称
    private String departmentName; // 部门名称
    private HomeCompanyBean mHomeBean; // 家庭


    private int userId;  // 用户id
    private int departmentId; // 部门id
    private boolean isBindSa;//是否绑定sa
    private final int DEPARTMENT_ACT_REQUEST_CODE = 100;

    private boolean needRefresh; // 回到房间列表是否需要刷新

    private List<LocationBean> mDepartmentList;
    private final int ADD_MEMBER_ACT = 200;
    private int position;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_department_detail;
    }


    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.mine_department));
        departmentId = getIntent().getIntExtra(IntentConstant.DEPARTMENT_ID, 0);
        companyName = getIntent().getStringExtra(IntentConstant.NAME);
        departmentName = getIntent().getStringExtra(IntentConstant.RA_NAME);
        mHomeBean = (HomeCompanyBean) getIntent().getSerializableExtra(IntentConstant.BEAN);
        mDepartmentList = (List<LocationBean>) getIntent().getSerializableExtra(IntentConstant.RA_List);
        position = getIntent().getIntExtra(IntentConstant.POSITION, 0);
        userId = mHomeBean.getUser_id();
        isBindSa = mHomeBean.isIs_bind_sa() || mHomeBean.getId() > 0;
        tvDepartmentSettings.setVisibility(!isBindSa ? View.VISIBLE : View.GONE);
        tvEmpty.setText(UiUtil.getString(R.string.mine_empty_member));
        tvCompany.setText(StringUtil.setImgInText(this, companyName, departmentName, R.drawable.icon_94a5be_right_arrow));
        tvDepartment.setText(departmentName);
        initRv();
        getData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == DEPARTMENT_ACT_REQUEST_CODE) {
                if (data != null) {
                    boolean del = data.getBooleanExtra(IntentConstant.BOOL, false);
                    String name = data.getStringExtra(IntentConstant.NAME);
                    departmentName = name;
                    if (CollectionUtil.isNotEmpty(mDepartmentList)){
                        mDepartmentList.get(position).setName(departmentName);
                    }
                    needRefresh = true;
                    if (del) {
                        finishResult();
                    } else {
//                        tvDepartment.setText(name);
                        tvCompany.setText(StringUtil.setImgInText(this, companyName+" ", " "+name, R.drawable.icon_94a5be_right_arrow));
                        getData();
                    }
                } else {
                    getData();
                }
            } else if (requestCode == ADD_MEMBER_ACT) {
                needRefresh = true;
                getData();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (needRefresh) {
            finishResult();
        } else {
            super.onBackPressed();
        }
    }

    private void finishResult() {
        setResult(RESULT_OK);
        finish();
    }


    /**
     * 获取数据
     */
    private void getData() {
        if (UserUtils.isLogin()) { // 登录了云端
            if (isBindSa) {
                mPresenter.getPermissions(Constant.CurrentHome.getUser_id());
            }
            mPresenter.getDepartmentDetail(departmentId);
        } else {  // 没登录云端
            if (isBindSa) {  // 已经绑定sa从服务器获取
                mPresenter.getPermissions(userId);
                mPresenter.getDepartmentDetail(departmentId);
            } else { // 否则，从本地获取
                setNullView(true);
//                tvAddMember.setVisibility(View.GONE);
            }
        }
    }

    private void initRv() {
        memberAdapter = new MemberAdapter();
        rvMember.setLayoutManager(new LinearLayoutManager(this));
        rvMember.setAdapter(memberAdapter);
        memberAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(DepartmentDetailActivity.this, CompanyMemberDetailActivity.class);
            intent.putExtra(IntentConstant.ID, memberAdapter.getItem(position).getUser_id());
            startActivityForResult(intent, 100);
        });
    }

    @OnClick({R.id.tvAddMember, R.id.tvDepartmentSettings})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tvAddMember: // 添加成员
                Bundle addMemberBundle = new Bundle();
                addMemberBundle.putInt(IntentConstant.DEPARTMENT_ID, departmentId);
                List<Integer> userIdList = new ArrayList<>();
                for (UserBean userBean : memberAdapter.getData()){
                    userIdList.add(userBean.getUser_id());
                }
                addMemberBundle.putSerializable(IntentConstant.ID_LIST, (Serializable) userIdList);
                switchToActivityForResult(AddMemberActivity.class, addMemberBundle, ADD_MEMBER_ACT);
                break;

            case R.id.tvDepartmentSettings:  // 部门设置
                Bundle settingBundle = new Bundle();
                settingBundle.putInt(IntentConstant.DEPARTMENT_ID, departmentId);
                settingBundle.putInt(IntentConstant.USER_ID, userId);
                settingBundle.putBoolean(IntentConstant.IS_BIND_SA, isBindSa);
                settingBundle.putString(IntentConstant.NAME, departmentName);
                settingBundle.putSerializable(IntentConstant.BEAN, mHomeBean);
                settingBundle.putSerializable(IntentConstant.RA_List, (Serializable) mDepartmentList);
                switchToActivityForResult(DepartmentSettingsActivity.class, settingBundle, DEPARTMENT_ACT_REQUEST_CODE);
                break;
        }
    }

    /**
     * 空视图
     */
    private void setNullView(boolean visible) {
        viewNull.setVisibility(visible ? View.VISIBLE : View.GONE);
        llMember.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    /**
     * 部门详情成功
     *
     * @param userBean
     */
    @Override
    public void getDepartmentDetailSuccess(DepartmentDetail userBean) {
        if (userBean != null) {
            List<UserBean> users = userBean.getUsers();
            if (CollectionUtil.isNotEmpty(users)) {
                tvMember.setText(String.format(UiUtil.getString(R.string.mine_member_num), users.size()));
                setNullView(false);
            } else {
                setNullView(true);
            }
            memberAdapter.setNewData(users);
        } else {
            setNullView(true);
        }
    }

    /**
     * 部门详情失败
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
        if (permissionBean != null) {
            PermissionBean.PermissionsBean pb = permissionBean.getPermissions();
            if (pb!=null){
                tvAddMember.setVisibility(pb.isAdd_department_user() ? View.VISIBLE : View.GONE);
                tvDepartmentSettings.setVisibility(pb.isUpdate_department() ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * 权限失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getPermissionFail(int errorCode, String msg) {

    }
}