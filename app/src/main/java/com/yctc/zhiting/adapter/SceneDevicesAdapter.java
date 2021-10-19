package com.yctc.zhiting.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.scene.SceneDevicesBean;

import java.util.List;

/**
 * 场景可控制设备列表
 */
public class SceneDevicesAdapter extends BaseQuickAdapter<SceneDevicesBean, BaseViewHolder> {

    public SceneDevicesAdapter() {
        super(R.layout.item_scene_devices);
    }

    @Override
    protected void convert(BaseViewHolder helper, SceneDevicesBean item) {
        TextView tvLocation = helper.getView(R.id.tvLocation);
        tvLocation.setVisibility(TextUtils.isEmpty(item.getLocation()) ? View.GONE : View.VISIBLE);
        helper.setText(R.id.tvLocation, item.getLocation());
        RecyclerView rvDevice = helper.getView(R.id.rvDevice);
        rvDevice.setLayoutManager(new LinearLayoutManager(mContext));
        SceneDevicesSonAdapter sceneDevicesSonAdapter = new SceneDevicesSonAdapter();
        rvDevice.setAdapter(sceneDevicesSonAdapter);
        sceneDevicesSonAdapter.setNewData(item.getDeviceMultipleBean());
        sceneDevicesSonAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DeviceMultipleBean deviceMultipleBean = sceneDevicesSonAdapter.getItem(position);
                if (deviceListener!=null){
                    deviceListener.onDevice(deviceMultipleBean, position);
                }
            }
        });
    }

    private OnDeviceListener deviceListener;

    public OnDeviceListener getDeviceListener() {
        return deviceListener;
    }

    public void setDeviceListener(OnDeviceListener deviceListener) {
        this.deviceListener = deviceListener;
    }

    public interface OnDeviceListener{
        void onDevice(DeviceMultipleBean deviceMultipleBean, int position);
    }
}
