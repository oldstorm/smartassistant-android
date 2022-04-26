package com.yctc.zhiting.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SceneDeviceTaskAttrContract;
import com.yctc.zhiting.activity.presenter.SceneDeviceTaskAttrPresenter;
import com.yctc.zhiting.adapter.SceneDeviceTaskAttrAdapter;
import com.yctc.zhiting.bean.ListBottomBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.ColorPickerDialog;
import com.yctc.zhiting.dialog.SceneSelectBottomDialog;
import com.yctc.zhiting.dialog.SeekBarBottomDialog;
import com.yctc.zhiting.dialog.TimingBottomDialog;
import com.yctc.zhiting.entity.DeviceDetailEntity;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.scene.SceneConditionAttrEntity;
import com.yctc.zhiting.entity.scene.SceneDeviceInfoEntity;
import com.yctc.zhiting.entity.scene.SceneTaskEntity;
import com.yctc.zhiting.utils.AttrConstant;
import com.yctc.zhiting.utils.AttrUtil;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.NeedDealAttrConstant;
import com.yctc.zhiting.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 创建/修改场景 选择设备属性（重构）
 */
public class SceneDeviceTaskAttrActivity extends MVPBaseActivity<SceneDeviceTaskAttrContract.View, SceneDeviceTaskAttrPresenter> implements SceneDeviceTaskAttrContract.View {

    @BindView(R.id.rvFunction)
    RecyclerView rvFunction;
    @BindView(R.id.tvNext)
    TextView tvNext;
    @BindView(R.id.tvDelay)
    TextView tvDelay;
    @BindView(R.id.llDelay)
    LinearLayout llDelay;

    private SceneSelectBottomDialog switchDialog; // 开关
    private SceneSelectBottomDialog switch1Dialog; // 开关（一键）
    private SceneSelectBottomDialog switch2Dialog; // 开关（二键）
    private SceneSelectBottomDialog switch3Dialog; // 开关（三键）

    private List<ListBottomBean> switchData = new ArrayList<>();
    private List<ListBottomBean> switch1Data = new ArrayList<>(); // 开关（一键）
    private List<ListBottomBean> switch2Data = new ArrayList<>(); // 开关（二键）
    private List<ListBottomBean> switch3Data = new ArrayList<>(); // 开关（三键）

    private SeekBarBottomDialog brightnessDialog; //温度
    private SeekBarBottomDialog colorTempDialog; //色温
    private TimingBottomDialog timingBottomDialog;  // 时间筛选
    private ColorPickerDialog mColorPickerDialog; // 颜色选择器

    private SceneDeviceTaskAttrAdapter sceneDeviceTaskAttrAdapter;

    private SceneTaskEntity sceneTaskEntity; // 从场景带过来的任务信息
    private List<SceneConditionAttrEntity> attrList; // 场景过来的数据

    private SceneSelectBottomDialog targetStateDialog; // 网关守护
    private List<ListBottomBean> targetStateData = new ArrayList<>(); // 守护

    private SceneSelectBottomDialog mCurtainStatusDialog; // 窗帘状态弹窗
    private List<ListBottomBean> mCurtainStatusData = new ArrayList<>(); // 窗帘状态数据
    private SeekBarBottomDialog mCurtainDialog; // 窗帘位置弹窗

    private int deviceId; // 设备id
    private String deviceName;  // 设备名称
    private SceneDeviceInfoEntity deviceInfo; // 设备信息

    private SceneConditionAttrEntity selectedAttr; // 选择的属性条件
    private int pos; // 属性的位置
    private int delaySeconds; // 延迟秒数
    private String delaySecondsStr = "";
    private boolean hasAttr;
    private double mCurtainVal;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_device_task_attr;
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
        sceneTaskEntity = (SceneTaskEntity) intent.getSerializableExtra(IntentConstant.BEAN);
        hasAttr = sceneTaskEntity != null;
        if (sceneTaskEntity != null) {
            attrList = sceneTaskEntity.getAttributes();
            delaySeconds = sceneTaskEntity.getDelay_seconds();
        }
        tvNext.setEnabled(sceneTaskEntity != null ? true : false);

        if (delaySeconds > 0) {
            delaySecondsStr = TimeUtil.seconds2String(delaySeconds);
            tvDelay.setText(delaySecondsStr);
        }
        setNextStatus();
        initSwitchDialog();
        initSwitch1Dialog();
        initSwitch2Dialog();
        initSwitch3Dialog();
        initBrightness();
        initColorTemp();
        initTimingDialog();
        initColorPickerDialog();
        initTargetStateDialog();
        initCurtainDialog();
        initCurtainStatusDialog();
        setTitleCenter(deviceName);
        mPresenter.getDeviceDetail(deviceId, 1);
    }

    /**
     * 初始化列表
     */
    private void initRv() {
        rvFunction.setLayoutManager(new LinearLayoutManager(this));
        sceneDeviceTaskAttrAdapter = new SceneDeviceTaskAttrAdapter();
        rvFunction.setAdapter(sceneDeviceTaskAttrAdapter);
        sceneDeviceTaskAttrAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                selectedAttr = sceneDeviceTaskAttrAdapter.getItem(position);
                pos = position;
                Object object = selectedAttr.getVal();
                // 开关:power; 色温：color_temp；亮度:brightness
                try {
                    switch (selectedAttr.getType()) {
                        case AttrConstant.ON_OFF:  // 开关
                            if (object != null) {
                                String val = (String) object;
                                if (val.equals(Constant.ON)) {
                                    resetSwitchData(0);
                                } else if (val.equals(Constant.OFF)) {
                                    resetSwitchData(1);
                                } else if (val.equals(Constant.TOGGLE)) {
                                    resetSwitchData(2);
                                }
                            } else {
                                resetSwitchData(-1);
                            }
                            switchDialog.show(SceneDeviceTaskAttrActivity.this);
                            break;

                        case AttrConstant.POWERS_1:  // 开关（一键）
                            if (object != null) {
                                String val = (String) object;
                                if (val.equals(Constant.ON)) {
                                    resetSwitch1Data(0);
                                } else if (val.equals(Constant.OFF)) {
                                    resetSwitch1Data(1);
                                } else if (val.equals(Constant.TOGGLE)) {
                                    resetSwitch1Data(2);
                                }
                            }
                            switch1Dialog.show(SceneDeviceTaskAttrActivity.this);
                            break;

                        case AttrConstant.POWERS_2: // 开关（二键）
                            if (object != null) {
                                String val = (String) object;
                                if (val.equals(Constant.ON)) {
                                    resetSwitch2Data(0);
                                } else if (val.equals(Constant.OFF)) {
                                    resetSwitch2Data(1);
                                } else if (val.equals(Constant.TOGGLE)) {
                                    resetSwitch2Data(2);
                                }
                            }
                            switch2Dialog.show(SceneDeviceTaskAttrActivity.this);
                            break;

                        case AttrConstant.POWERS_3:  // 开关（三键）
                            if (object != null) {
                                String val = (String) object;
                                if (val.equals(Constant.ON)) {
                                    resetSwitch3Data(0);
                                } else if (val.equals(Constant.OFF)) {
                                    resetSwitch3Data(1);
                                } else if (val.equals(Constant.TOGGLE)) {
                                    resetSwitch3Data(2);
                                }
                            }
                            switch3Dialog.show(SceneDeviceTaskAttrActivity.this);
                            break;

                        case AttrConstant.COLOR_TEMP:  // 色温
                            Bundle bundle = new Bundle();
                            if (object != null) {
                                double val = 0;
                                val = (Double) object;
                                bundle.putInt("val", AttrUtil.getPercentVal(selectedAttr.getMin(), selectedAttr.getMax(), val));
                                colorTempDialog.setArguments(bundle);
                            }
                            colorTempDialog.show(SceneDeviceTaskAttrActivity.this);
                            break;

                        case AttrConstant.BRIGHTNESS:  // 亮度
                            Bundle bundleB = new Bundle();
                            if (object != null) {
                                double val = 0;
                                val = (Double) object;
                                bundleB.putInt("val", AttrUtil.getPercentVal(selectedAttr.getMin(), selectedAttr.getMax(), val));
                                brightnessDialog.setArguments(bundleB);
                            }
                            brightnessDialog.show(SceneDeviceTaskAttrActivity.this);
                            break;

                        case AttrConstant.RGB:  // 颜色
                            Bundle bundleC = new Bundle();
                            if (selectedAttr != null) {
                                if (object != null) {
                                    String val = (String) object;
                                    bundleC.putString("rgb", val);
                                }
                            }
                            mColorPickerDialog.setArguments(bundleC);
                            mColorPickerDialog.show(SceneDeviceTaskAttrActivity.this);
                            break;

                        case AttrConstant.TARGET_STATE: // 守护
                            targetStateDialog.show(SceneDeviceTaskAttrActivity.this);
                            break;

                        case AttrConstant.TARGET_POSITION: // 窗帘位置
                            if (object != null) {
                                mCurtainVal = (Double) object;
                                if (mCurtainVal == selectedAttr.getMin()) {
                                    mCurtainStatusData.get(1).setSelected(true);
                                    mCurtainStatusData.get(2).setSelected(false);
                                } else if (mCurtainVal == selectedAttr.getMax()) {
                                    mCurtainStatusData.get(0).setSelected(true);
                                    mCurtainStatusData.get(2).setSelected(false);
                                } else {
                                    mCurtainStatusData.get(2).setSelected(true);
                                }
                            }
                            mCurtainStatusDialog.show(SceneDeviceTaskAttrActivity.this);
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
        boolean toggleSelected = false;

        if (CollectionUtil.isNotEmpty(attrList)) {
            for (SceneConditionAttrEntity sceneConditionAttrEntity : attrList) {
                if (sceneConditionAttrEntity.getType().equals(Constant.ON_OFF)) {
                    onSelected = sceneConditionAttrEntity.getType().equals(Constant.ON);
                    offSelected = sceneConditionAttrEntity.getType().equals(Constant.OFF);
                    toggleSelected = sceneConditionAttrEntity.getType().equals(Constant.TOGGLE);
                    break;
                }
            }
        }
        switchData.add(new ListBottomBean(1, getResources().getString(R.string.scene_turn_on), onSelected, true));
        switchData.add(new ListBottomBean(2, getResources().getString(R.string.scene_turn_off), offSelected, true));
        switchData.add(new ListBottomBean(3, getResources().getString(R.string.scene_toggle), toggleSelected, true));
        switchDialog = new SceneSelectBottomDialog(getResources().getString(R.string.scene_switch), switchData);
        switchDialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                switch (item.getId()) {
                    case 1:  // 打开
                        sceneDeviceTaskAttrAdapter.getItem(pos).setVal(Constant.ON);
                        break;

                    case 2:  // 关闭
                        sceneDeviceTaskAttrAdapter.getItem(pos).setVal(Constant.OFF);
                        break;

                    case 3:  // 切换
                        sceneDeviceTaskAttrAdapter.getItem(pos).setVal(Constant.TOGGLE);
                        break;
                }
                tvNext.setEnabled(true);
                sceneDeviceTaskAttrAdapter.getItem(pos).setVal_type(Constant.STR);
                sceneDeviceTaskAttrAdapter.notifyItemChanged(pos);
                setNextStatus();
                switchDialog.dismiss();
            }
        });
    }

    /**
     * 开关（一键）
     */
    private void initSwitch1Dialog() {
        boolean onSelected = false;
        boolean offSelected = false;
        boolean toggleSelected = false;

        if (CollectionUtil.isNotEmpty(attrList)) {
            for (SceneConditionAttrEntity sceneConditionAttrEntity : attrList) {
                if (sceneConditionAttrEntity.getType().equals(Constant.powers_1)) {
                    onSelected = sceneConditionAttrEntity.getType().equals(Constant.ON);
                    offSelected = sceneConditionAttrEntity.getType().equals(Constant.OFF);
                    toggleSelected = sceneConditionAttrEntity.getType().equals(Constant.TOGGLE);
                    break;
                }
            }
        }
        switch1Data.add(new ListBottomBean(1, getResources().getString(R.string.scene_turn_on), onSelected, true));
        switch1Data.add(new ListBottomBean(2, getResources().getString(R.string.scene_turn_off), offSelected, true));
        switch1Data.add(new ListBottomBean(3, getResources().getString(R.string.scene_toggle), toggleSelected, true));

        switch1Dialog = new SceneSelectBottomDialog(getResources().getString(R.string.scene_switch), switch1Data);
        switch1Dialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                switch (item.getId()) {
                    case 1:  // 打开
                        sceneDeviceTaskAttrAdapter.getItem(pos).setVal(Constant.ON);
                        break;

                    case 2:  // 关闭
                        sceneDeviceTaskAttrAdapter.getItem(pos).setVal(Constant.OFF);
                        break;

                    case 3:  // 切换
                        sceneDeviceTaskAttrAdapter.getItem(pos).setVal(Constant.TOGGLE);
                        break;
                }
                tvNext.setEnabled(true);
                sceneDeviceTaskAttrAdapter.getItem(pos).setVal_type(Constant.STR);
                sceneDeviceTaskAttrAdapter.notifyItemChanged(pos);
                setNextStatus();
                switch1Dialog.dismiss();
            }
        });
    }


    /**
     * 开关（二键）
     */
    private void initSwitch2Dialog() {
        boolean onSelected = false;
        boolean offSelected = false;
        boolean toggleSelected = false;

        if (CollectionUtil.isNotEmpty(attrList)) {
            for (SceneConditionAttrEntity sceneConditionAttrEntity : attrList) {
                if (sceneConditionAttrEntity.getType().equals(Constant.powers_2)) {
                    onSelected = sceneConditionAttrEntity.getType().equals(Constant.ON);
                    offSelected = sceneConditionAttrEntity.getType().equals(Constant.OFF);
                    toggleSelected = sceneConditionAttrEntity.getType().equals(Constant.TOGGLE);
                    break;
                }
            }
        }
        switch2Data.add(new ListBottomBean(1, getResources().getString(R.string.scene_turn_on), onSelected, true));
        switch2Data.add(new ListBottomBean(2, getResources().getString(R.string.scene_turn_off), offSelected, true));
        switch2Data.add(new ListBottomBean(3, getResources().getString(R.string.scene_toggle), toggleSelected, true));

        switch2Dialog = new SceneSelectBottomDialog(getResources().getString(R.string.scene_switch), switch2Data);
        switch2Dialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                switch (item.getId()) {
                    case 1:  // 打开
                        sceneDeviceTaskAttrAdapter.getItem(pos).setVal(Constant.ON);
                        break;

                    case 2:  // 关闭
                        sceneDeviceTaskAttrAdapter.getItem(pos).setVal(Constant.OFF);
                        break;

                    case 3:  // 切换
                        sceneDeviceTaskAttrAdapter.getItem(pos).setVal(Constant.TOGGLE);
                        break;
                }
                tvNext.setEnabled(true);
                sceneDeviceTaskAttrAdapter.getItem(pos).setVal_type(Constant.STR);
                sceneDeviceTaskAttrAdapter.notifyItemChanged(pos);
                setNextStatus();
                switch2Dialog.dismiss();
            }
        });
    }


    /**
     * 开关（三键）
     */
    private void initSwitch3Dialog() {
        boolean onSelected = false;
        boolean offSelected = false;
        boolean toggleSelected = false;

        if (CollectionUtil.isNotEmpty(attrList)) {
            for (SceneConditionAttrEntity sceneConditionAttrEntity : attrList) {
                if (sceneConditionAttrEntity.getType().equals(Constant.powers_1)) {
                    onSelected = sceneConditionAttrEntity.getType().equals(Constant.ON);
                    offSelected = sceneConditionAttrEntity.getType().equals(Constant.OFF);
                    toggleSelected = sceneConditionAttrEntity.getType().equals(Constant.TOGGLE);
                    break;
                }
            }
        }
        switch3Data.add(new ListBottomBean(1, getResources().getString(R.string.scene_turn_on), onSelected, true));
        switch3Data.add(new ListBottomBean(2, getResources().getString(R.string.scene_turn_off), offSelected, true));
        switch3Data.add(new ListBottomBean(3, getResources().getString(R.string.scene_toggle), toggleSelected, true));

        switch3Dialog = new SceneSelectBottomDialog(getResources().getString(R.string.scene_switch), switch3Data);
        switch3Dialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                switch (item.getId()) {
                    case 1:  // 打开
                        sceneDeviceTaskAttrAdapter.getItem(pos).setVal(Constant.ON);
                        break;

                    case 2:  // 关闭
                        sceneDeviceTaskAttrAdapter.getItem(pos).setVal(Constant.OFF);
                        break;

                    case 3:  // 切换
                        sceneDeviceTaskAttrAdapter.getItem(pos).setVal(Constant.TOGGLE);
                        break;
                }
                tvNext.setEnabled(true);
                sceneDeviceTaskAttrAdapter.getItem(pos).setVal_type(Constant.STR);
                sceneDeviceTaskAttrAdapter.notifyItemChanged(pos);
                setNextStatus();
                switch3Dialog.dismiss();
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
        if (position > -1) {
            switchData.get(position).setSelected(true);
        }
        switchDialog.notifyItemChange();
    }

    /**
     * 改变开关(一键)弹窗选项
     *
     * @param position
     */
    private void resetSwitch1Data(int position) {
        for (ListBottomBean listBottomBean : switch1Data) {
            listBottomBean.setSelected(false);
        }
        switch1Data.get(position).setSelected(true);
        switch1Dialog.notifyItemChange();
    }

    /**
     * 改变开关(一键)弹窗选项
     *
     * @param position
     */
    private void resetSwitch2Data(int position) {
        for (ListBottomBean listBottomBean : switch2Data) {
            listBottomBean.setSelected(false);
        }
        switch2Data.get(position).setSelected(true);
        switch2Dialog.notifyItemChange();
    }

    /**
     * 改变开关(一键)弹窗选项
     *
     * @param position
     */
    private void resetSwitch3Data(int position) {
        for (ListBottomBean listBottomBean : switch3Data) {
            listBottomBean.setSelected(false);
        }
        switch3Data.get(position).setSelected(true);
        switch3Dialog.notifyItemChange();
    }


    /**
     * 亮度
     */
    private void initBrightness() {
        brightnessDialog = new SeekBarBottomDialog(1, getResources().getString(R.string.scene_brightness), false);
        brightnessDialog.setClickTodoListener(new SeekBarBottomDialog.OnClickTodoListener() {
            @Override
            public void onTodo(String operator, String operatorName, int val) {
                tvNext.setEnabled(true);
                double min = selectedAttr != null && selectedAttr.getMin() == null ? 0 : selectedAttr.getMin();
                double max = selectedAttr != null && selectedAttr.getMax() == null ? 100 : selectedAttr.getMax();
                sceneDeviceTaskAttrAdapter.getItem(pos).setVal(AttrUtil.getActualVal(min, max, val));
                sceneDeviceTaskAttrAdapter.getItem(pos).setVal_type(Constant.INT_STR);
                sceneDeviceTaskAttrAdapter.notifyItemChanged(pos);
                setNextStatus();
                brightnessDialog.dismiss();
            }
        });
    }

    /**
     * 色温
     */
    private void initColorTemp() {
        colorTempDialog = new SeekBarBottomDialog(2, getResources().getString(R.string.scene_color_temperature), false);
        colorTempDialog.setClickTodoListener(new SeekBarBottomDialog.OnClickTodoListener() {
            @Override
            public void onTodo(String operator, String operatorName, int val) {
                tvNext.setEnabled(true);
                double min = selectedAttr != null && selectedAttr.getMin() == null ? 0 : selectedAttr.getMin();
                double max = selectedAttr != null && selectedAttr.getMax() == null ? 100 : selectedAttr.getMax();
                sceneDeviceTaskAttrAdapter.getItem(pos).setVal(AttrUtil.getActualVal(min, max, val));
                sceneDeviceTaskAttrAdapter.getItem(pos).setVal_type(Constant.INT_STR);
                sceneDeviceTaskAttrAdapter.notifyItemChanged(pos);
                setNextStatus();
                colorTempDialog.dismiss();
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
        if (CollectionUtil.isNotEmpty(attrList)) {
            for (SceneConditionAttrEntity sceneConditionAttrEntity : attrList) {
                if (sceneConditionAttrEntity.getType().equals(Constant.target_state)) {
                    Object val = sceneConditionAttrEntity.getVal();
                    if (val != null) {
                        double douVal = (Double) val;
                        int intVal = (int) douVal;
                        atHomeSelected = intVal == 0;
                        outHomeSelected = intVal == 1;
                        sleepSelected = intVal == 2;
                        guardSelected = intVal == 3;
                    }

                    break;
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
                tvNext.setEnabled(true);
                sceneDeviceTaskAttrAdapter.getItem(pos).setVal((double) item.getId());
                sceneDeviceTaskAttrAdapter.getItem(pos).setVal_type(Constant.INT_STR);
                sceneDeviceTaskAttrAdapter.notifyItemChanged(pos);
                setNextStatus();
                targetStateDialog.dismiss();

            }
        });
    }


    // 下一步按钮样式
    private void setNextStatus() {
        tvNext.setAlpha(tvNext.isEnabled() ? 1 : 0.5f);
    }

    @Override
    public void getDeviceDetailSuccess(DeviceDetailResponseEntity deviceDetailResponseEntity) {
        if (deviceDetailResponseEntity != null) {
            DeviceDetailEntity deviceDetailBean = deviceDetailResponseEntity.getDevice_info();
            List<SceneConditionAttrEntity> attrs = new ArrayList<>();
            if (deviceDetailBean != null) {
                String locationName = "";
                DeviceDetailEntity.AreaAndLocationBean locationBean = deviceDetailBean.getLocation();
                if (locationBean != null) {
                    locationName = locationBean.getName();
                }
                deviceInfo = new SceneDeviceInfoEntity(deviceDetailBean.getName(), locationName, deviceDetailBean.getLogo_url());

                List<SceneConditionAttrEntity> attrsAll = deviceDetailBean.getAttributes();
                if (CollectionUtil.isNotEmpty(attrsAll)) {
                    for (SceneConditionAttrEntity sceneConditionAttrEntity : attrsAll) {
                        String attribute = sceneConditionAttrEntity.getType();
                        if (NeedDealAttrConstant.inContainTaskDelaAttrs(attribute)) {
                            attrs.add(sceneConditionAttrEntity);
                        }
                    }
                }
                // 如果是场景过来需要设置显示的值
                if (CollectionUtil.isNotEmpty(attrs)) {
                    for (SceneConditionAttrEntity attr : attrs) {
                        attr.setVal(null);
                        String attribute = attr.getType();
                        if (attribute != null && (attribute.equals(AttrConstant.COLOR_TEMP) || attribute.equals(AttrConstant.BRIGHTNESS)
                                || attribute.equals(AttrConstant.HUMIDITY) || attribute.equals(AttrConstant.TARGET_POSITION))) {
                            if (attr.getMin() == null) {
                                attr.setMin(0);
                            }
                            if (attr.getMax() == null) {
                                attr.setMax(100);
                            }
                        }
                    }
                    if (CollectionUtil.isNotEmpty(attrList)) {  // 如果是场景过来
                        for (SceneConditionAttrEntity attr : attrs) {
                            for (SceneConditionAttrEntity sceneConditionAttrEntity : attrList) {
                                if (attr.getAid() == sceneConditionAttrEntity.getAid()) {
                                    attr.setVal(sceneConditionAttrEntity.getVal());
                                    attr.setVal_type(sceneConditionAttrEntity.getVal_type());
                                }
                            }
                        }
                    }
                }
                sceneDeviceTaskAttrAdapter.setNewData(attrs);
            }
            tvNext.setVisibility(View.VISIBLE);
            if (CollectionUtil.isNotEmpty(attrs)) {
                llDelay.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void getFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 定时
     */
    private void initTimingDialog() {
        timingBottomDialog = new TimingBottomDialog();
        timingBottomDialog.setTimeSelectListener(new TimingBottomDialog.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(int hour, int minute, int seconds, String timeStr) {
                delaySecondsStr = timeStr;
                tvDelay.setText(String.format(getResources().getString(R.string.scene_delay_after), timeStr));
                delaySeconds = hour * 3600 + minute * 60 + seconds;
                timingBottomDialog.dismiss();
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
                tvNext.setEnabled(true);
                sceneDeviceTaskAttrAdapter.getItem(pos).setVal(colorStr);
                sceneDeviceTaskAttrAdapter.notifyItemChanged(pos);
                setNextStatus();
                mColorPickerDialog.dismiss();
            }
        });
    }

    /**
     * 窗帘状态弹窗
     */
    private void initCurtainStatusDialog() {
        mCurtainStatusData.add(new ListBottomBean(1, UiUtil.getString(R.string.scene_curtain_open), false, true));
        mCurtainStatusData.add(new ListBottomBean(2, UiUtil.getString(R.string.scene_curtain_close), false, true));
        mCurtainStatusData.add(new ListBottomBean(3, UiUtil.getString(R.string.scene_curtain_open_percent), false, true));
        mCurtainStatusDialog = new SceneSelectBottomDialog(UiUtil.getString(R.string.scene_curtain_status), mCurtainStatusData);
        mCurtainStatusDialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                selectedAttr.setType(Constant.target_position);
                selectedAttr.setVal_type("int");
                SceneConditionAttrEntity sceneConditionAttrEntity = sceneDeviceTaskAttrAdapter.getItem(pos);
                double min = selectedAttr != null && selectedAttr.getMin() == null ? 0 : selectedAttr.getMin();
                double max = selectedAttr != null && selectedAttr.getMax() == null ? 100 : selectedAttr.getMax();
                switch (item.getId()) {
                    case 1: // 打开
                        sceneConditionAttrEntity.setVal(max);
                        sceneDeviceTaskAttrAdapter.notifyItemChanged(pos);
                        tvNext.setEnabled(true);
                        setNextStatus();
                        break;

                    case 2:  //关闭
                        sceneConditionAttrEntity.setVal(min);
                        sceneDeviceTaskAttrAdapter.notifyItemChanged(pos);
                        tvNext.setEnabled(true);
                        setNextStatus();
                        break;

                    case 3: // 百分比
                        Bundle bundleCurtain = new Bundle();
                        bundleCurtain.putInt("val", AttrUtil.getPercentVal(min, max, mCurtainVal));
                        bundleCurtain.putInt("minVal", (int) min);
                        bundleCurtain.putInt("maxVal", (int) max);
                        mCurtainDialog.setArguments(bundleCurtain);
                        mCurtainDialog.show(SceneDeviceTaskAttrActivity.this);
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
        mCurtainDialog = new SeekBarBottomDialog(5, UiUtil.getString(R.string.scene_curtain_position), false);
        mCurtainDialog.setClickTodoListener(new SeekBarBottomDialog.OnClickTodoListener() {
            @Override
            public void onTodo(String operator, String operatorName, int val) {
                SceneConditionAttrEntity sceneConditionAttrEntity = sceneDeviceTaskAttrAdapter.getItem(pos);
                double min = selectedAttr != null && selectedAttr.getMin() == null ? 0 : selectedAttr.getMin();
                double max = selectedAttr != null && selectedAttr.getMax() == null ? 100 : selectedAttr.getMax();
                sceneConditionAttrEntity.setVal(AttrUtil.getActualVal(min, max, val));
                sceneDeviceTaskAttrAdapter.notifyItemChanged(pos);
                tvNext.setEnabled(true);
                setNextStatus();
                mCurtainDialog.dismiss();
            }
        });
    }

    @OnClick({R.id.llDelay, R.id.tvNext})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.llDelay:  // 延时
                if (timingBottomDialog != null && !timingBottomDialog.isShowing()) {
                    if (!TextUtils.isEmpty(delaySecondsStr)) {
                        timingBottomDialog.setSelectTime(delaySecondsStr);
                    }
                    timingBottomDialog.show(this);
                }
                break;

            case R.id.tvNext:  // 下一步

                if (sceneTaskEntity == null) {
                    sceneTaskEntity = new SceneTaskEntity(1);
                }
                sceneTaskEntity.setDevice_id(deviceId);
                sceneTaskEntity.setDevice_info(deviceInfo);
                sceneTaskEntity.setDelay_seconds(delaySeconds);
                List<SceneConditionAttrEntity> attributes = new ArrayList<>();
                for (SceneConditionAttrEntity sceneConditionAttrEntity : sceneDeviceTaskAttrAdapter.getData()) {
                    if (sceneConditionAttrEntity.getVal() != null) {
                        attributes.add(sceneConditionAttrEntity);
                    }
                }
                sceneTaskEntity.setAttributes(attributes);
                if (hasAttr) {
                    EventBus.getDefault().post(sceneTaskEntity);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(IntentConstant.TASK_BEAN, sceneTaskEntity);
                    switchToActivity(SceneDetailActivity.class, bundle);
                }

                finish();
                break;
        }
    }

}