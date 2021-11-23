package com.yctc.zhiting.adapter;



import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.home.DeviceTypeBean;

/**
 * 设备分类
 */
public class DeviceCategoryAdapter extends BaseQuickAdapter<DeviceTypeBean, BaseViewHolder> {

    public DeviceCategoryAdapter() {
        super(R.layout.item_device_category);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceTypeBean item) {
        helper.getView(R.id.tvName).setSelected(item.isSelected());
        helper.setText(R.id.tvName, item.getName());
    }
}
