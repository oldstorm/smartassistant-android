package com.yctc.zhiting.adapter;

import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.DevicesBean;

import java.util.List;

/**
 * 房间/区域设备列表
 */
public class EquipmentAdapter extends
        BaseQuickAdapter<DevicesBean, BaseViewHolder> {
    public EquipmentAdapter() {
        super(R.layout.item_equipment);
    }

    @Override
    protected void convert(BaseViewHolder helper, DevicesBean item) {
        ImageView imageView = helper.getView(R.id.ivEquipment);
        GlideUtil.load(item.getLogo_url()).into(imageView);
        helper.setText(R.id.tvName, item.getName());
    }
}
