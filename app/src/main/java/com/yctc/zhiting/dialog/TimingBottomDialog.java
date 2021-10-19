package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;
import com.yctc.zhiting.widget.PickerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class TimingBottomDialog extends CommonBaseDialog implements PickerView.OnSelectListener {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.pvHour)
    PickerView pvHour;
    @BindView(R.id.pvMinute)
    PickerView pvMinute;
    @BindView(R.id.pvSecond)
    PickerView pvSecond;

    private int selectedIndex;


    private List<String> data = new ArrayList<>();
    private List<String> minuteData = new ArrayList<>();
    private List<String> secondsData = new ArrayList<>();

    private int hour = 0;
    private int minute = 0;
    private int seconds = 0;
    private String hourStr = "00";
    private String minuteStr = "00";
    private String secondsStr = "00";

    private String selectTime = "00:00:00";  // 选中时间

    public TimingBottomDialog() {
    }

    public TimingBottomDialog(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }



    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_timing;
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

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {
        String[] st = selectTime.split(":");
        // 时
        pvHour.setLabel(getResources().getString(R.string.scene_hour));
        pvHour.setOnSelectListener(this::onSelect);
        data.clear();
        for (int i = 0; i <= 23; i++) {
            data.add(i<10 ? "0"+i : String.valueOf(i));
        }
        pvHour.setDataList(data);
        pvHour.setSelected(pvHour.getItemIndex(st[0]));

        // 分
        minuteData.clear();
        pvMinute.setLabel(getResources().getString(R.string.scene_minute));
        pvMinute.setOnSelectListener(this::onSelect);
        for (int i = 0; i <= 59; i++) {
            minuteData.add(i<10 ? "0"+i : String.valueOf(i));
        }
        pvMinute.setDataList(minuteData);
        pvMinute.setSelected(pvMinute.getItemIndex(st[1]));

        // 秒
        secondsData.clear();
        pvSecond.setLabel(getResources().getString(R.string.scene_seconds));
        pvSecond.setOnSelectListener(this::onSelect);
        for (int i = 0; i <= 59; i++) {
            secondsData.add(i<10 ? "0"+i : String.valueOf(i));
        }
        pvSecond.setDataList(secondsData);
        pvSecond.setSelected(pvSecond.getItemIndex(st[2]));

    }

    @OnClick(R.id.ivClose)
    void onClickClose(){
        dismiss();
    }

    @OnClick(R.id.tvConfirm)
    void onClickConfirm(){
        if (timeSelectListener!=null){
            String timeStr = hourStr + ":" + minuteStr + ":" + secondsStr;
            timeSelectListener.onTimeSelect(hour, minute, seconds, timeStr);
        }
    }

    public void setSelectedItemIndex(int hour, int minute, int seconds){
        if (pvHour!=null)
        pvHour.setSelected(hour);
        if (pvMinute!=null)
        pvMinute.setSelected(minute);
        if (pvSecond!=null)
        pvSecond.setSelected(seconds);
    }

    @Override
    public void onSelect(View view, String selected, int selectedIndex) {
        switch (view.getId()){
            case R.id.pvHour:
                hour = Integer.parseInt(selected);
                hourStr = selected;
                break;

            case R.id.pvMinute:
                minute = Integer.parseInt(selected);
                minuteStr = selected;
                break;

            case R.id.pvSecond:
                seconds = Integer.parseInt(selected);
                secondsStr = selected;
                break;
        }
    }

    public String getSelectTime() {
        return selectTime;
    }

    public void setSelectTime(String selectTime) {
        this.selectTime = selectTime;
    }

    private OnTimeSelectListener timeSelectListener;

    public OnTimeSelectListener getTimeSelectListener() {
        return timeSelectListener;
    }

    public void setTimeSelectListener(OnTimeSelectListener timeSelectListener) {
        this.timeSelectListener = timeSelectListener;
    }

    public interface OnTimeSelectListener{
        void onTimeSelect(int hour, int minute, int seconds, String timeStr);
    }
}
