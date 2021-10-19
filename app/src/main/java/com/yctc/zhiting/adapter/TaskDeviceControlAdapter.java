package com.yctc.zhiting.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.scene.SceneDeviceStatusControlBean;

public class TaskDeviceControlAdapter extends BaseQuickAdapter<SceneDeviceStatusControlBean, BaseViewHolder> {

    public TaskDeviceControlAdapter() {
        super(R.layout.item_task_device_control);
    }

    @Override
    protected void convert(BaseViewHolder helper, SceneDeviceStatusControlBean item) {
        helper.setText(R.id.tvDeviceName, item.getName())
                .setText(R.id.tvDeviceValue, item.getValue());
    }
}
