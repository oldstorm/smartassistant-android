package com.yctc.zhiting.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;

public class DLAuthWayAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public DLAuthWayAdapter() {
        super(R.layout.item_dl_auth_way);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.addOnClickListener(R.id.tvBind);
        helper.addOnClickListener(R.id.tvRemove);
    }
}
