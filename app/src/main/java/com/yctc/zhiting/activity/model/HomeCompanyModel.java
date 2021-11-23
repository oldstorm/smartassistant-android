package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.HomeCompanyContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.PermissionBean;

/**
 * 家庭/公司
 */
public class HomeCompanyModel implements HomeCompanyContract.Model {

    @Override
    public void getHomeCompanyList(RequestDataCallback<HomeCompanyListBean> callback) {
        HTTPCaller.getInstance().get(HomeCompanyListBean.class, HttpUrlConfig.getSCAreasUrl()+ Constant.ONLY_SC,callback);
    }

    @Override
    public void getPermissions(int id, RequestDataCallback<PermissionBean> callback) {
        HTTPCaller.getInstance().get(PermissionBean.class, HttpUrlConfig.getPermissions(id), callback);
    }

    @Override
    public void addHomeCompany(String body, RequestDataCallback<IdBean> callback) {
        HTTPCaller.getInstance().post(IdBean.class, HttpUrlConfig.getSCAreasUrl()+ Constant.ONLY_SC, body, callback);
    }
}
