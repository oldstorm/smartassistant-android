package com.yctc.zhiting.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimajia.swipe.SwipeLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.scene.ConditionBean;
import com.yctc.zhiting.entity.scene.CreateScenePost;

import java.util.List;

/**
 * 创建场景条件列表
 */
public class ConditionAdapter extends BaseQuickAdapter<ConditionBean, BaseViewHolder> {

    private boolean swipe = true;

    public ConditionAdapter() {
        super(R.layout.item_condition);
    }

    public void setSwipe(boolean swipe) {
        this.swipe = swipe;
    }

    @Override
    protected void convert(BaseViewHolder helper, ConditionBean item) {
        helper.addOnClickListener(R.id.llContent);
        helper.addOnClickListener(R.id.tvDel);
        boolean notLast = helper.getAdapterPosition()<getData().size()-1;
        SwipeLayout swipeLayout = helper.getView(R.id.swipeLayout);
        swipeLayout.setSwipeEnabled(swipe);
        LinearLayout llContent = helper.getView(R.id.llContent);
        TextView tvDel = helper.getView(R.id.tvDel);
        View viewLine = helper.getView(R.id.viewLine);
        llContent.setBackgroundResource(notLast ? R.drawable.shape_white : R.drawable.shape_white_bottom_c10 );
        tvDel.setBackgroundResource(notLast ? R.drawable.shape_red : R.drawable.shape_red_bottom_right_c10);
        viewLine.setVisibility(notLast ? View.VISIBLE : View.GONE);

        int type = item.getType();
        TextView tvTitle = helper.getView(R.id.tvTitle);
        TextView tvSubtitle = helper.getView(R.id.tvSubtitle);
        TextView tvLocation = helper.getView(R.id.tvLocation);
        tvSubtitle.setVisibility(type>0 ? View.VISIBLE : View.GONE);
//        tvLocation.setVisibility(type == 2 ? View.VISIBLE : View.GONE);
        ImageView ivCover = helper.getView(R.id.ivCover);
        switch (type){
            case 0: // 手动
                tvTitle.setText(item.getName());
                ivCover.setBackgroundResource(R.color.white);
                ivCover.setImageResource(R.drawable.icon_scene_manual);
                break;

            case 1:  // 定时
                tvTitle.setText(item.getTimingStr());
                tvSubtitle.setText(item.getName());
                ivCover.setBackgroundResource(R.color.white);
                ivCover.setImageResource(R.drawable.icon_scene_timing);
                break;

            case 2: // 场景
                tvTitle.setText(item.getName());
                tvSubtitle.setText(item.getDeviceName());
                ivCover.setBackgroundResource(R.drawable.shape_stroke_eeeeee_c4);
                GlideUtil.load(item.getLogoUrl()).into(ivCover);
                break;
        }
    }
}
