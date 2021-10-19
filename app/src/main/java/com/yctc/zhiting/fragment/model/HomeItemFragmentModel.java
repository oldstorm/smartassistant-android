package com.yctc.zhiting.fragment.model;

import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.home.BrandBean;
import com.yctc.zhiting.entity.home.DeviceBean;
import com.yctc.zhiting.entity.home.RequestAddDeviceBean;
import com.yctc.zhiting.fragment.contract.HomeItemFragmentContract;

public class HomeItemFragmentModel implements HomeItemFragmentContract.Model {

    @Override
    public void getBrandList(RequestDataCallback<BrandBean> callback) {
        //HTTPCaller.getInstance().get(BrandBean.class, HttpUrlConfig.getBrandListUrl(),callback);
    }

    @Override
    public void postAddDevice(RequestAddDeviceBean bean, RequestDataCallback<DeviceBean> callback) {
//        String url = "http://192.168.0.110:8088/devices";
//        HTTPCaller.getInstance().post(DeviceBean.class,url,bean.toString(),callback);
//        HTTPCaller.getInstance().post(DeviceBean.class, HttpUrlConfig.getAddDeviceUrl(),new Gson().toJson(bean),callback);
    }
}
