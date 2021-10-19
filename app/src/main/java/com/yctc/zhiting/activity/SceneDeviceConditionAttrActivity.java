package com.yctc.zhiting.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SceneDeviceConditionAttrContract;
import com.yctc.zhiting.activity.presenter.SceneDeviceDeviceConditionPresenter;
import com.yctc.zhiting.adapter.SceneDeviceConditionAttrAdapter;
import com.yctc.zhiting.bean.ListBottomBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.SceneSelectBottomDialog;
import com.yctc.zhiting.dialog.SeekBarBottomDialog;
import com.yctc.zhiting.entity.DeviceDetailEntity;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.scene.ConditionBean;
import com.yctc.zhiting.entity.scene.CreateScenePost;
import com.yctc.zhiting.entity.scene.SceneConditionAttrEntity;
import com.yctc.zhiting.entity.scene.SceneConditionEntity;
import com.yctc.zhiting.entity.scene.SceneDeviceInfoEntity;
import com.yctc.zhiting.utils.AttrUtil;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;

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
    private List<ListBottomBean> switchData = new ArrayList<>();
    private SeekBarBottomDialog brightnessDialog; //温度
    private SeekBarBottomDialog colorTempDialog; //色温
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
        if (sceneConditionEntity!=null){
            sceneConditionAttrEntity = sceneConditionEntity.getCondition_attr();
        }
        initSwitchDialog();
        initBrightness();
        initColorTemp();
        setTitleCenter(deviceName);
        mPresenter.getDeviceDetail(deviceId);
    }



    /**
     * 初始化列表
     */
    private void initRv(){
        rvFunction.setLayoutManager(new LinearLayoutManager(this));
        sceneDeviceConditionAttrAdapter = new SceneDeviceConditionAttrAdapter();
        rvFunction.setAdapter(sceneDeviceConditionAttrAdapter);
        sceneDeviceConditionAttrAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                selectedAttr = sceneDeviceConditionAttrAdapter.getItem(position);

                // 开关:power; 色温：color_temp；亮度:brightness
                try {
                    switch (selectedAttr.getAttribute()){
                        case "power":
                            switchDialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case "color_temp":
                            Bundle bundle = new Bundle();
                            if (selectedAttr!=null){
                                Object object = selectedAttr.getVal();
                                if (object!=null) {
                                    double val = 0;
                                    val = (Double)object;
                                    bundle.putInt("val", val>0 ? AttrUtil.getPercentVal(selectedAttr.getMin(), selectedAttr.getMax(), val):0);
                                }


                            }
                            if (sceneConditionEntity!=null){
                                bundle.putString("operator", sceneConditionEntity.getOperator());
                            }
                            colorTempDialog.setArguments(bundle);
                            colorTempDialog.show(SceneDeviceConditionAttrActivity.this);
                            break;

                        case "brightness":
                            Bundle bundleB = new Bundle();
                            if (selectedAttr!=null){
                                Object object = selectedAttr.getVal();
                                if (object!=null) {
                                    double val = 0;
                                    val = (Double)object;
                                    bundleB.putInt("val", val>0 ? AttrUtil.getPercentVal(selectedAttr.getMin(), selectedAttr.getMax(), val):0);
                                }

                            }
                            if (sceneConditionEntity!=null){
                                bundleB.putString("operator", sceneConditionEntity.getOperator());
                            }
                            brightnessDialog.setArguments(bundleB);
                            brightnessDialog.show(SceneDeviceConditionAttrActivity.this);
                            break;
                    }
                }catch (ClassCastException e){
                    e.printStackTrace();
                }

            }
        });
    }


    /**
     * 开关
     */
    private void initSwitchDialog(){
        boolean onSelected = false;
        boolean offSelected = false;
        if (sceneConditionAttrEntity!=null){
            if (sceneConditionAttrEntity.getAttribute().equals(Constant.POWER)){
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
                selectedAttr.setAttribute(Constant.POWER);
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
     * 亮度
     */
    private void initBrightness(){
        brightnessDialog = new SeekBarBottomDialog(1, getResources().getString(R.string.scene_brightness), true);
        brightnessDialog.setClickTodoListener(new SeekBarBottomDialog.OnClickTodoListener() {
            @Override
            public void onTodo(String operator, String operatorName, int val) {
                selectedAttr.setAttribute(Constant.brightness);
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
    private void initColorTemp(){
        colorTempDialog = new SeekBarBottomDialog(2, getResources().getString(R.string.scene_color_temperature), true);
        colorTempDialog.setClickTodoListener(new SeekBarBottomDialog.OnClickTodoListener() {
            @Override
            public void onTodo(String operator, String operatorName, int val) {
                selectedAttr.setAttribute(Constant.color_temp);
                op = operator;
                selectedAttr.setVal(AttrUtil.getActualVal(selectedAttr.getMin(), selectedAttr.getMax(), val));
                selectedAttr.setVal_type("int");
                toSceneDetail();
            }
        });
    }

    /**
     * 回到场景详情
     */
    private void toSceneDetail(){
        if (sceneConditionEntity!=null){
            sceneConditionEntity.setCondition_attr(selectedAttr);
            sceneConditionEntity.setOperator(op);
            sceneConditionEntity.setDevice_info(deviceInfo);
            EventBus.getDefault().post(sceneConditionEntity);
        }else {
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
     * @param deviceDetailResponseEntity
     */
    @Override
    public void getDeviceDetailSuccess(DeviceDetailResponseEntity deviceDetailResponseEntity) {
        if (deviceDetailResponseEntity!=null) {
            DeviceDetailEntity deviceDetailBean = deviceDetailResponseEntity.getDevice_info();
            if (deviceDetailBean != null) {
                String locationName = "";
                DeviceDetailEntity.AreaAndLocationBean locationBean = deviceDetailBean.getLocation();
                if (locationBean!=null){
                    locationName = locationBean.getName();
                }
                List<SceneConditionAttrEntity> attrs = deviceDetailBean.getAttributes();
                if (CollectionUtil.isNotEmpty(attrs)){
                    if (sceneConditionAttrEntity!=null){
                        for (SceneConditionAttrEntity attr : attrs){
                            if (attr.getAttribute().equals(sceneConditionAttrEntity.getAttribute())) {
                                attr.setVal(sceneConditionAttrEntity.getVal());
                                attr.setVal_type(sceneConditionAttrEntity.getVal_type());
                            }else {
                                attr.setVal(null);
                            }
                        }
                    }else {
                        for (SceneConditionAttrEntity attr : attrs){
                            attr.setVal(null);
                        }
                    }
                }
                deviceInfo = new SceneDeviceInfoEntity(deviceDetailBean.getName(), locationName, deviceDetailBean.getLogo_url());
                sceneDeviceConditionAttrAdapter.setNewData(deviceDetailBean.getAttributes());
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
        ToastUtil.show(msg);
    }
}