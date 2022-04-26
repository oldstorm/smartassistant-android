package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.adapter.DLFunctionAdapter;
import com.yctc.zhiting.entity.DLFunctionBean;

import butterknife.BindView;
import butterknife.OnClick;

public class DLFunctionDialog extends CommonBaseDialog {

    @BindView(R.id.rvFunction)
    RecyclerView rvFunction;

    private DLFunctionAdapter mDLFunctionAdapter;

    public static DLFunctionDialog getInstance() {
        DLFunctionDialog dlFunctionDialog = new DLFunctionDialog();
        return dlFunctionDialog;
    }

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {
        rvFunction.setLayoutManager(new LinearLayoutManager(getContext()));
        mDLFunctionAdapter = new DLFunctionAdapter();
        mDLFunctionAdapter.setNewData(DLFunctionBean.getDLFunctionData());
        rvFunction.setAdapter(mDLFunctionAdapter);
        mDLFunctionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mFuncListener != null) {
                    mFuncListener.onFunc(mDLFunctionAdapter.getItem(position));
                }
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_dl_function;
    }

    @Override
    protected int obtainWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int obtainHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.BOTTOM;
    }

    @OnClick({R.id.ivClose})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.ivClose:
                dismiss();
                break;
        }
    }

    private OnFuncListener mFuncListener;

    public void setFuncListener(OnFuncListener funcListener) {
        this.mFuncListener = funcListener;
    }

    public interface OnFuncListener {
        void onFunc(DLFunctionBean dlFunctionBean);
    }
}
