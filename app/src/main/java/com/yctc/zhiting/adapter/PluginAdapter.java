package com.yctc.zhiting.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.PluginsBean;
import com.yctc.zhiting.widget.RingProgressBar;

import java.util.List;

/**
 *  品牌详情的插件列表
 */
public class PluginAdapter extends BaseQuickAdapter<PluginsBean, BaseViewHolder> {

    public PluginAdapter() {
        super(R.layout.item_plugin);
    }

    @Override
    protected void convert(BaseViewHolder helper, PluginsBean item) {

        helper.addOnClickListener(R.id.tvDel);
        helper.addOnClickListener(R.id.tvUpdate);
        helper.addOnClickListener(R.id.tvAdd);

        TextView tvDel = helper.getView(R.id.tvDel);
        TextView tvUpdate = helper.getView(R.id.tvUpdate);
        TextView tvAdd = helper.getView(R.id.tvAdd);
        RingProgressBar ringProgressBar = helper.getView(R.id.ringProgressBar);
        View viewLine= helper.getView(R.id.viewLine);

        viewLine.setVisibility(helper.getAdapterPosition() == getData().size()-1 ? View.GONE : View.VISIBLE);
        helper.setText(R.id.tvName, item.getName())
                .setText(R.id.tvVersion, mContext.getResources().getString(R.string.brand_versionCode) + item.getVersion())
                .setText(R.id.tvDesc, item.getInfo());
        boolean isNew = item.isIs_newest();
        boolean isAdded = item.isIs_added();
        if (item.isUpdating()){ // 更新中
            ringProgressBar.setRotating(true);
            ringProgressBar.setProgress(30);
            tvDel.setVisibility(View.GONE);
            tvUpdate.setVisibility(View.GONE);
            tvAdd.setVisibility(View.GONE);
            ringProgressBar.setVisibility(View.VISIBLE);
        }else { // 没更新
            ringProgressBar.setRotating(false);
            ringProgressBar.setVisibility(View.GONE);
            tvDel.setVisibility(isAdded ? View.VISIBLE : View.GONE);
            tvUpdate.setVisibility(isAdded && !isNew ? View.VISIBLE : View.GONE);
            tvAdd.setVisibility(isAdded ? View.GONE : View.VISIBLE);
        }

    }
}
