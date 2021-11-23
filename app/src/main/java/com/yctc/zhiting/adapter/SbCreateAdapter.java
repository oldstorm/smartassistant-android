package com.yctc.zhiting.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.CreatePluginListBean;
import com.yctc.zhiting.widget.RingProgressBar;
import com.yctc.zhiting.widget.TagTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 品牌创作
 */
public class SbCreateAdapter extends BaseQuickAdapter<CreatePluginListBean.PluginsBean, BaseViewHolder> {

    public SbCreateAdapter() {
        super(R.layout.item_sb_create);
    }

    @Override
    protected void convert(BaseViewHolder helper, CreatePluginListBean.PluginsBean item) {
        helper.addOnClickListener(R.id.tvDel);
        TagTextView tagTextView = helper.getView(R.id.tvName);
        List<String> tags = new ArrayList<>();
        int buildStatus = item.getBuild_status();
        boolean loading = item.isLoading();
        if (buildStatus == -1)
        tags.add(UiUtil.getString(R.string.add_fail));
        tagTextView.setContentAndTag(item.getName()+"\t", tags);
        helper.setText(R.id.tvDesc, item.getInfo());
        RingProgressBar ringProgressBar = helper.getView(R.id.ringProgressBar);
        TextView tvDel = helper.getView(R.id.tvDel);
        TextView tvDesc = helper.getView(R.id.tvDesc);
        tvDesc.setVisibility(TextUtils.isEmpty(item.getInfo()) ? View.GONE : View.VISIBLE);
        tvDesc.setText(item.getInfo());
        ringProgressBar.setVisibility(loading || buildStatus == 0 ? View.VISIBLE : View.GONE);
        ringProgressBar.setProgress(30);
        ringProgressBar.setRotating(loading || buildStatus == 0);
        tvDel.setVisibility(!loading && buildStatus != 0 ? View.VISIBLE : View.GONE);
    }
}
