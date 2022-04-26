package com.yctc.zhiting.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.AreaCodeBean;

public class AreaCodeAdapter extends BaseQuickAdapter<AreaCodeBean, BaseViewHolder> {

    public AreaCodeAdapter() {
        super(R.layout.item_area_code);
    }

    @Override
    protected void convert(BaseViewHolder helper, AreaCodeBean item) {
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setText(item.getCn() + "  +" + item.getCode());
        tvName.setSelected(item.isSelected());
    }
}
