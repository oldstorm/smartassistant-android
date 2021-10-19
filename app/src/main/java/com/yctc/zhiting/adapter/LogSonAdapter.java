package com.yctc.zhiting.adapter;

import android.os.strictmode.Violation;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.main.framework.baseutil.UiUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.scene.LogBean;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.StringUtil;
import com.yctc.zhiting.utils.TimeUtil;

import java.util.List;
import java.util.zip.Inflater;

public class LogSonAdapter extends BaseQuickAdapter<LogBean.ItemsBean, BaseViewHolder> {

    public LogSonAdapter() {
        super(R.layout.item_log_son2);
    }

    @Override
    protected void convert(BaseViewHolder helper, LogBean.ItemsBean item) {

        /************************** 显示圆点上下的线 ***********************************/
//        View viewLine1 = helper.getView(R.id.viewLine1);
//        View viewLine2 = helper.getView(R.id.viewLine2);
//        viewLine1.setVisibility(helper.getAdapterPosition()>0 ? View.VISIBLE : View.GONE);
//        viewLine2.setVisibility(helper.getAdapterPosition()<getData().size()-1 ? View.VISIBLE : View.GONE);
        ImageView ivSmallCircle1 = helper.getView(R.id.ivSmallCircle1);
        ImageView ivSmallCircle2 = helper.getView(R.id.ivSmallCircle2);
        ivSmallCircle1.setVisibility(helper.getAdapterPosition()==0 ? View.VISIBLE : View.GONE);
        ivSmallCircle2.setVisibility(helper.getAdapterPosition()==getData().size()-1 ? View.VISIBLE : View.GONE);

        /************************** 展示列表数据 ***********************************/
        TextView tvTime = helper.getView(R.id.tvTime);
        LinearLayout llDevice = helper.getView(R.id.llDevice);
        ImageView ivExpand = helper.getView(R.id.ivExpand);
        String status = TimeUtil.getMDHM(item.getFinished_at()) + " " + StringUtil.getLogStatus(mContext, item.getResult());
        helper.setText(R.id.tvName, item.getName())
                .setText(R.id.tvTime, status);
        ivExpand.setVisibility(CollectionUtil.isNotEmpty(item.getItems()) && item.getResult()!=1 ? View.VISIBLE : View.GONE);
        int color = UiUtil.getColor(R.color.color_3f4663);
        /**
         * 1执行完成;2部分执行完成;3执行失败
         */
        switch (item.getResult()){
            case 1:
                color = UiUtil.getColor(R.color.color_3f4663);
                break;
            case 2:
                color = UiUtil.getColor(R.color.color_F3A934);
                break;
            case 3:
                color = UiUtil.getColor(R.color.color_FF0000);
                break;
        }
        tvTime.setTextColor(color);
//        llDevice.setVisibility(CollectionUtil.isNotEmpty(item.getItems()) && item.getResult()!=1 ? View.VISIBLE : View.GONE);
        if (CollectionUtil.isNotEmpty(item.getItems())) {
            setPerformItem(llDevice, item.getItems());
        }
        ivExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivExpand.setSelected(!ivExpand.isSelected());
                llDevice.setVisibility(ivExpand.isSelected() ? View.VISIBLE : View.GONE);
            }
        });
    }

    // 设置子任务数据
    private void setPerformItem(LinearLayout llParent, List<LogBean.DeviceStatusBean> data){
        llParent.removeAllViews();
        for (int i=0; i<data.size(); i++){
            LogBean.DeviceStatusBean deviceStatusBean = data.get(i);
            View view = View.inflate(mContext, R.layout.layout_log_detail, null);
            TextView tvDevice = view.findViewById(R.id.tvDevice);
            TextView tvStatus = view.findViewById(R.id.tvStatus);
            TextView tvLocation = view.findViewById(R.id.tvLocation);
            View viewLine = view.findViewById(R.id.viewLine);
            viewLine.setVisibility(i==data.size()-1 ? View.VISIBLE : View.GONE);
            tvDevice.setText(deviceStatusBean.getName());
            tvLocation.setText(deviceStatusBean.getLocation_name());
            tvStatus.setText(StringUtil.getLogStatus(mContext, deviceStatusBean.getResult()));
            tvStatus.setTextColor(deviceStatusBean.getResult() == 1 ? UiUtil.getColor(R.color.color_3f4663) : UiUtil.getColor(R.color.color_FF0000));
            llParent.addView(view);
        }
    }
}
