package com.yctc.zhiting.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.bean.FuncBottomBean;

public class FuncBottomAdapter extends BaseQuickAdapter<FuncBottomBean, BaseViewHolder> {

    public FuncBottomAdapter() {
        super(R.layout.item_func_bottom);
    }

    @Override
    protected void convert(BaseViewHolder helper, FuncBottomBean item) {
        helper.setText(R.id.tvName, item.getName());
    }
}
