package com.yctc.zhiting.event;

import com.yctc.zhiting.entity.mine.HomeCompanyBean;

public class HomeEvent {

    private boolean add;
    private HomeCompanyBean homeCompanyBean;


    public HomeEvent(HomeCompanyBean homeCompanyBean) {
        this.homeCompanyBean = homeCompanyBean;
    }

    public HomeEvent(boolean add, HomeCompanyBean homeCompanyBean) {
        this.add = add;
        this.homeCompanyBean = homeCompanyBean;
    }

    public HomeCompanyBean getHomeCompanyBean() {
        return homeCompanyBean;
    }

    public void setHomeCompanyBean(HomeCompanyBean homeCompanyBean) {
        this.homeCompanyBean = homeCompanyBean;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }
}
