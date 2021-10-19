package com.yctc.zhiting.adapter;

import android.widget.ImageView;
import android.widget.TextView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;


/**
 * 切换家庭
 */
public class HomeSelectAdapter extends BaseQuickAdapter<HomeCompanyBean, BaseViewHolder> {

    public HomeSelectAdapter() {
        super(R.layout.item_home_select);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeCompanyBean item) {
        ImageView imageView = helper.getView(R.id.imageView);
        ImageView ivSel = helper.getView(R.id.ivSel);
        TextView tvName = helper.getView(R.id.tvName);
        imageView.setSelected(item.isSelected());
        ivSel.setSelected(item.isSelected());
        tvName.setSelected(item.isSelected());
        tvName.setText(item.getName());
    }
}
