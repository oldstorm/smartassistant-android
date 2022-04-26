package com.yctc.zhiting.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;

public class DLPwdAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private boolean mIsEdit;

    public void setIsEdit(boolean isEdit) {
        this.mIsEdit = isEdit;
        notifyDataSetChanged();
    }

    public DLPwdAdapter() {
        super(R.layout.item_dl_pwd);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.addOnClickListener(R.id.tvRemove);
        helper.addOnClickListener(R.id.tvEdit);
        TextView tvRemove = helper.getView(R.id.tvRemove);
        TextView tvEdit = helper.getView(R.id.tvEdit);
        tvRemove.setVisibility(mIsEdit ? View.VISIBLE : View.GONE);
        tvEdit.setVisibility(mIsEdit ? View.VISIBLE : View.GONE);
    }
}
