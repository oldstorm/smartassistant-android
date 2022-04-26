package com.yctc.zhiting.adapter;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.FeedbackCategoryBean;


public class FeedbackCategoryAdapter extends BaseQuickAdapter<FeedbackCategoryBean, BaseViewHolder> {

    public FeedbackCategoryAdapter() {
        super(R.layout.item_feedback_category);
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedbackCategoryBean item) {
        ConstraintLayout clParent = helper.getView(R.id.clParent);
        clParent.setSelected(item.isSelected());
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setText(item.getName());
        tvName.setSelected(item.isSelected());
        ImageView ivSelected = helper.getView(R.id.ivSelected);
        ivSelected.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);
    }

    public boolean isSelected() {
        for (FeedbackCategoryBean feedbackCategoryBean : getData()) {
            if (feedbackCategoryBean.isSelected()) {
                return true;
            }
        }
        return false;
    }

    public int getType() {
        for (FeedbackCategoryBean feedbackCategoryBean : getData()) {
            if (feedbackCategoryBean.isSelected()) {
                return feedbackCategoryBean.getType();
            }
        }
        return 0;
    }
}
