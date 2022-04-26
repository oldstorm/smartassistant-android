package com.yctc.zhiting.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;

/**
 *  一次性密码
 */
public class DisposablePwdAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public DisposablePwdAdapter() {
        super(R.layout.item_disposable_pwd);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.addOnClickListener(R.id.tvRemove);
    }
}
