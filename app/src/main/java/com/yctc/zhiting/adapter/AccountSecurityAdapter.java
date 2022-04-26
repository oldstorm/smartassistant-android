package com.yctc.zhiting.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.ABFunctionBean;


public class AccountSecurityAdapter extends BaseQuickAdapter<ABFunctionBean, BaseViewHolder> {

    public AccountSecurityAdapter() {
        super(R.layout.item_account_security);
    }

    @Override
    protected void convert(BaseViewHolder helper, ABFunctionBean item) {
        helper.setText(R.id.tvName, item.getName());
    }
}
