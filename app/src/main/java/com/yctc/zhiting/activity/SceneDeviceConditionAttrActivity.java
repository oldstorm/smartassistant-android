package com.yctc.zhiting.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SceneDeviceConditionAttrContract;
import com.yctc.zhiting.activity.presenter.SceneDeviceDeviceConditionPresenter;
import com.yctc.zhiting.adapter.SceneDeviceConditionAttrAdapter;
import com.yctc.zhiting.bean.ListBottomBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.ColorPickerDialog;
import com.yctc.zhiting.dialog.SceneSelectBottomDialog;
import com.yctc.zhiting.dialog.SeekBarBottomDialog;
import com.yctc.zhiting.entity.DeviceDetailEntity;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.scene.ConditionBean;
import com.yctc.zhiting.entity.scene.CreateScenePost;
import com.yctc.zhiting.entity.scene.SceneConditionAttrEntity;
import com.yctc.zhiting.entity.scene.SceneConditionEntity;
import com.yctc.zhiting.entity.scene.SceneDeviceInfoEntity;
import com.yctc.zhiting.utils.AttrConstant;
import com.yctc.zhiting.utils.AttrUtil;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.NeedDealAttrConstant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 场景详情 设备条件选择（重构）
 */
public class SceneDeviceConditionAttrActivity extends MVPBaseActivity<SceneDeviceConditionAttrContract.View, SceneDeviceDeviceConditionPresenter> implements SceneDeviceConditionAttrContract.View {

    private int deviceId;
    private String deviceName;
    private SceneConditionEntity sceneConditionEntity;  // 从修改场景带过来条件信息
    private SceneConditionAttrEntity sceneConditionAttrEntity; // 从修改场景选择的属性条件
    private SceneConditionAttrEntity selectedAttr; // 选择的属性条件

    private SceneSelectBottomDialog switchDialog; // 开关
    private SceneSelectBottomDialog switch1Dialog; // 开关（一键）
    private SceneSelectBottomDialog switch2Dialog; // 开关（二键）
    private SceneSelectBottomDialog switch3Dialog; // 开关（三键）
    private SceneSelectBottomDialog humanSensorDialog; // 人体传感器状态弹窗
    private SceneSelectBottomDialog doorSensorDialog; // 门窗传感器状态弹窗
    private SceneSelectBottomDialog waterSensorDialog; // 水浸传感器状态弹窗
    private SceneSelectBottomDialog targetStateDialog; // 网关守护
    private List<ListBottomBean> switchData = new ArrayList<>(); // 开关
    private List<ListBottomBean> switch1Data = new ArrayList<>(); // 开关（一键）
    private List<ListBottomBean> switch2Data = new ArrayList<>(); // 开关（二键）
    private List<ListBottomBean> switch3Data = new ArrayList<>(); // 开关（三键）
    private List<ListBottomBean> targetStateData = new ArrayList<>(); // 守护
    private List<ListBottomBean> humanSensorData = new ArrayList<>(); // 人体传感器状态数据
    private List<ListBottomBean> doorSensorData = new ArrayList<>(); // 门窗传感器状态数据
    private List<ListBottomBean> waterSensorData = new ArrayList<>(); // 水浸传感器状态数据
    private SeekBarBottomDialog brightnessDialog; //温度
    private SeekBarBottomDialog colorTempDialog; //色温
    private SeekBarBottomDialog humidityDialog; //湿度
    private SeekBarBottomDialog temperatureDialog; //温度
    private ColorPickerDialog mColorPickerDialog; // 颜色选择器
    private SceneSelectBottomDialog mCurtainStatusDialog; // 窗帘状态弹窗
    private List<ListBottomBean> mCurtainStatusData = new ArrayList<>(); // 窗帘状态数据
    private SeekBarBottomDialog mCurtainDialog; // 窗帘位置弹窗
    private SceneSelectBottomDialog mSwitchEventDialog; // 无线开关 0单击 1双击 2三击
    private List<ListBottomBean> mSwitchEventData = new ArrayList<>(); // 无线开关数据


    private String op; // < = >
    private SceneDeviceInfoEntity deviceInfo;  // 设备信息

    @BindView(R.id.rvFunction)
    RecyclerView rvFunction;
    private SceneDeviceConditionAttrAdapter sceneDeviceConditionAttrAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_device_condition_attr;
    }

    @Override
    protected void initUI() {
        super.initUI();
        initRv();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        deviceId = intent.getIntExtra(IntentConstant.ID, 0);
        deviceName = getIntent().getStringExtra(IntentConstant.NAME);
        sceneConditionEntity = (SceneConditionEntity) intent.getSerializableExtra(IntentConstant.BEAN);
        if (sceneConditionEntity != null) {
            sceneConditionAttrEntity = sceneConditionEntity.getCondition_attr();
        }
        initSwitchDialog();
        initSwitch1Dialog();
        initSwitch2Dialog();
        initSwitch3Dialog();
        initBrightness();
        initColorTemp();
        initHumidityDialog();
        initTemperature();
        initColorPickerDialog();
        initHumanSensorDialog();
        initDoorSensorDialog();
        initWaterSensorDialog();
        initTargetStateDialog();
        initCurtainDialog();
        initCurtainStatusDialog();
        initSwitchEventDialog();
        setTitleCenter(deviceName);
        mPresenter.getDeviceDetail(deviceId, 2);
    }


    /**
     * 初始化列表
     */
    private void initRv() {
        rvFunction.setLayoutManager(new LinearLayoutManager(this));
        sceneDeviceConditionAttrAdapter = new SceneDeviceConditionAttrAdapter();
        rvFunction.setAdapter(sceneDeviceConditionAttrAdapter);
        sceneDeviceConditionAttrAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                selectedAttr = sceneDeviceConditionAttrAdapter.getItem(position);
                // 开关:power; 色温：color_temp；亮度:brightness
                try {
                    switch (selectedAttr.getType()) {
                        case AttrConstant.ON_OFF: // 开关
                            Object switchStatus = selectedAttr.getVal();
                            LogUtil.e("switchStatus=================="+switchStatus);
                            if (switchStatus != null) {
                                String val = (String) switchStatus;
                                if (val.equals(Constant.ON)) {
                                    resetSwitchData(0);
                                } else if (val.equals(Constant.OFF)) {
                                    resetSwitchData(1);
                                }
                            } else {
                                resetSwitchData(-1);
                            }
                            switchDialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case AttrConstant.COLOR_TEMP:  // 色温
                            Bundle bundle = new Bundle();
                            if (selectedAttr != null) {
                                Object object = selectedAttr.getVal();
                                if (object != null) {
                                    double val = 0;
                                    val = (Double) object;
                                    bundle.putInt("val", val > 0 ? AttrUtil.getPercentVal(selectedAttr.getMin(), selectedAttr.getMax(), val) : 0);
                                }


                            }
                            if (sceneConditionEntity != null) {
                                bundle.putString("operator", sceneConditionEntity.getOperator());
                            }
                            colorTempDialog.setArguments(bundle);
                            colorTempDialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case AttrConstant.BRIGHTNESS:  // 亮度
                            Bundle bundleB = new Bundle();
                            if (selectedAttr != null) {
                                Object object = selectedAttr.getVal();
                                if (object != null) {
                                    double val = 0;
                                    val = (Double) object;
                                    bundleB.putInt("val", val > 0 ? AttrUtil.getPercentVal(selectedAttr.getMin(), selectedAttr.getMax(), val) : 0);
                                }

                            }
                            if (sceneConditionEntity != null) {
                                bundleB.putString("operator", sceneConditionEntity.getOperator());
                            }
                            brightnessDialog.setArguments(bundleB);
                            brightnessDialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case AttrConstant.LEAK_DETECTED:  // 水浸
                            waterSensorDialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case AttrConstant.TEMPERATURE:  // 温度
                            Bundle bundleT = new Bundle();
                            if (selectedAttr != null) {
                                Object object = selectedAttr.getVal();
                                if (object != null) {
                                    double val = 0;
                                    val = (Double) object;
                                    bundleT.putInt("val", (int) val);
                                }
                                Integer minVal = selectedAttr.getMin();
                                if (minVal != null) {
                                    bundleT.putInt("minVal", minVal);
                                }
                                Integer maxVal = selectedAttr.getMax();
                                if (maxVal != null) {
                                    bundleT.putInt("maxVal", maxVal);
                                }
                            }
                            if (sceneConditionEntity != null) {
                                bundleT.putString("operator", sceneConditionEntity.getOperator());
                            }
                            temperatureDialog.setArguments(bundleT);

                            temperatureDialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case AttrConstant.HUMIDITY:  // 湿度
                            Bundle bundleH = new Bundle();
                            if (selectedAttr != null) {
                                Object object = selectedAttr.getVal();
                                if (object != null) {
                                    double val = 0;
                                    val = (Double) object;
                                    bundleH.putInt("val", val > 0 ? AttrUtil.getPercentVal(selectedAttr.getMin(), selectedAttr.getMax(), val) : 0);
                                }

                            }
                            if (sceneConditionEntity != null) {
                                bundleH.putString("operator", sceneConditionEntity.getOperator());
                            }
                            humidityDialog.setArguments(bundleH);
                            humidityDialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case AttrConstant.WINDOW_DOOR_CLOSE:  // 门窗
                            doorSensorDialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case AttrConstant.DETECTED:  // 人体
                            humanSensorDialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case AttrConstant.POWERS_1: // 开关（一键）
                            switch1Dialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case AttrConstant.POWERS_2: // 开关（二键）
                            switch2Dialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case AttrConstant.POWERS_3: // 开关（三键）
                            switch3Dialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case AttrConstant.RGB:  // 颜色
                            Bundle bundleC = new Bundle();
                            if (selectedAttr != null) {
                                Object object = selectedAttr.getVal();
                                if (object != null) {
                                    String val = (String) object;
                                    bundleC.putString("rgb", val);
                                }
                            }
                            mColorPickerDialog.setArguments(bundleC);
                            mColorPickerDialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case AttrConstant.TARGET_STATE: // 守护
                            targetStateDialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case AttrConstant.TARGET_POSITION: // 窗帘位置
                            boolean openSelected = false;
                            boolean closeSelected = false;
                            boolean percentSelected = false;

                            Integer min = selectedAttr.getMin();
                            Integer max = selectedAttr.getMax();
                            String operator = "";
                            if (sceneConditionEntity != null) {
                                operator = sceneConditionEntity.getOperator();
                            }
                            if (min != null) {
                                intMin = min;
                                LogUtil.e("最小值：" + intMin);
                                closeSelected = intCurtainVal == min; // 等于最小值就是关闭
                                if (!TextUtils.isEmpty(operator)) {
                                    closeSelected = closeSelected && operator.equals("=");
                                }
                            }
                            if (max != null) {
                                intMax = max;
                                LogUtil.e("最大值：" + intMax);
                                openSelected = intCurtainVal == max;  // 等于最大值就是打开
                                if (!TextUtils.isEmpty(operator)) {
                                    openSelected = openSelected && operator.equals("=");
                                }
                            }
                            if (min != null && max != null) {
                                if (!TextUtils.isEmpty(operator)) {
                                    if (operator.equals("=")) { // 如果为等号
                                        if (intCurtainVal == intMax || intCurtainVal == intMin) { // 如果当前为最大值或最小值
                                            percentSelected = false;
                                        } else {
                                            percentSelected = true;
                                        }
                                    } else {
                                        percentSelected = true;
                                    }
                                }
                            }
                            if (hasVal) {
                                mCurtainStatusData.get(0).setSelected(openSelected);
                                mCurtainStatusData.get(1).setSelected(closeSelected);
                                mCurtainStatusData.get(2).setSelected(percentSelected);
                            }
                            mCurtainStatusDialog.notifyItemChange();
                            mCurtainStatusDialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case AttrConstant.SWITCH_EVENT: // 无线开关（一二三击）
                            mSwitchEventDialog.show(SceneDeviceConditionAttrActivity.this);
                            break;
                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    /**
     * 开关
     */
    private void initSwitchDialog() {
        boolean onSelected = false;
        boolean offSelected = false;
        if (sceneConditionAttrEntity != null) {
            if (sceneConditionAttrEntity.getType().equals(Constant.ON_OFF)) {
                onSelected = sceneConditionAttrEntity.getVal().equals(Constant.ON);
                offSelected = sceneConditionAttrEntity.getVal().equals(Constant.OFF);
            }
        }
        switchData.add(new ListBottomBean(1, getResources().getString(R.string.scene_turn_on), onSelected, true));
        switchData.add(new ListBottomBean(2, getResources().getString(R.string.scene_turn_off), offSelected, true));
        switchDialog = new SceneSelectBottomDialog(getResources().getString(R.string.scene_switch), switchData);
        switchDialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                selectedAttr.setType(Constant.ON_OFF);
                switch (item.getId()) {
                    case 1:  // 打开
                        selectedAttr.setVal(Constant.ON);
                        break;

                    case 2:  // 关闭
                        selectedAttr.setVal(Constant.OFF);
                        break;
                }
                selectedAttr.setVal_type("string");
                toSceneDetail();

            }
        });
    }

    /**
     * 改变开关弹窗选项
     *
     * @param position
     */
    private void resetSwitchData(int position) {
        for (ListBottomBean listBottomBean : switchData) {
            listBottomBean.setSelected(false);
        }
        if (position>-1) {
            switchData.get(position).setSelected(true);
        }
        switchDialog.notifyItemChange();
    }


    /**
     * 开关 (一键)
     */
    private void initSwitch1Dialog() {
        boolean onSelected1 = false;
        boolean offSelected1 = false;
        if (sceneConditionAttrEntity != null) {
            if (sceneConditionAttrEntity.getType().equals(Constant.powers_1)) {
                onSelected1 = sceneConditionAttrEntity.getVal().equals(Constant.ON);
                offSelected1 = sceneConditionAttrEntity.getVal().equals(Constant.OFF);
            }
        }
        switch1Data.add(new ListBottomBean(1, getResources().getString(R.string.scene_turn_on), onSelected1, true));
        switch1Data.add(new ListBottomBean(2, getResources().getString(R.string.scene_turn_off), offSelected1, true));
        switch1Dialog = new SceneSelectBottomDialog(getResources().getString(R.string.scene_switch), switch1Data);
        switch1Dialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                selectedAttr.setType(Constant.powers_1);
                switch (item.getId()) {
                    case 1:  // 打开
                        selectedAttr.setVal(Constant.ON);
                        break;

                    case 2:  // 关闭
                        selectedAttr.setVal(Constant.OFF);
                        break;
                }
                selectedAttr.setVal_type("string");
                switch1Dialog.dismiss();
                toSceneDetail();

            }
        });
    }


    /**
     * 开关(二键)
     */
    private void initSwitch2Dialog() {
        boolean onSelected2 = false;
        boolean offSelected2 = false;
        if (sceneConditionAttrEntity != null) {
            if (sceneConditionAttrEntity.getType().equals(Constant.powers_2)) {
                onSelected2 = sceneConditionAttrEntity.getVal().equals(Constant.ON);
                offSelected2 = sceneConditionAttrEntity.getVal().equals(Constant.OFF);
            }
        }
        switch2Data.add(new ListBottomBean(1, getResources().getString(R.string.scene_turn_on), onSelected2, true));
        switch2Data.add(new ListBottomBean(2, getResources().getString(R.string.scene_turn_off), offSelected2, true));
        switch2Dialog = new SceneSelectBottomDialog(getResources().getString(R.string.scene_switch), switch2Data);
        switch2Dialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                selectedAttr.setType(Constant.powers_2);
                switch (item.getId()) {
                    case 1:  // 打开
                        selectedAttr.setVal(Constant.ON);
                        break;

                    case 2:  // 关闭
                        selectedAttr.setVal(Constant.OFF);
                        break;
                }
                selectedAttr.setVal_type("string");
                switch2Dialog.dismiss();
                toSceneDetail();

            }
        });
    }

    /**
     * 开关(三键)
     */
    private void initSwitch3Dialog() {
        boolean onSelected3 = false;
        boolean offSelected3 = false;
        if (sceneConditionAttrEntity != null) {
            if (sceneConditionAttrEntity.getType().equals(Constant.powers_3)) {
                onSelected3 = sceneConditionAttrEntity.getVal().equals(Constant.ON);
                offSelected3 = sceneConditionAttrEntity.getVal().equals(Constant.OFF);
            }
        }
        switch3Data.add(new ListBottomBean(1, getResources().getString(R.string.scene_turn_on), onSelected3, true));
        switch3Data.add(new ListBottomBean(2, getResources().getString(R.string.scene_turn_off), offSelected3, true));
        switch3Dialog = new SceneSelectBottomDialog(getResources().getString(R.string.scene_switch), switch3Data);
        switch3Dialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                selectedAttr.setType(Constant.powers_3);
                switch (item.getId()) {
                    case 1:  // 打开
                        selectedAttr.setVal(Constant.ON);
                        break;

                    case 2:  // 关闭
                        selectedAttr.setVal(Constant.OFF);
                        break;
                }
                selectedAttr.setVal_type("string");
                switch3Dialog.dismiss();
                toSceneDetail();

            }
        });
    }

    /**
     * 亮度
     */
    private void initBrightness() {
        brightnessDialog = new SeekBarBottomDialog(1, getResources().getString(R.string.scene_brightness), true);
        brightnessDialog.setClickTodoListener(new SeekBarBottomDialog.OnClickTodoListener() {
            @Override
            public void onTodo(String operator, String operatorName, int val) {
                selectedAttr.setType(Constant.brightness);
                op = operator;
                selectedAttr.setVal(AttrUtil.getActualVal(selectedAttr.getMin(), selectedAttr.getMax(), val));
                selectedAttr.setVal_type("int");
                toSceneDetail();
            }
        });
    }

    /**
     * 色温
     */
    private void initColorTemp() {
        colorTempDialog = new SeekBarBottomDialog(2, getResources().getString(R.string.scene_color_temperature), true);
        colorTempDialog.setClickTodoListener(new SeekBarBottomDialog.OnClickTodoListener() {
            @Override
            public void onTodo(String operator, String operatorName, int val) {
                selectedAttr.setType(Constant.color_temp);
                op = operator;
                selectedAttr.setVal(AttrUtil.getActualVal(selectedAttr.getMin(), selectedAttr.getMax(), val));
                selectedAttr.setVal_type("int");
                colorTempDialog.dismiss();
                toSceneDetail();
            }
        });
    }

    /**
     * 湿度弹窗
     */
    private void initHumidityDialog() {
        humidityDialog = new SeekBarBottomDialog(3, UiUtil.getString(R.string.scene_humidity), true);
        humidityDialog.setClickTodoListener(new SeekBarBottomDialog.OnClickTodoListener() {
            @Override
            public void onTodo(String operator, String operatorName, int val) {
                selectedAttr.setType(Constant.humidity);
                op = operator;
                selectedAttr.setVal(AttrUtil.getActualVal(selectedAttr.getMin(), selectedAttr.getMax(), val));
                selectedAttr.setVal_type("int");
                toSceneDetail();
                humidityDialog.dismiss();
            }
        });
    }

    /**
     * 温度弹窗
     */
    private void initTemperature() {
        temperatureDialog = new SeekBarBottomDialog(4, UiUtil.getString(R.string.scene_temperature), true);
        temperatureDialog.setClickTodoListener(new SeekBarBottomDialog.OnClickTodoListener() {
            @Override
            public void onTodo(String operator, String operatorName, int val) {
                selectedAttr.setType(Constant.temperature);
                op = operator;
                selectedAttr.setVal((double)val);
                selectedAttr.setVal_type("int");
                toSceneDetail();
                temperatureDialog.dismiss();
            }
        });
    }

    /**
     * 颜色选择器弹窗
     */
    private void initColorPickerDialog() {
        mColorPickerDialog = new ColorPickerDialog();
        mColorPickerDialog.setConfirmListener(new ColorPickerDialog.OnConfirmListener() {
            @Override
            public void onConfirm(String colorStr, int color, float hue, float saturation) {
                selectedAttr.setType(Constant.rgb);
                op = "=";
                selectedAttr.setVal(colorStr);
                selectedAttr.setVal_type("string");
                mColorPickerDialog.dismiss();
                toSceneDetail();
            }
        });
    }

    /**
     * 人体传感器状态弹窗
     */
    private void initHumanSensorDialog() {
        boolean selected = false;
        if (sceneConditionAttrEntity != null) {
            if (sceneConditionAttrEntity.getType().equals(Constant.detected)) {
                Object val = sceneConditionAttrEntity.getVal();
                if (val != null) {
                    selected = (double) val == 1;
                }
            }
        }
        humanSensorData.add(new ListBottomBean(1, UiUtil.getString(R.string.scene_check_moving), selected, true));
        humanSensorDialog = new SceneSelectBottomDialog(UiUtil.getString(R.string.scene_status), humanSensorData);
        humanSensorDialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                selectedAttr.setType(Constant.detected);
                selectedAttr.setVal(Constant.STATUE_1);
                selectedAttr.setVal_type("int");
                humanSensorDialog.dismiss();
                toSceneDetail();
            }
        });
    }

    /**
     * 门窗传感器状态弹窗
     */
    private void initDoorSensorDialog() {
        boolean onSelected = false;
        boolean offSelected = false;
        if (sceneConditionAttrEntity != null) {
            if (sceneConditionAttrEntity.getType().equals(Constant.window_door_close)) {
                Object val = sceneConditionAttrEntity.getVal();
                if (val != null) {
                    onSelected = (double) val == 1;
                    offSelected = (double) val == 0;
                }
            }
        }
        doorSensorData.add(new ListBottomBean(1, UiUtil.getString(R.string.scene_close_to_open), onSelected, true));
        doorSensorData.add(new ListBottomBean(2, UiUtil.getString(R.string.scene_open_to_close), offSelected, true));
        doorSensorDialog = new SceneSelectBottomDialog(UiUtil.getString(R.string.scene_status), doorSensorData);
        doorSensorDialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                selectedAttr.setType(Constant.window_door_close);
                switch (item.getId()) {
                    case 1:  // 打开
                        selectedAttr.setVal((double)Constant.STATUE_1);
                        break;

                    case 2:  // 关闭
                        selectedAttr.setVal((double)Constant.STATUE_0);
                        break;
                }
                selectedAttr.setVal_type("int");
                doorSensorDialog.dismiss();
                toSceneDetail();
            }
        });
    }

    /**
     * 水浸传感器状态弹窗
     */
    private void initWaterSensorDialog() {
        boolean selected = false;
        if (sceneConditionAttrEntity != null) {
            if (sceneConditionAttrEntity.getType().equals(Constant.leak_detected)) {
                Object val = sceneConditionAttrEntity.getVal();
                if (val != null) {
                    selected = (double) val == 1;
                }
            }
        }
        waterSensorData.add(new ListBottomBean(1, UiUtil.getString(R.string.scene_check_watering), selected, true));
        waterSensorDialog = new SceneSelectBottomDialog(UiUtil.getString(R.string.scene_status), waterSensorData);
        waterSensorDialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                selectedAttr.setType(Constant.leak_detected);
                selectedAttr.setVal(Constant.STATUE_1);
                selectedAttr.setVal_type("int");
                toSceneDetail();
                waterSensorDialog.dismiss();
            }
        });
    }

    /**
     * 窗帘状态弹窗
     */
    private int intMin = 0;
    private int intMax = 0;
    private int intCurtainVal = 0;
    private boolean hasVal;

    private void initCurtainStatusDialog() {
        if (sceneConditionAttrEntity != null) {
            String attr = sceneConditionAttrEntity.getType();
            if (attr != null) {
                if (attr.equals(Constant.target_position)) {
                    Object val = sceneConditionAttrEntity.getVal();
                    if (val != null) {
                        hasVal = true;
                        intCurtainVal = ((Double) val).intValue();
                        LogUtil.e("当前值：" + intCurtainVal);
                    }
                }
            }
        }
        mCurtainStatusData.add(new ListBottomBean(1, UiUtil.getString(R.string.scene_curtain_open), false, true));
        mCurtainStatusData.add(new ListBottomBean(2, UiUtil.getString(R.string.scene_curtain_close), false, true));
        mCurtainStatusData.add(new ListBottomBean(3, UiUtil.getString(R.string.scene_curtain_open_percent), false, true));
        mCurtainStatusDialog = new SceneSelectBottomDialog(UiUtil.getString(R.string.scene_curtain_status), mCurtainStatusData);
        mCurtainStatusDialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                selectedAttr.setType(Constant.target_position);
                selectedAttr.setVal_type("int");
                switch (item.getId()) {
                    case 1: // 打开
                        selectedAttr.setVal((double) intMax);
                        toSceneDetail();
                        break;

                    case 2:  //关闭
                        selectedAttr.setVal((double) intMin);
                        toSceneDetail();
                        break;

                    case 3: // 百分比
                        Bundle bundleCurtain = new Bundle();
                        bundleCurtain.putInt("val", AttrUtil.getPercentVal(intMin, intMax, intCurtainVal));
                        bundleCurtain.putInt("minVal", intMin);
                        bundleCurtain.putInt("maxVal", intMax);
                        if (sceneConditionEntity != null) {
                            bundleCurtain.putString("operator", sceneConditionEntity.getOperator());
                        }
                        mCurtainDialog.setArguments(bundleCurtain);
                        mCurtainDialog.show(SceneDeviceConditionAttrActivity.this);
                        break;
                }

                mCurtainStatusDialog.dismiss();
            }
        });
    }

    /**
     * 初始化窗帘位置弹窗
     */
    private void initCurtainDialog() {
        mCurtainDialog = new SeekBarBottomDialog(5, UiUtil.getString(R.string.scene_curtain_position), true);
        mCurtainDialog.setClickTodoListener(new SeekBarBottomDialog.OnClickTodoListener() {
            @Override
            public void onTodo(String operator, String operatorName, int val) {
                selectedAttr.setType(Constant.target_position);
                op = operator;
                selectedAttr.setVal(AttrUtil.getActualVal(intMin, intMax, val));
                selectedAttr.setVal_type("int");
                LogUtil.e("当前值：" + AttrUtil.getActualVal(intMin, intMax, intCurtainVal));
                toSceneDetail();
                mCurtainDialog.dismiss();
            }
        });
    }

    /**
     * 无线开关
     */
    private void initSwitchEventDialog() {
        boolean singleSelected = false;
        boolean doubleSelected = false;
        boolean tripleSelected = false;
        if (sceneConditionAttrEntity != null) {
            String attr = sceneConditionAttrEntity.getType();
            if (attr != null) {
                if (attr.equals(Constant.switch_event)) {
                    Object val = sceneConditionAttrEntity.getVal();
                    if (val != null) {
                        int intVal = ((Double) val).intValue();
                        singleSelected = intVal == 0;
                        doubleSelected = intVal == 1;
                        tripleSelected = intVal == 2;
                    }
                }
            }
        }

        mSwitchEventData.add(new ListBottomBean(0, UiUtil.getString(R.string.scene_single_click), singleSelected, true));
        mSwitchEventData.add(new ListBottomBean(1, UiUtil.getString(R.string.scene_double_click), doubleSelected, true));
        mSwitchEventData.add(new ListBottomBean(2, UiUtil.getString(R.string.scene_triple_click), tripleSelected, true));
        mSwitchEventDialog = new SceneSelectBottomDialog(UiUtil.getString(R.string.scene_switch), mSwitchEventData);
        mSwitchEventDialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                selectedAttr.setType(Constant.switch_event);
                selectedAttr.setVal((double) item.getId());
                toSceneDetail();
            }
        });
    }

    /**
     * 守护
     */
    private void initTargetStateDialog() {
        boolean atHomeSelected = false;
        boolean outHomeSelected = false;
        boolean sleepSelected = false;
        boolean guardSelected = false;
        if (sceneConditionAttrEntity != null) {
            if (sceneConditionAttrEntity.getType().equals(Constant.target_state)) {
                Object val = sceneConditionAttrEntity.getVal();
                if (val != null) {
                    atHomeSelected = (Double) val == 0;
                    outHomeSelected = (Double) val == 1;
                    sleepSelected = (Double) val == 2;
                    guardSelected = (Double) val == 3;
                }

            }
        }
        targetStateData.add(new ListBottomBean(0, getResources().getString(R.string.scene_open_at_home), atHomeSelected, true));
        targetStateData.add(new ListBottomBean(1, getResources().getString(R.string.scene_open_out_home), outHomeSelected, true));
        targetStateData.add(new ListBottomBean(2, getResources().getString(R.string.scene_open_sleep), sleepSelected, true));
        targetStateData.add(new ListBottomBean(3, getResources().getString(R.string.scene_close_guard), guardSelected, true));
        targetStateDialog = new SceneSelectBottomDialog(getResources().getString(R.string.scene_switch), targetStateData);
        targetStateDialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                selectedAttr.setType(Constant.target_state);
                selectedAttr.setVal((double) (item.getId()));
                selectedAttr.setVal_type("int");
                targetStateDialog.dismiss();
                toSceneDetail();

            }
        });
    }

    /**
     * 回到场景详情
     */
    private void toSceneDetail() {
        if (sceneConditionEntity != null) {
            sceneConditionEntity.setCondition_attr(selectedAttr);
            sceneConditionEntity.setOperator(op);
            sceneConditionEntity.setDevice_info(deviceInfo);
            EventBus.getDefault().post(sceneConditionEntity);
        } else {
            sceneConditionEntity = new SceneConditionEntity(2);
            sceneConditionEntity.setDevice_id(deviceId);
            sceneConditionEntity.setOperator(op);
            sceneConditionEntity.setCondition_attr(selectedAttr);
            sceneConditionEntity.setDevice_info(deviceInfo);
            Bundle bundle = new Bundle();
            bundle.putSerializable(IntentConstant.BEAN, sceneConditionEntity);
            switchToActivity(SceneDetailActivity.class, bundle);
        }
        finish();
    }


    /**
     * 设备详情成功
     *
     * @param deviceDetailResponseEntity
     */
    @Override
    public void getDeviceDetailSuccess(DeviceDetailResponseEntity deviceDetailResponseEntity) {
        if (deviceDetailResponseEntity != null) {
            DeviceDetailEntity deviceDetailBean = deviceDetailResponseEntity.getDevice_info();
            if (deviceDetailBean != null) {
                String locationName = "";
                DeviceDetailEntity.AreaAndLocationBean locationBean = deviceDetailBean.getLocation();
                if (locationBean != null) {
                    locationName = locationBean.getName();
                }
                List<SceneConditionAttrEntity> allAttrs = deviceDetailBean.getAttributes();
                List<SceneConditionAttrEntity> attrs = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(allAttrs)) {
                    if (sceneConditionAttrEntity != null) {
                        for (SceneConditionAttrEntity attr : allAttrs) {
                            if (!TextUtils.isEmpty(attr.getType()) && NeedDealAttrConstant.isContainConditionDealAttrs(attr.getType())) {
                                if (attr.getAid() == sceneConditionAttrEntity.getAid()) {
                                    attr.setVal(sceneConditionAttrEntity.getVal());
                                    attr.setVal_type(sceneConditionAttrEntity.getVal_type());
                                } else {
                                    attr.setVal(null);
                                }
                                attrs.add(attr);
                            }
                        }
                    } else {
                        for (SceneConditionAttrEntity attr : allAttrs) {
                            if (!TextUtils.isEmpty(attr.getType()) && NeedDealAttrConstant.isContainConditionDealAttrs(attr.getType())) {
                                attr.setVal(null);
                                attrs.add(attr);
                            }
                        }
                    }
                }
                deviceInfo = new SceneDeviceInfoEntity(deviceDetailBean.getName(), locationName, deviceDetailBean.getLogo_url());
                sceneDeviceConditionAttrAdapter.setNewData(attrs);
            }
        }
    }

    /**
     * 设备详情失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}