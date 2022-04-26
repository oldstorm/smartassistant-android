package com.yctc.zhiting.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.KeyValBean;

public class FeedbackDetailInfoAdapter extends BaseQuickAdapter<KeyValBean, BaseViewHolder> {

    public FeedbackDetailInfoAdapter() {
        super(R.layout.item_feedback_detail_info);
    }

    @Override
    protected void convert(BaseViewHolder helper, KeyValBean item) {
        helper.setText(R.id.tvKey, item.getKey())
                .setText(R.id.tvValue, item.getValue());
    }
}
