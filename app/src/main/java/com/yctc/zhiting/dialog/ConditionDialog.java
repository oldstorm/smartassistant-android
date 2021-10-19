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
import com.yctc.zhiting.adapter.ConditionTypeAdapter;
import com.yctc.zhiting.entity.scene.ConditionSelectBean;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ConditionDialog extends CommonBaseDialog {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.rvData)
    RecyclerView rvData;

    private ConditionTypeAdapter conditionAdapter;
    private List<ConditionSelectBean> data;
    private String title;

    public static ConditionDialog newInstance(List<ConditionSelectBean> data) {
        ConditionDialog fragment = new ConditionDialog();
        return fragment;
    }

    public ConditionDialog() {
    }

    public ConditionDialog(List<ConditionSelectBean> data) {
        this.data = data;
    }

    public ConditionDialog(List<ConditionSelectBean> data, String title) {
        this.data = data;
        this.title = title;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_condition;
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

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {
        tvTitle.setText(title);
        conditionAdapter = new ConditionTypeAdapter();
        rvData.setLayoutManager(new LinearLayoutManager(getContext()));
        rvData.setAdapter(conditionAdapter);
        conditionAdapter.setNewData(data);
        conditionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (itemListener!=null){
                    itemListener.onItem(conditionAdapter.getItem(position), position);
                }
            }
        });
    }

    @OnClick(R.id.ivClose)
    void onClickClose(){
        dismiss();
    }

    public void notifyItemChange(){
        if (conditionAdapter!=null){
            conditionAdapter.notifyDataSetChanged();
        }
    }

    private OnItemListener itemListener;

    public OnItemListener getItemListener() {
        return itemListener;
    }

    public void setItemListener(OnItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public interface OnItemListener{
        void onItem(ConditionSelectBean conditionSelectBean, int pos);
    }
}
