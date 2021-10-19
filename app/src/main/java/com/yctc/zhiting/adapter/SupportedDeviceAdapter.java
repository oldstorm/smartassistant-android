package com.yctc.zhiting.adapter;

import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.SupportDevicesBean;

import java.util.List;

public class SupportedDeviceAdapter extends BaseQuickAdapter<SupportDevicesBean, BaseViewHolder> {

    public SupportedDeviceAdapter() {
        super(R.layout.item_supported_device);
    }

    @Override
    protected void convert(BaseViewHolder helper, SupportDevicesBean item) {
        ImageView ivCover = helper.getView(R.id.ivCover);
        GlideUtil.load(item.getLogo_url()).into(ivCover);
        helper.setText(R.id.tvName, item.getName());
    }
}
