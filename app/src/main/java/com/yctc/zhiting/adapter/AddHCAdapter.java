package com.yctc.zhiting.adapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.LocationTmpl;
import com.yctc.zhiting.entity.mine.RoomAreaBean;

import java.util.List;

/**
 * 添加家庭/公司等区域
 */
public class AddHCAdapter extends BaseQuickAdapter<LocationTmpl, BaseViewHolder> {

    public AddHCAdapter() {
        super(R.layout.item_add_h_c);
    }

    @Override
    protected void convert(BaseViewHolder helper, LocationTmpl item) {
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setText(item.getName());
        tvName.setSelected(item.isChosen());
    }
}
