package com.yctc.zhiting.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.BrandDetailContract;
import com.yctc.zhiting.activity.contract.DevicesSortContract;
import com.yctc.zhiting.activity.presenter.BrandDetailPresenter;
import com.yctc.zhiting.activity.presenter.DevicesSortPresenter;
import com.yctc.zhiting.adapter.DevicesSortAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.event.DeviceDataEvent;
import com.yctc.zhiting.fragment.HomeFragment;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.MyItemTouchHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author by Ouyangle, Date on 2022/4/2.
 * PS: Not easy to write code, please indicate.
 */
public class DevicesSortActivity extends MVPBaseActivity<DevicesSortContract.View, DevicesSortPresenter> implements  DevicesSortContract.View {
    @BindView(R.id.rcDevices)
    RecyclerView rcDevices;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.layout_null)
    View viewNull;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;
    @BindView(R.id.ivEmpty)
    ImageView ivEmpty;

    private ArrayList<DeviceMultipleBean> dataList = new ArrayList<>();
    private DevicesSortAdapter adapter;

    @Override
    protected void initUI() {
        super.initUI();
        initRv();
    }

    private void initRv() {
        rcDevices.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DevicesSortAdapter();
        rcDevices.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MyItemTouchHelper(adapter, false));
        itemTouchHelper.attachToRecyclerView(rcDevices);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_devices_sort;
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(DeviceDataEvent event) {
        if (CollectionUtil.isNotEmpty(event.getDevices())) {
            dataList.clear();
            dataList.addAll(event.getDevices());
            adapter.setNewData(dataList);
        } else {
            setNullView(true);
        }
    }

    private void setNullView(boolean visible) {
        viewNull.setVisibility(visible ? View.VISIBLE : View.GONE);
        rcDevices.setVisibility(visible ? View.GONE : View.VISIBLE);
        tvEmpty.setText(getResources().getString(R.string.home_no_device));
        ivEmpty.setImageResource(R.drawable.icon_device_empty);
    }

    @OnClick({R.id.ivBack})
    void onClick(View view){
        int viewId = view.getId();
        switch (viewId){
            case R.id.ivBack:

                break;
        }
    }
}
