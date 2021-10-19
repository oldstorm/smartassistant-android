package com.yctc.zhiting.activity;


import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.TransferOwnerContract;
import com.yctc.zhiting.activity.presenter.TransferOwnerPresenter;
import com.yctc.zhiting.adapter.TransferMemberAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.mine.MembersBean;
import com.yctc.zhiting.entity.mine.UserBean;
import com.yctc.zhiting.utils.CollectionUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 转移拥有者
 */
public class TransferOwnerActivity extends MVPBaseActivity<TransferOwnerContract.View, TransferOwnerPresenter> implements TransferOwnerContract.View {

    @BindView(R.id.clParent)
    ConstraintLayout clParent;
    @BindView(R.id.rvMember)
    RecyclerView rvMember;
    @BindView(R.id.tvTransfer)
    TextView tvTransfer;

    private TransferMemberAdapter mTransferMemberAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_owner;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.mine_transfer_owner));
        initRv();
        mPresenter.getMembers();
    }

    private void initRv(){
        rvMember.setLayoutManager(new LinearLayoutManager(this));
        mTransferMemberAdapter = new TransferMemberAdapter();
        rvMember.setAdapter(mTransferMemberAdapter);
        mTransferMemberAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (int i=0; i<mTransferMemberAdapter.getData().size(); i++){
                    mTransferMemberAdapter.getData().get(i).setSelected(false);
                }
                mTransferMemberAdapter.getItem(position).setSelected(true);
                mTransferMemberAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 成员列表成功
     * @param membersBean
     */
    @Override
    public void getMembersSuccess(MembersBean membersBean) {
        if (membersBean!=null){
            clParent.setVisibility(View.VISIBLE);
            List<UserBean> users = membersBean.getUsers();
            if (CollectionUtil.isNotEmpty(users)){
                for (UserBean userBean : users){
                    if (userBean.getUser_id() == Constant.CurrentHome.getUser_id()){
                        users.remove(userBean);
                        break;
                    }
                }
            }
            mTransferMemberAdapter.setNewData(users);
            tvTransfer.setVisibility(CollectionUtil.isNotEmpty(users) ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 成员列表失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getMemberFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 转移拥有者成功
     */
    @Override
    public void transferSuccess() {
        ToastUtil.show(getResources().getString(R.string.mine_transfer_success));
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 转移拥有者失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void transferFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    @OnClick(R.id.tvTransfer)
    void onClickTransfer(){
        int id = -1;
        for (UserBean user : mTransferMemberAdapter.getData()){
            if (user.isSelected()){
                id = user.getUser_id();
            }
        }
        if (id == -1){
            ToastUtil.show(getResources().getString(R.string.mine_please_select_member));
            return;
        }
        mPresenter.transferOwner(id);
    }
}