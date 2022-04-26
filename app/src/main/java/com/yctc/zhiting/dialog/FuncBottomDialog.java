package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.adapter.FuncBottomAdapter;
import com.yctc.zhiting.bean.FuncBottomBean;

import butterknife.BindView;
import butterknife.OnClick;

public class FuncBottomDialog extends CommonBaseDialog {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.rvFunction)
    RecyclerView rvFunction;

    private String mTitle;
    private FuncBottomAdapter mFuncBottomAdapter;

    public static FuncBottomDialog getInstance() {
        FuncBottomDialog funcBottomDialog = new FuncBottomDialog();
        return funcBottomDialog;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        mTitle = arguments.getString("title");
    }

    @Override
    protected void initView(View view) {
        mFuncBottomAdapter = new FuncBottomAdapter();
        tvTitle.setText(mTitle);
        rvFunction.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFunction.setAdapter(mFuncBottomAdapter);
        mFuncBottomAdapter.setNewData(FuncBottomBean.getDLPwdWayData());
        mFuncBottomAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mFuncBottomItemListener != null) {
                    mFuncBottomItemListener.onItem(mFuncBottomAdapter.getItem(position));
                }
            }
        });
    }

    @OnClick(R.id.ivClose)
    void onClose() {
        dismiss();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_func_bottom;
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

    private OnFuncBottomItemListener mFuncBottomItemListener;

    public void setFuncBottomItemListener(OnFuncBottomItemListener funcBottomItemListener) {
        this.mFuncBottomItemListener = funcBottomItemListener;
    }

    public interface OnFuncBottomItemListener {
        void onItem(FuncBottomBean funcBottomBean);
    }
}
