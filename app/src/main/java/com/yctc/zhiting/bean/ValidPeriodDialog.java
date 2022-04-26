package com.yctc.zhiting.bean;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;
import com.yctc.zhiting.widget.PickerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ValidPeriodDialog extends CommonBaseDialog implements PickerView.OnSelectListener{
    @BindView(R.id.pvBeginHour)
    PickerView pvBeginHour;
    @BindView(R.id.pvBeginMinute)
    PickerView pvBeginMinute;
    @BindView(R.id.pvEndHour)
    PickerView pvEndHour;
    @BindView(R.id.pvEndMinute)
    PickerView pvEndMinute;

    private List<String> beginHourData = new ArrayList<>();
    private List<String> beginMinuteData = new ArrayList<>();
    private List<String> endHourData = new ArrayList<>();
    private List<String> endMinuteData = new ArrayList<>();

    private String mBeginHour = "00";
    private String mBeginMinute = "00";
    private String mEndHour = "00";
    private String mEndMinute = "00";

    private String mBeginTime = "00:00";
    private String mEndTime = "00:00";

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {
        String[] bt = mBeginTime.split(":");
        /*********** 开始时  ***********/
        pvBeginHour.setOnSelectListener(this::onSelect);
        beginHourData.clear();
        for (int i = 0; i <= 23; i++) {
            beginHourData.add(i<10 ? "0"+i : String.valueOf(i));
        }
        pvBeginHour.setDataList(beginHourData);
        pvBeginHour.setSelected(pvBeginHour.getItemIndex(bt[0]));

        /*********** 开始分  ***********/
        pvBeginMinute.setOnSelectListener(this::onSelect);
        beginMinuteData.clear();
        for (int i = 0; i <= 23; i++) {
            beginMinuteData.add(i<10 ? "0"+i : String.valueOf(i));
        }
        pvBeginMinute.setDataList(beginMinuteData);
        pvBeginMinute.setSelected(pvBeginMinute.getItemIndex(bt[0]));

        String[] et = mEndTime.split(":");
        /*********** 结束时  ***********/
        pvEndHour.setOnSelectListener(this::onSelect);
        endHourData.clear();
        for (int i = 0; i <= 23; i++) {
            endHourData.add(i<10 ? "0"+i : String.valueOf(i));
        }
        pvEndHour.setDataList(endHourData);
        pvEndHour.setSelected(pvEndHour.getItemIndex(et[0]));

        /*********** 结束分  ***********/
        pvEndMinute.setOnSelectListener(this::onSelect);
        endMinuteData.clear();
        for (int i = 0; i <= 23; i++) {
            endMinuteData.add(i<10 ? "0"+i : String.valueOf(i));
        }
        pvEndMinute.setDataList(endMinuteData);
        pvEndMinute.setSelected(pvEndMinute.getItemIndex(et[1]));
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_valid_period;
    }

    @Override
    protected int obtainWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int obtainHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.BOTTOM;
    }

    @OnClick({R.id.ivClose, R.id.tvConfirm})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.ivClose:
                dismiss();
                break;

            case R.id.tvConfirm:
                if (mConfirmListener != null) {
                    mBeginTime = mBeginHour + ":" + mBeginMinute;
                    mEndTime = mEndHour + ":" + mEndMinute;
                    mConfirmListener.onConfirm(mBeginTime, mEndTime);
                }
                break;
        }
    }

    @Override
    public void onSelect(View view, String selected, int selectedIndex) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.pvBeginHour:  // 开始时
                mBeginHour = selected;
                break;

            case R.id.pvBeginMinute:  // 开始分
                mBeginMinute = selected;
                break;

            case R.id.pvEndHour:  // 结束时
                mEndHour = selected;
                break;

            case R.id.pvEndMinute:  // 结束分
                mEndMinute = selected;
                break;
        }
    }

    private OnConfirmListener mConfirmListener;

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.mConfirmListener = confirmListener;
    }

    public interface OnConfirmListener {
        void onConfirm(String beginTime, String endTime);
    }
}
