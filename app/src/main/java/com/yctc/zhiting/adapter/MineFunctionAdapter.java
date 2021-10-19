package com.yctc.zhiting.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.bean.MineFunctionBean;

import java.util.List;

/**
 * 我的功能列表适配器
 */
public class MineFunctionAdapter extends BaseQuickAdapter<MineFunctionBean, BaseViewHolder> {

    public MineFunctionAdapter() {
        super(R.layout.item_mine_function);
    }

    @Override
    protected void convert(BaseViewHolder helper, MineFunctionBean item) {
        ImageView ivLogo = helper.getView(R.id.ivLogo);
        ivLogo.setImageResource(item.getLogo());
        TextView tvName = helper.getView(R.id.tvName);
        tvName.setEnabled(item.isEnable());
        helper.setText(R.id.tvName, item.getName());
    }
}
