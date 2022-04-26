package com.yctc.zhiting.adapter;


import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.ACProductBean;

/**
 * 账号注销 -- 产品列表
 */
public class ACProductAdapter extends BaseQuickAdapter<ACProductBean, BaseViewHolder> {

    public ACProductAdapter() {
        super(R.layout.item_ac_product);
    }

    @Override
    protected void convert(BaseViewHolder helper, ACProductBean item) {
        ImageView ivLogo = helper.getView(R.id.ivLogo);
        ivLogo.setImageResource(item.getLogo());
        helper.setText(R.id.tvName, item.getName());
    }
}
