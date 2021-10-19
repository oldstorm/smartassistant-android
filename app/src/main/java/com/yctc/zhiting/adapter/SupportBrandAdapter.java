package com.yctc.zhiting.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.entity.mine.BrandsBean;
import com.yctc.zhiting.widget.RingProgressBar;

import java.util.List;

public class SupportBrandAdapter extends BaseQuickAdapter<BrandsBean, BaseViewHolder> {

    public SupportBrandAdapter() {
        super(R.layout.item_support_brand);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    protected void convert(BaseViewHolder helper, BrandsBean item) {
        helper.addOnClickListener(R.id.tvUpdate);
        TextView tvUpdate = helper.getView(R.id.tvUpdate);
        TextView tvAdded = helper.getView(R.id.tvAdded);
        TextView tvCount = helper.getView(R.id.tvCount);
        RingProgressBar ringProgressBar = helper.getView(R.id.ringProgressBar);
        ImageView ivCover = helper.getView(R.id.ivCover);
        GlideUtil.load(item.getLogo_url()).into(ivCover);
        helper.setText(R.id.tvName, item.getName())
                .setText(R.id.tvCount, String.format(mContext.getResources().getString(R.string.brand_count), item.getPlugin_amount()));
        if (item.isUpdating()) {  // 更新中
            ringProgressBar.setRotating(true);
            ringProgressBar.setProgress(30);
            tvAdded.setVisibility( View.GONE);
            tvUpdate.setVisibility(View.GONE);
            ringProgressBar.setVisibility(View.VISIBLE);
        }else {
            ringProgressBar.setRotating(false);
            ringProgressBar.setVisibility(View.GONE);
            if (item.isIs_added()) {  // 已添加
                tvAdded.setVisibility(item.isIs_newest() ? View.VISIBLE : View.GONE);  // 是最新，已添加可见
                tvUpdate.setText(mContext.getResources().getString(R.string.mine_mine_update));
                tvUpdate.setVisibility(item.isIs_newest() ? View.GONE : View.VISIBLE);// 不是最新，更新可见
            } else { // 为添加
                tvUpdate.setText(mContext.getResources().getString(R.string.mine_add));
                tvUpdate.setVisibility(View.VISIBLE);
                tvAdded.setVisibility(View.GONE);
            }
        }
    }
}
