package com.yctc.zhiting.adapter;

import android.widget.ImageView;
import android.widget.TextView;


import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;




/**
 * 场景可控制设备列表
 */
public class SceneDevicesSonAdapter extends BaseQuickAdapter<DeviceMultipleBean, BaseViewHolder> {

    public SceneDevicesSonAdapter() {
        super(R.layout.item_scene_devices_son);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceMultipleBean item) {
        ImageView ivCover = helper.getView(R.id.ivCover);
        TextView tvName = helper.getView(R.id.tvName);
        TextView tvLocation = helper.getView(R.id.tvLocation);
        GlideUtil.load(item.getLogo_url()).into(ivCover);
        tvName.setText(item.getName());
        tvLocation.setText(item.getLocation_name());

    }
}
