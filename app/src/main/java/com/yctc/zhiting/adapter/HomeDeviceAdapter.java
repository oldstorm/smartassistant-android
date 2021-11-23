package com.yctc.zhiting.adapter;

import android.view.View;
import android.widget.ImageView;
import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;

import java.util.List;

public class HomeDeviceAdapter extends BaseMultiItemQuickAdapter<DeviceMultipleBean, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public HomeDeviceAdapter(List<DeviceMultipleBean> data) {
        super(data);
        addItemType(DeviceMultipleBean.DEVICE, R.layout.item_home_device);
        addItemType(DeviceMultipleBean.ADD, R.layout.item_home_add);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceMultipleBean item) {

        switch (helper.getItemViewType()) {
            case DeviceMultipleBean.DEVICE:
                helper.setText(R.id.tvName, item.getName());
                helper.addOnClickListener(R.id.ivSwitch);
                ImageView ivSwitch = helper.getView(R.id.ivSwitch);
                ImageView ivCover = helper.getView(R.id.ivCover);
                GlideUtil.load(item.getLogo_url()).into(ivCover);

                if (item.isIs_sa()) {
                    ivSwitch.setVisibility(View.GONE);
                    helper.setVisible(R.id.tvStatus, false);
                } else {
                    boolean select = (item.getPower() != null && item.getPower().equalsIgnoreCase(Constant.PowerType.TYPE_ON));
                    ivSwitch.setSelected(select);
                    ivSwitch.setVisibility(item.isIs_permit() && item.isOnline() ? View.VISIBLE : View.GONE);
                    helper.setVisible(R.id.tvStatus, !item.isOnline());
                }
                break;
            default:
                break;
        }
    }
}
