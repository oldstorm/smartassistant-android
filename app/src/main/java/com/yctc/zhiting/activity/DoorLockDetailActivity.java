package com.yctc.zhiting.activity;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DoorLockDetailContract;
import com.yctc.zhiting.activity.presenter.DoorLockDetailPresenter;
import com.yctc.zhiting.adapter.DLEventAdapter;
import com.yctc.zhiting.adapter.DLLogAdapter;
import com.yctc.zhiting.dialog.DLFunctionDialog;
import com.yctc.zhiting.entity.DLFunctionBean;
import com.yctc.zhiting.widget.DLStatusView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *  门锁详情
 */
public class DoorLockDetailActivity extends MVPBaseActivity<DoorLockDetailContract.View, DoorLockDetailPresenter> implements DoorLockDetailContract.View {

    @BindView(R.id.rvEvent)
    RecyclerView rvEvent;
    @BindView(R.id.rvLog)
    RecyclerView rvLog;
    @BindView(R.id.tvExpand)
    TextView tvExpand;
    @BindView(R.id.ivExpand)
    ImageView ivExpand;
    @BindView(R.id.dlvStatus)
    DLStatusView dlvStatus;

    private DLEventAdapter mDLEventAdapter;  // 事件列表适配器
    private DLLogAdapter mDLLogAdapter;  // 日志列表适配器

    private boolean mIsExpand; // 是否展开
    private DLFunctionDialog mDLFunctionDialog; // 更多功能
    private boolean isConnectBluetooth;
    private boolean isOpen;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_door_lock_detail;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void initUI() {
        super.initUI();
        initRvEvent();
        initRvLog();
    }

    /**
     * 事件
     */
    private void initRvEvent() {
        mDLEventAdapter = new DLEventAdapter();
        rvEvent.setLayoutManager(new LinearLayoutManager(this));
        rvEvent.setAdapter(mDLEventAdapter);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            data.add("");
        }
        mDLEventAdapter.setNewData(data);
    }

    /**
     * 日志
     */
    private void initRvLog() {
        mDLLogAdapter = new DLLogAdapter(false);
        rvLog.setLayoutManager(new LinearLayoutManager(this));
        rvLog.setAdapter(mDLLogAdapter);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            data.add("");
        }
        mDLLogAdapter.setNewData(data);
    }

    @OnClick({R.id.ivBack, R.id.llExpand, R.id.tvLook, R.id.slSetting, R.id.tvBluetooth, R.id.tvProStatus})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.ivBack:  // 返回
                finish();
                break;

            case R.id.llExpand:  // 更多
                expand();
                break;

            case R.id.tvLook:  // 查看日志
                switchToActivity(DLLogActivity.class);
                break;

            case R.id.slSetting:  // 设置
                showFuncDialog();
                break;

            case R.id.tvBluetooth:  // 蓝牙
                isConnectBluetooth = !isConnectBluetooth;
                dlvStatus.showArcAndDot(isConnectBluetooth);
                break;

            case R.id.tvProStatus:
                isOpen = !isOpen;
                dlvStatus.setStatusDrawable(isOpen);
                break;
        }
    }

    private void showFuncDialog() {
        if (mDLFunctionDialog == null) {
            mDLFunctionDialog = DLFunctionDialog.getInstance();
            mDLFunctionDialog.setFuncListener(new DLFunctionDialog.OnFuncListener() {
                @Override
                public void onFunc(DLFunctionBean dlFunctionBean) {
                    switch (dlFunctionBean) {
                        case DISPOSAL_PWD:
                            switchToActivity(DisposablePwdActivity.class);
                            break;

                        case USER_MANAGE:
                            switchToActivity(UserManageActivity.class);
                            break;

                        case DL_SETTING:
                            switchToActivity(DoorSettingActivity.class);
                            break;
                    }
                    mDLFunctionDialog.dismiss();
                }
            });
        }
        if (mDLFunctionDialog!=null && !mDLFunctionDialog.isShowing()) {
            mDLFunctionDialog.show(this);
        }
    }

    /**
     * 展开
     */
    private void expand() {
        mIsExpand = !mIsExpand;
        tvExpand.setText(mIsExpand ? UiUtil.getString(R.string.home_shrink) :  UiUtil.getString(R.string.home_expand));
        ivExpand.setImageResource(mIsExpand ? R.drawable.icon_gray_double_up_arrow : R.drawable.icon_gray_double_down_arrow);
        List<String> data = new ArrayList<>();
        int size = mIsExpand ? 10 : 3;
        for (int i = 0; i < size; i++) {
            data.add("");
        }
        mDLEventAdapter.setNewData(data);
    }
}