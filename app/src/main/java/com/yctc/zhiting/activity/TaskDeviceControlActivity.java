package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SceneDeviceStatusControlContract;
import com.yctc.zhiting.activity.contract.TaskDeviceControlContract;
import com.yctc.zhiting.activity.presenter.SceneDeviceStatusControlPresenter;
import com.yctc.zhiting.activity.presenter.TaskDeviceControlPresenter;
import com.yctc.zhiting.adapter.SceneDeviceStatusControlAdapter;
import com.yctc.zhiting.adapter.TaskDeviceControlAdapter;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.bean.ListBottomBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.SceneSelectBottomDialog;
import com.yctc.zhiting.dialog.SeekBarBottomDialog;
import com.yctc.zhiting.dialog.TimingBottomDialog;
import com.yctc.zhiting.entity.SceneTaskBean;
import com.yctc.zhiting.entity.scene.ConditionBean;
import com.yctc.zhiting.entity.scene.CreateScenePost;
import com.yctc.zhiting.entity.scene.SceneDeviceStatusControlBean;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.StringUtil;
import com.yctc.zhiting.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 任务设备控制
 */
public class TaskDeviceControlActivity extends MVPBaseActivity<TaskDeviceControlContract.View, TaskDeviceControlPresenter> implements TaskDeviceControlContract.View {

    @BindView(R.id.rvFunction)
    RecyclerView rvFunction;
    @BindView(R.id.tvNext)
    TextView tvNext;
    @BindView(R.id.tvDelay)
    TextView tvDelay;

    private SceneSelectBottomDialog switchDialog; // 开关
    private List<ListBottomBean> switchData = new ArrayList<>();

    private SeekBarBottomDialog brightnessDialog; //温度
    private SeekBarBottomDialog colorTempDialog; //色温
    private TimingBottomDialog timingBottomDialog;  // 时间筛选

    private TaskDeviceControlAdapter taskDeviceControlAdapter;
    /**
     * 0. 设备列表
     * 1. 创建场景
     */
    private int from;
    private int deviceId; // 场景时才有，设备id
    private String deviceName; // 场景时才有，设备名称
    private String deviceType; // 设备类型
    private String logoUrl; // 设备logo
    private String location; // 设备位置

    private String switchStatus;
    private String brightness;
    private String colorTemp;


    private int position; // 开关在列表位置
    private int h;  // 时
    private int m;  // 分
    private int s;  // 秒

    private String delayTime="00:00:00";

    private int sceneTaskId;
    private int switchId;
    private int brightnessId;
    private int colorTempId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_task_device_control;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setNextStatus();
        setTitleRightText(getResources().getString(R.string.scene_reset));
        getRightTitleText().setTextColor(UiUtil.getColor(R.color.color_3F4663));
        getRightTitleText().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        getRightTitleText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNext.setEnabled(false);
                for (SceneDeviceStatusControlBean sceneDeviceStatusControlBean : taskDeviceControlAdapter.getData()){
                    sceneDeviceStatusControlBean.setValue("");
                }
                taskDeviceControlAdapter.notifyDataSetChanged();
                h=0;
                m=0;
                s=0;
                delayTime = "00:00:00";
                tvDelay.setText("");
                setNextStatus();
            }
        });
        initSwitchDialog();
        initBrightness();
        initColorTemp();
        initTimingDialog();
        rvFunction.setLayoutManager(new LinearLayoutManager(this));
        taskDeviceControlAdapter = new TaskDeviceControlAdapter();
        rvFunction.setAdapter(taskDeviceControlAdapter);
        taskDeviceControlAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SceneDeviceStatusControlBean sceneDeviceStatusControlBean = taskDeviceControlAdapter.getItem(position);
                TaskDeviceControlActivity.this.position = position;
                switch (sceneDeviceStatusControlBean.getType()){
                    case 1:  // 开关
                        if (switchDialog!=null && !switchDialog.isShowing()){
                            switchDialog.show(TaskDeviceControlActivity.this);
                        }
                        break;
                    case 2:  // 亮度
                        if (brightnessDialog!=null && !brightnessDialog.isShowing()){
                            brightnessDialog.show(TaskDeviceControlActivity.this);
                        }
                        break;

                    case 3:  // 色温
                        if (colorTempDialog!=null && !colorTempDialog.isShowing()){
                            colorTempDialog.show(TaskDeviceControlActivity.this);
                        }
                        break;

                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getIntExtra(IntentConstant.ID, 0);
        deviceName = getIntent().getStringExtra(IntentConstant.NAME);
        deviceType = getIntent().getStringExtra(IntentConstant.TYPE);
        logoUrl = getIntent().getStringExtra(IntentConstant.LOGO_URL);
        location = getIntent().getStringExtra(IntentConstant.RA_NAME);
        switchStatus = getIntent().getStringExtra(IntentConstant.SWITCH_STATUS);
        brightness = getIntent().getStringExtra(IntentConstant.BRIGHTNESS);
        colorTemp = getIntent().getStringExtra(IntentConstant.COLOR_TEMP);

        //修改场景额外需要的数据
        sceneTaskId = getIntent().getIntExtra(IntentConstant.SCENE_TASK_ID, 0);
        switchId = getIntent().getIntExtra(IntentConstant.SWITCH_ID, 0);
        brightnessId = getIntent().getIntExtra(IntentConstant.BRIGHT_ID, 0);
        colorTempId = getIntent().getIntExtra(IntentConstant.COLOR_TEMP_ID, 0);

        h = getIntent().getIntExtra(IntentConstant.HOUR, 0);
        m = getIntent().getIntExtra(IntentConstant.MINUTE, 0);
        s = getIntent().getIntExtra(IntentConstant.SECONDS, 0);
        String timeStr = getIntent().getStringExtra(IntentConstant.TIME_STR);
        delayTime = !TextUtils.isEmpty(timeStr) ? timeStr : "00:00:00";
        from = getIntent().getIntExtra(IntentConstant.FROM, 0);
        setTitleCenter(deviceName);
        tvNext.setEnabled(from == 1 ? true : false);
        setNextStatus();
        if (!TextUtils.isEmpty(timeStr) && !timeStr.equals("00:00:00")) {
            tvDelay.setText(String.format(getResources().getString(R.string.scene_delay_after), timeStr));
        }

        mPresenter.getDeviceDetail(deviceId);
    }

    /**
     * 开关
     */
    private void initSwitchDialog(){
        switchData.add(new ListBottomBean(1, getResources().getString(R.string.scene_turn_on), false, true));
        switchData.add(new ListBottomBean(2, getResources().getString(R.string.scene_turn_off), false, true));
        switchData.add(new ListBottomBean(3, getResources().getString(R.string.scene_toggle), false, true));
        switchDialog = new SceneSelectBottomDialog(getResources().getString(R.string.scene_switch), switchData);
        switchDialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                String name = item.getName();
                String switchOperate = "";
                switch (item.getId()) {
                    case 1:  // 打开
                        switchOperate = Constant.ON;
                        break;

                    case 2:  // 关闭
                        switchOperate = Constant.OFF;
                        break;

                    case 3:  // 切换
                        switchOperate = Constant.TOGGLE;
                        break;
                }
                tvNext.setEnabled(true);
                taskDeviceControlAdapter.getItem(position).setValue(name);
                taskDeviceControlAdapter.notifyDataSetChanged();
                setNextStatus();
                switchDialog.dismiss();


            }
        });
    }

    /**
     * 亮度
     */
    private void initBrightness(){
        brightnessDialog = new SeekBarBottomDialog(1, getResources().getString(R.string.scene_brightness), false);
        brightnessDialog.setClickTodoListener(new SeekBarBottomDialog.OnClickTodoListener() {
            @Override
            public void onTodo(String operator, String operatorName, int val) {
                tvNext.setEnabled(true);
                taskDeviceControlAdapter.getItem(position).setValue(val+"%");
                taskDeviceControlAdapter.notifyDataSetChanged();
                setNextStatus();
                brightnessDialog.dismiss();


            }
        });
    }

    /**
     * 色温
     */
    private void initColorTemp(){
        colorTempDialog = new SeekBarBottomDialog(2, getResources().getString(R.string.scene_color_temperature), false);
        colorTempDialog.setClickTodoListener(new SeekBarBottomDialog.OnClickTodoListener() {
            @Override
            public void onTodo(String operator, String operatorName, int val) {
                tvNext.setEnabled(true);
                taskDeviceControlAdapter.getItem(position).setValue(val+"%");
                taskDeviceControlAdapter.notifyDataSetChanged();
                setNextStatus();
                colorTempDialog.dismiss();
            }
        });
    }

    /**
     * 定时
     */
    private void initTimingDialog(){
        timingBottomDialog = new TimingBottomDialog();
        timingBottomDialog.setTimeSelectListener(new TimingBottomDialog.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(int hour, int minute, int seconds, String timeStr) {
                delayTime = timeStr;
                tvDelay.setText(String.format(getResources().getString(R.string.scene_delay_after), timeStr));
                h = hour;
                m = minute;
                s = seconds;
                timingBottomDialog.dismiss();
            }
        });
    }

    // 下一步按钮样式
    private void setNextStatus(){
        tvNext.setAlpha( tvNext.isEnabled() ? 1 : 0.5f);
    }

    @OnClick({R.id.llDelay, R.id.tvNext})
    void onClick(View view){
        switch (view.getId()){
            case R.id.llDelay:  // 延时
                if (timingBottomDialog!=null && !timingBottomDialog.isShowing()){
                    timingBottomDialog.setSelectTime(delayTime);
                    timingBottomDialog.show(this);
                }
                break;

            case R.id.tvNext:  // 下一步

                    List<CreateScenePost.SceneTasksBean.SceneTaskDevicesBean> deviceStatus = new ArrayList<>();
                    String title = "";
                    // 执行设备状态
                    for (int i=0; i< taskDeviceControlAdapter.getData().size(); i++){
                        SceneDeviceStatusControlBean sceneDeviceStatusControlBean = taskDeviceControlAdapter.getData().get(i);
                        if (sceneDeviceStatusControlBean.getValue()!=null && sceneDeviceStatusControlBean.getValue().trim().length()>0){
                            String action = "";
                            int actionId=0;
                            switch (sceneDeviceStatusControlBean.getType()){
                                case 1:
                                    action = Constant.SWITCH;
                                    title =  title + sceneDeviceStatusControlBean.getValue() +  "、" ;
                                    actionId = switchId;
                                    break;
                                case 2:
                                    action = Constant.set_bright;
                                    title = title +  sceneDeviceStatusControlBean.getName()+sceneDeviceStatusControlBean.getValue() + "、" ;
                                    actionId = brightnessId;
                                    break;
                                case 3:
                                    action = Constant.set_color_temp;
                                    title = title +  sceneDeviceStatusControlBean.getName()+sceneDeviceStatusControlBean.getValue() + "、";
                                    actionId = colorTempId;
                                    break;

                            }
                            String actionVal = sceneDeviceStatusControlBean.getValue();

                            if (actionVal.equals(getResources().getString(R.string.scene_turn_on))){  // 如果是打开 on
                                actionVal = Constant.ON;

                            }
                            if (actionVal.equals(getResources().getString(R.string.scene_turn_off))){  // 关闭
                                actionVal = Constant.OFF;
                            }
                            if (actionVal.equals(getResources().getString(R.string.scene_toggle))){  // 开关切换
                                actionVal = Constant.TOGGLE;
                            }
                            if (actionVal.contains("%")){
                                actionVal = actionVal.replace("%", "");
                            }
                            CreateScenePost.SceneTasksBean.SceneTaskDevicesBean sceneTaskDevicesBean = new CreateScenePost.SceneTasksBean.SceneTaskDevicesBean(action, deviceId, actionVal, sceneDeviceStatusControlBean.getAlias());

                            // 修改场景要设置值
                            if (actionId>0){
                                sceneTaskDevicesBean.setId(actionId);
                            }
                            // 修改场景要设置值
                            if (sceneTaskId>0 && actionId>0){
                                sceneTaskDevicesBean.setScene_task_id(sceneTaskId);
                            }
                            deviceStatus.add(sceneTaskDevicesBean);
                        }
                    }

                    // 创建任务
                    SceneTaskBean sceneTaskBean = new SceneTaskBean(title.substring(0, title.length()-1), deviceName);
                    // 修改场景要设置值
                    if (sceneTaskId>0){
                        sceneTaskBean.setId(sceneTaskId);
                    }
                    sceneTaskBean.setTimeStr(delayTime);
                    sceneTaskBean.setDeviceType(deviceType);
                    sceneTaskBean.setType(1);
                    sceneTaskBean.setHour(h);
                    sceneTaskBean.setMinute(m);
                    sceneTaskBean.setSeconds(s);
                    sceneTaskBean.setDelay_seconds(TimeUtil.toSeconds(h, m, s));
                    sceneTaskBean.setLogo(logoUrl);
                    sceneTaskBean.setLocation(location);
                    sceneTaskBean.setScene_task_devices(deviceStatus);
                if (from == 0) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(IntentConstant.TASK_BEAN, sceneTaskBean);
                    switchToActivity(CreateSceneActivity.class, bundle);

                }else {
                    EventBus.getDefault().post(sceneTaskBean);
                }
                finish();
                break;
        }
    }

    /**
     * 设备详情成功
     * @param deviceDetailBean
     */
    @Override
    public void getDeviceDetailSuccess(DeviceDetailBean deviceDetailBean) {

        if (deviceDetailBean!=null){
            DeviceDetailBean.DeviceInfoBean deviceInfoBean = deviceDetailBean.getDevice_info();
            if (deviceInfoBean!=null){
                List<DeviceDetailBean.DeviceInfoBean.ActionsBean> actions = deviceInfoBean.getActions();
                if (CollectionUtil.isNotEmpty(actions)){
                    List<SceneDeviceStatusControlBean> data = new ArrayList<>();
                    for (DeviceDetailBean.DeviceInfoBean.ActionsBean actionsBean : actions){
                        switch (actionsBean.getAction()){
                            case "switch":
                                data.add(new SceneDeviceStatusControlBean(1, actionsBean.getName(), !TextUtils.isEmpty(switchStatus) ? StringUtil.switchStatus2String(switchStatus, this) : "", Constant.Attribute.POWER));
                                break;

                            case "set_bright":
                                if (!TextUtils.isEmpty(brightness)) {
                                    brightness = brightness + "%";
                                }
                                data.add(new SceneDeviceStatusControlBean(2, actionsBean.getName() , brightness, Constant.Attribute.COLOR_TEMP));
                                break;

                            case "set_color_temp":
                                if (!TextUtils.isEmpty(colorTemp)) {
                                    colorTemp = colorTemp + "%";
                                }
                                data.add(new SceneDeviceStatusControlBean(3, actionsBean.getName(), colorTemp, Constant.Attribute.BRIGHTNESS));
                                break;
                        }
                    }
                    taskDeviceControlAdapter.setNewData(data);
                }
            }
        }
    }

    /**
     * 设备详情失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getFail(int errorCode, String msg) {

    }
}