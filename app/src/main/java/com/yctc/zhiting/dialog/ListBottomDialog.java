package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.adapter.ListBottomAdapter;
import com.yctc.zhiting.bean.ListBottomBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 底部列表弹窗
 */
public class ListBottomDialog extends CommonBaseDialog {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvTip)
    TextView tvTip;
    @BindView(R.id.tvTodo)
    TextView tvTodo;
    @BindView(R.id.rvData)
    RecyclerView rvData;

    private String title; // 标题
    private String tip; // 提示
    private String strTodo; // 确定按钮文本
    private boolean multiply; // 是否多选
    private List<ListBottomBean> data;

    private ListBottomAdapter listBottomAdapter;

    /**
     * @param title    标题
     * @param tip      提示
     * @param strTodo  确定文本
     * @param multiply 是否多选
     * @return
     */
    public static ListBottomDialog newInstance(String title, String tip, String strTodo, boolean multiply, List<ListBottomBean> data) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("tip", tip);
        args.putString("strTodo", strTodo);
        args.putBoolean("multiply", multiply);
        args.putSerializable("data", (Serializable) data);

        ListBottomDialog fragment = new ListBottomDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public List<ListBottomBean> getData() {
        return data;
    }

    public void setData(List<ListBottomBean> data) {
        this.data = data;
        if(listBottomAdapter!=null)
        listBottomAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_list_bottom;
    }

    @Override
    protected int obtainWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int obtainHeight() {
        return UiUtil.getScreenHeight() * 2 / 3;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        title = arguments.getString("title");
        tip = arguments.getString("tip");
        strTodo = arguments.getString("strTodo");
        multiply = arguments.getBoolean("multiply");
        data = (List<ListBottomBean>) arguments.getSerializable("data");
    }

    @Override
    protected void initView(View view) {
        tvTitle.setText(title);
        if (!TextUtils.isEmpty(tip)) {
            tvTip.setVisibility(View.VISIBLE);
            tvTip.setText(tip);
        } else {
            tvTip.setVisibility(View.GONE);
        }
        tvTodo.setText(strTodo);
        listBottomAdapter = new ListBottomAdapter();
        rvData.setLayoutManager(new LinearLayoutManager(getContext()));
        rvData.setAdapter(listBottomAdapter);
        listBottomAdapter.setNewData(data);
        listBottomAdapter.setOnItemClickListener((adapter, view1, position) -> {
            ListBottomBean listBottomBean = listBottomAdapter.getItem(position);
            if (multiply) {  // 多选
                listBottomBean.setSelected(!listBottomBean.isSelected());
                listBottomAdapter.notifyItemChanged(position);
            } else { // 单选
                for (int i = 0; i < listBottomAdapter.getData().size(); i++) {
                    listBottomAdapter.getData().get(i).setSelected(false);
                }
                listBottomBean.setSelected(true);
                listBottomAdapter.notifyDataSetChanged();
            }
            tvTodo.setEnabled(checkSelected());
        });
        tvTodo.setEnabled(checkSelected());
    }

    /**
     * 判断是否选中一个
     *
     * @return
     */
    private boolean checkSelected() {
        for (ListBottomBean listBottomBean : listBottomAdapter.getData()) {
            if (listBottomBean.isSelected()) {
                return true;
            }
        }
        return false;
    }

    @OnClick(R.id.ivClose)
    void onClickClose() {
        dismiss();
    }

    @OnClick(R.id.tvTodo)
    void onClickTodo() {
        if (clickTodoListener != null) {
            clickTodoListener.onTodo(getSelectedData());
        }
    }

    /**
     * 获取选中的数据
     *
     * @return
     */
    private List<ListBottomBean> getSelectedData() {
        List<ListBottomBean> data = new ArrayList<>();
        for (ListBottomBean listBottomBean : listBottomAdapter.getData()) {
            if (listBottomBean.isSelected()) {
                data.add(listBottomBean);
            }
        }
        return data;
    }

    public void notifyItemChange(){
        if(listBottomAdapter!=null)
        listBottomAdapter.notifyDataSetChanged();
    }

    private OnClickTodoListener clickTodoListener;

    public OnClickTodoListener getClickTodoListener() {
        return clickTodoListener;
    }

    public void setClickTodoListener(OnClickTodoListener clickTodoListener) {
        this.clickTodoListener = clickTodoListener;
    }

    public interface OnClickTodoListener {
        void onTodo(List<ListBottomBean> data);
    }
}
