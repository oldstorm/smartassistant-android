package com.yctc.zhiting.popup_window;


import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.BaseActivity;
import com.app.main.framework.popupwindow.BasePopupWindow;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.CommonDeviceSetActivity;
import com.yctc.zhiting.activity.DepartmentListActivity;
import com.yctc.zhiting.activity.DevicesSortActivity;
import com.yctc.zhiting.activity.RoomAreaActivity;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.AreaCodeBean;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.utils.IntentConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Author by Ouyangle, Date on 2022/4/2.
 * PS: Not easy to write code, please indicate.
 */
public class HomeSetPopupWindow extends BasePopupWindow {
    private TextView tvHomeVisible;
    private boolean isVisibleOffLineDevices = true;
    private TextView tvHomeManage;
    private TextView tvHomeSort;
    private TextView tvHomeDrivesSet;

    public HomeSetPopupWindow(BaseActivity context) {
        super(context);
        initView();
    }

    public HomeSetPopupWindow(BaseActivity context, boolean isVisible) {
        super(context);
        this.isVisibleOffLineDevices = isVisible;
        initView();
    }

    private void initView() {
        setWidth(UiUtil.dip2px(175));
        tvHomeManage = view.findViewById(R.id.tvHomeManage);
        tvHomeSort = view.findViewById(R.id.tvHomeSort);
        tvHomeVisible = view.findViewById(R.id.tvHomeVisible);
        tvHomeDrivesSet = view.findViewById(R.id.tvHomeDrivesSet);

        tvHomeManage.setOnClickListener(this);
        tvHomeSort.setOnClickListener(this);
        tvHomeVisible.setOnClickListener(this);
        tvHomeDrivesSet.setOnClickListener(this);

        tvHomeVisible.setSelected(isVisibleOffLineDevices);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.popup_home_setup;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvHomeManage:
                if(listener != null){
                    listener.onClick(tvHomeManage);
                }
                break;
            case R.id.tvHomeSort:
                if(listener != null){
                    listener.onClick(tvHomeSort);
                }
                break;
            case R.id.tvHomeVisible:
                tvHomeVisible.setSelected(!tvHomeVisible.isSelected());
                tvHomeVisible.setText(tvHomeVisible.isSelected() ?
                        mContext.getString(R.string.home_setup_hint3_1)
                        :
                        mContext.getString(R.string.home_setup_hint3_2) );
                if(listener != null){
                    listener.onClick(tvHomeVisible);
                }
                break;
            case R.id.tvHomeDrivesSet:
                Intent intent3 = new Intent(mContext, CommonDeviceSetActivity.class);
                mContext.startActivity(intent3);
                break;
        }
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }

    public interface OnItemClickListener {
        void onClick(View view);
    }
}
