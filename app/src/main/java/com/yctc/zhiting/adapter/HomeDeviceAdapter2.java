package com.yctc.zhiting.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.ws_response.InstanceBean;
import com.yctc.zhiting.entity.ws_response.ServicesBean;
import com.yctc.zhiting.entity.ws_response.WSDeviceResponseBean;
import com.yctc.zhiting.factory.HomeDeviceFactory;
import com.yctc.zhiting.utils.CollectionUtil;

import java.util.List;

public class HomeDeviceAdapter2 extends BaseMultiItemQuickAdapter<DeviceMultipleBean, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public HomeDeviceAdapter2(List<DeviceMultipleBean> data, boolean isLinear) {
        super(data);
        if(!isLinear){
            addItemType(DeviceMultipleBean.DEVICE, R.layout.item_home_device);
            addItemType(DeviceMultipleBean.ADD, R.layout.item_home_add);
        }else {
            addItemType(DeviceMultipleBean.DEVICE, R.layout.item_home_device2);
            addItemType(DeviceMultipleBean.ADD, R.layout.item_home_add2);
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceMultipleBean item) {
        item.setAttributes(null);
        switch (helper.getItemViewType()) {
            case DeviceMultipleBean.DEVICE:
                TextView tvSituation = helper.getView(R.id.tvSituation);
                tvSituation.setText("");
                tvSituation.setVisibility(View.GONE);
                int maxWidth = (UiUtil.getScreenWidth() - UiUtil.dip2px(28)) / 2 - UiUtil.dip2px(34);
                TextView tvName = helper.getView(R.id.tvName);
                tvName.setMaxWidth((item.isIs_sa() || item.isOnline()) ? maxWidth : maxWidth - UiUtil.dip2px(50));
                helper.setText(R.id.tvName, item.getName());
                helper.addOnClickListener(R.id.ivSwitch);
                ImageView ivSwitch = helper.getView(R.id.ivSwitch);
                ImageView ivCover = helper.getView(R.id.ivCover);
                GlideUtil.load(item.getLogo_url()).into(ivCover);

                if (item.isIs_sa()) {
                    ivSwitch.setVisibility(View.GONE);
                    helper.setVisible(R.id.tvStatus, false);
                } else {
                    ivSwitch.setVisibility(View.GONE);
                    helper.setVisible(R.id.tvStatus, !item.isOnline());
                    WSDeviceResponseBean deviceInstances = item.getDevice_instances();
                    if (deviceInstances != null) {
                        List<InstanceBean> instances = deviceInstances.getInstances();

                        if (CollectionUtil.isNotEmpty(instances)) {  // instance不为空
                            HomeDeviceFactory homeDeviceFactory = new HomeDeviceFactory(instances);
                            InstanceBean instanceBean = homeDeviceFactory.getInstance();
                            List<ServicesBean> services = instanceBean.getServices();  // 获取service列表
                            homeDeviceFactory.classifyService(services);
                            List<ServicesBean> containSwitchServices = homeDeviceFactory.getSwitchService(tvSituation, ivSwitch, item); //存储包含开关的服务列表
                            if (CollectionUtil.isNotEmpty(containSwitchServices)) return;
                            List<ServicesBean> thServices = homeDeviceFactory.getTHService(tvSituation, item); // 存温湿度感器服务列表
                            if (CollectionUtil.isNotEmpty(thServices)) return;
                            List<ServicesBean> leakServices = homeDeviceFactory.getLeakService(tvSituation, item); // 水浸传感器服务列表
                            if (CollectionUtil.isNotEmpty(leakServices)) return;
                            List<ServicesBean> wdServices = homeDeviceFactory.getWDService(tvSituation, item); // 门窗传感器服务列表
                            if (CollectionUtil.isNotEmpty(wdServices)) return;
                            List<ServicesBean> msServices = homeDeviceFactory.getMSService(tvSituation, item); // 人体传感器服务列表
                            if (CollectionUtil.isNotEmpty(msServices)) return;
                        }
                    }
                }

            default:
                break;
        }
    }
}
