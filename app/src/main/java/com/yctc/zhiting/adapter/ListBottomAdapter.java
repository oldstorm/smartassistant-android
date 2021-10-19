package com.yctc.zhiting.adapter;

import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.bean.ListBottomBean;

import java.util.List;

/**
 * 底部列表弹窗
 */
public class ListBottomAdapter extends BaseQuickAdapter<ListBottomBean, BaseViewHolder> {

    public ListBottomAdapter() {
        super(R.layout.item_list_bottom);
    }

    @Override
    protected void convert(BaseViewHolder helper, ListBottomBean item) {
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setText(item.getName());
        tvName.setSelected(item.isSelected());
    }
}
