package com.yctc.zhiting.adapter;

import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.home.DeviceTypeBean;
import com.yctc.zhiting.entity.home.DeviceTypeDeviceBean;

public class DeviceSecondCategoryAdapter extends BaseQuickAdapter<DeviceTypeBean, BaseViewHolder> {

    public DeviceSecondCategoryAdapter() {
        super(R.layout.item_device_second_category);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceTypeBean item) {
        String name = item.getName();
        helper.setText(R.id.tvName, !TextUtils.isEmpty(name) ? name : UiUtil.getString(R.string.other));
        RecyclerView recyclerView = helper.getView(R.id.rvDeviceInCategory);
        AddDeviceInCategoryAdapter addDeviceInCategoryAdapter = new AddDeviceInCategoryAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        recyclerView.setAdapter(addDeviceInCategoryAdapter);
        addDeviceInCategoryAdapter.setNewData(item.getDevices());
        String type = item.getType();
        addDeviceInCategoryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (clickDeviceListener!=null){
                    clickDeviceListener.onClickDevice(type, addDeviceInCategoryAdapter.getItem(position));
                }
            }
        });

    }

    private OnClickDeviceListener clickDeviceListener;

    public OnClickDeviceListener getClickDeviceListener() {
        return clickDeviceListener;
    }

    public void setClickDeviceListener(OnClickDeviceListener clickDeviceListener) {
        this.clickDeviceListener = clickDeviceListener;
    }

    public interface OnClickDeviceListener{
        void onClickDevice(String type, DeviceTypeDeviceBean item);
    }
}
