package com.yctc.zhiting.adapter;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.FeedbackTypeBean;


public class FeedbackTypeAdapter extends BaseQuickAdapter<FeedbackTypeBean, BaseViewHolder> {

    public FeedbackTypeAdapter() {
        super(R.layout.item_feedback_type);
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedbackTypeBean item) {
        ConstraintLayout clParent = helper.getView(R.id.clParent);
        clParent.setSelected(item.isSelected());
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setText(item.getName());
        tvName.setSelected(item.isSelected());
        ImageView ivSelected = helper.getView(R.id.ivSelected);
        ivSelected.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);
    }

    public int getType() {
        for (FeedbackTypeBean feedbackTypeBean : getData()) {
            if (feedbackTypeBean.isSelected()) {
                return feedbackTypeBean.getType();
            }
        }
        return 0;
    }
}
