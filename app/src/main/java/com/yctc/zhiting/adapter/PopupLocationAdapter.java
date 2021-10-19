package com.yctc.zhiting.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.RoomListBean;

import java.util.List;

public class PopupLocationAdapter extends BaseQuickAdapter<LocationBean, BaseViewHolder> {

    public PopupLocationAdapter() {
        super(R.layout.item_popup_location);
    }

    @Override
    protected void convert(BaseViewHolder helper, LocationBean item) {
        boolean notLast = helper.getAdapterPosition()<getData().size()-1;
        int size = getData().size();
        TextView tvName= helper.getView(R.id.tvName);
        View viewLine = helper.getView(R.id.viewLine);
        tvName.setText(item.getName());
        tvName.setSelected(item.isCheck());
        viewLine.setVisibility(notLast ? View.VISIBLE : View.GONE);
        if (!notLast &&  size == 1){
            tvName.setBackgroundResource(R.drawable.shape_white_c10);
        }else if (!notLast && size>1){
            tvName.setBackgroundResource(R.drawable.shape_white_bottom_c10);
        }else {
            tvName.setBackgroundResource(R.drawable.shape_white);
        }
    }
}
