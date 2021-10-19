package com.yctc.zhiting.adapter;

import android.widget.ImageView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.home.DeviceBean;

import java.util.List;

public class AddDeviceAdapter extends BaseQuickAdapter<DeviceBean, BaseViewHolder> {

    public AddDeviceAdapter(List<DeviceBean> data) {
        super(R.layout.item_add_device, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceBean item) {
        helper.setText(R.id.tvName, item.getName());
        ImageView ivLogo = helper.getView(R.id.ivCover);
        GlideUtil.load(item.getLogoUrl()).into(ivLogo);

        String decText = item.isBind() ? UiUtil.getString(R.string.mine_home_device_join) : UiUtil.getString(R.string.mine_home_device_add);
        helper.setText(R.id.tvTodo, decText);
        helper.addOnClickListener(R.id.tvTodo);
    }
}
