package com.yctc.zhiting.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.ThirdPartyBean;

public class ThirdPartyAdapter extends BaseQuickAdapter<ThirdPartyBean.AppsBean, BaseViewHolder> {

    public ThirdPartyAdapter() {
        super(R.layout.item_third_party);
    }

    @Override
    protected void convert(BaseViewHolder helper, ThirdPartyBean.AppsBean item) {
        ImageView ivCover = helper.getView(R.id.ivCover);
        ImageView ivAuthorized = helper.getView(R.id.ivAuthorized);
        TextView tvName = helper.getView(R.id.tvName);
        GlideUtil.load(item.getImg()).into(ivCover);
        tvName.setText(item.getName());
        ivAuthorized.setVisibility(item.isIs_bind() ? View.VISIBLE : View.GONE);
    }
}
