package com.yctc.zhiting.activity;


import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SceneListContract;
import com.yctc.zhiting.activity.presenter.SceneListPresenter;
import com.yctc.zhiting.adapter.SceneListAdapter;
import com.yctc.zhiting.dialog.TimingBottomDialog;
import com.yctc.zhiting.entity.SceneTaskBean;
import com.yctc.zhiting.entity.scene.SceneBean;
import com.yctc.zhiting.entity.scene.SceneDeviceStatusControlBean;
import com.yctc.zhiting.entity.scene.SceneListBean;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.TimeUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 控制场景  场景选择
 */
public class SceneListActivity extends MVPBaseActivity<SceneListContract.View, SceneListPresenter> implements  SceneListContract.View {

    @BindView(R.id.llContent)
    LinearLayout llContent;
    @BindView(R.id.rvFunction)
    RecyclerView rvFunction;
    @BindView(R.id.tvNext)
    TextView tvNext;
    @BindView(R.id.tvDelay)
    TextView tvDelay;
    @BindView(R.id.nsv)
    NestedScrollView nsv;

    private TimingBottomDialog timingBottomDialog;  // 时间筛选

    private SceneListAdapter sceneListAdapter;

    /**
     *  2. 执行某条场景
     *  3. 开启自动执行
     *  4 关闭自动执行
     */
    private int type;
    private String title;

    private String taskName; // 任务名称
    private int h;
    private int m;
    private int s;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_list;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setNextStatus();
        type = getIntent().getIntExtra(IntentConstant.TYPE, 0);
        title = getIntent().getStringExtra(IntentConstant.TITLE);
        switch (type){
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
        setTitleCenter(title);
        setTitleRightText(getResources().getString(R.string.scene_reset));
        getRightTitleText().setTextColor(UiUtil.getColor(R.color.color_3F4663));
        getRightTitleText().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        getRightTitleText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNext.setEnabled(false);
                for (SceneBean sceneBean: sceneListAdapter.getData()){
                    sceneBean.setSelected(false);
                }
                sceneListAdapter.notifyDataSetChanged();
                tvDelay.setText("");
                setNextStatus();
            }
        });
        initTimingDialog();
        rvFunction.setLayoutManager(new LinearLayoutManager(this));
        sceneListAdapter = new SceneListAdapter();
        rvFunction.setAdapter(sceneListAdapter);
        sceneListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SceneBean sceneBean = sceneListAdapter.getItem(position);
                sceneBean.setSelected(!sceneBean.isSelected());
                sceneListAdapter.notifyItemChanged(position);
                if (hasSelected()){
                    tvNext.setEnabled(true);
                }else {
                    tvNext.setEnabled(false);
                }
                setNextStatus();
            }
        });
        mPresenter.getSceneList();
    }

    /**
     * 定时
     */
    private void initTimingDialog(){
        timingBottomDialog = new TimingBottomDialog();
        timingBottomDialog.setTimeSelectListener(new TimingBottomDialog.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(int hour, int minute, int seconds, String timeStr) {
                tvDelay.setText(String.format(getResources().getString(R.string.scene_delay_after), timeStr));
                h = hour;
                m = minute;
                s = seconds;
                timingBottomDialog.dismiss();
            }
        });
    }

    @Override
    public void getSceneListSuccess(SceneListBean data) {
        if (data != null){
            List<SceneBean> manual = data.getManual();
            List<SceneBean> autoData = data.getAuto_run();
            if (type>2){
                if (CollectionUtil.isNotEmpty(autoData)){  // 开启/关闭自动执行数据
                    sceneListAdapter.setNewData(autoData);
                }else {
                    setNullView();
                }
            }else {  // 执行某条场景
                if (CollectionUtil.isNotEmpty(manual)){
                    sceneListAdapter.setNewData(manual);
                }else {
                    setNullView();
                }
            }
        }else {
            setNullView();
        }
    }

    @Override
    public void getSceneListError(int errorCode, String msg) {

    }

    // 下一步按钮样式
    private void setNextStatus(){
        tvNext.setAlpha( tvNext.isEnabled() ? 1 : 0.5f);
    }

    private void  setNullView(){
        showEmpty(nsv, R.drawable.icon_empty_list, getResources().getString(R.string.common_no_content));
        tvNext.setVisibility(View.GONE);
    }

    /**
     * 检查是否已选择
     * @return
     */
    private boolean hasSelected(){
        for (SceneBean sceneBean : sceneListAdapter.getData()){
            if (sceneBean.isSelected()){
                return true;
            }
        }
        return false;
    }



    /***************************** 点击事件 *******************************/
    @OnClick({R.id.llDelay, R.id.tvNext})
    void onClick(View view){
        switch (view.getId()){
            case R.id.llDelay:  // 延时
                if (timingBottomDialog!=null && !timingBottomDialog.isShowing()){
                    timingBottomDialog.show(this);
                }
                break;

            case R.id.tvNext:  // 下一步
                List<SceneTaskBean> taskData = new ArrayList<>();
                for (SceneBean sceneBean : sceneListAdapter.getData()){
                    if (sceneBean.isSelected()){
                        SceneTaskBean sceneTaskBean = new SceneTaskBean(taskName, sceneBean.getName());
                        sceneTaskBean.setType(type);
                        sceneTaskBean.setControl_scene_id(sceneBean.getId());
                        sceneTaskBean.setHour(h);
                        sceneTaskBean.setMinute(m);
                        sceneTaskBean.setSeconds(s);
                        sceneTaskBean.setDelay_seconds(TimeUtil.toSeconds(h, m, s));
                        taskData.add(sceneTaskBean);
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(IntentConstant.TASK_LIST, (Serializable) taskData);
                switchToActivity(CreateSceneActivity.class, bundle);
                finish();
                break;
        }
    }
}