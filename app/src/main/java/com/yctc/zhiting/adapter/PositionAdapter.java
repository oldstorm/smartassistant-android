package com.yctc.zhiting.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.LocationBean;

import java.util.List;

public class PositionAdapter extends BaseQuickAdapter<LocationBean, BaseViewHolder> {

    public PositionAdapter(List<LocationBean> data) {
        super(R.layout.item_position, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LocationBean item) {
        helper.setText(R.id.tvPosition, item.getName());
        TextView tvPosition = helper.getView(R.id.tvPosition);
        tvPosition.setSelected(item.isCheck());
    }
}
