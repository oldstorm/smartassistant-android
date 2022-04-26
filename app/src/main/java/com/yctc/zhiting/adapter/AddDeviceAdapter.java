package com.yctc.zhiting.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.home.DeviceBean;
import com.yctc.zhiting.utils.StringUtil;

import java.util.List;

public class AddDeviceAdapter extends BaseQuickAdapter<DeviceBean, BaseViewHolder> {

    public AddDeviceAdapter(List<DeviceBean> data) {
        super(R.layout.item_add_device, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceBean item) {
        helper.setText(R.id.tvName, item.getName());
        ImageView ivLogo = helper.getView(R.id.ivCover);
        if (TextUtils.isEmpty(item.getSa_id())) {
            GlideUtil.load(item.getLogoUrl()).into(ivLogo);
        } else {
            ivLogo.setImageResource(R.drawable.img_sa);
        }

        String decText = item.isBind() ? UiUtil.getString(R.string.mine_home_device_join) : UiUtil.getString(R.string.mine_home_device_add);
        String tag = item.getPluginId();
        helper.setText(R.id.tvTodo, decText)
                .setText(R.id.tvTag, StringUtil.isNotEmpty(tag) ? tag : "zhiting");
        helper.addOnClickListener(R.id.tvTodo);
    }
}
