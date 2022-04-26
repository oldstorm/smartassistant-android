package com.yctc.zhiting.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.LocationBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择部门弹窗部门适配器
 */
public class SelectDepartmentAdapter extends BaseQuickAdapter<LocationBean, BaseViewHolder> {

    public SelectDepartmentAdapter() {
        super(R.layout.item_select_department);
    }

    @Override
    protected void convert(BaseViewHolder helper, LocationBean item) {
        ImageView ivSel = helper.getView(R.id.ivSel);
        ivSel.setSelected(item.isCheck());
        helper.setText(R.id.tvName, item.getName());
    }


    public List<LocationBean> getSelectedData(){
        List<LocationBean> data = new ArrayList<>();
        for (LocationBean locationBean : getData()){
            if (locationBean.isCheck()){
                data.add(locationBean);
            }
        }
        return data;
    }
}
