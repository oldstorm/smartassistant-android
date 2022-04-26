package com.yctc.zhiting.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.CommonDevicesContract;
import com.yctc.zhiting.activity.presenter.CommonDevicesPresenter;
import com.yctc.zhiting.adapter.CommonDeviceSetAdapter;
import com.yctc.zhiting.adapter.DevicesSortAdapter;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.MyItemTouchHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author by Ouyangle, Date on 2022/4/2.
 * PS: Not easy to write code, please indicate.
 */
public class CommonDeviceSetActivity extends MVPBaseActivity<CommonDevicesContract.View, CommonDevicesPresenter> implements CommonDevicesContract.View{
    @BindView(R.id.rvDevice1)
    RecyclerView rcDevices1;
    @BindView(R.id.rvDevice2)
    RecyclerView rcDevices2;
    @BindView(R.id.ivTop)
    ImageView ivTop;
    @BindView(R.id.ivBottom)
    ImageView ivBottom;
    @BindView(R.id.tvSave)
    TextView tvSave;

    private ArrayList<Object> dataListTop = new ArrayList<>();
    private CommonDeviceSetAdapter adapterTop;
    private ArrayList<Object> dataListBottom = new ArrayList<>();
    private CommonDeviceSetAdapter adapterBottom;

    private boolean isExpendTop = true;
    private boolean isExpendBottom = true;

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getString(R.string.home_setup_hint4));
        initRv();
    }

    private void initRv() {
        rcDevices1.setLayoutManager(new LinearLayoutManager(this));
        dataListTop.add(new Object());
        dataListTop.add(new Object());
        dataListTop.add(new Object());
        adapterTop = new CommonDeviceSetAdapter();
        adapterTop.setNewData(dataListTop);
        rcDevices1.setAdapter(adapterTop);

        rcDevices2.setLayoutManager(new LinearLayoutManager(this));
        dataListBottom.add(new Object());
        dataListBottom.add(new Object());
        dataListBottom.add(new Object());
        adapterBottom = new CommonDeviceSetAdapter();
        adapterBottom.setNewData(dataListBottom);
        rcDevices2.setAdapter(adapterBottom);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_common_device_settings;
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @OnClick({R.id.ivTop, R.id.ivBottom, R.id.tvSave})
    void onClick(View view){
        int viewId = view.getId();
        switch (viewId){
            case R.id.ivTop:
                isExpendTop = !isExpendTop;
                initTopViewState(isExpendTop);
                break;
            case R.id.ivBottom:
               isExpendBottom = !isExpendBottom;
               initBottomViewState(isExpendBottom);
                break;
            case R.id.tvSave:

                break;
        }
    }

    private void initTopViewState(boolean isExpend){
        if(isExpend){
           ivTop.setRotation(180);
        }else {
            ivTop.setRotation(0);
        }
        rcDevices1.setVisibility(isExpend ? View.VISIBLE : View.GONE);
    }

    private void initBottomViewState(boolean isExpend){
        if(isExpend){
            ivBottom.setRotation(180);
        }else {
            ivBottom.setRotation(0);
        }
        rcDevices2.setVisibility(isExpend ? View.VISIBLE : View.GONE);
    }

    @Override
    protected boolean isLoadTitleBar() {
        return true;
    }
}
