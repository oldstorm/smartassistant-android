package com.yctc.zhiting.adapter;


import android.widget.TextView;

import com.app.main.framework.baseutil.TimeFormatUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.FeedbackListBean;
import com.yctc.zhiting.utils.StringUtil;

/**
 * 反馈列表
 */
public class FeedbackListAdapter extends BaseQuickAdapter<FeedbackListBean.FeedbacksBean, BaseViewHolder> {

    public FeedbackListAdapter() {
        super(R.layout.item_feedback_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedbackListBean.FeedbacksBean item) {
        TextView tvTitle = helper.getView(R.id.tvTitle);
        tvTitle.setText(item.getDescription());

        TextView tvType = helper.getView(R.id.tvType);
        int feedbackType = item.getFeedback_type();
        String feedbackTypeStr = feedbackType == 1 ? UiUtil.getString(R.string.mine_meet_problem) : UiUtil.getString(R.string.mine_advise_suggest);
        String type = feedbackTypeStr + "：" + StringUtil.getFeedbackType(mContext, item.getType());
        tvType.setText(type);
        tvType.setCompoundDrawablesWithIntrinsicBounds(feedbackType == 1 ? R.drawable.icon_feedback_problem : R.drawable.icon_feedback_suggest, 0, 0, 0);
        String time = TimeFormatUtil.getFormatString(item.getCreated_at()*1000, "yyyy-MM-dd HH:mm");
        helper.setText(R.id.tvTime, time);
    }
}
