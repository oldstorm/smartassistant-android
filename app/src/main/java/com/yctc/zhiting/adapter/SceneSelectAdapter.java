package com.yctc.zhiting.adapter;

import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.bean.ListBottomBean;


public class SceneSelectAdapter extends BaseQuickAdapter<ListBottomBean, BaseViewHolder> {

    public SceneSelectAdapter() {
        super(R.layout.item_scene_select);
    }

    @Override
    protected void convert(BaseViewHolder helper, ListBottomBean item) {
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setText(item.getName());
        tvName.setSelected(item.isSelected());
        ConstraintLayout clParent =  helper.getView(R.id.clParent);
        clParent.setAlpha( item.isEnabled() ? 1 : 0.5f);
        clParent.setEnabled(item.isEnabled());
    }
}
