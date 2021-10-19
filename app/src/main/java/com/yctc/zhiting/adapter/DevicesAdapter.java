package com.yctc.zhiting.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.home.TestBean;

import java.util.List;

public class DevicesAdapter extends BaseQuickAdapter<TestBean, BaseViewHolder> {

    public DevicesAdapter(List<TestBean> data) {
        super(R.layout.item_device, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TestBean item) {
        helper.setText(R.id.tvDeviceName, item.getName());
    }
}
