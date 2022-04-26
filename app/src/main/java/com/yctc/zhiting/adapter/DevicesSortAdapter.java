package com.yctc.zhiting.adapter;

import android.widget.ImageView;

import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;

/**
 * Author by Ouyangle, Date on 2022/4/2.
 * PS: Not easy to write code, please indicate.
 */
public class DevicesSortAdapter extends BaseQuickAdapter<DeviceMultipleBean, BaseViewHolder> {
    public DevicesSortAdapter() {
        super(R.layout.item_devices_sort_view);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceMultipleBean item) {
        ImageView ivType = helper.getView(R.id.ivType);
        GlideUtil.load(item.getLogo_url()).into(ivType);
        helper.setText(R.id.tvName, item.getName());
    }
}
