package com.yctc.zhiting.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;

/**
 * 门锁事件适配器
 */
public class DLEventAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public DLEventAdapter() {
        super(R.layout.item_dl_event);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {

    }
}
