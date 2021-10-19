package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AddHCContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.home.SynPost;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.LocationsBean;

/**
 * 添加家庭/公司等区域
 */
public class AddHCModel implements AddHCContract.Model {
    @Override
    public void getDefaultRoom(RequestDataCallback<LocationsBean> callback) {
        HTTPCaller.getInstance().get(LocationsBean.class, HttpUrlConfig.getLocation_tmpl(), callback);
    }

    @Override
    public void postCreateSCHome(SynPost.AreaBean areaBean, RequestDataCallback<IdBean> callback) {
        HTTPCaller.getInstance().post(IdBean.class, HttpUrlConfig.getScAreas(), areaBean, callback);
    }
}
