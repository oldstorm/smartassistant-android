package com.yctc.zhiting.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.RoomAreaBean;

/**
 * 房间/区域管理
 */
public class RoomAreaAdapter extends BaseQuickAdapter<LocationBean, BaseViewHolder> {

    private boolean isEdit;

    public RoomAreaAdapter() {
        super(R.layout.item_room_area);
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, LocationBean item) {
        ImageView imageView = helper.getView(R.id.ivIndicator);
        imageView.setImageResource(isEdit ? R.drawable.icon_mine_edit : R.drawable.icon_gray_right_arrow);
        helper.setText(R.id.tvName, item.getName());
    }
}
