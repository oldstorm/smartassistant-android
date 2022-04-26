package com.yctc.zhiting.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.home.SocketDeviceInfoBean;
import com.yctc.zhiting.utils.CollectionUtil;

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
                TextView tvSituation = helper.getView(R.id.tvSituation);

                int maxWidth = (UiUtil.getScreenWidth()-UiUtil.dip2px(28))/2-UiUtil.dip2px(34);
                TextView tvName = helper.getView(R.id.tvName);
                tvName.setMaxWidth((item.isIs_sa() || item.isOnline())? maxWidth : maxWidth-UiUtil.dip2px(50));
                helper.setText(R.id.tvName, item.getName());
                helper.addOnClickListener(R.id.ivSwitch);
                ImageView ivSwitch = helper.getView(R.id.ivSwitch);
                ImageView ivCover = helper.getView(R.id.ivCover);
                GlideUtil.load(item.getLogo_url()).into(ivCover);

                if (item.isIs_sa()) {
                    ivSwitch.setVisibility(View.GONE);
                    helper.setVisible(R.id.tvStatus, false);
                } else {
                    String power = item.getPower();
                    boolean select = (item.getPower() != null && (power.equalsIgnoreCase(Constant.PowerType.TYPE_ON) || power.equals(Constant.PowerType.HOMEKIT_TYPE_ON)));
                    ivSwitch.setSelected(select);
                    ivSwitch.setVisibility(item.isIs_permit() && item.isOnline() ? View.VISIBLE : View.GONE);
                    helper.setVisible(R.id.tvStatus, !item.isOnline());
                }

//                if (ivSwitch.getVisibility() == View.VISIBLE) {
//                    tvSituation.setVisibility(View.GONE);
//                    return;
//                }

                //1开关 2权限/开关 3水浸传感器 4门窗传感器 5人体传感器 6温湿度传感器
                if (item.getDeviceType() == 3 && item.getLeak_detected() != null) {
                    double leakDetected = Double.parseDouble(item.getLeak_detected());
                    tvSituation.setVisibility(item.isOnline() ? View.VISIBLE : View.GONE);
                    if (leakDetected == 0) {
                        tvSituation.setText(R.string.home_no_water);
                        tvSituation.setTextColor(UiUtil.getColor(R.color.color_94a5be));
                    } else {
                        tvSituation.setText(R.string.home_water);
                        tvSituation.setTextColor(UiUtil.getColor(R.color.color_ff7f7f));
                    }
                } else if (item.getDeviceType() == 4 && item.getWindow_door_close() != null) {
                    tvSituation.setVisibility(item.isOnline() ? View.VISIBLE : View.GONE);
                    tvSituation.setTextColor(UiUtil.getColor(R.color.color_94a5be));
                    double windowDoorClose = Double.parseDouble(item.getWindow_door_close());
                    if (windowDoorClose == 0) {
                        tvSituation.setText(R.string.home_door_close);
                    } else {
                        tvSituation.setText(R.string.home_door_open);
                    }
                } else if (item.getDeviceType() == 5 && item.getDetected() != null) {
                    double detected = Double.parseDouble(item.getDetected());
                    tvSituation.setText(R.string.home_person_move);
                    tvSituation.setTextColor(UiUtil.getColor(R.color.color_ff7f7f));
                    tvSituation.setVisibility(item.isOnline() && detected == 1 ? View.VISIBLE : View.GONE);
                } else if (item.getDeviceType() == 6) {
                    tvSituation.setText(item.getTemperature());
                    tvSituation.setTextColor(UiUtil.getColor(R.color.color_94a5be));
                    tvSituation.setVisibility(item.isOnline() ? View.VISIBLE : View.GONE);
                } else {
                    tvSituation.setVisibility(View.GONE);
                }

                handleSwitch(ivSwitch, tvSituation, item);
                break;
            default:
                break;
        }
    }

    /**
     * 开关
     */
    private void handleSwitch(ImageView ivSwitch, TextView tvSituation, DeviceMultipleBean item) {
        int switchCount = 0;
        String switchDesc = "";
        if (CollectionUtil.isNotEmpty(item.getSwitchList())) {
            LogUtil.e("handleSwitch2=" + item.getSwitchList().size());
            for (SocketDeviceInfoBean.ResultBean.DeviceBean.InstancesBean instancesBean : item.getSwitchList()) {
                if (instancesBean.getType().equals("switch")) {
                    switchCount++;
                    for (SocketDeviceInfoBean.ResultBean.DeviceBean.InstancesBean.AttributesBean attribute : instancesBean.getAttributes()) {
                        if (attribute.getAttribute().equals("power")) {
                            String value = attribute.getVal().toString();
                            if (TextUtils.isEmpty(value) || value.equals("")) value = "off";
                            if (value.equals("on")) {
                                switchDesc = switchDesc + "开|";
                            } else {
                                switchDesc = switchDesc + "关|";
                            }
                        }
                    }
                }
            }
            if (switchDesc.endsWith("|"))
                switchDesc = switchDesc.substring(0,switchDesc.length()-1);
            if (switchCount > 1) {
                ivSwitch.setVisibility(View.GONE);
                tvSituation.setVisibility(View.VISIBLE);
                tvSituation.setText(switchDesc);
                tvSituation.setTextColor(UiUtil.getColor(R.color.color_94A5BE));
            } else {
                tvSituation.setVisibility(View.GONE);
            }
        }
    }
}
