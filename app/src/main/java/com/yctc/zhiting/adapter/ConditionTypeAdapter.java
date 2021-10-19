package com.yctc.zhiting.adapter;

import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.scene.ConditionSelectBean;

/**
 * 出发条件
 */
public class ConditionTypeAdapter extends BaseQuickAdapter<ConditionSelectBean, BaseViewHolder> {

    public ConditionTypeAdapter() {
        super(R.layout.item_condition_type);
    }

    @Override
    protected void convert(BaseViewHolder helper, ConditionSelectBean item) {
        ImageView ivCover = helper.getView(R.id.ivCover);
        ivCover.setImageResource(item.getDrawable());
        helper.setText(R.id.tvTitle, item.getTitle())
                .setText(R.id.tvSubtitle, item.getSubtitle());
        ConstraintLayout clParent =  helper.getView(R.id.clParent);
        clParent.setAlpha( item.isEnabled() ? 1 : 0.5f);
        clParent.setEnabled(item.isEnabled());

    }
}
