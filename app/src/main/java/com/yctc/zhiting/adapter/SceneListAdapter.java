package com.yctc.zhiting.adapter;

import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.scene.SceneBean;

import java.util.List;

/**
 * 控制场景  场景选择
 */
public class SceneListAdapter extends BaseQuickAdapter<SceneBean, BaseViewHolder> {

    public SceneListAdapter() {
        super(R.layout.item_scene_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, SceneBean item) {
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setText(item.getName());
        tvName.setSelected(item.isSelected());
    }
}
