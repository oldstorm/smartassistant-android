package com.yctc.zhiting.factory;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.yctc.zhiting.R;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.ws_request.WSConstant;
import com.yctc.zhiting.entity.ws_response.AttributesBean;
import com.yctc.zhiting.entity.ws_response.InstanceBean;
import com.yctc.zhiting.entity.ws_response.ServicesBean;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.InstanceServiceUtil;
import com.yctc.zhiting.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeDeviceFactory {

    private List<InstanceBean> instances;
    private List<ServicesBean> services;

    List<ServicesBean> switchServices; //存储包含开关的服务列表
    List<ServicesBean> thServices; // 存温湿度感器服务列表
    List<ServicesBean> leakServices; // 水浸传感器服务列表
    List<ServicesBean> wdServices; // 门窗传感器服务列表
    List<ServicesBean> msServices; // 人体传感器服务列表
    List<ServicesBean> curtainServices; // 窗帘传感器服务列表

    public HomeDeviceFactory(List<InstanceBean> instances) {
        this.instances = instances;
    }

    public InstanceBean getInstance() {
        if (CollectionUtil.isNotEmpty(instances)) {
            InstanceBean instanceBean = instances.get(0);
            if (instances.size() > 1) {  // instance个数大于1
                for (InstanceBean instance : instances) { // 遍历instance
                    List<ServicesBean> services = instance.getServices();  // 获取service列表
                    if (CollectionUtil.isNotEmpty(services)) {  // service列表不为空
                        for (ServicesBean service : services) { // 遍历service
                            String type = service.getType();
                            // 如果是LIGHT_BULB、OUT_LET或SWITCH
                            if (!TextUtils.isEmpty(type) && InstanceServiceUtil.isSwitch(type)) {
                                instanceBean = instance;
                                break;
                            }
                        }
                    }
                }
            }
            return instanceBean;
        } else {
            return new InstanceBean();
        }
    }

    /**
     * 分类
     *
     * @param services
     */
    public void classifyService(List<ServicesBean> services) {
        this.services = services;
        if (CollectionUtil.isEmpty(services)) {
            LogUtil.e("services不能为空");
            return;
        }
        // services不为空
        if (CollectionUtil.isNotEmpty(services)) {
            // 分类装数据
            for (ServicesBean servicesBean : services) {
                String serviceType = servicesBean.getType();
                if (!TextUtils.isEmpty(serviceType)) { // 如果
                    int type = InstanceServiceUtil.getServiceType(serviceType);
                    switch (type) { // 开关
                        case InstanceServiceUtil.SWITCH_SERVICE:
                            if (switchServices == null) {
                                switchServices = new ArrayList<>();
                            }
                            switchServices.add(servicesBean);
                            break;

                        case InstanceServiceUtil.TH_SERVICE:
                            if (thServices == null) {
                                thServices = new ArrayList<>();
                            }
                            thServices.add(servicesBean);
                            break;

                        case InstanceServiceUtil.LEAK_SERVICE:
                            if (leakServices == null) {
                                leakServices = new ArrayList<>();
                            }
                            leakServices.add(servicesBean);
                            break;

                        case InstanceServiceUtil.WS_SERVICE:
                            if (wdServices == null) {
                                wdServices = new ArrayList<>();
                            }
                            wdServices.add(servicesBean);
                            break;

                        case InstanceServiceUtil.MS_SERVICE:
                            if (msServices == null) {
                                msServices = new ArrayList<>();
                            }
                            msServices.add(servicesBean);
                            break;

                        case InstanceServiceUtil.CURTAIN_SERVICE:
                            if (curtainServices == null) {
                                curtainServices = new ArrayList<>();
                            }
                            curtainServices.add(servicesBean);
                            break;
                    }
                }
            }
        }
    }

    /**
     * 获取开关服务列表
     *
     * @return
     */
    public List<ServicesBean> getSwitchService(TextView tvSituation, ImageView ivSwitch, DeviceMultipleBean item) {
        if (CollectionUtil.isNotEmpty(switchServices)) {
            List<ServicesBean> switchServiceList = new ArrayList<>();
            for (ServicesBean servicesBean : switchServices) {  // 遍历service
                String type = servicesBean.getType();
                if (!TextUtils.isEmpty(type) && type.equals(WSConstant.SWITCH)) { // type不为空且是switch类型
                    switchServiceList.add(servicesBean);
                }
            }
            if (CollectionUtil.isNotEmpty(switchServiceList) && switchServiceList.size() > 1) {  // 开关类型大于1
                ivSwitch.setVisibility(View.GONE);
                tvSituation.setVisibility(item.isOnline() ? View.VISIBLE : View.GONE);
                tvSituation.setText(getMulSwitchStatusStr(switchServiceList));
                tvSituation.setTextColor(UiUtil.getColor(R.color.color_94A5BE));
            } else {  // 开关类型小于等于1
                handleSwitchService(ivSwitch, getServiceBean(switchServices, InstanceServiceUtil.SWITCH_SERVICE), item);
            }
        }
        return switchServices;
    }

    /**
     * 获取温湿度感器服务列表
     *
     * @return
     */
    public List<ServicesBean> getTHService(TextView tvSituation, DeviceMultipleBean item) {
        if (CollectionUtil.isNotEmpty(thServices)) {
            tvSituation.setText(getTHStatusStr(thServices));
            tvSituation.setTextColor(UiUtil.getColor(R.color.color_94a5be));
            tvSituation.setVisibility(item.isOnline() ? View.VISIBLE : View.GONE);
        }
        return thServices;
    }

    /**
     * 获取水浸传感器服务列表
     *
     * @return
     */
    public List<ServicesBean> getLeakService(TextView tvSituation, DeviceMultipleBean item) {
        if (CollectionUtil.isNotEmpty(leakServices)) {
            handleLeakService(tvSituation, getServiceBean(leakServices, InstanceServiceUtil.LEAK_SERVICE), item);
            tvSituation.setVisibility(item.isOnline() ? View.VISIBLE : View.GONE);
        }
        return leakServices;
    }

    /**
     * 获取门窗传感器服务列表
     *
     * @return
     */
    public List<ServicesBean> getWDService(TextView tvSituation, DeviceMultipleBean item) {
        if (CollectionUtil.isNotEmpty(wdServices)) {
            handleWDService(tvSituation, getServiceBean(wdServices, InstanceServiceUtil.WS_SERVICE), item);
        }
        return wdServices;
    }

    /**
     * 获取人体传感器服务列表
     *
     * @return
     */
    public List<ServicesBean> getMSService(TextView tvSituation, DeviceMultipleBean item) {
        if (CollectionUtil.isNotEmpty(msServices)) {
            handleMSService(tvSituation, getServiceBean(msServices, InstanceServiceUtil.MS_SERVICE), item);
        }
        return msServices;
    }

    /**
     * 获取窗帘传感器服务列表
     *
     * @return
     */
    public List<ServicesBean> getCurtainService() {
        return curtainServices;
    }

    /*************************** 获取服务 *******************************/
    public ServicesBean getServiceBean(List<ServicesBean> services, int serviceType) {
        ServicesBean servicesBean = null;
        switch (serviceType) {
            case InstanceServiceUtil.SWITCH_SERVICE:
                servicesBean = getSwitchServiceBean(services);
                break;

            case InstanceServiceUtil.LEAK_SERVICE:
                servicesBean = getLeakServiceBean(services);
                break;

            case InstanceServiceUtil.WS_SERVICE:
                servicesBean = getWDServiceBean(services);
                break;

            case InstanceServiceUtil.MS_SERVICE:
                servicesBean = getMSServiceBean(services);
                break;

            case InstanceServiceUtil.CURTAIN_SERVICE:
                servicesBean = getCurtainServiceBean(services);
                break;
        }
        return servicesBean;
    }


    /**
     * 获取开关服务
     *
     * @param services
     * @return
     */
    public ServicesBean getSwitchServiceBean(List<ServicesBean> services) {
        if (CollectionUtil.isNotEmpty(services))
            for (ServicesBean servicesBean : services) {
                String type = servicesBean.getType();
                if (!TextUtils.isEmpty(type) && InstanceServiceUtil.isSwitch(type)) { // type不为空且是switch类型
                    return servicesBean;
                }
            }
        return new ServicesBean();
    }

    /**
     * 获取水浸服务
     *
     * @param services
     * @return
     */
    public ServicesBean getLeakServiceBean(List<ServicesBean> services) {
        if (CollectionUtil.isNotEmpty(services))
            for (ServicesBean servicesBean : services) {
                String type = servicesBean.getType();
                if (!TextUtils.isEmpty(type) && type.equals(WSConstant.WATER_LEAK_SENSOR)) {
                    return servicesBean;
                }
            }
        return new ServicesBean();
    }

    /**
     * 获取门窗服务
     *
     * @param services
     * @return
     */
    public ServicesBean getWDServiceBean(List<ServicesBean> services) {
        if (CollectionUtil.isNotEmpty(services))
            for (ServicesBean servicesBean : services) {
                String type = servicesBean.getType();
                if (!TextUtils.isEmpty(type) && type.equals(WSConstant.WINDOW_DOOR_SENSOR)) {
                    return servicesBean;
                }
            }
        return new ServicesBean();
    }

    /**
     * 获取人体传感器服务
     *
     * @param services
     * @return
     */
    public ServicesBean getMSServiceBean(List<ServicesBean> services) {
        if (CollectionUtil.isNotEmpty(services))
            for (ServicesBean servicesBean : services) {
                String type = servicesBean.getType();
                if (!TextUtils.isEmpty(type) && type.equals(WSConstant.MOTION_SENSOR)) {
                    return servicesBean;
                }
            }
        return new ServicesBean();
    }

    /**
     * 获取窗帘传感器服务
     *
     * @param services
     * @return
     */
    public ServicesBean getCurtainServiceBean(List<ServicesBean> services) {
        if (CollectionUtil.isNotEmpty(services))
            for (ServicesBean servicesBean : services) {
                String type = servicesBean.getType();
                if (!TextUtils.isEmpty(type) && type.equals(WSConstant.CURTAIN)) {
                    return servicesBean;
                }
            }
        return new ServicesBean();
    }


    /*************************** 处理服务属性 *******************************/

    /**
     * 处理开关属性
     *
     * @param ivSwitch
     * @param servicesBean
     * @param item
     */
    public void handleSwitchService(ImageView ivSwitch, ServicesBean servicesBean, DeviceMultipleBean item) {
        ivSwitch.setVisibility(item.isOnline() ? View.VISIBLE : View.GONE);
        List<AttributesBean> attributes = servicesBean.getAttributes();
        if (CollectionUtil.isNotEmpty(attributes)) {
            for (AttributesBean attributesBean : attributes) {
                if (attributesBean != null) {
                    String attrType = attributesBean.getType();
                    if (!TextUtils.isEmpty(attrType) && attrType.equals(WSConstant.ATTR_ON_OFF)) {
                        item.setAttributes(attributesBean);
                        Object val = attributesBean.getVal();
                        if (val != null) {
                            String valStr = "";
                            double valDou = 0;
                            if (val instanceof String) {
                                valStr = val.toString();
                            } else if (val instanceof Double) {
                                valDou = (double) val;
                            }
                            ivSwitch.setSelected((!TextUtils.isEmpty(valStr) && valStr.equals(Constant.ON)) || valDou == Constant.DOU_ON);
                        }
                        ivSwitch.setAlpha((attributesBean.getPermission() != null && attributesBean.getPermission() > 0) ? 1.0f : 0.5f);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 获取多个开关状态
     *
     * @param switchServices
     * @return
     */
    public String getMulSwitchStatusStr(List<ServicesBean> switchServices) {
        String switchDesc = "";
        for (ServicesBean switchService : switchServices) { // 遍历多个开关类型列表
            List<AttributesBean> attributes = switchService.getAttributes();
            if (CollectionUtil.isNotEmpty(attributes)) { // 属性不为空
                for (AttributesBean attributesBean : attributes) { // 遍历属性
                    String attrType = attributesBean.getType();
                    if (!TextUtils.isEmpty(attrType) && attrType.equalsIgnoreCase(WSConstant.ATTR_ON_OFF)) {
                        String val = attributesBean.getVal().toString();
                        String valStr = val.equals(Constant.PowerType.TYPE_ON) ? UiUtil.getString(R.string.home_switch_open_vertical_bar)
                                : UiUtil.getString(R.string.home_switch_close_vertical_bar);
                        switchDesc = switchDesc + valStr + " | ";

                    }
                }
            }
        }
        if (switchDesc.endsWith(" | "))
            switchDesc = switchDesc.substring(0, switchDesc.length() - 3);
        return switchDesc;
    }

    /**
     * 获取温湿度值
     *
     * @param thServices
     * @return
     */
    public String getTHStatusStr(List<ServicesBean> thServices) {
        String temperature = "0℃";
        String humidity = "0%";
        for (ServicesBean thService : thServices) {
            List<AttributesBean> attributes = thService.getAttributes();
            if (CollectionUtil.isNotEmpty(attributes)) { // 属性不为空
                for (AttributesBean attributesBean : attributes) { // 遍历属性
                    String attrType = attributesBean.getType();
                    if (!TextUtils.isEmpty(attrType)) {
                        Object val = attributesBean.getVal();
                        if (attrType.equalsIgnoreCase(WSConstant.ATTR_TEMPERATURE)) {
                            temperature = StringUtil.getTemperatureString(val);
                            break;
                        } else if (attrType.equalsIgnoreCase(WSConstant.ATTR_HUMIDITY)) {
                            humidity = StringUtil.getHumidityString(val);
                            break;
                        }
                    }
                }
            }
        }
        return temperature + " | " + humidity;
    }

    /**
     * 处理水浸传感器属性
     * @param tvSituation
     * @param servicesBean
     * @param item
     */
    public void handleLeakService(TextView tvSituation, ServicesBean servicesBean, DeviceMultipleBean item) {
        List<AttributesBean> attributes = servicesBean.getAttributes();
        if (CollectionUtil.isNotEmpty(attributes)) {
            for (AttributesBean attributesBean : attributes) {
                String attrType = attributesBean.getType();
                Object val = attributesBean.getVal();
                if (val == null) continue;
                if (attrType.equals(WSConstant.ATTR_LEAK_DETECTED)) {  // 水浸传感器
                    double leakDetected = (double) attributesBean.getVal();
                    tvSituation.setVisibility(item.isOnline() ? View.VISIBLE : View.GONE);
                    if (leakDetected == 0) {
                        tvSituation.setText(R.string.home_no_water);
                        tvSituation.setTextColor(UiUtil.getColor(R.color.color_94a5be));
                    } else {
                        tvSituation.setText(R.string.home_water);
                        tvSituation.setTextColor(UiUtil.getColor(R.color.color_ff7f7f));
                    }
                    break;
                }
            }
        }
    }

    /**
     * 处理门窗传感器属性
     * @param tvSituation
     * @param servicesBean
     * @param item
     */
    public void handleWDService(TextView tvSituation, ServicesBean servicesBean, DeviceMultipleBean item) {
        List<AttributesBean> attributes = servicesBean.getAttributes();
        if (CollectionUtil.isNotEmpty(attributes)) {
            for (AttributesBean attributesBean : attributes) {
                String attrType = attributesBean.getType();
                Object val = attributesBean.getVal();
                if (val == null) continue;
                if (attrType.equals(WSConstant.ATTR_WIDOW_DOOR_CLOSE)) { // 门窗传感器
                    tvSituation.setVisibility(item.isOnline() ? View.VISIBLE : View.GONE);
                    tvSituation.setTextColor(UiUtil.getColor(R.color.color_94a5be));
                    double windowDoorClose = (double) val;
                    if (windowDoorClose == 0) {
                        tvSituation.setText(R.string.home_door_close);
                    } else {
                        tvSituation.setText(R.string.home_door_open);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 处理人体传感器属性
     * @param tvSituation
     * @param servicesBean
     * @param item
     */
    public void handleMSService(TextView tvSituation, ServicesBean servicesBean, DeviceMultipleBean item) {
        List<AttributesBean> attributes = servicesBean.getAttributes();
        if (CollectionUtil.isNotEmpty(attributes)) {
            for (AttributesBean attributesBean : attributes) {
                String attrType = attributesBean.getType();
                Object val = attributesBean.getVal();
                if (val == null) continue;
                if (attrType.equals(WSConstant.ATTR_DETECTED)) { // 人体传感器
                    // val有时是true有时是1，根据类型赋值
                    boolean detected = false;
                    double detectedDou = 0;
                    if (val instanceof Boolean) {
                        detected = (boolean) val;
                    } else if (val instanceof Double) {
                        detectedDou = (double) val;
                    }
                    tvSituation.setText(R.string.home_person_move);
                    tvSituation.setTextColor(UiUtil.getColor(R.color.color_ff7f7f));
                    tvSituation.setVisibility(item.isOnline() && (detected || detectedDou == 1) ? View.VISIBLE : View.GONE);
                    break;
                }
            }
        }
    }
}
