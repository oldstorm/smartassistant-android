package com.yctc.zhiting.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.CompanyDetailContract;
import com.yctc.zhiting.activity.presenter.CompanyDetailPresenter;
import com.yctc.zhiting.adapter.MemberAdapter;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.InvitationCodeBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.MembersBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RolesBean;
import com.yctc.zhiting.entity.mine.UserBean;
import com.yctc.zhiting.utils.IntentConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 公司详情
 */
public class CompanyDetailActivity extends MVPBaseActivity<CompanyDetailContract.View, CompanyDetailPresenter> implements CompanyDetailContract.View {

    @BindView(R.id.rvMember)
    RecyclerView rvMember;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvRoom)
    TextView tvRoom;
    @BindView(R.id.tvQuit)
    TextView tvQuit;
    @BindView(R.id.rlCode)
    RelativeLayout rlCode;
    @BindView(R.id.tvMember)
    TextView tvMember;
    @BindView(R.id.llMember)
    LinearLayout llMember;
    @BindView(R.id.llTips)
    LinearLayout llTips;
    @BindView(R.id.rlName)
    RelativeLayout rlName;
    @BindView(R.id.rlDepartment)
    RelativeLayout rlDepartment;
    @BindView(R.id.rlInvalid)
    RelativeLayout rlInvalid;
    @BindView(R.id.tvTips)
    TextView tvTips;
    @BindView(R.id.ivGo)
    ImageView ivGo;
    @BindView(R.id.llHome)
    LinearLayout llHome;
    @BindView(R.id.rlVerificationCode)
    RelativeLayout rlVerificationCode;

    private final int ROOM_ACT_REQUEST_CODE = 100;

    private MemberAdapter memberAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_company_detail;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.mine_company));
        memberAdapter = new MemberAdapter();
        rvMember.setLayoutManager(new LinearLayoutManager(this));
        rvMember.setAdapter(memberAdapter);
        List<UserBean> data = new ArrayList<>();
        UserBean userBean1 = new UserBean();
        userBean1.setNickname("用户1");
        data.add(userBean1);
        UserBean userBean2 = new UserBean();
        userBean2.setNickname("用户2");
        data.add(userBean2);
        memberAdapter.setNewData(data);
        memberAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(CompanyDetailActivity.this, CompanyMemberDetailActivity.class);
            intent.putExtra(IntentConstant.ID, memberAdapter.getItem(position).getUser_id());
            startActivityForResult(intent, ROOM_ACT_REQUEST_CODE);
        });
    }

    /**
     * 点击事件
     *
     * @param view
     */
    @OnClick({R.id.rlDepartment})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.rlDepartment: // 部门
                switchToActivity(DepartmentListActivity.class);
                break;
        }
    }

    @Override
    public void getDataSuccess(HomeCompanyBean homeCompanyBean) {

    }

    @Override
    public void getMembersData(MembersBean membersBean) {

    }

    @Override
    public void updateNameSuccess() {

    }

    @Override
    public void getPermissionsSuccess(PermissionBean permissionBean) {

    }

    @Override
    public void exitHomeCompanySuccess() {

    }

    @Override
    public void delHomeCompanySuccess() {

    }

    @Override
    public void getRoleListSuccess(RolesBean rolesBean) {

    }

    @Override
    public void generateCodeSuccess(InvitationCodeBean invitationCodeBean) {

    }

    @Override
    public void getMemberDetailSuccess(MemberDetailBean memberDetailBean) {

    }

    @Override
    public void getFail(int errorCode, String msg) {

    }

    @Override
    public void getDetailFail(int errorCode, String msg) {

    }

    @Override
    public void getSATokenSuccess(FindSATokenBean findSATokenBean) {

    }

    @Override
    public void getSATokenFail(int errorCode, String msg) {

    }

    @Override
    public void addScHomeSuccess(IdBean idBean) {

    }

    @Override
    public void addScHomeFail(int errorCode, String msg) {

    }

    @Override
    public void onVerificationCodeSuccess(String code) {

    }

    @Override
    public void onVerificationCodeFail(int errorCode, String msg) {

    }
}