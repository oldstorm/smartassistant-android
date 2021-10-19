package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.main.framework.baseutil.DateUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SceneDeviceTaskAttrContract;
import com.yctc.zhiting.activity.contract.TaskDeviceControlContract;
import com.yctc.zhiting.activity.presenter.SceneDeviceTaskAttrPresenter;
import com.yctc.zhiting.activity.presenter.TaskDeviceControlPresenter;
import com.yctc.zhiting.adapter.SceneDeviceTaskAttrAdapter;
import com.yctc.zhiting.bean.ListBottomBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.DBConfig;
import com.yctc.zhiting.dialog.SceneSelectBottomDialog;
import com.yctc.zhiting.dialog.SeekBarBottomDialog;
import com.yctc.zhiting.dialog.TimingBottomDialog;
import com.yctc.zhiting.entity.DeviceDetailEntity;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.SceneTaskBean;
import com.yctc.zhiting.entity.scene.CreateScenePost;
import com.yctc.zhiting.entity.scene.SceneConditionAttrEntity;
import com.yctc.zhiting.entity.scene.SceneDeviceInfoEntity;
import com.yctc.zhiting.entity.scene.SceneDeviceStatusControlBean;
import com.yctc.zhiting.entity.scene.SceneTaskEntity;
import com.yctc.zhiting.utils.AttrUtil;
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
    private List<ListBottomBean> switchData = new ArrayList<>();

    private SeekBarBottomDialog brightnessDialog; //温度
    private SeekBarBottomDialog colorTempDialog; //色温
    private TimingBottomDialog timingBottomDialog;  // 时间筛选

    private SceneDeviceTaskAttrAdapter sceneDeviceTaskAttrAdapter;

    private SceneTaskEntity sceneTaskEntity; // 从场景带过来的任务信息
    private List<SceneConditionAttrEntity> attrList; // 场景过来的数据

    private int deviceId; // 设备id
    private String deviceName;  // 设备名称
    private SceneDeviceInfoEntity deviceInfo; // 设备信息

    private SceneConditionAttrEntity selectedAttr; // 选择的属性条件
    private int pos; // 属性的位置
    private int delaySeconds; // 延迟秒数
    private String delaySecondsStr="";

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
        if (sceneTaskEntity!=null){
            attrList = sceneTaskEntity.getAttributes();
            delaySeconds = sceneTaskEntity.getDelay_seconds();
        }
        tvNext.setEnabled(sceneTaskEntity != null ? true : false);

        if (delaySeconds>0){
            delaySecondsStr = TimeUtil.seconds2String(delaySeconds);
            tvDelay.setText(delaySecondsStr);
        }
        setNextStatus();
        initSwitchDialog();
        initBrightness();
        initColorTemp();
        initTimingDialog();
        setTitleCenter(deviceName);
        mPresenter.getDeviceDetail(deviceId);
    }

    /**
     * 初始化列表
     */
    private void initRv(){
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
                    switch (selectedAttr.getAttribute()){
                        case "power":
                            if (object!=null) {
                                String val = (String) object;
                                if (val.equals(Constant.ON)) {
                                    resetSwitchData(0);
                                } else if (val.equals(Constant.OFF)) {
                                    resetSwitchData(1);
                                } else if (val.equals(Constant.TOGGLE)) {
                                    resetSwitchData(2);
                                }
                            }
                            switchDialog.show(SceneDeviceTaskAttrActivity.this);
                            break;

                        case "color_temp":
                            Bundle bundle = new Bundle();
                            if (object!=null) {
                                double val = 0;
                                val = (Double)object;
                                bundle.putInt("val", AttrUtil.getPercentVal(selectedAttr.getMin(), selectedAttr.getMax(), val));
                                colorTempDialog.setArguments(bundle);
                            }
                            colorTempDialog.show(SceneDeviceTaskAttrActivity.this);
                            break;

                        case "brightness":
                            Bundle bundleB = new Bundle();
                            if (object!=null) {
                                double val = 0;
                                val = (Double)object;
                                bundleB.putInt("val", AttrUtil.getPercentVal(selectedAttr.getMin(), selectedAttr.getMax(), val));
                                brightnessDialog.setArguments(bundleB);
                            }
                            brightnessDialog.show(SceneDeviceTaskAttrActivity.this);
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
        boolean toggleSelected = false;

        if (CollectionUtil.isNotEmpty(attrList)){
            for (SceneConditionAttrEntity sceneConditionAttrEntity : attrList){
                if (sceneConditionAttrEntity.getAttribute().equals(Constant.POWER)){
                    onSelected = sceneConditionAttrEntity.getAttribute().equals(Constant.ON);
                    offSelected = sceneConditionAttrEntity.getAttribute().equals(Constant.OFF);
                    toggleSelected = sceneConditionAttrEntity.getAttribute().equals(Constant.TOGGLE);
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
                switch (item.getId()){
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
     * 改变开关弹窗选项
     * @param position
     */
    private void resetSwitchData(int position){
        for (ListBottomBean listBottomBean : switchData){
            listBottomBean.setSelected(false);
        }
        switchData.get(position).setSelected(true);
        switchDialog.notifyItemChange();
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
                sceneDeviceTaskAttrAdapter.getItem(pos).setVal(AttrUtil.getActualVal(selectedAttr.getMin(), selectedAttr.getMax(), val));
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
    private void initColorTemp(){
        colorTempDialog = new SeekBarBottomDialog(2, getResources().getString(R.string.scene_color_temperature), false);
        colorTempDialog.setClickTodoListener(new SeekBarBottomDialog.OnClickTodoListener() {
            @Override
            public void onTodo(String operator, String operatorName, int val) {
                tvNext.setEnabled(true);
                sceneDeviceTaskAttrAdapter.getItem(pos).setVal(AttrUtil.getActualVal(selectedAttr.getMin(), selectedAttr.getMax(), val));
                sceneDeviceTaskAttrAdapter.getItem(pos).setVal_type(Constant.INT_STR);
                sceneDeviceTaskAttrAdapter.notifyItemChanged(pos);
                setNextStatus();
                colorTempDialog.dismiss();
            }
        });
    }

    // 下一步按钮样式
    private void setNextStatus(){
        tvNext.setAlpha( tvNext.isEnabled() ? 1 : 0.5f);
    }

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
                deviceInfo = new SceneDeviceInfoEntity(deviceDetailBean.getName(), locationName, deviceDetailBean.getLogo_url());
                List<SceneConditionAttrEntity> attrs = deviceDetailBean.getAttributes();
                // 如果是场景过来需要设置显示的值
                if (CollectionUtil.isNotEmpty(attrs)){
                    for (SceneConditionAttrEntity attr : attrs) {
                        attr.setVal(null);
                    }
                    if (CollectionUtil.isNotEmpty(attrList)) {  // 如果是场景过来
                        for (SceneConditionAttrEntity attr : attrs) {
                            for (SceneConditionAttrEntity sceneConditionAttrEntity : attrList) {
                                if (attr.getAttribute().equals(sceneConditionAttrEntity.getAttribute())) {
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
            llDelay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 定时
     */
    private void initTimingDialog(){
        timingBottomDialog = new TimingBottomDialog();
        timingBottomDialog.setTimeSelectListener(new TimingBottomDialog.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(int hour, int minute, int seconds, String timeStr) {
                delaySecondsStr = timeStr;
                tvDelay.setText(String.format(getResources().getString(R.string.scene_delay_after), timeStr));
                delaySeconds = hour*3600 + minute*60 + seconds;
                timingBottomDialog.dismiss();
            }
        });
    }


    @OnClick({R.id.llDelay, R.id.tvNext})
    void onClick(View view){
        switch (view.getId()){
            case R.id.llDelay:  // 延时
                if (timingBottomDialog!=null && !timingBottomDialog.isShowing()){
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
                if (sceneTaskEntity.getId()==null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(IntentConstant.TASK_BEAN, sceneTaskEntity);
                    switchToActivity(SceneDetailActivity.class, bundle);

                }else {
                    EventBus.getDefault().post(sceneTaskEntity);
                }

                finish();
                break;
        }
    }

}