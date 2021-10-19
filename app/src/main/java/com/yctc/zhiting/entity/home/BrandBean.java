package com.yctc.zhiting.entity.home;

import com.app.main.framework.entity.BaseEntity;

import java.util.List;

public class BrandBean extends BaseEntity {
    private List<BrandBeanItem> brands;

    public List<BrandBeanItem> getBrands() {
        return brands;
    }

    public void setBrands(List<BrandBeanItem> brands) {
        this.brands = brands;
    }
}
