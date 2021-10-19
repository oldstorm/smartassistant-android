package com.yctc.zhiting.activity;

import androidx.constraintlayout.solver.state.State;
import androidx.constraintlayout.utils.widget.ImageFilterButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.main.framework.baseutil.KeyboardHelper;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SceneDetailContract;
import com.yctc.zhiting.activity.presenter.SceneDetailPresenter;
import com.yctc.zhiting.adapter.SceneDetailConditionAdapter;
import com.yctc.zhiting.adapter.SceneDetailTaskAdapter;
import com.yctc.zhiting.bean.ListBottomBean;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.dialog.ConditionDialog;
import com.yctc.zhiting.dialog.SceneSelectBottomDialog;
import com.yctc.zhiting.dialog.TimingBottomDialog;
import com.yctc.zhiting.entity.scene.ConditionSelectBean;
import com.yctc.zhiting.entity.scene.SceneConditionAttrEntity;
import com.yctc.zhiting.entity.scene.SceneConditionEntity;
import com.yctc.zhiting.entity.scene.SceneDetailEntity;
import com.yctc.zhiting.entity.scene.SceneDeviceInfoEntity;
import com.yctc.zhiting.entity.scene.SceneTaskEntity;
import com.yctc.zhiting.event.SceneEffectTimeEvent;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.TimeUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 创建修改场景重构
 */
public class SceneDetailActivity extends MVPBaseActivity<SceneDetailContract.View, SceneDetailPresenter> implements  SceneDetailContract.View{

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
    private List<ConditionSelectBean> conditionSelectData = new ArrayList<>();

    private SceneDetailConditionAdapter sceneDetailConditionAdapter;  // 触发条件适配器
    private List<SceneConditionEntity> sceneConditionData = new ArrayList<>(); // 触发条件数据
    private SceneDetailTaskAdapter sceneDetailTaskAdapter; // 执行任务列表适配器
    private List<SceneTaskEntity> sceneTaskData = new ArrayList<>();

    private TimingBottomDialog timingBottomDialog;  // 时间筛选弹窗
    private boolean updateTime = false; // 是否是更新选择的时间

    private SceneSelectBottomDialog conditionTypeDialog; // 条件类型弹窗
    private List<ListBottomBean> conditionTypeData = new ArrayList<>(); // 条件类型数据

    private ConditionDialog taskDialog;  // 任务类型弹窗
    List<ConditionSelectBean> taskData = new ArrayList<>();  // 任务类型数据

    private SceneSelectBottomDialog controlSceneDialog; // 控制场景类型弹窗
    private List<ListBottomBean> controlSceneData = new ArrayList<>(); // 控制场景类型数据

    private CenterAlertDialog delDialog; // 删除确认弹窗

    private int sceneId;  // 场景id， 大于0就是场景详情
    private int conditionPos;  // 点击条件列表的位置
    private int taskPos; // 点击任务列表的位置

    private boolean auto_run; // true 为自动，false为
    private boolean hasTiming; // 是否已经有定时// 手动
    private boolean allCondition; // 是否是全部条件
    private boolean allConditionSubmit; // 是否是全部条件  提交数据用

    private int time_period = 1;  //  生效时间类型，全天为1，时间段为2,auto_run为false可不传
    private long effect_start_time; // 生效开始时间,time_period为1时应传某天0点;auto_run为false可不传
    private long effect_end_time;  // 生效结束时间,time_period为1时应传某天24点;auto_run为false可不传
    private int repeat_type = 1;  // 重复执行的类型；1：每天; 2:工作日 ；3：自定义;auto_run为false可不传
    private String repeat_date = "1234567"; // 只能传长度为7包含1-7的数字；"1122"视为不合法传参;repeat_type为1时:"1234567"; 2:12345; 3：任意

    private boolean hasDelP; // 是否有删除场景的权限
    private List<Integer> del_condition_ids;  // 要移除的触发条件(修改场景才有)
    private List<Integer> del_task_ids;  // 要移除的任务(修改场景才有)

    private boolean isDel; // 是否删除操作

    private SceneDetailEntity sceneDetailEntity; // 场景详情数据
    private String sceneName;  // 场景名称
    private int repeat_typeConst; // 重复类型 获取场景详情时的初始值
    private String repeat_dateConst; // 重复星期 获取场景详情时的初始值
    private int timePeriodConst = 1;  //  生效时间类型，全天为1，时间段为2,auto_run为false可不传  获取场景详情时的初始值
    private boolean updateAutoRun; // 获取修改场景详情时 是否自动执行

    private boolean taskChange; // 修改场景，判断设备是否改过
    private boolean conditionChange; // 修改场景，判断条件是否改过
    private int conditionSize;  //  条件个数 获取场景详情时的初始值
    private int taskSize;  //  任务个数 获取场景详情时的初始值

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_detail;
    }

    @Override
    protected void initUI() {
        super.initUI();
        SpUtil.init(this);
        llTask.setEnabled(false);
        tvTask.setEnabled(false);
        effect_start_time = TimeUtil.string2Stamp(TimeUtil.getToday() + " 00:00:00")/1000;
        effect_end_time = TimeUtil.string2Stamp(TimeUtil.getToday() + " 23:59:59")/1000;

        initConditionTypeDialog();
        initTaskDialog();
        initControlSceneDialog();
        initTimingDialog();
        iniDelDialog();
        initRvCondition();
        initRvTask();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        sceneId = intent.getIntExtra(IntentConstant.ID, 0);
        hasDelP = intent.getBooleanExtra(IntentConstant.REMOVE_SCENE, false);
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
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SceneConditionEntity sceneConditionEntity = (SceneConditionEntity) intent.getSerializableExtra(IntentConstant.BEAN);
        // 条件
        if (sceneConditionEntity!=null){
            sceneConditionData.add(sceneConditionEntity);
            sceneDetailConditionAdapter.notifyDataSetChanged();
            auto_run = true;
            conditionSelectData.get(0).setEnabled(false);
            setLLAddConditionVisible();
        }

        // 任务（设备）
        SceneTaskEntity sceneTaskEntity = (SceneTaskEntity) intent.getSerializableExtra(IntentConstant.TASK_BEAN);
        if (sceneTaskEntity!=null){
            sceneTaskData.add(sceneTaskEntity);
            sceneDetailTaskAdapter.notifyDataSetChanged();
            setLLTaskVisible();
        }

        // 任务列表（场景）
        List<SceneTaskEntity> sceneTaskEntities = (List<SceneTaskEntity>) intent.getSerializableExtra(IntentConstant.TASK_LIST);
        if (CollectionUtil.isNotEmpty(sceneTaskEntities)){
            sceneTaskData.addAll(sceneTaskEntities);
            sceneDetailTaskAdapter.notifyDataSetChanged();
            setLLTaskVisible();
        }
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
     * 修改场景成功
     */
    @Override
    public void modifySceneSuccess() {
        ToastUtil.show(getResources().getString(R.string.mine_update_success));
        finish();
    }

    /**
     * 删除场景成功
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
     * 创建/修改/删除场景失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void requestFail(int errorCode, String msg) {
        if (isDel){  // 删除的操作
            if (delDialog!=null){
                delDialog.dismiss();
            }
        }else {  // 创建/修改操作
            llFinish.setEnabled(true);
            tvFinish.setText(getResources().getString(R.string.common_finish));
            rbFinish.setVisibility(View.GONE);
        }

        ToastUtil.show(msg);
    }

    /**
     * 获取详情成功
     * @param sceneDetail
     */
    @Override
    public void getDetailSuccess(SceneDetailEntity sceneDetail) {
        if (sceneDetail!=null){
            sceneName = sceneDetail.getName();
            sceneDetailEntity = sceneDetail;
            etName.setText(sceneDetail.getName());
            KeyboardHelper.hideKeyboard(etName);
            auto_run = sceneDetail.getAuto_run();
            updateAutoRun = sceneDetail.getAuto_run();
            if (auto_run){ // 自动执行
                setIvAddEnable(true);
                allConditionSubmit = sceneDetail.getCondition_logic() == 1;
                time_period = sceneDetail.getTime_period();
                timePeriodConst = sceneDetail.getTime_period();
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
                sceneConditionData = sceneDetail.getScene_conditions();
                tvConditionType.setText(allConditionSubmit ? getResources().getString(R.string.scene_meet_with_all_condition) : getResources().getString(R.string.scene_meet_with_certain_condition));
            }else {  // 手动
                setIvAddEnable(false);
                sceneConditionData.add(new SceneConditionEntity(0));

                sceneDetailConditionAdapter.setSwipe(false);
            }
            sceneDetailConditionAdapter.setNewData(sceneConditionData);
            sceneTaskData = sceneDetail.getScene_tasks();
            sceneDetailTaskAdapter.setNewData(sceneTaskData);
            if (CollectionUtil.isNotEmpty(sceneConditionData))
            conditionSize = sceneConditionData.size();
            if (CollectionUtil.isNotEmpty(sceneTaskData))
            taskSize = sceneTaskData.size();
            setLLAddConditionVisible();
            setLLTaskVisible();
        }
    }

    /**
     * 获取详情失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getDetailFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 执行条件列表
     */
    private void initRvCondition() {
        rvCondition.setLayoutManager(new LinearLayoutManager(this));
        sceneDetailConditionAdapter = new SceneDetailConditionAdapter();
        rvCondition.setAdapter(sceneDetailConditionAdapter);
        sceneDetailConditionAdapter.setNewData(sceneConditionData);
        sceneDetailConditionAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                SceneConditionEntity sceneConditionEntity = sceneDetailConditionAdapter.getItem(position);
                conditionPos = position;
                int type = sceneConditionEntity.getCondition_type();
                switch (view.getId()){
                    case   R.id.llContent:
                        if (type == 1){  // 定时
                            updateTime = true;
                            String time = TimeUtil.getTodayHMS(sceneConditionEntity.getTiming());
                            timingBottomDialog.setSelectTime(time);
                            timingBottomDialog.show(SceneDetailActivity.this);
                        }else if (type == 2){  // 设备状态变化时
                            Bundle bundle = new Bundle();
                            bundle.putInt(IntentConstant.ID, sceneConditionEntity.getDevice_id());
                            SceneDeviceInfoEntity deviceInfo = sceneConditionEntity.getDevice_info();
                            if (deviceInfo!=null) {
                                bundle.putString(IntentConstant.NAME, deviceInfo.getName());
                            }
                            bundle.putSerializable(IntentConstant.BEAN, sceneConditionEntity);
                            conditionChange = true;
                            switchToActivity(SceneDeviceConditionAttrActivity.class, bundle);
                        }
                        break;

                    case   R.id.tvDel:  // 删除
                        sceneConditionData.remove(sceneConditionEntity);
                        sceneDetailConditionAdapter.notifyDataSetChanged();
                        if(sceneId>0){
                            Integer cId =  sceneConditionEntity.getId();
                            if (cId!=null && cId>0){
                                del_condition_ids.add(cId);
                            }
                        }
                        setLLAddConditionVisible();
                        break;
                }
            }
        });
    }

    /**
     * 更新条件设备
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SceneConditionEntity event) {
        sceneConditionData.set(conditionPos, event);
        sceneDetailConditionAdapter.notifyItemChanged(conditionPos);
    }

    /**
     * 更新任务设备
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SceneTaskEntity event) {
        sceneTaskData.set(taskPos, event);
        sceneDetailTaskAdapter.notifyItemChanged(taskPos);
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
     * 执行任务列表
     */
    private void initRvTask(){
        rvTask.setLayoutManager(new LinearLayoutManager(this));
        sceneDetailTaskAdapter = new SceneDetailTaskAdapter();
        rvTask.setAdapter(sceneDetailTaskAdapter);
        sceneDetailTaskAdapter.setNewData(sceneTaskData);
        sceneDetailTaskAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                SceneTaskEntity sceneTaskEntity = sceneDetailTaskAdapter.getItem(position);
                taskPos = position;
                switch (view.getId()){
                    case R.id.llContent:
                        if (sceneTaskEntity.getType() == 1){  // 设备
                            Bundle bundle = new Bundle();
                            bundle.putInt(IntentConstant.ID, sceneTaskEntity.getDevice_id());
                            SceneDeviceInfoEntity deviceInfo = sceneTaskEntity.getDevice_info();
                            if (deviceInfo!=null) {
                                bundle.putString(IntentConstant.NAME, deviceInfo.getName());
                            }
                            bundle.putSerializable(IntentConstant.BEAN, sceneTaskEntity);
                            taskChange = true;
                            switchToActivity(SceneDeviceTaskAttrActivity.class, bundle);
                        }
                        break;

                    case R.id.tvDel:  // 删除
                        sceneTaskData.remove(sceneTaskEntity);
                        sceneDetailTaskAdapter.notifyDataSetChanged();
                        if (sceneId>0){
                            Integer tId = sceneTaskEntity.getId();
                            if (tId!=null && tId>0){
                                del_task_ids.add(tId);
                            }
                        }
                        setLLTaskVisible();
                        break;
                }
            }
        });
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
                        sceneConditionData.add(new SceneConditionEntity(0));
                        sceneDetailConditionAdapter.notifyDataSetChanged();
                        break;

                    case 1:  // 定时
                        timingBottomDialog.setSelectedItemIndex(0, 0, 0);
                        auto_run = true;
                        updateTime = false;
                        timingBottomDialog.setSelectTime("00:00:00");
                        timingBottomDialog.show(SceneDetailActivity.this);
                        break;

                    case 2:  // 设备状态变化时
                        auto_run = true;
                        conditionSelectData.get(0).setEnabled(false);
                        conditionDialog.notifyItemChange();
                        Bundle bundle = new Bundle();
                        bundle.putInt(IntentConstant.FROM, 1);
                        bundle.putString(IntentConstant.TITLE, getResources().getString(R.string.scene_device_status_change));
                        switchToActivity(SceneDetailDeviceActivity.class, bundle);
                        break;
                }
                setLLAddConditionVisible();
                conditionDialog.dismiss();
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
                        if (sceneConditionData.size()<2)
                            conditionDialog.show(SceneDetailActivity.this);
                        break;

                    case 2:  // 满足任一条件
                        allCondition = false;
                        allConditionSubmit = false;
                        conditionSelectData.get(1).setEnabled(true);
                        if (sceneConditionData.size()<2)
                            conditionDialog.show(SceneDetailActivity.this);
                        break;
                }
                tvConditionType.setText(item.getName());
                conditionTypeDialog.dismiss();
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
                        switchToActivity(SceneDetailDeviceActivity.class, bundle);
                        break;

                    case 1:  // 控制场景
                        if (controlSceneDialog!=null && !controlSceneDialog.isShowing()){
                            controlSceneDialog.setAllNotSelected();
                            controlSceneDialog.show(SceneDetailActivity.this);
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
                switchToActivity(SceneListNewActivity.class, bundle);
                controlSceneDialog.dismiss();
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
                    sceneDetailConditionAdapter.getItem(conditionPos).setTiming(TimeUtil.string2Stamp(TimeUtil.getToday() + " "+timeStr)/1000);
                    sceneDetailConditionAdapter.notifyItemChanged(conditionPos);
                }else {  // 添加定时
                    conditionSelectData.get(0).setEnabled(false);
                    conditionDialog.notifyItemChange();
                    SceneConditionEntity sceneConditionEntity = new SceneConditionEntity(1);
                    sceneConditionEntity.setTiming(TimeUtil.string2Stamp(TimeUtil.getToday() + " "+timeStr)/1000);
                    sceneConditionData.add(sceneConditionEntity);
                    sceneDetailConditionAdapter.notifyDataSetChanged();
                    setIvAddEnable(true);
                }
                setLLAddConditionVisible();
                timingBottomDialog.dismiss();
            }
        });
    }

    /**
     * 删除场景确认弹窗
     */
    private void iniDelDialog(){
        delDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.scene_remove), null, true);
        delDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                isDel = true;
                mPresenter.delScene(sceneId);

            }
        });
    }

    /**
     * 设置继续添加是否可用
     * @param enable
     */
    private void setIvAddEnable(boolean enable){
        ivAdd.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置添加触发条件是否可见
     */
    private void setLLAddConditionVisible(){
        clConditionData.setVisibility(CollectionUtil.isNotEmpty(sceneConditionData) ? View.VISIBLE : View.GONE);
        llCondition.setVisibility(CollectionUtil.isEmpty(sceneConditionData) ? View.VISIBLE : View.GONE);
        llTask.setEnabled(CollectionUtil.isNotEmpty(sceneConditionData) ? true : false);
        tvTask.setEnabled(CollectionUtil.isNotEmpty(sceneConditionData) ? true : false);
        if (CollectionUtil.isEmpty(sceneConditionData)){
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
        if (sceneConditionData.size()>1 && auto_run){
            tvConditionType.setVisibility(View.VISIBLE);
        }else {
            tvConditionType.setVisibility(View.GONE);
        }
    }

    /**
     * 检查是否有定时条件
     * @return
     */
    private boolean checkHasTiming(){
        for (SceneConditionEntity sceneConditionEntity : sceneConditionData){
            if (sceneConditionEntity.getCondition_type() == 1){
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
        for (SceneConditionEntity sceneConditionEntity : sceneConditionData){
            if (sceneConditionEntity.getCondition_type() == 1){
                count++;
            }
        }
        return  count>1;
    }


    /**
     * 添加任务
     */
    private void setLLTaskVisible(){
        llTask.setVisibility(CollectionUtil.isEmpty(sceneTaskData) ? View.VISIBLE : View.GONE);
        clTaskData.setVisibility( CollectionUtil.isNotEmpty(sceneTaskData) ? View.VISIBLE : View.GONE);
    }

    /**
     * 完成
     */
    private void complete() {
        isDel = false;
        String name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.show(getResources().getString(R.string.scene_name_not_empty));
            return;
        }
        if (CollectionUtil.isEmpty(sceneConditionData)) {
            ToastUtil.show(getResources().getString(R.string.scene_please_add_condition));
            return;
        }
        if (CollectionUtil.isEmpty(sceneTaskData)) {
            ToastUtil.show(getResources().getString(R.string.scene_please_add_perform_task));
            return;
        }

        if (sceneDetailEntity == null){  // 如果sceneDetailEntity如果为null则创建场景
            sceneDetailEntity = new SceneDetailEntity();
        }
        sceneDetailEntity.setName(name);
        sceneDetailEntity.setAuto_run(auto_run);
        if (auto_run) {
            sceneDetailEntity.setCondition_logic(allConditionSubmit ? 1 : 2);
            sceneDetailEntity.setTime_period(time_period);
            sceneDetailEntity.setEffect_start_time(effect_start_time);
            sceneDetailEntity.setEffect_end_time(effect_end_time);
            sceneDetailEntity.setRepeat_type(repeat_type);
            sceneDetailEntity.setRepeat_date(repeat_date);
        }

        if (CollectionUtil.isNotEmpty(del_condition_ids)){ // 已删除的条件
            sceneDetailEntity.setDel_condition_ids(del_condition_ids);
        }
        if (CollectionUtil.isNotEmpty(del_task_ids)){  // 已删除的任务
            sceneDetailEntity.setDel_task_ids(del_task_ids);
        }
        if (auto_run){
            for (SceneConditionEntity sceneConditionEntity: sceneConditionData){
                SceneConditionAttrEntity sceneConditionAttrEntity = sceneConditionEntity.getCondition_attr();
                if (sceneConditionAttrEntity!=null) {
                    Object object = sceneConditionAttrEntity.getVal();
                    if (object != null && object instanceof Double) {
                        sceneConditionAttrEntity.setVal(Math.round((Double) object));
                    }
                }
            }
        }
        sceneDetailEntity.setScene_conditions(auto_run ? sceneConditionData : null);

        for (SceneTaskEntity sceneTaskEntity : sceneTaskData){
            if (CollectionUtil.isNotEmpty( sceneTaskEntity.getAttributes())) {
                for (SceneConditionAttrEntity sceneConditionAttrEntity : sceneTaskEntity.getAttributes()) {
                    if (sceneConditionAttrEntity!=null) {
                        Object object = sceneConditionAttrEntity.getVal();
                        if (object != null && object instanceof Double) {
                            sceneConditionAttrEntity.setVal(Math.round((Double) object));
                        }
                    }
                }
            }
        }
        sceneDetailEntity.setScene_tasks(sceneTaskData);
        String body = GsonConverter.getGson().toJson(sceneDetailEntity);
        if (sceneId>0){  // 修改场景
            mPresenter.modifyScene(sceneId, body);
        }else {  // 创建场景
            mPresenter.createScene(body);
        }
        tvFinish.setText(getResources().getString(R.string.scene_saving));
        rbFinish.setVisibility(View.VISIBLE);
        llFinish.setEnabled(false);
    }

    /**
     * 返回
     */
    private void back(){
        boolean showBackTip = false;
        String name = etName.getText().toString().trim();
        if (sceneId>0){ // 修改场景
            if (updateAutoRun){  // 如果时自动执行，判断执行条件是否修改
                showBackTip = (!TextUtils.isEmpty(sceneName) && !TextUtils.isEmpty(name) && !sceneName.equals(name)) || CollectionUtil.isNotEmpty(del_condition_ids)  || CollectionUtil.isNotEmpty(del_task_ids)
                        || (!TextUtils.isEmpty(repeat_date) && !TextUtils.isEmpty(repeat_dateConst) && !repeat_date.equals(repeat_dateConst)) || repeat_type!=repeat_typeConst || time_period != timePeriodConst
                        || (conditionSize > 0 && conditionSize!=sceneDetailConditionAdapter.getData().size())  || (taskSize>0 && taskSize!=sceneDetailTaskAdapter.getData().size()) || taskChange || conditionChange;
            }else {
                showBackTip = !sceneName.equals(name)   || CollectionUtil.isNotEmpty(del_task_ids)  || taskSize!=sceneDetailTaskAdapter.getData().size() || taskChange;
            }
        }else { // 创建场景
            showBackTip = !TextUtils.isEmpty(name) || CollectionUtil.isNotEmpty(sceneDetailConditionAdapter.getData()) || CollectionUtil.isNotEmpty(sceneDetailTaskAdapter.getData());
        }
        if (showBackTip) {
            CenterAlertDialog centerAlertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.scene_back_tips), null);
            centerAlertDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
                @Override
                public void onConfirm() {
                    centerAlertDialog.dismiss();
                    onBackPressed();
                }
            });
            centerAlertDialog.show(this);
        }else {
            onBackPressed();
        }
    }

    /**************************** 点击事件 ********************************/
    @OnClick({R.id.ivBack, R.id.llCondition, R.id.llTask, R.id.tvConditionType, R.id.ivAdd, R.id.clTimePeriod, R.id.ivTaskAdd, R.id.llFinish, R.id.tvRemove})
    void onClick(View view){
        switch (view.getId()){
            case R.id.ivBack:  // 返回
                back();
                break;

            case R.id.llCondition:  // 触发条件
                if (conditionDialog!=null && !conditionDialog.isShowing()){
                    conditionDialog.show(this);
                }
                break;

            case R.id.llTask:  // 执行任务
            case R.id.ivTaskAdd:  // 继续添加任务
                if (taskDialog!=null && !taskDialog.isShowing()){
                    taskDialog.show(this);
                }
                break;

            case R.id.tvConditionType:  // 条件类型
                conditionTypeDialog.show(SceneDetailActivity.this);
                break;

            case R.id.ivAdd:  // 继续添加
                if(sceneConditionData.size() < 2){  // 条件个数为1，需显示条件类型弹窗
                    conditionTypeDialog.show(SceneDetailActivity.this);
                }else {  // 否则，直接显示条件添加触发条件弹窗
                    if (allCondition && checkHasTwoTiming()) {  // 满足所有条件
                        conditionSelectData.get(1).setEnabled(false);
                        conditionDialog.notifyItemChange();
                    }
                    conditionDialog.show(SceneDetailActivity.this);
                }
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

            case R.id.tvRemove: // 删除
                if (delDialog!=null && !delDialog.isShowing()){
                    delDialog.show(this);
                }
                break;

        }
    }
}