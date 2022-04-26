package com.yctc.zhiting.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.NameBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;

/**
 * 账号注销里的家庭
 */
public class ACHomeAdapter extends BaseQuickAdapter<HomeCompanyBean, BaseViewHolder> {

    public ACHomeAdapter() {
        super(R.layout.item_account_cancel_home);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeCompanyBean item) {
        helper.setText(R.id.tvName, item.getName());
    }
}
