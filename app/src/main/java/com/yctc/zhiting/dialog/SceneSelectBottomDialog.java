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
import com.yctc.zhiting.adapter.SceneSelectAdapter;
import com.yctc.zhiting.bean.ListBottomBean;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 场景的选中列表
 */
public class SceneSelectBottomDialog extends CommonBaseDialog {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.rvData)
    RecyclerView rvData;

    private String title;
    private List<ListBottomBean> data;
    private SceneSelectAdapter sceneSelectAdapter;

    public SceneSelectBottomDialog(String title, List<ListBottomBean> data) {
        this.title = title;
        this.data = data;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_scene_select_bottom;
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
        rvData.setLayoutManager(new LinearLayoutManager(getContext()));
        sceneSelectAdapter = new SceneSelectAdapter();
        rvData.setAdapter(sceneSelectAdapter);
        sceneSelectAdapter.setNewData(data);
        sceneSelectAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (ListBottomBean listBottomBean : sceneSelectAdapter.getData()){
                    listBottomBean.setSelected(false);
                }
                sceneSelectAdapter.getItem(position).setSelected(true);
                sceneSelectAdapter.notifyDataSetChanged();
                if (onSelectedListener!=null){
                    onSelectedListener.onSelected(sceneSelectAdapter.getItem(position));
                }
            }
        });
    }

    @OnClick(R.id.ivClose)
    void onClickClose(){
        dismiss();
    }

    /**
     * 全部未选
     */
    public void setAllNotSelected(){
        if (sceneSelectAdapter!=null){
            for (ListBottomBean listBottomBean : sceneSelectAdapter.getData()){
                listBottomBean.setSelected(false);
            }
            sceneSelectAdapter.notifyDataSetChanged();
        }
    }

    public void notifyItemChange(){
        if (sceneSelectAdapter!=null) {
            sceneSelectAdapter.notifyDataSetChanged();
        }
    }

    private OnSelectedListener onSelectedListener;

    public OnSelectedListener getOnSelectedListener() {
        return onSelectedListener;
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    public interface OnSelectedListener{
        void onSelected(ListBottomBean item);
    }
}
