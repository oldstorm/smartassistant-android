package com.yctc.zhiting.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.EffectTimePeriodContract;
import com.yctc.zhiting.activity.presenter.EffectTimePeriodPresenter;
import com.yctc.zhiting.bean.ListBottomBean;
import com.yctc.zhiting.dialog.ListBottomDialog;
import com.yctc.zhiting.dialog.SceneSelectBottomDialog;
import com.yctc.zhiting.dialog.TimePeriodDialog;
import com.yctc.zhiting.entity.mine.InvitationCodePost;
import com.yctc.zhiting.event.SceneEffectTimeEvent;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 生效时段
 */
public class EffectTimePeriodActivity extends MVPBaseActivity<EffectTimePeriodContract.View, EffectTimePeriodPresenter> implements  EffectTimePeriodContract.View  {

    @BindView(R.id.llTime)
    LinearLayout llTime;
    @BindView(R.id.tvAll)
    TextView tvAll;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvBegin)
    TextView tvBegin;
    @BindView(R.id.tvEnd)
    TextView tvEnd;
    @BindView(R.id.tvRepeat)
    TextView tvRepeat;

    private TimePeriodDialog timePeriodDialog;  // 时间选择

    private SceneSelectBottomDialog sceneSelectBottomDialog;  // 重复类型
    private List<ListBottomBean> repeatTypeData = new ArrayList<>();

    private ListBottomDialog listBottomDialog; // 自定义星期
    private List<ListBottomBean> weekData = new ArrayList<>();


    private int beginHour;
    private int beginMinute;
    private int beginSeconds;

    private int endHour = 23;
    private int endMinute = 59;
    private int endSeconds = 59;

    private String beginHourStr = "00";
    private String beginMinuteStr = "00";
    private String beginSecondsStr = "00";

    private String endHourStr = "23";
    private String endMinuteStr = "59";
    private String endSecondsStr = "59";

    private int time_period;  // 生效时间类型，全天为1，时间段为2,auto_run为false可不传
    private String timeStr;
    private int effect_start_time;
    private int effect_end_time;
    private int repeat_type; // 重复执行的类型；1：每天; 2:工作日 ；3：自定义;auto_run为false可不传
    private String repeat_date;  // 只能传长度为7包含1-7的数字；"1122"视为不合法传参;repeat_type为1时:"1234567"; 2:12345; 3：任意

    private boolean first = true;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_effect_time_period;
    }

    @Override
    protected void initUI() {
        super.initUI();
        initTimePeriodDialog();

        initListBottomDialog();
    }

    @Override
    protected void initData() {
        super.initData();
        time_period = getIntent().getIntExtra(IntentConstant.TIME_PERIOD, 1);
        repeat_type = getIntent().getIntExtra(IntentConstant.REPEAT_TYPE, 1);
        repeat_date = getIntent().getStringExtra(IntentConstant.REPEAT_DATE);
        timeStr = getIntent().getStringExtra(IntentConstant.TIME_STR);
        tvAll.setSelected(time_period == 1 ? true : false);
        tvTime.setSelected(time_period == 2 ? true : false);
        llTime.setVisibility(time_period == 2? View.VISIBLE : View.GONE);
        initRepeatDialog();
        // 重复类型
        switch (repeat_type){
            case 1:
                tvRepeat.setText(getResources().getString(R.string.scene_everyday));
                break;

            case 2:
                tvRepeat.setText(getResources().getString(R.string.scene_monday_friday));
                break;

            case 3:
                String repeatDateStr = "";
                for (int i=0; i<repeat_date.length(); i++){
                    if (i<repeat_date.length()-1) {
                        repeatDateStr = repeatDateStr + TimeUtil.toWeek(String.valueOf(repeat_date.charAt(i)), this)+"、";
                    }else {
                        repeatDateStr = repeatDateStr + TimeUtil.toWeek(String.valueOf(repeat_date.charAt(i)), this);
                    }
                }
                tvRepeat.setText(repeatDateStr);
                break;
        }
        // 时间
        if (time_period == 2) {
            if (!TextUtils.isEmpty(timeStr)) {
                String[] beginAndEnd = timeStr.split("-");
                String beginTime = beginAndEnd[0];
                String endTime = beginAndEnd[1];
                tvBegin.setText(beginTime);
                tvEnd.setText(endTime);

                String[] beginStr = beginTime.split(":");
                beginHourStr = beginStr[0];
                beginMinuteStr = beginStr[1];
                beginSecondsStr = beginStr[0];
                beginHour = Integer.parseInt(beginHourStr);
                beginMinute = Integer.parseInt(beginMinuteStr);
                beginSeconds = Integer.parseInt(beginSecondsStr);

                String[] endStr = endTime.split(":");
                endHourStr = endStr[0];
                endMinuteStr = endStr[1];
                endSecondsStr = endStr[0];
                endHour = Integer.parseInt(endHourStr);
                endMinute = Integer.parseInt(endMinuteStr);
                endSeconds = Integer.parseInt(endSecondsStr);
            }
        }
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    /**
     * 时间筛选
     */
    private void initTimePeriodDialog(){
        timePeriodDialog = new TimePeriodDialog();
        timePeriodDialog.setTimePeriodSelectListener(new TimePeriodDialog.OnTimePeriodSelectListener() {
            @Override
            public void onBegin(int beginHour, int beginMinute, int beginSeconds, String beginHourStr, String beginMinuteStr, String beginSecondsStr) {
                tvBegin.setText(beginHourStr+":"+beginMinuteStr+":"+beginSecondsStr);
                EffectTimePeriodActivity.this.beginHour = beginHour;
                EffectTimePeriodActivity.this.beginMinute = beginMinute;
                EffectTimePeriodActivity.this.beginSeconds = beginSeconds;
                EffectTimePeriodActivity.this.beginHourStr = beginHourStr;
                EffectTimePeriodActivity.this.beginMinuteStr = beginMinuteStr;
                EffectTimePeriodActivity.this.beginSecondsStr = beginSecondsStr;

            }

            @Override
            public void onEnd(int endHour, int endMinute, int endSeconds, String endHourStr, String endMinuteStr, String endSecondsStr) {
                tvEnd.setText(endHourStr+":"+endMinuteStr+":"+endSecondsStr);
                EffectTimePeriodActivity.this.endHour = endHour;
                EffectTimePeriodActivity.this.endMinute = endMinute;
                EffectTimePeriodActivity.this.endSeconds = endSeconds;
                EffectTimePeriodActivity.this.endHourStr = endHourStr;
                EffectTimePeriodActivity.this.endMinuteStr = endMinuteStr;
                EffectTimePeriodActivity.this.endSecondsStr = endSecondsStr;
            }
        });

    }

    /**
     * 重复类型
     */
    private void initRepeatDialog(){
        repeatTypeData.add(new ListBottomBean(1, getResources().getString(R.string.scene_everyday), repeat_type == 1, true));
        repeatTypeData.add(new ListBottomBean(2, getResources().getString(R.string.scene_monday_friday), repeat_type == 2, true));
        repeatTypeData.add(new ListBottomBean(3, getResources().getString(R.string.scene_custom), repeat_type == 3, true));
        sceneSelectBottomDialog = new SceneSelectBottomDialog("", repeatTypeData);
        sceneSelectBottomDialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                switch (item.getId()){
                    case 1:  // 每天
                        repeat_date = "1234567";
                        tvRepeat.setText(item.getName());
                        repeat_type = item.getId();
                        break;
                    case 2:  // 周一到周五
                        repeat_date = "12345";
                        tvRepeat.setText(item.getName());
                        repeat_type = item.getId();
                        break;

                    case 3:  // 自定义
                        if (first && !TextUtils.isEmpty(repeat_date) && repeat_type==3){  // 第一次
                            for (int i=0; i<repeat_date.length(); i++){
                                int index = Integer.parseInt(String.valueOf(repeat_date.charAt(i)));
                                weekData.get(index-1).setSelected(true);
                            }
                            listBottomDialog.notifyItemChange();
                            first = false;
                        }
                        listBottomDialog.show(EffectTimePeriodActivity.this);
                        break;
                }
                sceneSelectBottomDialog.dismiss();
            }
        });
    }

    /**
     * 自定义星期
     */
    private void initListBottomDialog(){

        weekData.add(new ListBottomBean(1, getResources().getString(R.string.scene_monday), false));
        weekData.add(new ListBottomBean(2, getResources().getString(R.string.scene_tuesday), false));
        weekData.add(new ListBottomBean(3, getResources().getString(R.string.scene_wednesday), false));
        weekData.add(new ListBottomBean(4, getResources().getString(R.string.scene_thursday), false));
        weekData.add(new ListBottomBean(5, getResources().getString(R.string.scene_friday), false));
        weekData.add(new ListBottomBean(6, getResources().getString(R.string.scene_saturday), false));
        weekData.add(new ListBottomBean(7, getResources().getString(R.string.scene_sunday), false));



        listBottomDialog = ListBottomDialog.newInstance(getResources().getString(R.string.scene_custom), "", getResources().getString(R.string.common_confirm), true,weekData);

        listBottomDialog.setClickTodoListener(new ListBottomDialog.OnClickTodoListener() {
            @Override
            public void onTodo(List<ListBottomBean> data) {
                StringBuffer stringBuffer = new StringBuffer();
                repeat_date = "";
                for (int i=0; i<data.size(); i++){
                    if (i<data.size()-1){
                        stringBuffer.append(data.get(i).getName()+"、");
                    }else {
                        stringBuffer.append(data.get(i).getName());
                    }
                    repeat_date = repeat_date+data.get(i).getId();
                }
                repeat_type = 3;
                tvRepeat.setText(stringBuffer.toString());
                listBottomDialog.dismiss();
            }
        });
    }

    @OnClick({R.id.ivBack, R.id.tvFinish, R.id.tvAll, R.id.tvTime, R.id.llBegin, R.id.llEnd, R.id.llRepeat})
    void OnClick(View view){
        switch (view.getId()){
            case R.id.ivBack:  // 返回
                onBackPressed();
                break;

            case R.id.tvFinish: // 完成
                String beginTime = beginHourStr+":"+beginMinuteStr+":"+beginSecondsStr;
                String endTime = endHourStr + ":"+endMinuteStr +":"+endSecondsStr;
                timeStr = time_period == 1 ? tvAll.getText().toString() : beginTime + "-" + endTime;
                long bt =  TimeUtil.string2Stamp(TimeUtil.getToday() + " " + beginTime);
                long et =   TimeUtil.string2Stamp(TimeUtil.getToday() + " " + endTime);
                if (et<=bt){
                    ToastUtil.show(getResources().getString(R.string.scene_end_greater_than_begin));
                    return;
                }
                SceneEffectTimeEvent sceneEffectTimeEvent = new SceneEffectTimeEvent(time_period, timeStr, bt,
                        et, repeat_type, repeat_date, tvRepeat.getText().toString());
                EventBus.getDefault().post(sceneEffectTimeEvent);
                onBackPressed();
                break;

            case R.id.tvAll:  // 全天
                llTime.setVisibility(View.GONE);
                tvAll.setSelected(true);
                tvTime.setSelected(false);
                time_period = 1;
                break;

            case R.id.tvTime:  // 时间段
                llTime.setVisibility(View.VISIBLE);
                tvTime.setSelected(true);
                tvAll.setSelected(false);
                time_period = 2;

                break;

            case R.id.llBegin: // 开始
                timePeriodDialog.setType(1);
                setTimeDialogData();
                timePeriodDialog.show(this);
                break;

            case R.id.llEnd:  // 结束
                timePeriodDialog.setType(2);
                setTimeDialogData();
                timePeriodDialog.show(this);
                break;

            case R.id.llRepeat: // 重复
                for (ListBottomBean listBottomBean : repeatTypeData){
                    listBottomBean.setSelected(false);
                }
                repeatTypeData.get(repeat_type-1).setSelected(true);
                sceneSelectBottomDialog.notifyItemChange();
                sceneSelectBottomDialog.show(this);
                break;
        }
    }

    /**
     * 设置数据
     */
    private void setTimeDialogData(){
        timePeriodDialog.setBeginHour(beginHour);
        timePeriodDialog.setBeginMinute(beginMinute);
        timePeriodDialog.setBeginSeconds(beginSeconds);
        timePeriodDialog.setBeginHourStr(beginHourStr);
        timePeriodDialog.setBeginMinuteStr(beginMinuteStr);
        timePeriodDialog.setBeginSecondsStr(beginSecondsStr);
        timePeriodDialog.setEndHour(endHour);
        timePeriodDialog.setEndMinute(endMinute);
        timePeriodDialog.setEndSeconds(endSeconds);
        timePeriodDialog.setEndHourStr(endHourStr);
        timePeriodDialog.setEndMinuteStr(endMinuteStr);
        timePeriodDialog.setEndSecondsStr(endSecondsStr);
    }
}