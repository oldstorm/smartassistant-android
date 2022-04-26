package com.yctc.zhiting.adapter;


import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;


public class DLLogAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private boolean mShowSmallCir;

    public DLLogAdapter(boolean showSmallCir) {
        super(R.layout.item_dl_log);
        this.mShowSmallCir = showSmallCir;
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        ImageView ivSmallCircle1 = helper.getView(R.id.ivSmallCircle1);
        View viewLine1 = helper.getView(R.id.viewLine1);
        View viewLine2 = helper.getView(R.id.viewLine2);
        View ivSmallCircle2 = helper.getView(R.id.ivSmallCircle2);
        if (mShowSmallCir) {
            ivSmallCircle1.setVisibility(helper.getAdapterPosition() == 0 ? View.VISIBLE : View.GONE);
            ivSmallCircle2.setVisibility(helper.getAdapterPosition() == getData().size()-1 ? View.VISIBLE : View.GONE);
            viewLine1.setVisibility(View.VISIBLE);
            viewLine2.setVisibility(View.VISIBLE);
        } else {
            viewLine1.setVisibility(helper.getAdapterPosition() == 0 ? View.GONE : View.VISIBLE);
            viewLine2.setVisibility(helper.getAdapterPosition() == getData().size()-1 ? View.GONE : View.VISIBLE);
        }
    }
}
