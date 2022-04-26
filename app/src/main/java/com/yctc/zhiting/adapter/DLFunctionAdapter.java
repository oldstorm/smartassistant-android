package com.yctc.zhiting.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.DLFunctionBean;


public class DLFunctionAdapter extends BaseQuickAdapter<DLFunctionBean, BaseViewHolder> {

    public DLFunctionAdapter() {
        super(R.layout.item_dl_function);
    }

    @Override
    protected void convert(BaseViewHolder helper, DLFunctionBean item) {
        helper.setText(R.id.tvName, item.getName());
    }
}
