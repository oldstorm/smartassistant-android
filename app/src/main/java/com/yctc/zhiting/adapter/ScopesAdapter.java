package com.yctc.zhiting.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.ScopesBean;

import java.util.List;

public class ScopesAdapter extends BaseQuickAdapter<ScopesBean.ScopeBean, BaseViewHolder> {

    public ScopesAdapter() {
        super(R.layout.item_scope);
    }

    @Override
    protected void convert(BaseViewHolder helper, ScopesBean.ScopeBean item) {
        helper.setText(R.id.tvName, item.getDescription());
    }
}
