package com.yctc.zhiting.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.entity.scene.SceneDeviceStatusControlBean;

import java.util.List;

public class SceneDeviceStatusControlAdapter extends BaseQuickAdapter<DeviceDetailBean.DeviceInfoBean.ActionsBean, BaseViewHolder> {

    public SceneDeviceStatusControlAdapter() {
        super(R.layout.item_scene_device_status_control);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceDetailBean.DeviceInfoBean.ActionsBean item) {
        helper.setText(R.id.tvDeviceName, item.getName());
    }
}
