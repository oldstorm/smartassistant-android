package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.adapter.SelectDepartmentAdapter;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.utils.CollectionUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectDepartmentDialog extends CommonBaseDialog {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvTodo)
    TextView tvTodo;
    @BindView(R.id.rvData)
    RecyclerView rvData;

    private SelectDepartmentAdapter mSelectDepartmentAdapter;
    private List<LocationBean> mData;

    public static SelectDepartmentDialog getInstance(List<LocationBean> data){
        SelectDepartmentDialog selectDepartmentDialog = new SelectDepartmentDialog();
        Bundle args = new Bundle();
        args.putSerializable("data", (Serializable) data);
        selectDepartmentDialog.setArguments(args);
        return selectDepartmentDialog;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_select_department;
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
        mData = (List<LocationBean>) arguments.getSerializable("data");
    }

    @Override
    protected void initView(View view) {
        rvData.setLayoutManager(new LinearLayoutManager(getContext()));
        mSelectDepartmentAdapter = new SelectDepartmentAdapter();
        rvData.setAdapter(mSelectDepartmentAdapter);
        mSelectDepartmentAdapter.setNewData(mData);
        mSelectDepartmentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LocationBean locationBean = mSelectDepartmentAdapter.getItem(position);
                locationBean.setCheck(!locationBean.isCheck());
                mSelectDepartmentAdapter.notifyItemChanged(position);
            }
        });
    }

    @OnClick({R.id.ivClose, R.id.tvTodo})
    void onClick(View view){
        int viewId = view.getId();
        switch (viewId){
            case R.id.ivClose:
                dismiss();
                break;

            case R.id.tvTodo:
                if (departmentListener!=null){
                    departmentListener.onDepartment(mSelectDepartmentAdapter.getSelectedData());
                }
                break;
        }
    }

    private OnDepartmentListener departmentListener;

    public void setDepartmentListener(OnDepartmentListener departmentListener) {
        this.departmentListener = departmentListener;
    }

    public interface OnDepartmentListener{
        void onDepartment(List<LocationBean> locationBean);
    }
}
