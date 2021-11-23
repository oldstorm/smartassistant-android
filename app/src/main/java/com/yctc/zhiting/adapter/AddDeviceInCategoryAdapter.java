package com.yctc.zhiting.adapter;

import android.widget.ImageView;

import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.home.DeviceTypeDeviceBean;

public class AddDeviceInCategoryAdapter extends BaseQuickAdapter<DeviceTypeDeviceBean, BaseViewHolder> {

    public AddDeviceInCategoryAdapter() {
        super(R.layout.item_add_device_in_category);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceTypeDeviceBean item) {
        ImageView ivLogo = helper.getView(R.id.ivLogo);
        GlideUtil.load(item.getLogo()).into(ivLogo);
        helper.setText(R.id.tvName, item.getName());
    }
}
