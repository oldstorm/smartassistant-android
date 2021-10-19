
package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SceneDeviceStatusControlContract;
import com.yctc.zhiting.activity.presenter.SceneDeviceStatusControlPresenter;
import com.yctc.zhiting.adapter.SceneDeviceStatusControlAdapter;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.bean.ListBottomBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.ConditionDialog;
import com.yctc.zhiting.dialog.SceneSelectBottomDialog;
import com.yctc.zhiting.dialog.SeekBarBottomDialog;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.scene.ConditionBean;
import com.yctc.zhiting.entity.scene.ConditionSelectBean;
import com.yctc.zhiting.entity.scene.CreateScenePost;
import com.yctc.zhiting.entity.scene.SceneDeviceStatusControlBean;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 创建场景选择设备状态变化时
 */
public class SceneDeviceStatusControlActivity extends MVPBaseActivity<SceneDeviceStatusControlContract.View, SceneDeviceStatusControlPresenter> implements SceneDeviceStatusControlContract.View {


    @BindView(R.id.rvFunction)
    RecyclerView rvFunction;

    private SceneDeviceStatusControlAdapter sceneDeviceStatusControlAdapter;

    private SceneSelectBottomDialog switchDialog; // 开关
    private List<ListBottomBean> switchData = new ArrayList<>();

    private SeekBarBottomDialog brightnessDialog; //温度
    private SeekBarBottomDialog colorTempDialog; //色温

    /**
     * 0. 设备列表
     * 1. 创建场景
     */
    private int from;
    private int deviceId; // 场景时才有，设备id
    private String deviceName; // 场景时才有，设备名称
    private String deviceType; // 设备类型
    private String logoUrl; // 设备logo

    /**
     * 1.开关 2.亮度 3.色温
     */
    private int type;

    private int conditionId;
    private int sceneConditionId;
    private int sceneId;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_device_status_control;

    }

    @Override
    protected void initUI() {
        super.initUI();
        initSwitchDialog();
        initBrightness();
        initColorTemp();
        rvFunction.setLayoutManager(new LinearLayoutManager(this));
        sceneDeviceStatusControlAdapter = new SceneDeviceStatusControlAdapter();
        rvFunction.setAdapter(sceneDeviceStatusControlAdapter);
        sceneDeviceStatusControlAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DeviceDetailBean.DeviceInfoBean.ActionsBean sceneDeviceStatusControlBean = sceneDeviceStatusControlAdapter.getItem(position);
                switch (sceneDeviceStatusControlBean.getAction()){
                    case "switch":  // 开关
                        if (switchDialog!=null && !switchDialog.isShowing()){
                            switchDialog.show(SceneDeviceStatusControlActivity.this);
                        }
                        break;
                    case "set_bright":  // 亮度
                        if (brightnessDialog!=null && !brightnessDialog.isShowing()) {
                            brightnessDialog.show(SceneDeviceStatusControlActivity.this);
                        }
                        break;

                    case "set_color_temp":  // 色温
                        if (colorTempDialog!=null && !colorTempDialog.isShowing()) {
                            colorTempDialog.show(SceneDeviceStatusControlActivity.this);
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
        from = getIntent().getIntExtra(IntentConstant.FROM, 0);

        conditionId = getIntent().getIntExtra(IntentConstant.CONDITION_ID, 0);
        sceneConditionId = getIntent().getIntExtra(IntentConstant.SCENE_CONDITION_ID, 0);
        sceneId = getIntent().getIntExtra(IntentConstant.SCENE_ID, 0);


        setTitleCenter(deviceName);
        mPresenter.getDeviceDetail(deviceId);
    }


    /**
     * 开关
     */
    private void initSwitchDialog(){
        switchData.add(new ListBottomBean(1, getResources().getString(R.string.scene_turn_on), false, true));
        switchData.add(new ListBottomBean(2, getResources().getString(R.string.scene_turn_off), false, true));
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
                }
                ConditionBean conditionBean = new ConditionBean(name, 2);
                conditionBean.setDeviceId(deviceId);
                conditionBean.setDeviceName(deviceName);
                conditionBean.setDeviceType(deviceType);
                conditionBean.setLogoUrl(logoUrl);
                CreateScenePost.SceneConditionsBean.ConditionItemBean conditionItemBean = new CreateScenePost.SceneConditionsBean.ConditionItemBean(Constant.EQUAL, Constant.SWITCH, switchOperate, Constant.POWER);
                if (sceneId>0){
                    conditionBean.setScene_id(sceneId);
                }
                if (conditionId>0){
                    conditionItemBean.setId(conditionId);
                }
                if (sceneConditionId>0){
                    conditionItemBean.setScene_condition_id(sceneConditionId);
                    conditionBean.setId(sceneConditionId);
                }
                conditionBean.setCondition_item(conditionItemBean);
                switchDialog.dismiss();
                toCreateSceneActivity(conditionBean);

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
                ConditionBean conditionBean = new ConditionBean(getResources().getString(R.string.scene_brightness) +operatorName+val+"%", 2);
                conditionBean.setDeviceId(deviceId);
                conditionBean.setDeviceName(deviceName);
                conditionBean.setDeviceType(deviceType);
                conditionBean.setLogoUrl(logoUrl);
                CreateScenePost.SceneConditionsBean.ConditionItemBean conditionItemBean = new CreateScenePost.SceneConditionsBean.ConditionItemBean(operator, Constant.set_bright, val+"", Constant.brightness);
                if (sceneId>0){
                    conditionBean.setScene_id(sceneId);
                }
                if (conditionId>0){
                    conditionItemBean.setId(conditionId);
                }
                if (sceneConditionId>0){
                    conditionItemBean.setScene_condition_id(sceneConditionId);
                    conditionBean.setId(sceneConditionId);
                }
                conditionBean.setCondition_item(conditionItemBean);
                brightnessDialog.dismiss();
                toCreateSceneActivity(conditionBean);

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
                ConditionBean conditionBean = new ConditionBean(getResources().getString(R.string.scene_color_temperature) +operatorName+val+"%", 2);
                conditionBean.setDeviceId(deviceId);
                conditionBean.setDeviceName(deviceName);
                conditionBean.setDeviceType(deviceType);
                conditionBean.setLogoUrl(logoUrl);
                CreateScenePost.SceneConditionsBean.ConditionItemBean conditionItemBean = new CreateScenePost.SceneConditionsBean.ConditionItemBean(operator, Constant.set_color_temp, val+"", Constant.color_temp);
                if (sceneId>0){
                    conditionBean.setScene_id(sceneId);
                }
                if (conditionId>0){
                    conditionItemBean.setId(conditionId);
                }
                if (sceneConditionId>0){
                    conditionItemBean.setScene_condition_id(sceneConditionId);
                    conditionBean.setId(sceneConditionId);
                }
                conditionBean.setCondition_item(conditionItemBean);
                colorTempDialog.dismiss();
                toCreateSceneActivity(conditionBean);
            }
        });
    }

    /**
     * 去到创建场景
     * @param conditionBean
     */
    private void toCreateSceneActivity(ConditionBean conditionBean){
        if (from == 0) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(IntentConstant.BEAN, conditionBean);
            switchToActivity(CreateSceneActivity.class, bundle);
        }else {
            EventBus.getDefault().post(conditionBean);
        }
        finish();
    }

    /**
     * 设备详情
     * @param deviceDetailBean
     */
    @Override
    public void getDeviceDetailSuccess(DeviceDetailBean deviceDetailBean) {
        if (deviceDetailBean!=null){
            DeviceDetailBean.DeviceInfoBean deviceInfoBean = deviceDetailBean.getDevice_info();
            if (deviceInfoBean!=null){
                List<DeviceDetailBean.DeviceInfoBean.ActionsBean> actions = deviceInfoBean.getActions();
                if (CollectionUtil.isNotEmpty(actions)){
                    sceneDeviceStatusControlAdapter.setNewData(actions);
                }
            }
        }
    }

    @Override
    public void getFail(int errorCode, String msg) {

    }
}