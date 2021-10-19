package com.yctc.zhiting.activity;



import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.DateUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SceneListNewContract;
import com.yctc.zhiting.activity.presenter.SceneListNewPresenter;
import com.yctc.zhiting.adapter.SceneListAdapter;
import com.yctc.zhiting.dialog.TimingBottomDialog;
import com.yctc.zhiting.entity.SceneTaskBean;
import com.yctc.zhiting.entity.scene.SceneBean;
import com.yctc.zhiting.entity.scene.SceneControlSceneInfoEntity;
import com.yctc.zhiting.entity.scene.SceneListBean;
import com.yctc.zhiting.entity.scene.SceneTaskEntity;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.TimeUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 重构的场景列表
 */
public class SceneListNewActivity extends MVPBaseActivity<SceneListNewContract.View, SceneListNewPresenter> implements  SceneListNewContract.View {

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

    /**
     *  2. 执行某条场景
     *  3. 开启自动执行
     *  4 关闭自动执行
     */
    private int type;
    private String title;

    private TimingBottomDialog timingBottomDialog;  // 时间筛选

    private SceneListAdapter sceneListAdapter;
    private int delaySeconds;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_list_new;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setNextStatus();
        type = getIntent().getIntExtra(IntentConstant.TYPE, 0);
        title = getIntent().getStringExtra(IntentConstant.TITLE);

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
                delaySeconds = 0;
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
        ToastUtil.show(msg);
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

    /**
     * 定时
     */
    private void initTimingDialog(){
        timingBottomDialog = new TimingBottomDialog();
        timingBottomDialog.setTimeSelectListener(new TimingBottomDialog.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(int hour, int minute, int seconds, String timeStr) {
                tvDelay.setText(String.format(getResources().getString(R.string.scene_delay_after), timeStr));
                delaySeconds = hour*3600 + minute*60 + seconds;
                timingBottomDialog.dismiss();
            }
        });
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
                List<SceneTaskEntity> taskData = new ArrayList<>();
                for (SceneBean sceneBean : sceneListAdapter.getData()){
                    if (sceneBean.isSelected()){
                        SceneTaskEntity sceneTaskEntity = new SceneTaskEntity(type, sceneBean.getId());
                        if (delaySeconds>0){
                            sceneTaskEntity.setDelay_seconds(delaySeconds);
                        }
                        SceneControlSceneInfoEntity sceneControlSceneInfoEntity = new SceneControlSceneInfoEntity(sceneBean.getName(), 1);
                        sceneTaskEntity.setControl_scene_info(sceneControlSceneInfoEntity);
                        taskData.add(sceneTaskEntity);
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(IntentConstant.TASK_LIST, (Serializable) taskData);
                switchToActivity(SceneDetailActivity.class, bundle);
                finish();
                break;
        }
    }
}