package com.yctc.zhiting.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.KeyboardHelper;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.CreateSceneContract;
import com.yctc.zhiting.activity.presenter.CreateScenePresenter;
import com.yctc.zhiting.adapter.ConditionAdapter;
import com.yctc.zhiting.adapter.SceneTaskAdapter;
import com.yctc.zhiting.bean.ListBottomBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.dialog.ConditionDialog;
import com.yctc.zhiting.dialog.SceneSelectBottomDialog;
import com.yctc.zhiting.dialog.TimingBottomDialog;
import com.yctc.zhiting.entity.scene.SceneDetailBean;
import com.yctc.zhiting.entity.SceneTaskBean;
import com.yctc.zhiting.entity.scene.ConditionBean;
import com.yctc.zhiting.entity.scene.ConditionSelectBean;
import com.yctc.zhiting.entity.scene.CreateScenePost;
import com.yctc.zhiting.event.SceneEffectTimeEvent;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.StringUtil;
import com.yctc.zhiting.utils.TimeUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 创建场景
 * 修改场景
 */
public class CreateSceneActivity extends MVPBaseActivity<CreateSceneContract.View, CreateScenePresenter> implements  CreateSceneContract.View {

    @BindView(R.id.llCondition)
    LinearLayout llCondition;
    @BindView(R.id.clConditionData)
    ConstraintLayout clConditionData;
    @BindView(R.id.tvConditionType)
    TextView tvConditionType;
    @BindView(R.id.ivAdd)
    ImageView ivAdd;
    @BindView(R.id.rvCondition)
    RecyclerView rvCondition;
    @BindView(R.id.llTask)
    LinearLayout llTask;
    @BindView(R.id.tvTask)
    TextView tvTask;
    @BindView(R.id.clTimePeriod)
    ConstraintLayout clTimePeriod;
    @BindView(R.id.tvDay)
    TextView tvDay;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.rvTask)
    RecyclerView rvTask;
    @BindView(R.id.clTaskData)
    ConstraintLayout clTaskData;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.tvFinish)
    TextView tvFinish;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvRemove)
    TextView tvRemove;
    @BindView(R.id.llFinish)
    LinearLayout llFinish;
    @BindView(R.id.rbDel)
    ProgressBar rbDel;
    @BindView(R.id.rbFinish)
    ProgressBar rbFinish;

    private ConditionDialog conditionDialog; // 触发条件筛选
    List<ConditionSelectBean> conditionSelectData = new ArrayList<>();

    private ConditionDialog taskDialog;  // 任务类型
    List<ConditionSelectBean> taskData = new ArrayList<>();

    private SceneSelectBottomDialog controlSceneDialog; // 控制场景类型
    private List<ListBottomBean> controlSceneData = new ArrayList<>();

    private SceneSelectBottomDialog conditionTypeDialog; // 条件类型
    private List<ListBottomBean> conditionTypeData = new ArrayList<>();
    
    private TimingBottomDialog timingBottomDialog;  // 时间筛选

    private ConditionAdapter conditionAdapter;  // 条件适配器
    private SceneTaskAdapter sceneTaskAdapter; // 任务适配器

    private List<ConditionBean> conditionData = new ArrayList<>(); // 条件列表
    private List<SceneTaskBean> performData = new ArrayList<>(); // 任务列表

    private List<Integer> del_condition_ids;  // 要移除的触发条件(修改场景才有)
    private List<Integer> del_task_ids;  // 要移除的任务(修改场景才有)

    private boolean updateTime = false;
    private boolean auto_run; // true 为自动，false为手动


    private boolean mContinue; // 继续添加
    private boolean allCondition; // 是否是全部条件
    private boolean allConditionSubmit; // 是否是全部条件  提交数据用
    private boolean hasTiming; // 是否已经有定时

    private int time_period = 1;  //  生效时间类型，全天为1，时间段为2,auto_run为false可不传
    private long effect_start_time; // 生效开始时间,time_period为1时应传某天0点;auto_run为false可不传
    private long effect_end_time;  // 生效结束时间,time_period为1时应传某天24点;auto_run为false可不传
    private int repeat_type = 1;  // 重复执行的类型；1：每天; 2:工作日 ；3：自定义;auto_run为false可不传
    private String repeat_date = "1234567"; // 只能传长度为7包含1-7的数字；"1122"视为不合法传参;repeat_type为1时:"1234567"; 2:12345; 3：任意

    private int conditionPos;  // 点击条件列表的位置
    private int taskPos; // 点击任务列表的位置

    private int sceneId;  // 场景id， 大于0就是场景详情

    private String sceneName;  // 场景名称
    private int repeat_typeConst; // 重复类型
    private String repeat_dateConst; // 重复星期

    private int conditionSize;
    private int taskSize;

    private boolean isDel; // 是否删除操作
    private boolean updateAutoRun; // 获取修改场景详情时 是否自动执行

    private boolean taskChange; // 修改场景，判断设备是否改过
    private boolean conditionChange; // 修改场景，判断条件是否改过

    private boolean hasDelP;

    private CenterAlertDialog delDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_scene;
    }


    @Override
    protected void initUI() {
        super.initUI();
        llTask.setEnabled(false);
        tvTask.setEnabled(false);
        effect_start_time = TimeUtil.string2Stamp(TimeUtil.getToday() + " 00:00:00")/1000;
        effect_end_time = TimeUtil.string2Stamp(TimeUtil.getToday() + " 23:59:59")/1000;


        initTaskDialog();
        initControlSceneDialog();
        initConditionTypeDialog();
        initTimingDialog();
        initRvCondition();
        initRvTask();
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void initData() {
        super.initData();
        sceneId = getIntent().getIntExtra(IntentConstant.ID, 0);
        hasDelP = getIntent().getBooleanExtra(IntentConstant.REMOVE_SCENE, false);
        tvTitle.setText(sceneId > 0 ? getResources().getString(R.string.scene_modify) : getResources().getString(R.string.scene_create));
        initConditionDialog();
        if (sceneId>0){
            tvRemove.setVisibility(hasDelP ? View.VISIBLE : View.GONE);
            del_condition_ids = new ArrayList<>();
            del_task_ids = new ArrayList<>();
            mPresenter.getDetail(sceneId);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 条件
        ConditionBean conditionBean = (ConditionBean) intent.getSerializableExtra(IntentConstant.BEAN);
        if (conditionBean!=null) {
            conditionData.add(conditionBean);
            conditionAdapter.notifyDataSetChanged();
            auto_run = true;
            conditionSelectData.get(0).setEnabled(false);
            setLLAddConditionVisible();
        }

        // 任务（设备）
        SceneTaskBean sceneTaskBean = (SceneTaskBean) intent.getSerializableExtra(IntentConstant.TASK_BEAN);
        if (sceneTaskBean!=null){
            performData.add(sceneTaskBean);
            sceneTaskAdapter.notifyDataSetChanged();
            setLLTaskVisible();
        }
        // 任务列表（场景）
        List<SceneTaskBean> sceneTaskBeans = (List<SceneTaskBean>) intent.getSerializableExtra(IntentConstant.TASK_LIST);
        if (CollectionUtil.isNotEmpty(sceneTaskBeans)){
            performData.addAll(sceneTaskBeans);
            sceneTaskAdapter.notifyDataSetChanged();
            setLLTaskVisible();
        }
//        setFinishEnable();
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    /**
     * 触发条件筛选
     */
    private void initConditionDialog(){
        conditionSelectData.add(new ConditionSelectBean(R.drawable.icon_scene_manual, getResources().getString(R.string.scene_manual_perform),  getResources().getString(R.string.scene_click_perform), sceneId<=0));
        conditionSelectData.add(new ConditionSelectBean(R.drawable.icon_scene_timing, getResources().getString(R.string.scene_timing), getResources().getString(R.string.scene_timing_example), true));
        conditionSelectData.add(new ConditionSelectBean(R.drawable.icon_device_status, getResources().getString(R.string.scene_device_status_change), getResources().getString(R.string.scene_device_status_change_example), true));
        conditionDialog = new ConditionDialog(conditionSelectData, getResources().getString(R.string.scene_add_condition));
        conditionDialog.setItemListener(new ConditionDialog.OnItemListener() {
            @Override
            public void onItem(ConditionSelectBean conditionSelectBean, int pos) {
                switch (pos){
                    case 0:  // 手动执行
                        setIvAddEnable(false);
                        conditionData.add(new ConditionBean(getResources().getString(R.string.scene_manual_click_perform), 0));
                        conditionAdapter.notifyDataSetChanged();
//                        setFinishEnable();
                        break;

                    case 1:  // 定时
//                        timingBottomDialog.setSelectedItemIndex(0, 0, 0);
                        auto_run = true;
                        updateTime = false;
                        timingBottomDialog.setSelectTime("00:00:00");
                        timingBottomDialog.show(CreateSceneActivity.this);
                        break;

                    case 2:  // 设备状态变化时
                        auto_run = true;
                        conditionSelectData.get(0).setEnabled(false);
                        conditionDialog.notifyItemChange();
                        Bundle bundle = new Bundle();
                        bundle.putInt(IntentConstant.FROM, 1);
                        bundle.putString(IntentConstant.TITLE, getResources().getString(R.string.scene_device_status_change));
                        switchToActivity(SceneDeviceActivity.class, bundle);
                        break;
                }
                setLLAddConditionVisible();
                conditionDialog.dismiss();
            }
        });
    }

    /**
     * 执行任务筛选
     */
    private void initTaskDialog(){
        taskData.add(new ConditionSelectBean(R.drawable.icon_scene_device, getResources().getString(R.string.scene_inteeligent_device),  getResources().getString(R.string.scene_inteeligent_device_example), true));
        taskData.add(new ConditionSelectBean(R.drawable.icon_scene, getResources().getString(R.string.scene_control_scene), getResources().getString(R.string.scene_control_scene_example), true));
        taskDialog = new ConditionDialog(taskData, getResources().getString(R.string.scene_add_task));
        taskDialog.setItemListener(new ConditionDialog.OnItemListener() {
            @Override
            public void onItem(ConditionSelectBean conditionSelectBean, int pos) {
                switch (pos){
                    case 0:  // 智能设备
                        Bundle bundle = new Bundle();
                        bundle.putString(IntentConstant.TITLE, getResources().getString(R.string.scene_device_control));
                        bundle.putInt(IntentConstant.FROM, 2);
                        switchToActivity(SceneDeviceActivity.class, bundle);
                        break;

                    case 1:  // 控制场景
                        if (controlSceneDialog!=null && !controlSceneDialog.isShowing()){
                            controlSceneDialog.setAllNotSelected();
                            controlSceneDialog.show(CreateSceneActivity.this);
                        }
                        break;
                }
                taskDialog.dismiss();
            }
        });
    }

    /**
     * 控制场景筛选
     */
    private void initControlSceneDialog(){
        controlSceneData.add(new ListBottomBean(2, getResources().getString(R.string.scene_perform_certain_scene), false, true));
        controlSceneData.add(new ListBottomBean(3, getResources().getString(R.string.scene_perform_automatic_open), false, true));
        controlSceneData.add(new ListBottomBean(4, getResources().getString(R.string.scene_perform_automatic_close), false, true));
        controlSceneDialog = new SceneSelectBottomDialog(getResources().getString(R.string.scene_control_scene), controlSceneData);
        controlSceneDialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                Bundle bundle = new Bundle();
                bundle.putInt(IntentConstant.TYPE, item.getId());
                bundle.putString(IntentConstant.TITLE, item.getName());
                switchToActivity(SceneListActivity.class, bundle);
                controlSceneDialog.dismiss();
            }
        });
    }

    /**
     * 条件类型筛选
     */
    private void initConditionTypeDialog(){
        conditionTypeData.add(new ListBottomBean(1, getResources().getString(R.string.scene_meet_with_all_condition), true, true));
        conditionTypeData.add(new ListBottomBean(2, getResources().getString(R.string.scene_meet_with_certain_condition), false, true));
        conditionTypeDialog = new SceneSelectBottomDialog(getResources().getString(R.string.scene_condition_relativation), conditionTypeData);
        conditionTypeDialog.setOnSelectedListener(new SceneSelectBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(ListBottomBean item) {
                    switch (item.getId()) {
                        case 1:  // 满足所有条件
                            allCondition = true;
                            allConditionSubmit = true;
                            if (hasTiming) {
                                conditionSelectData.get(1).setEnabled(false);
                            }
                            conditionDialog.notifyItemChange();
                            if (conditionData.size()<2)
                            conditionDialog.show(CreateSceneActivity.this);
                            break;

                        case 2:  // 满足任一条件
                            allCondition = false;
                            allConditionSubmit = false;
                            conditionSelectData.get(1).setEnabled(true);
                            if (conditionData.size()<2)
                            conditionDialog.show(CreateSceneActivity.this);
                            break;
                    }
                tvConditionType.setText(item.getName());
                conditionTypeDialog.dismiss();
            }
        });
    }

    /**
     * 定时弹窗
     */
    private void initTimingDialog(){
        timingBottomDialog = new TimingBottomDialog();
        timingBottomDialog.setTimeSelectListener(new TimingBottomDialog.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(int hour, int minute, int seconds, String timeStr) {
                auto_run = true;
                if (updateTime){  // 更新列表定时
                    conditionAdapter.getItem(conditionPos).setTiming(TimeUtil.string2Stamp(TimeUtil.getToday() + " "+timeStr)/1000);
                    conditionAdapter.getItem(conditionPos).setTimingStr(timeStr);
                    conditionAdapter.notifyItemChanged(conditionPos);
                }else {  // 添加定时
                    conditionSelectData.get(0).setEnabled(false);
                    conditionDialog.notifyItemChange();
                    ConditionBean conditionBean = new ConditionBean(getResources().getString(R.string.scene_timing), 1);
                    conditionBean.setTiming(TimeUtil.string2Stamp(TimeUtil.getToday() + " "+timeStr)/1000);
                    conditionBean.setTimingStr(timeStr);
                    conditionData.add(conditionBean);
                    conditionAdapter.notifyDataSetChanged();
                    setIvAddEnable(true);
//                    setFinishEnable();
                }
                setLLAddConditionVisible();
                timingBottomDialog.dismiss();
            }
        });
    }

    /**
     * 条件列表
     */
    private void initRvCondition(){
        rvCondition.setLayoutManager(new LinearLayoutManager(this));
        conditionAdapter = new ConditionAdapter();
        rvCondition.setAdapter(conditionAdapter);
        conditionAdapter.setNewData(conditionData);
        conditionAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ConditionBean conditionBean = conditionAdapter.getItem(position);
                conditionPos = position;
                switch (view.getId()){
                    case   R.id.llContent:
                        if (conditionBean.getType() == 1){  // 定时
                            updateTime = true;
                            timingBottomDialog.setSelectTime(conditionBean.getTimingStr());
                            timingBottomDialog.show(CreateSceneActivity.this);
                        }else if (conditionBean.getType() == 2){  // 设备状态变化时
                            Bundle bundle = new Bundle();
                            bundle.putInt(IntentConstant.ID, conditionBean.getDeviceId());
                            bundle.putString(IntentConstant.NAME, conditionBean.getDeviceName());
                            bundle.putString(IntentConstant.TYPE, conditionBean.getDeviceType());
                            bundle.putString(IntentConstant.LOGO_URL, conditionBean.getLogoUrl());
                            bundle.putString(IntentConstant.RA_NAME, conditionBean.getLocation());
                            bundle.putInt(IntentConstant.FROM, 1);
                            CreateScenePost.SceneConditionsBean.ConditionItemBean ci = conditionBean.getCondition_item();
                            if (ci!=null){
                                if (ci.getId()>0)
                                bundle.putInt(IntentConstant.CONDITION_ID, ci.getId());
                                if (ci.getScene_condition_id()>0){
                                    bundle.putInt(IntentConstant.SCENE_CONDITION_ID, ci.getScene_condition_id());
                                }
                            }
                            if (conditionBean.getScene_id()>0){
                                bundle.putInt(IntentConstant.SCENE_ID, conditionBean.getScene_id());
                            }

                            conditionChange = true;
                            switchToActivity(SceneDeviceStatusControlActivity.class, bundle);
                        }
                        break;

                    case   R.id.tvDel:  // 删除
                        if (conditionBean.getId()!=null){
                            del_condition_ids.add(conditionBean.getId());
                        }
                        conditionData.remove(position);
                        conditionAdapter.notifyDataSetChanged();
                        setLLAddConditionVisible();
                        break;
                }
            }
        });
    }

    /**
     * 任务列表
     */
    private void initRvTask(){
        rvTask.setLayoutManager(new LinearLayoutManager(this));
        sceneTaskAdapter = new SceneTaskAdapter();
        rvTask.setAdapter(sceneTaskAdapter);
        sceneTaskAdapter.setNewData(performData);
        sceneTaskAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                SceneTaskBean sceneTaskBean = sceneTaskAdapter.getItem(position);
                switch (view.getId()){
                    case   R.id.llContent:
                        if (sceneTaskBean.getType() == 1){
                            taskPos = position;
                            Bundle bundle = new Bundle();
                            bundle.putInt(IntentConstant.ID, sceneTaskBean.getScene_task_devices().get(0).getDevice_id());
                            bundle.putString(IntentConstant.NAME, sceneTaskBean.getSubTitle());
                            bundle.putString(IntentConstant.TYPE, sceneTaskBean.getDeviceType());
                            bundle.putString(IntentConstant.LOGO_URL, sceneTaskBean.getLogo());
                            bundle.putString(IntentConstant.RA_NAME, sceneTaskBean.getLocation());
                            for (CreateScenePost.SceneTasksBean.SceneTaskDevicesBean sceneTaskDevicesBean : sceneTaskBean.getScene_task_devices()){
                                if (sceneTaskDevicesBean.getAction().equals(Constant.SWITCH)){  // 开关
                                    bundle.putString(IntentConstant.SWITCH_STATUS, sceneTaskDevicesBean.getAction_val());
                                    if (sceneId>0 && sceneTaskDevicesBean.getId()!=null) // 修改场景
                                    bundle.putInt(IntentConstant.SWITCH_ID, sceneTaskDevicesBean.getId());
                                }else if (sceneTaskDevicesBean.getAction().equals(Constant.set_bright)){ // 亮度
                                    bundle.putString(IntentConstant.BRIGHTNESS, sceneTaskDevicesBean.getAction_val());
                                    if (sceneId>0 && sceneTaskDevicesBean.getId()!=null)// 修改场景
                                    bundle.putInt(IntentConstant.BRIGHT_ID, sceneTaskDevicesBean.getId());
                                }else if (sceneTaskDevicesBean.getAction().equals(Constant.set_color_temp)){  // 色温
                                    bundle.putString(IntentConstant.COLOR_TEMP, sceneTaskDevicesBean.getAction_val());
                                    if (sceneId>0 && sceneTaskDevicesBean.getId()!=null)// 修改场景
                                    bundle.putInt(IntentConstant.COLOR_TEMP_ID, sceneTaskDevicesBean.getId());
                                }
                                if (sceneId>0 && sceneTaskDevicesBean.getScene_task_id()!=null)// 修改场景
                                bundle.putInt(IntentConstant.SCENE_TASK_ID, sceneTaskDevicesBean.getScene_task_id());
                            }
                            bundle.putString(IntentConstant.TIME_STR, sceneTaskBean.getTimeStr());
                            bundle.putInt(IntentConstant.FROM, 1);
                            taskChange = true;
                            switchToActivity(TaskDeviceControlActivity.class, bundle);
                        }
                        break;

                    case   R.id.tvDel:  // 删除
                        if (sceneTaskBean.getId()!=null){
                            del_task_ids.add(sceneTaskBean.getId());
                        }
                        sceneTaskAdapter.remove(position);
                        sceneTaskAdapter.notifyDataSetChanged();
                        setLLTaskVisible();
                        break;
                }
            }
        });
    }

    /**
     * 设置添加触发条件是否可见
     */
    private void setLLAddConditionVisible(){
        clConditionData.setVisibility(CollectionUtil.isNotEmpty(conditionData) ? View.VISIBLE : View.GONE);
        llCondition.setVisibility(CollectionUtil.isEmpty(conditionData) ? View.VISIBLE : View.GONE);
        llTask.setEnabled(CollectionUtil.isNotEmpty(conditionData) ? true : false);
        tvTask.setEnabled(CollectionUtil.isNotEmpty(conditionData) ? true : false);
        if (CollectionUtil.isEmpty(conditionData)){
            auto_run = false;
            // 场景详情时，是定时条件，不能选手动
            conditionSelectData.get(0).setEnabled(sceneId<=0);
            conditionSelectData.get(1).setEnabled(true);
            setIvAddEnable(true);
        }else {
            hasTiming = checkHasTiming();
            if (checkHasTwoTiming()){
                conditionTypeData.get(0).setSelected(false);
                conditionTypeData.get(0).setEnabled(false);
                conditionTypeData.get(1).setSelected(true);

            }else {
                conditionTypeData.get(0).setEnabled(true);
            }

        }
        // 生效时段
        clTimePeriod.setVisibility(auto_run ? View.VISIBLE : View.GONE);
        // 条件类型
        if (conditionData.size()>1 && auto_run){
            tvConditionType.setVisibility(View.VISIBLE);
        }else {
            tvConditionType.setVisibility(View.GONE);
        }
    }

    /**
     * 添加任务
     */
    private void setLLTaskVisible(){
        llTask.setVisibility(CollectionUtil.isEmpty(performData) ? View.VISIBLE : View.GONE);
        clTaskData.setVisibility( CollectionUtil.isNotEmpty(performData) ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置继续添加是否可用
     * @param enable
     */
    private void setIvAddEnable(boolean enable){
//        ivAdd.setEnabled(enable);
        ivAdd.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    /**
     * 检查是否有定时条件
     * @return
     */
    private boolean checkHasTiming(){
        for (ConditionBean conditionBean : conditionData){
            if (conditionBean.getType() == 1){
                return true;
            }
        }
        return false;
    }

    /**
     * 是否有两个定时条件
     * @return
     */
    private boolean checkHasTwoTiming(){
        int count = 0;
        for (ConditionBean conditionBean : conditionData){
            if (conditionBean.getType() == 1){
                count++;
            }
        }
        return  count>1;
    }

    /**
     * 设置生效时间段
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SceneEffectTimeEvent event) {
        time_period = event.getTime_period();
        effect_start_time = event.getEffect_start_time()/1000;
        effect_end_time = event.getEffect_end_time()/1000;
        repeat_type = event.getRepeat_type();
        repeat_date = event.getRepeat_date();
        tvDay.setText(event.getRepeatDateStr());
        tvTime.setText(event.getTimeStr());
    }

    /**
     * 更新条件设备
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ConditionBean event) {
        conditionAdapter.getData().set(conditionPos, event);
        conditionAdapter.notifyItemChanged(conditionPos);
    }

    /**
     * 更新任务设备
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SceneTaskBean event) {

        sceneTaskAdapter.getData().set(taskPos, event);
        sceneTaskAdapter.notifyItemChanged(taskPos);
    }

    /**
     * 设置完成可点击
     */
    private void setFinishEnable(){
        if (!TextUtils.isEmpty(etName.getText().toString().trim()) && CollectionUtil.isNotEmpty(conditionData) && CollectionUtil.isNotEmpty(taskData)){
            llFinish.setEnabled(true);
        }else {
            llFinish.setEnabled(false);
        }
    }



    /**************************** 点击事件 ********************************/
    @OnClick({R.id.ivBack, R.id.llCondition, R.id.llTask, R.id.tvConditionType, R.id.ivAdd, R.id.clTimePeriod, R.id.ivTaskAdd, R.id.llFinish, R.id.tvRemove})
    void onClick(View view){
        switch (view.getId()){
            case R.id.ivBack:  // 返回
                boolean show;
                String name = etName.getText().toString();
                if (sceneId>0){ // 修改场景
                    if (updateAutoRun){  // 如果时自动执行，判断执行条件是否修改
                        show = !sceneName.equals(name) || CollectionUtil.isNotEmpty(del_condition_ids)  || CollectionUtil.isNotEmpty(del_task_ids) || !repeat_date.equals(repeat_dateConst) || repeat_type!=repeat_typeConst
                                || conditionSize!=conditionAdapter.getData().size()  || taskSize!=sceneTaskAdapter.getData().size() || taskChange || conditionChange;
                    }else {
                        show = !sceneName.equals(name)   || CollectionUtil.isNotEmpty(del_condition_ids)  || taskSize!=sceneTaskAdapter.getData().size() || taskChange;
                    }
                }else { // 创建场景
                    show = !TextUtils.isEmpty(name) || CollectionUtil.isNotEmpty(conditionAdapter.getData()) || CollectionUtil.isNotEmpty(sceneTaskAdapter.getData());
                }
                if (show) {
                    CenterAlertDialog centerAlertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.scene_back_tips), null);
                    centerAlertDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
                        @Override
                        public void onConfirm(boolean del) {
                            centerAlertDialog.dismiss();
                            onBackPressed();
                        }
                    });
                    centerAlertDialog.show(this);
                }else {
                    onBackPressed();
                }
                break;

            case R.id.llCondition:  // 触发条件
                if (conditionDialog!=null && !conditionDialog.isShowing()){
                    conditionDialog.show(this);
                }
//                pvCustomTime.show();
                break;

            case R.id.llTask:  // 执行任务
            case R.id.ivTaskAdd:  // 继续添加任务
                if (taskDialog!=null && !taskDialog.isShowing()){
                    taskDialog.show(this);
                }

                break;

            case R.id.tvConditionType:  // 条件类型
                mContinue = false;
                conditionTypeDialog.show(CreateSceneActivity.this);
                break;

            case R.id.ivAdd:  // 继续添加
                if(conditionData.size() < 2){  // 条件个数为1，需显示条件类型弹窗
                    conditionTypeDialog.show(CreateSceneActivity.this);
                }else {  // 否则，直接显示条件添加触发条件弹窗
                    if (allCondition && checkHasTwoTiming()) {  // 满足所有条件
                        conditionSelectData.get(1).setEnabled(false);
                        conditionDialog.notifyItemChange();
                    }
                    conditionDialog.show(CreateSceneActivity.this);
                }
//                if (allCondition && checkHasTwoTiming()){  // 满足所有条件
//                    conditionSelectData.get(1).setEnabled(false);
//                    conditionDialog.notifyItemChange();
//                    conditionDialog.show(CreateSceneActivity.this);
//                }else {
//                    mContinue = true;
//                    conditionTypeDialog.show(CreateSceneActivity.this);
//                }
                break;

            case R.id.clTimePeriod:  // 生效时段
                Bundle bundle = new Bundle();
                bundle.putInt(IntentConstant.TIME_PERIOD, time_period);
                bundle.putInt(IntentConstant.REPEAT_TYPE, repeat_type);
                bundle.putString(IntentConstant.REPEAT_DATE, repeat_date);
                bundle.putString(IntentConstant.TIME_STR, tvTime.getText().toString());

                switchToActivity(EffectTimePeriodActivity.class, bundle);
                break;

            case R.id.llFinish: // 完成
                complete();
                break;

            case R.id.tvRemove:
                delDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.scene_remove), null, true);
                delDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm(boolean del) {
//                        tvRemove.setVisibility(View.GONE);
//                        rbDel.setVisibility(View.VISIBLE);
                        isDel = true;
                        mPresenter.delScene(sceneId);

                    }
                });
                delDialog.show(this);

                break;

        }
    }

    /**
     * 完成
     */
    private void complete(){
        isDel = false;

        if(TextUtils.isEmpty(etName.getText().toString().trim())){
            ToastUtil.show(getResources().getString(R.string.scene_name_not_empty));
            return;
        }
        if (CollectionUtil.isEmpty(conditionData)){
            ToastUtil.show(getResources().getString(R.string.scene_please_add_condition));
            return;
        }
        if (CollectionUtil.isEmpty(performData)){
            ToastUtil.show(getResources().getString(R.string.scene_please_add_perform_task));
            return;
        }
        CreateScenePost createScenePost = new CreateScenePost();
        createScenePost.setName(etName.getText().toString().trim());

        createScenePost.setAuto_run(auto_run);
        if (auto_run) {

            createScenePost.setCondition_logic(allConditionSubmit ? 1 : 2);
            createScenePost.setTime_period(time_period);
            createScenePost.setEffect_start_time(effect_start_time);
            createScenePost.setEffect_end_time(effect_end_time);
            createScenePost.setRepeat_type(repeat_type);
            createScenePost.setRepeat_date(repeat_date);
            // 执行条件
            List<CreateScenePost.SceneConditionsBean> scene_conditions = new ArrayList<>();
            for (ConditionBean conditionBean : conditionAdapter.getData()){
                CreateScenePost.SceneConditionsBean sceneConditionsBean = new CreateScenePost.SceneConditionsBean(conditionBean.getType(), conditionBean.getTiming(), conditionBean.getDeviceId(), conditionBean.getCondition_item());
                if (sceneId>0){
                    if (conditionBean.getId()!=null)
                    sceneConditionsBean.setId(conditionBean.getId());
                    if (conditionBean.getScene_id()>0)
                    conditionBean.setScene_id(conditionBean.getScene_id());
                }
                scene_conditions.add(sceneConditionsBean);
            }
            createScenePost.setScene_conditions(scene_conditions);
        }

        // 执行任务
        List<CreateScenePost.SceneTasksBean> scene_tasks = new ArrayList<>();
        for (SceneTaskBean sceneTaskBean : sceneTaskAdapter.getData()){
            CreateScenePost.SceneTasksBean sceneTasksBean = new CreateScenePost.SceneTasksBean();
            if (sceneId>0){
                sceneTasksBean.setId(sceneTaskBean.getId());
                sceneTasksBean.setScene_id(sceneId);
            }
            sceneTasksBean.setType(sceneTaskBean.getType());
            if (sceneTaskBean.getType()>1){
                sceneTasksBean.setControl_scene_id(sceneTaskBean.getControl_scene_id());
            }else {
                sceneTasksBean.setScene_task_devices(sceneTaskBean.getScene_task_devices());
            }
            sceneTasksBean.setDelay_seconds(sceneTaskBean.getDelay_seconds());
            scene_tasks.add(sceneTasksBean);
        }
        createScenePost.setScene_tasks(scene_tasks);
        // 设置移除条件id， 修改场景才有
        if (CollectionUtil.isNotEmpty(del_condition_ids)){
            createScenePost.setDel_condition_ids(del_condition_ids);
        }

        // 设置移除任务id， 修改场景才有
        if (CollectionUtil.isNotEmpty(del_task_ids)){
            createScenePost.setDel_task_ids(del_task_ids);
        }
        if (sceneId>0) {
            createScenePost.setId(sceneId);
        }
        String body = new Gson().toJson(createScenePost);

        if (sceneId>0){
            mPresenter.modifyScene(sceneId, body);
        }else {
            mPresenter.createScene(body);
        }
        tvFinish.setText(getResources().getString(R.string.scene_saving));
        rbFinish.setVisibility(View.VISIBLE);
        llFinish.setEnabled(false);
    }

    /**
     * 场景名文本改变监听
     */
    @OnTextChanged(R.id.etName)
    void onNameChange(){
//        setFinishEnable();
    }

    /**
     * 创建场景成功
     */
    @Override
    public void createSceneSuccess() {
        ToastUtil.show(getResources().getString(R.string.scene_create_success));
        finish();
    }

    /**
     * 修改成功
     */
    @Override
    public void modifySceneSuccess() {
        ToastUtil.show(getResources().getString(R.string.mine_update_success));
        finish();
    }

    /**
     * 删除
     */
    @Override
    public void delSuccess() {
        ToastUtil.show(getResources().getString(R.string.mine_remove_success));
        if (delDialog!=null){
            delDialog.dismiss();
        }
        finish();
    }

    /**
     * 创建场景失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void requestFail(int errorCode, String msg) {
        if (isDel){
//            tvRemove.setVisibility(View.VISIBLE);
//            rbDel.setVisibility(View.GONE);
            if (delDialog!=null){
                delDialog.dismiss();
            }
        }else {
            llFinish.setEnabled(true);
            tvFinish.setText(getResources().getString(R.string.common_finish));
            rbFinish.setVisibility(View.GONE);
        }

        ToastUtil.show(msg);
    }



    /******************************* 修改场景 ****************************************/
    /**
     * 详情成功
     * @param sceneDetail
     */
    @Override
    public void getDetailSuccess(SceneDetailBean sceneDetail) {
        if (sceneDetail!=null){
            sceneName = sceneDetail.getName();
            etName.setText(sceneDetail.getName());
            KeyboardHelper.hideKeyboard(etName);
            auto_run = sceneDetail.isAuto_run();
            updateAutoRun = sceneDetail.isAuto_run();
            if (!sceneDetail.isAuto_run()){
                setIvAddEnable(false);
                conditionData.add(new ConditionBean(getResources().getString(R.string.scene_manual_click_perform), 0));
                conditionAdapter.setSwipe(false);
            }else {
                setIvAddEnable(true);
                allConditionSubmit = sceneDetail.getCondition_logic() == 1;
                time_period = sceneDetail.getTime_period();
                effect_start_time = sceneDetail.getEffect_start_time();
                effect_end_time = sceneDetail.getEffect_end_time();
                repeat_type = sceneDetail.getRepeat_type();
                repeat_typeConst = sceneDetail.getRepeat_type();
                repeat_date = sceneDetail.getRepeat_date();
                repeat_dateConst = sceneDetail.getRepeat_date();
                clTimePeriod.setVisibility(View.VISIBLE);
                if (time_period == 1){
                    tvTime.setText(getResources().getString(R.string.scene_all_day));
                }else {
                    String beginTimeStr = TimeUtil.getTodayHMS(sceneDetail.getEffect_start_time());
                    String endTimeStr = TimeUtil.getTodayHMS(sceneDetail.getEffect_end_time());
                    tvTime.setText(beginTimeStr+"-"+endTimeStr);
                }

                /**
                 * 重复执行的配置1为每天，2为工作日，3为自定义
                 */
                String repeatDateStr = "";
                switch (repeat_type){
                    case 1:
                        repeatDateStr = getResources().getString(R.string.scene_everyday);

                        break;

                    case 2:
                        repeatDateStr = getResources().getString(R.string.scene_monday_friday);
                        break;

                    case 3:
                        String repeatDate = sceneDetail.getRepeat_date();
                        for (int i=0; i<repeatDate.length(); i++){
                            if (i<repeatDate.length()-1) {
                                repeatDateStr = repeatDateStr + TimeUtil.toWeek(String.valueOf(repeatDate.charAt(i)), this)+"、";
                            }else {
                                repeatDateStr = repeatDateStr + TimeUtil.toWeek(String.valueOf(repeatDate.charAt(i)), this);
                            }
                        }
                        break;

                }
                tvDay.setText(repeatDateStr);
                // 如果条件列表不为空 创建条件数据
                if (CollectionUtil.isNotEmpty(sceneDetail.getScene_conditions())) {
                    setConditionData(sceneDetail.getScene_conditions());
                }
            }
            conditionAdapter.notifyDataSetChanged();
            setLLAddConditionVisible();

            // 执行任务数据
            List<SceneDetailBean.SceneTasksBean> scene_tasks = sceneDetail.getScene_tasks();
            if (CollectionUtil.isNotEmpty(scene_tasks)){  // 任务列表
                setTaskData(scene_tasks);
            }

        }

    }

    /**
     * 设置条件数据
     * @param conditionList
     */
    private void setConditionData(List<SceneDetailBean.SceneConditionsBean> conditionList){
        conditionSize = conditionList.size();
        for (SceneDetailBean.SceneConditionsBean sceneConditionsBean : conditionList) {
            if (sceneConditionsBean.getCondition_type() == 1) {  // 定时

                hasTiming = true;
                ConditionBean conditionBean = new ConditionBean(getResources().getString(R.string.scene_timing), 1);
                String time = TimeUtil.getTodayHMS(sceneConditionsBean.getTiming());
                String[] hms = time.split(":");
                conditionBean.setTiming(sceneConditionsBean.getTiming());
                conditionBean.setTimingStr(time);
                conditionBean.setId(sceneConditionsBean.getId());
                conditionData.add(conditionBean);

            } else if (sceneConditionsBean.getCondition_type() == 2) {  // 设备状态变化

                SceneDetailBean.SceneConditionsBean.ConditionItemBean conditionItemBean = sceneConditionsBean.getCondition_item();
                SceneDetailBean.DeviceInfoBean deviceInfoBean = sceneConditionsBean.getDevice_info();
                String value = conditionItemBean.getAction_val();
                int id = conditionItemBean.getId();
                int conditionId = conditionItemBean.getScene_condition_id();
                if (conditionItemBean != null){  // 设备状态变化条件
                    if (conditionItemBean.getAction().equals(Constant.SWITCH)){ // 开关
                        ConditionBean conditionBean = new ConditionBean(conditionItemBean.getAction_val().equals(Constant.ON) ? getResources().getString(R.string.scene_turn_on) :
                                getResources().getString(R.string.scene_turn_off), 2);

                        conditionBean.setDeviceId(sceneConditionsBean.getDevice_id());

                        if (deviceInfoBean!=null){
                            conditionBean.setDeviceName(deviceInfoBean.getName());
                            conditionBean.setLogoUrl(deviceInfoBean.getLogo_url());
                            conditionBean.setLocation(deviceInfoBean.getLocation_name());
                        }
                        CreateScenePost.SceneConditionsBean.ConditionItemBean cib = new CreateScenePost.SceneConditionsBean.ConditionItemBean(id, conditionId, Constant.EQUAL, Constant.SWITCH, value, Constant.POWER);
                        conditionBean.setScene_id(sceneConditionsBean.getScene_id());
                        conditionBean.setCondition_item(cib);
                        conditionBean.setId(sceneConditionsBean.getId());
                        conditionData.add(conditionBean);
                    }else  if (conditionItemBean.getAction().equals(Constant.set_bright)) { // 亮度

                        ConditionBean conditionBean = new ConditionBean(getResources().getString(R.string.scene_brightness) + StringUtil.operator2String(conditionItemBean.getOperator(), this) +value+"%", 2);
                        conditionBean.setDeviceId(sceneConditionsBean.getDevice_id());
                        if (deviceInfoBean!=null){
                            conditionBean.setDeviceName(deviceInfoBean.getName());
                            conditionBean.setLogoUrl(deviceInfoBean.getLogo_url());
                            conditionBean.setLocation(deviceInfoBean.getLocation_name());
                        }

                        CreateScenePost.SceneConditionsBean.ConditionItemBean cib = new CreateScenePost.SceneConditionsBean.ConditionItemBean(id, conditionId,conditionItemBean.getOperator(), Constant.set_bright, value+"", Constant.brightness);
                        conditionBean.setScene_id(sceneConditionsBean.getScene_id());
                        conditionBean.setCondition_item(cib);
                        conditionBean.setCondition_item(cib);
                        conditionBean.setId(sceneConditionsBean.getId());
                        conditionData.add(conditionBean);
                    }else  if (conditionItemBean.getAction().equals(Constant.set_color_temp)) { // 色温
                        ConditionBean conditionBean = new ConditionBean(getResources().getString(R.string.scene_color_temperature) +StringUtil.operator2String(conditionItemBean.getOperator(), this)+value+"%", 2);
                        conditionBean.setDeviceId(sceneConditionsBean.getDevice_id());
                        if (deviceInfoBean!=null){
                            conditionBean.setDeviceName(deviceInfoBean.getName());
                            conditionBean.setLogoUrl(deviceInfoBean.getLogo_url());
                            conditionBean.setLocation(deviceInfoBean.getLocation_name());
                        }
                        CreateScenePost.SceneConditionsBean.ConditionItemBean cib = new CreateScenePost.SceneConditionsBean.ConditionItemBean(id, conditionId, conditionItemBean.getOperator(), Constant.set_color_temp, value+"", Constant.color_temp);
                        conditionBean.setScene_id(sceneConditionsBean.getScene_id());
                        conditionBean.setCondition_item(cib);
                        conditionBean.setId(sceneConditionsBean.getId());
                        conditionBean.setScene_id(sceneId);
                        conditionData.add(conditionBean);
                    }


                }
            }

        }
    }

    /**
     * 设置任务数据
     * @param scene_tasks
     */
    private void setTaskData(List<SceneDetailBean.SceneTasksBean> scene_tasks){
        taskSize = scene_tasks.size();
        for (SceneDetailBean.SceneTasksBean stb : scene_tasks){
            SceneTaskBean sceneTaskBean = new SceneTaskBean();
            // 创建任务
            int delaySeconds = stb.getDelay_seconds();
            int hour = delaySeconds/3600;
            int minute =  (delaySeconds % 3600) / 60;
            int seconds = (delaySeconds % 3600) % 60;
            String time = StringUtil.hms2String(hour, minute, seconds);
            if (stb.getType() == 1){  // 设备
                List<CreateScenePost.SceneTasksBean.SceneTaskDevicesBean> deviceStatus = new ArrayList<>();
                String title = "";
                List<SceneDetailBean.SceneTasksBean.SceneTaskDevicesBean> deviceData = stb.getScene_task_devices();
                SceneDetailBean.DeviceInfoBean deviceInfo = stb.getDevice_info();
                // 设备状态列表
                for (int i=0; i<deviceData.size(); i++){
                    SceneDetailBean.SceneTasksBean.SceneTaskDevicesBean deviceInfoBean = stb.getScene_task_devices().get(i);
                    String action = "";
                    String actionVal = deviceInfoBean.getAction_val();
                    switch (deviceInfoBean.getAction()){  // 开关
                        case "switch":
                            action = Constant.SWITCH;
                            title =  title + StringUtil.switchStatus2String(actionVal, this) + (i<deviceData.size()-1 ? "、" : "");
                            break;
                        case "set_bright":  // 亮度
                            action = Constant.set_bright;

                            title = title +  getResources().getString(R.string.scene_brightness)+actionVal+"%" + (i<deviceData.size()-1 ? "、" : "");
                            break;
                        case "set_color_temp":  // 色温
                            action = Constant.set_color_temp;
                            title = title +  getResources().getString(R.string.scene_color_temperature)+actionVal+"%" + (i<deviceData.size()-1 ? "、" : "");
                            break;

                    }

                    deviceStatus.add(new CreateScenePost.SceneTasksBean.SceneTaskDevicesBean(action, deviceInfoBean.getDevice_id(), actionVal, deviceInfoBean.getAttribute(), deviceInfoBean.getId(), deviceInfoBean.getScene_task_id()));
                }
                sceneTaskBean.setDeviceStatus(deviceInfo.getStatus());
                sceneTaskBean.setTitle(title);
                sceneTaskBean.setId(stb.getId());
                sceneTaskBean.setType(stb.getType());
                sceneTaskBean.setControl_scene_id(stb.getControl_scene_id());
                sceneTaskBean.setTimeStr(time);
                sceneTaskBean.setType(1);
                sceneTaskBean.setHour(hour);
                sceneTaskBean.setMinute(minute);
                sceneTaskBean.setSeconds(seconds);
                sceneTaskBean.setDelay_seconds(TimeUtil.toSeconds(hour, minute, seconds));
                if (deviceInfo!=null) {
                    sceneTaskBean.setSubTitle(deviceInfo.getName());
                    sceneTaskBean.setLogo(deviceInfo.getLogo_url());
                    sceneTaskBean.setLocation(deviceInfo.getLocation_name());
                }
                sceneTaskBean.setScene_task_devices(deviceStatus);
            }else {  // 场景
                // 2:手动执行场景;3:开启自动执行场景;4:关闭自动执行场景
                String taskName = "";
                switch (stb.getType()){
                    case 2:
                        taskName = getResources().getString(R.string.scene_perform);
                        break;

                    case 3:
                        taskName = getResources().getString(R.string.scene_turn_on);
                        break;

                    case 4:
                        taskName = getResources().getString(R.string.scene_turn_off);
                        break;
                }
                sceneTaskBean.setTitle(taskName);
                SceneDetailBean.SceneTasksBean.ControlSceneInfoBean controlSceneInfoBean = stb.getControl_scene_info();
                if (controlSceneInfoBean!=null){
                    sceneTaskBean.setSubTitle(controlSceneInfoBean.getName());
                    sceneTaskBean.setSceneStatus(controlSceneInfoBean.getStatus());
                }
                sceneTaskBean.setId(stb.getId());
                sceneTaskBean.setType(stb.getType());
                sceneTaskBean.setControl_scene_id(stb.getControl_scene_id());
                sceneTaskBean.setHour(hour);
                sceneTaskBean.setMinute(minute);
                sceneTaskBean.setSeconds(seconds);
                sceneTaskBean.setDelay_seconds(TimeUtil.toSeconds(hour, minute, seconds));
            }
            performData.add(sceneTaskBean);


        }
        tvConditionType.setText(allConditionSubmit ? getResources().getString(R.string.scene_meet_with_all_condition) : getResources().getString(R.string.scene_meet_with_certain_condition));
//        conditionSelectData.get(1).setEnabled(!allCondition);
        conditionDialog.notifyItemChange();
        sceneTaskAdapter.notifyDataSetChanged();
        setLLTaskVisible();
//        setFinishEnable();
    }

    /**
     * 详情失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getDetailFail(int errorCode, String msg) {

    }
}