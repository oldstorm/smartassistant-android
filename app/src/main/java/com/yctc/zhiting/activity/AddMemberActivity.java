package com.yctc.zhiting.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.AddMemberContract;
import com.yctc.zhiting.activity.presenter.AddMemberPresenter;
import com.yctc.zhiting.adapter.AddMemberAdapter;
import com.yctc.zhiting.adapter.SelectedMemberAdapter;
import com.yctc.zhiting.entity.mine.DepartmentDetail;
import com.yctc.zhiting.entity.mine.MembersBean;
import com.yctc.zhiting.entity.mine.UserBean;
import com.yctc.zhiting.request.AddDMMemberRequest;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 添加成功
 */
public class AddMemberActivity extends MVPBaseActivity<AddMemberContract.View, AddMemberPresenter> implements AddMemberContract.View {

    @BindView(R.id.rvSelectedMember)
    RecyclerView rvSelectedMember;
    @BindView(R.id.rvAllMember)
    RecyclerView rvAllMember;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;
    @BindView(R.id.layout_null)
    View viewNull;

    private SelectedMemberAdapter mSelectedMemberAdapter;
    private AddMemberAdapter mAddMemberAdapter;
    private List<UserBean> selectedData = new ArrayList<>();

    private int mDepartmentId; // 部门id
    private boolean needRefresh;

    private List<Integer> userIdList; // 已加入成员id

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_member;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void initUI() {
        super.initUI();
        tvEmpty.setText(UiUtil.getString(R.string.mine_empty_member));
        tvConfirm.setEnabled(false);
        initRvSelected();
        initRvAll();
        mPresenter.getMembers();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mDepartmentId = intent.getIntExtra(IntentConstant.DEPARTMENT_ID, 0);
        userIdList = (List<Integer>) intent.getSerializableExtra(IntentConstant.ID_LIST);
    }

    /**
     * 已选择的成员列表
     */
    private void initRvSelected(){
        rvSelectedMember.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mSelectedMemberAdapter = new SelectedMemberAdapter();
        rvSelectedMember.setAdapter(mSelectedMemberAdapter);
        mSelectedMemberAdapter.setNewData(selectedData);
        mSelectedMemberAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int viewId = view.getId();
                if (viewId == R.id.ivClose){
                    UserBean userBean = mSelectedMemberAdapter.getItem(position);
                    changeAddMemberAdapter(userBean);
                    selectedData.remove(position);
                    mSelectedMemberAdapter.notifyItemRemoved(position);
                    changeConfirmText();
                }
            }
        });
    }

    /**
     * 改变对应全部成员的选中状态
     * @param userBean
     */
    private void changeAddMemberAdapter(UserBean userBean){
        List<UserBean> users = mAddMemberAdapter.getData();
        for (int i=0; i<users.size(); i++){
            UserBean user = users.get(i);
            if (user.equals(userBean)){
                user.setSelected(false);
                mAddMemberAdapter.notifyItemChanged(i);
                break;
            }
        }
        tvConfirm.setEnabled(CollectionUtil.isNotEmpty(mAddMemberAdapter.getSelectedData()));
    }

    /**
     * 所有成员
     */
    private void initRvAll(){
        rvAllMember.setLayoutManager(new LinearLayoutManager(this));
        mAddMemberAdapter = new AddMemberAdapter();
        rvAllMember.setAdapter(mAddMemberAdapter);
        mAddMemberAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                UserBean userBean = mAddMemberAdapter.getItem(position);
                boolean selected = userBean.isSelected();
                userBean.setSelected(!selected);
                if (selected){
                    selectedData.remove(userBean);
                    mSelectedMemberAdapter.notifyItemRemoved(selectedData.size()-1);
                }else {
                    selectedData.add(userBean);
                    mSelectedMemberAdapter.notifyItemInserted(selectedData.size()-1);
                }

                mAddMemberAdapter.notifyDataSetChanged();

                tvConfirm.setEnabled(CollectionUtil.isNotEmpty(mAddMemberAdapter.getSelectedData()));
                changeConfirmText();
            }
        });
    }

    /**
     * 改变确定数量
     */
    private void changeConfirmText(){
        tvConfirm.setText(CollectionUtil.isEmpty(selectedData) ? UiUtil.getString(R.string.confirm) : String.format(UiUtil.getString(R.string.mine_confirm_count), selectedData.size()));
        rvSelectedMember.setVisibility(CollectionUtil.isNotEmpty(selectedData) ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.ivBack, R.id.tvConfirm})
    void onClick(View view){
        int viewId = view.getId();
        switch (viewId){
            case R.id.ivBack:  // 返回
                finisResult();
                break;

            case R.id.tvConfirm:  // 确定
                List<Integer> users = new ArrayList<>();
                for (UserBean userBean : selectedData){
                    users.add(userBean.getUser_id());
                }
                AddDMMemberRequest addDMDepartmentMember = new AddDMMemberRequest(users);
                String json = addDMDepartmentMember.toString();
                mPresenter.addDMDepartmentMember(mDepartmentId, json);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finisResult();
    }

    private void finisResult(){
        if (needRefresh) {
            setResult(RESULT_OK);
        }
        finish();
    }

    /**
     * 所有成员列表成功
     * @param membersBean
     */
    @Override
    public void getMembersSuccess(MembersBean membersBean) {
        if (membersBean!=null){
            List<UserBean> allMember = membersBean.getUsers();
            if (CollectionUtil.isNotEmpty(userIdList) && CollectionUtil.isNotEmpty(allMember)){
                for (UserBean userBean : allMember){
                    if (userIdList.contains(userBean.getUser_id())){
                        userBean.setSelected(true);
                        selectedData.add(userBean);
                    }
                }
            }
            mSelectedMemberAdapter.notifyDataSetChanged();
            mAddMemberAdapter.setNewData(allMember);
            changeConfirmText();
            setNullView(CollectionUtil.isEmpty(allMember));
        }else {
            setNullView(true);
        }
    }

    /**
     * 空视图
     */
    private void setNullView(boolean visible) {
        viewNull.setVisibility(visible ? View.VISIBLE : View.GONE);
        rvAllMember.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    /**
     * 所有成员列表失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getMembersFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public void getDepartmentDetailSuccess(DepartmentDetail userBean) {

    }

    @Override
    public void getDepartmentDetailFail(int errorCode, String msg) {

    }

    /**
     * 添加成员成功
     */
    @Override
    public void addDMDepartmentMemberSuccess() {
        needRefresh = true;
        ToastUtil.show(UiUtil.getString(R.string.mine_add_member_success));
        finisResult();
    }

    /**
     * 添加成员失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void addDMDepartmentMemberFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}
