package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AddHCContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.home.SynPost;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.LocationsBean;
import com.yctc.zhiting.request.AddHCRequest;

/**
 * 添加家庭/公司等区域
 */
public class AddCompanyModel implements AddHCContract.Model {
    @Override
    public void getDefaultRoom(RequestDataCallback<LocationsBean> callback) {
        HTTPCaller.getInstance().get(LocationsBean.class, HttpUrlConfig.getLocation_tmpl(), callback);
    }

    @Override
    public void postCreateSCHome(SynPost.AreaBean areaBean, RequestDataCallback<IdBean> callback) {
        HTTPCaller.getInstance().post(IdBean.class, HttpUrlConfig.getScAreas()+ Constant.ONLY_SC, areaBean, callback);
    }


    /**
     * 添加云端家庭
     * @param addHCRequest
     * @param callback
     */
    @Override
    public void addScHome(AddHCRequest addHCRequest, RequestDataCallback<IdBean> callback) {
        HTTPCaller.getInstance().post(IdBean.class, HttpUrlConfig.getScAreasNoHeader() + Constant.ONLY_SC, addHCRequest, callback);
    }
}
