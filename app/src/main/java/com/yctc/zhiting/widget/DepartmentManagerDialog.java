package com.yctc.zhiting.widget;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.adapter.DMMemberAdapter;
import com.yctc.zhiting.entity.mine.UserBean;
import com.yctc.zhiting.utils.IntentConstant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DepartmentManagerDialog extends CommonBaseDialog {

    @BindView(R.id.rvMember)
    RecyclerView rvMember;

    private DMMemberAdapter mDMMemberAdapter;
    private List<UserBean> mData;

    public static DepartmentManagerDialog getInstance(List<UserBean> data){
        DepartmentManagerDialog departmentManagerDialog = new DepartmentManagerDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(IntentConstant.BEAN_LIST, (Serializable) data);
        departmentManagerDialog.setArguments(bundle);
        return departmentManagerDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_department_manager;
    }

    @Override
    protected int obtainWidth() {
        return LinearLayout.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int obtainHeight() {
        return UiUtil.getScreenHeight()/2;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        mData = (List<UserBean>) arguments.getSerializable(IntentConstant.BEAN_LIST);
    }

    @Override
    protected void initView(View view) {
        rvMember.setLayoutManager(new LinearLayoutManager(getContext()));
        mDMMemberAdapter = new DMMemberAdapter();
        rvMember.setAdapter(mDMMemberAdapter);
        mDMMemberAdapter.setNewData(mData);
        mDMMemberAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UserBean ub = mDMMemberAdapter.getItem(position);
                boolean selected = ub.isSelected();
                for (UserBean userBean : mData){
                    userBean.setSelected(false);
                }
                ub.setSelected(!selected);
                mDMMemberAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick({R.id.ivClose, R.id.tvConfirm})
    void onClick(View view){
        int viewId = view.getId();
        switch (viewId){
            case R.id.ivClose:
                dismiss();
                break;

            case R.id.tvConfirm:
                if (confirmListener!=null){
                    confirmListener.onConfirm(mDMMemberAdapter.getSelectedUser());
                }
                break;
        }
    }

    private OnConfirmListener confirmListener;

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface OnConfirmListener{
        void onConfirm(UserBean userBean);
    }
}
