package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;
import com.yctc.zhiting.adapter.ListBottomAdapter;
import com.yctc.zhiting.bean.ListBottomBean;
import com.yctc.zhiting.widget.CustomLinearLayoutManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 选择角色对话框
 */
public class SelectRoleDialog extends CommonBaseDialog {

    @BindView(R.id.tvTodo)
    TextView tvTodo;
    @BindView(R.id.rvData)
    RecyclerView rvData;
    @BindView(R.id.clTop)
    ConstraintLayout clTop;

    private List<ListBottomBean> data;
    private ListBottomAdapter listBottomAdapter;

    /**
     * @return
     */
    public static SelectRoleDialog newInstance(List<ListBottomBean> data) {
        Bundle args = new Bundle();
        args.putSerializable("data", (Serializable) data);
        SelectRoleDialog fragment = new SelectRoleDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_select_role;
    }

    @Override
    protected int obtainWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
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
        data = (List<ListBottomBean>) arguments.getSerializable("data");
    }

    @Override
    protected void initView(View view) {
        rvData.post(new Runnable() {
            @Override
            public void run() {
                int maxHeight = UiUtil.getScreenHeight() * 3 / 4 - clTop.getHeight() - tvTodo.getHeight();
                rvData.setLayoutManager(new CustomLinearLayoutManager(getContext(), maxHeight));
                listBottomAdapter = new ListBottomAdapter();
                rvData.setAdapter(listBottomAdapter);
                listBottomAdapter.setNewData(data);
                listBottomAdapter.setOnItemClickListener((adapter, view1, position) -> {
                    ListBottomBean listBottomBean = listBottomAdapter.getItem(position);
                    listBottomBean.setSelected(!listBottomBean.isSelected());
                    listBottomAdapter.notifyItemChanged(position);
                    tvTodo.setEnabled(checkSelected());
                });
                tvTodo.setEnabled(checkSelected());
            }
        });


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
        dismiss();
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

    private OnClickTodoListener clickTodoListener;

    public void setClickTodoListener(OnClickTodoListener clickTodoListener) {
        this.clickTodoListener = clickTodoListener;
    }

    public interface OnClickTodoListener {
        void onTodo(List<ListBottomBean> data);
    }
}
