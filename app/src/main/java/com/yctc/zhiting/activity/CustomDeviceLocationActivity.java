package com.yctc.zhiting.activity;


import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DeviceLocationContract;
import com.yctc.zhiting.activity.presenter.DeviceLocationPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author by Ouyangle, Date on 2022/4/6.
 * PS: Not easy to write code, please indicate.
 */
public class CustomDeviceLocationActivity extends MVPBaseActivity<DeviceLocationContract.View, DeviceLocationPresenter> implements DeviceLocationContract.View {
    @BindView(R.id.llConfirm)
    LinearLayout llConfirm;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom_device_location;
    }
    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @OnClick({R.id.llConfirm})
    void onClick(View view){
        int viewId = view.getId();
        switch (viewId){
            case R.id.llConfirm:

                break;
        }
    }
}
