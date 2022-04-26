package com.yctc.zhiting.activity;


import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DLLogContract;
import com.yctc.zhiting.activity.presenter.DLLogPresenter;
import com.yctc.zhiting.adapter.DLLogParentAdapter;
import com.yctc.zhiting.popup_window.BTStatusPopupWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *  门锁日志
 */
public class DLLogActivity extends MVPBaseActivity<DLLogContract.View, DLLogPresenter> implements DLLogContract.View {

    @BindView(R.id.tvBluetooth)
    TextView tvBluetooth;
    @BindView(R.id.rvLogParent)
    RecyclerView rvLogParent;

    private DLLogParentAdapter mDLLogParentAdapter;
    private BTStatusPopupWindow mBTStatusPopupWindow;
    private boolean isConnected;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dl_log;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void initUI() {
        super.initUI();
        initRv();
    }

    private void initRv() {
        rvLogParent.setLayoutManager(new LinearLayoutManager(this));
        mDLLogParentAdapter = new DLLogParentAdapter();
        rvLogParent.setAdapter(mDLLogParentAdapter);
        List<String> data = new ArrayList<>();
        for (int i=0; i<2; i++) {
            data.add("");
        }
        mDLLogParentAdapter.setNewData(data);
    }

    @OnClick({R.id.ivBack, R.id.ivDate, R.id.tvEvent, R.id.tvBluetooth})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.ivBack:  // 返回
                onBackPressed();
                break;

            case R.id.ivDate:  // 日期

                break;

            case R.id.tvEvent:  // 事件

                break;

            case R.id.tvBluetooth: // 蓝牙
                isConnected = !isConnected;
                showBTStatusPopupWindow();
                break;
        }
    }

    private void showBTStatusPopupWindow() {
        if (mBTStatusPopupWindow == null) {
            mBTStatusPopupWindow = new BTStatusPopupWindow(this);
        }
        mBTStatusPopupWindow.setStatusText(isConnected ? UiUtil.getString(R.string.home_dl_bluetooth_connected) : UiUtil.getString(R.string.home_dl_bluetooth_disconnected));
        mBTStatusPopupWindow.showAsDown(tvBluetooth);
    }

}