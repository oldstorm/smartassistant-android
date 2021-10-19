package com.yctc.zhiting.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimajia.swipe.SwipeLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.SceneTaskBean;
import com.yctc.zhiting.utils.TimeUtil;


public class SceneTaskAdapter extends BaseQuickAdapter<SceneTaskBean, BaseViewHolder> {


    public SceneTaskAdapter() {
        super(R.layout.item_scene_task);
    }




    @Override
    protected void convert(BaseViewHolder helper, SceneTaskBean item) {
        helper.addOnClickListener(R.id.llContent);
        helper.addOnClickListener(R.id.tvDel);
        boolean notLast = helper.getAdapterPosition()<getData().size()-1;
        LinearLayout llContent = helper.getView(R.id.llContent);
        TextView tvDel = helper.getView(R.id.tvDel);
        View viewLine = helper.getView(R.id.viewLine);
        llContent.setBackgroundResource(notLast ? R.drawable.shape_white : R.drawable.shape_white_bottom_c10 );
        tvDel.setBackgroundResource(notLast ? R.drawable.shape_red : R.drawable.shape_red_bottom_right_c10);
        viewLine.setVisibility(notLast ? View.VISIBLE : View.GONE);
        TextView tvTitle = helper.getView(R.id.tvTitle);
        TextView tvSubtitle = helper.getView(R.id.tvSubtitle);
        tvTitle.setText(item.getTitle());
        tvSubtitle.setText(item.getSubTitle());
        TextView tvLocation = helper.getView(R.id.tvLocation);
        int deviceStatus = item.getDeviceStatus();
        if(item.getType() == 1) {
            tvLocation.setVisibility(!TextUtils.isEmpty(item.getLocation()) || deviceStatus == 2 ? View.VISIBLE : View.GONE);
            tvLocation.setText(deviceStatus == 2 ? mContext.getResources().getString(R.string.scene_device_removed) : item.getLocation());
            tvLocation.setTextColor(deviceStatus == 2 ? UiUtil.getColor(R.color.color_F6AE1E) : UiUtil.getColor(R.color.color_94A5BE));
        }else {
            tvLocation.setVisibility(item.getSceneStatus() == 2 ? View.VISIBLE : View.GONE);
            tvLocation.setText(mContext.getResources().getString(R.string.scene_scene_removed));
            tvLocation.setTextColor(UiUtil.getColor(R.color.color_F6AE1E));
        }
        /**
         * 设备状态:1为正常;2为被删除;3为离线
         */


        TextView tvTime = helper.getView(R.id.tvTime);
        tvTime.setVisibility(item.getDelay_seconds()>0 ? View.VISIBLE : View.GONE);
        String time = TimeUtil.toHMSString(item.getHour(), item.getMinute(), item.getSeconds(), mContext);
        tvTime.setText(String.format(mContext.getResources().getString(R.string.scene_delay_after), time));

        ImageView ivCover = helper.getView(R.id.ivCover);
        if (item.getType() == 1){ // 设备
            ivCover.setBackgroundResource(R.drawable.shape_stroke_eeeeee_c4);
            GlideUtil.load(item.getLogo()).into(ivCover);
        }else {  // 场景
            ivCover.setBackgroundResource(R.color.white);
            ivCover.setImageResource(R.drawable.icon_scene);
        }
    }
}
