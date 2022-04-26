package com.yctc.zhiting.adapter;


import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.TabBean;


public class TabAdapter extends BaseQuickAdapter<TabBean, BaseViewHolder> {

    public TabAdapter() {
        super(R.layout.item_tab);
    }

    @Override
    protected void convert(BaseViewHolder helper, TabBean item) {
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setText(item.getName());
        tvName.setSelected(item.isSelected());
        helper.setVisible(R.id.viewLine, item.isSelected());
    }
}
