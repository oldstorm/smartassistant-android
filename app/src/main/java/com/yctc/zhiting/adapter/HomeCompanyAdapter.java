package com.yctc.zhiting.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;

/**
 * 家庭/公司
 */
public class HomeCompanyAdapter extends BaseQuickAdapter<HomeCompanyBean, BaseViewHolder> {

    public HomeCompanyAdapter() {
        super(R.layout.item_home_company);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeCompanyBean item) {
        helper.setText(R.id.tvName, item.getName());
    }
}
