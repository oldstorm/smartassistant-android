package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;
import com.yctc.zhiting.adapter.SelectDepartmentAdapter;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.widget.CustomLinearLayoutManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SingleSelectDepartmentDialog extends CommonBaseDialog {

    @BindView(R.id.rvData)
    RecyclerView rvData;
    //    @BindView(R.id.rlParent)
//    RelativeLayout rlParent;
    @BindView(R.id.clTop)
    ConstraintLayout clTop;
    @BindView(R.id.tvTodo)
    TextView tvTodo;

    private SelectDepartmentAdapter mSelectDepartmentAdapter;
    private List<LocationBean> mData = new ArrayList<>();
    private LocationBean mSelectedLocationBean;//选中的部门

    public static SingleSelectDepartmentDialog getInstance(List<LocationBean> data) {
        SingleSelectDepartmentDialog selectDepartmentDialog = new SingleSelectDepartmentDialog();
        Bundle args = new Bundle();
        args.putSerializable("data", (Serializable) data);
        selectDepartmentDialog.setArguments(args);
        return selectDepartmentDialog;
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_single_select_department;
    }

    @Override
    protected int obtainWidth() {
        return LinearLayout.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int obtainHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
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

        rvData.post(new Runnable() {
            @Override
            public void run() {
                int maxHeight = UiUtil.getScreenHeight() * 3 / 4 - clTop.getHeight() - tvTodo.getHeight();
                rvData.setLayoutManager(new CustomLinearLayoutManager(getContext(), maxHeight));
                mSelectDepartmentAdapter = new SelectDepartmentAdapter();
                rvData.setAdapter(mSelectDepartmentAdapter);
                mSelectDepartmentAdapter.setNewData(mData);
                mSelectDepartmentAdapter.setOnItemClickListener((adapter, view1, position) -> {
                    for (LocationBean bean : mData) {
                        bean.setCheck(false);
                    }
                    mSelectedLocationBean = mSelectDepartmentAdapter.getItem(position);
                    mSelectedLocationBean.setCheck(true);
                    mSelectDepartmentAdapter.notifyDataSetChanged();
                });

            }
        });
    }

    @OnClick({R.id.ivClose, R.id.tvTodo})
    public void onClick(View view) {
        if (view.getId() == R.id.tvTodo) {
            if (departmentListener != null && mSelectedLocationBean != null) {
                departmentListener.onDepartment(mSelectedLocationBean);
            }
        }
        dismiss();
    }

    private OnDepartmentListener departmentListener;

    public void setDepartmentListener(OnDepartmentListener departmentListener) {
        this.departmentListener = departmentListener;
    }

    public interface OnDepartmentListener {
        void onDepartment(LocationBean locationBean);
    }
}
