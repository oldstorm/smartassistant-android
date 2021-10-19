package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;
import com.yctc.zhiting.widget.PickerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 时间段
 */
public class TimePeriodDialog extends CommonBaseDialog implements PickerView.OnSelectListener{


    @BindView(R.id.tvBegin)
    TextView tvBegin;
    @BindView(R.id.tvEnd)
    TextView tvEnd;

    @BindView(R.id.pvHour)
    PickerView pvHour;
    @BindView(R.id.pvMinute)
    PickerView pvMinute;
    @BindView(R.id.pvSecond)
    PickerView pvSecond;

    /**
     * 1.开始
     * 2.结束
     */
    private int type;

    private int beginHour;  // 开始 时
    private int beginMinute;  // 开始 分
    private int beginSeconds;  // 开始 秒

    private int endHour;  // 结束 时
    private int endMinute;  // 结束 分
    private int endSeconds;  // 结束 秒

    private List<String> data = new ArrayList<>();
    private List<String> minuteData = new ArrayList<>();
    private List<String> secondsData = new ArrayList<>();

    private int hour = 0;
    private int minute = 0;
    private int seconds = 0;
    private String hourStr = "00";
    private String minuteStr = "00";
    private String secondsStr = "00";

    private String beginHourStr = "00";  // 开始 时 字符串
    private String beginMinuteStr = "00";  // 开始 分 字符串
    private String beginSecondsStr = "00";  // 开始 秒 字符串

    private String endHourStr = "00";  // 结束 时 字符串
    private String endMinuteStr = "00";  // 结束 分 字符串
    private String endSecondsStr = "00";  // 结束 秒 字符串


    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_time_period;
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
        // 时
        pvHour.setLabel(getResources().getString(R.string.scene_hour));
        pvHour.setOnSelectListener(this::onSelect);
        data.clear();
        for (int i = 0; i <= 23; i++) {
            data.add(i<10 ? "0"+i : String.valueOf(i));
        }
        pvHour.setDataList(data);
        pvHour.setSelected(type == 1 ? beginHour : endHour);

        // 分
        minuteData.clear();
        pvMinute.setLabel(getResources().getString(R.string.scene_minute));
        pvMinute.setOnSelectListener(this::onSelect);
        for (int i = 0; i <= 59; i++) {
            minuteData.add(i<10 ? "0"+i : String.valueOf(i));
        }
        pvMinute.setDataList(minuteData);
        pvMinute.setSelected(type == 1 ? beginMinute : endMinute);

        // 秒
        secondsData.clear();
        pvSecond.setLabel(getResources().getString(R.string.scene_seconds));
        pvSecond.setOnSelectListener(this::onSelect);
        for (int i = 0; i <= 59; i++) {
            secondsData.add(i<10 ? "0"+i : String.valueOf(i));
        }
        pvSecond.setDataList(secondsData);
        pvSecond.setSelected(type == 1 ? beginSeconds : endSeconds);

        setSelected();
        if (type == 1) {
            setBeginTime();
        }else {
            setEndTime();
        }
        tvBegin.setText(beginHourStr + ":" + beginMinuteStr + ":" + beginSecondsStr);
        tvEnd.setText(endHourStr + ":" + endMinuteStr + ":" + endSecondsStr);
    }

    @Override
    public void onSelect(View view, String selected, int selectedIndex) {
        switch (view.getId()){
            case R.id.pvHour:
                hour = Integer.parseInt(selected);
                hourStr = selected;
                if (type == 1){
                    beginHourStr = selected;
                    beginHour = Integer.parseInt(selected);
                    tvBegin.setText(beginHourStr + ":" + beginMinuteStr + ":" + beginSecondsStr);
                }else {
                    endHourStr = selected;
                    endHour = Integer.parseInt(selected);
                    tvEnd.setText(endHourStr + ":" + endMinuteStr + ":" + endSecondsStr);
                }
                break;

            case R.id.pvMinute:
                minute = Integer.parseInt(selected);
                minuteStr = selected;
                if (type == 1){
                    beginMinute = Integer.parseInt(selected);
                    beginMinuteStr = selected;
                    tvBegin.setText(beginHourStr + ":" + beginMinuteStr + ":" + beginSecondsStr);
                }else {
                    endMinute = Integer.parseInt(selected);
                    endMinuteStr = selected;
                    tvEnd.setText(endHourStr + ":" + endMinuteStr + ":" + endSecondsStr);
                }
                break;

            case R.id.pvSecond:
                seconds = Integer.parseInt(selected);
                secondsStr = selected;
                if (type == 1){
                    beginSeconds = Integer.parseInt(selected);
                    beginSecondsStr = selected;
                    tvBegin.setText(beginHourStr + ":" + beginMinuteStr + ":" + beginSecondsStr);
                }else {
                    endSeconds = Integer.parseInt(selected);
                    endSecondsStr = selected;
                    tvEnd.setText(endHourStr + ":" + endMinuteStr + ":" + endSecondsStr);
                }
                break;
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBeginHour() {
        return beginHour;
    }

    public void setBeginHour(int beginHour) {
        this.beginHour = beginHour;
    }

    public int getBeginMinute() {
        return beginMinute;
    }

    public void setBeginMinute(int beginMinute) {
        this.beginMinute = beginMinute;
    }

    public int getBeginSeconds() {
        return beginSeconds;
    }

    public void setBeginSeconds(int beginSeconds) {
        this.beginSeconds = beginSeconds;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public int getEndSeconds() {
        return endSeconds;
    }

    public void setEndSeconds(int endSeconds) {
        this.endSeconds = endSeconds;
    }

    private void setSelected(){
        tvBegin.setSelected(type == 1);
        tvEnd.setSelected(type == 2);
    }

    /**
     * 开始时间字符显示
     */
    private void setBeginTime(){
        pvHour.setSelected(pvHour.getItemIndex(beginHourStr));
        pvMinute.setSelected(pvMinute.getItemIndex(beginMinuteStr));
        pvSecond.setSelected(pvSecond.getItemIndex(beginSecondsStr));
    }

    /**
     * 结束时间字符显示
     */
    private void setEndTime(){
        pvHour.setSelected(pvHour.getItemIndex(endHourStr));
        pvMinute.setSelected(pvMinute.getItemIndex(endMinuteStr));
        pvSecond.setSelected(pvSecond.getItemIndex(endSecondsStr));
    }


    @OnClick({R.id.ivClose,R.id.llBegin, R.id.llEnd, R.id.tvConfirm})
    void onClick(View view){
        switch (view.getId()){
            case R.id.ivClose:
                dismiss();
                break;

            case R.id.llBegin:
                type = 1;
                setSelected();
                setBeginTime();
                break;

            case R.id.llEnd:
                type = 2;
                setSelected();
                setEndTime();
                break;

            case R.id.tvConfirm:
                if (timePeriodSelectListener!=null){
                    timePeriodSelectListener.onBegin(beginHour, beginMinute, beginSeconds, beginHourStr, beginMinuteStr, beginSecondsStr);
                    timePeriodSelectListener.onEnd(endHour, endMinute, endSeconds, endHourStr, endMinuteStr, endSecondsStr);
                }
                dismiss();
                break;
        }
    }


    public String getSecondsStr() {
        return secondsStr;
    }

    public void setSecondsStr(String secondsStr) {
        this.secondsStr = secondsStr;
    }

    public String getBeginHourStr() {
        return beginHourStr;
    }

    public void setBeginHourStr(String beginHourStr) {
        this.beginHourStr = beginHourStr;
    }

    public String getBeginMinuteStr() {
        return beginMinuteStr;
    }

    public void setBeginMinuteStr(String beginMinuteStr) {
        this.beginMinuteStr = beginMinuteStr;
    }

    public String getBeginSecondsStr() {
        return beginSecondsStr;
    }

    public void setBeginSecondsStr(String beginSecondsStr) {
        this.beginSecondsStr = beginSecondsStr;
    }

    public String getEndHourStr() {
        return endHourStr;
    }

    public void setEndHourStr(String endHourStr) {
        this.endHourStr = endHourStr;
    }

    public String getEndMinuteStr() {
        return endMinuteStr;
    }

    public void setEndMinuteStr(String endMinuteStr) {
        this.endMinuteStr = endMinuteStr;
    }

    public String getEndSecondsStr() {
        return endSecondsStr;
    }

    public void setEndSecondsStr(String endSecondsStr) {
        this.endSecondsStr = endSecondsStr;
    }

    private OnTimePeriodSelectListener timePeriodSelectListener;

    public OnTimePeriodSelectListener getTimePeriodSelectListener() {
        return timePeriodSelectListener;
    }

    public void setTimePeriodSelectListener(OnTimePeriodSelectListener timePeriodSelectListener) {
        this.timePeriodSelectListener = timePeriodSelectListener;
    }

    public interface OnTimePeriodSelectListener{
        void onBegin(int beginHour, int beginMinute, int beginSeconds, String beginHourStr, String beginMinuteStr, String beginSecondsStr);
        void onEnd(int endHour, int endMinute, int endSeconds, String endHourStr, String endMinuteStr, String endSecondsStr);
    }
}
