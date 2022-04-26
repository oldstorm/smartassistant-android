package com.yctc.zhiting.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lihang.ShadowLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.home.DeviceLogoBean;

public class ChangeIconAdapter extends BaseQuickAdapter<DeviceLogoBean, BaseViewHolder> {

    public ChangeIconAdapter() {
        super(R.layout.item_change_icon);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceLogoBean item) {
        ShadowLayout slParent= helper.getView(R.id.slParent);
        ImageView ivSelected = helper.getView(R.id.ivSelected);
        ImageView ivCover = helper.getView(R.id.ivCover);
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setText(item.getName());
        boolean selected = item.isSelected();
        slParent.setShadowColor(selected ? UiUtil.getColor(R.color.color_26151515) : UiUtil.getColor(R.color.transparent));
        ivSelected.setVisibility(selected ? View.VISIBLE : View.GONE);
        GlideUtil.load(item.getUrl()).into(ivCover);
    }
}
