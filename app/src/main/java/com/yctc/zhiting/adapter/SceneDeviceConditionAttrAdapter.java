package com.yctc.zhiting.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.DeviceDetailEntity;
import com.yctc.zhiting.entity.scene.SceneConditionAttrEntity;
import com.yctc.zhiting.utils.StringUtil;

public class SceneDeviceConditionAttrAdapter extends BaseQuickAdapter<SceneConditionAttrEntity, BaseViewHolder> {

    public SceneDeviceConditionAttrAdapter() {
        super(R.layout.item_scene_device_condition_attr);
    }

    @Override
    protected void convert(BaseViewHolder helper, SceneConditionAttrEntity item) {
        helper.setText(R.id.tvDeviceName, StringUtil.attr2String(item.getType(), mContext));
    }
}
