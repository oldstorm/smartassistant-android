package com.yctc.zhiting.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.SceneDetailActivity;
import com.yctc.zhiting.adapter.SceneAdapter;
import com.yctc.zhiting.entity.scene.SceneBean;
import com.yctc.zhiting.event.RefreshSceneEvent;
import com.yctc.zhiting.fragment.contract.SceneItemFragmentContract;
import com.yctc.zhiting.fragment.presenter.SceneItemFragmentPresenter;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.MyItemTouchHelper;
import com.yctc.zhiting.utils.UserUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;

/**
 * 子场景
 */
public class SceneItemFragment extends MVPBaseFragment<SceneItemFragmentContract.View, SceneItemFragmentPresenter> implements SceneItemFragmentContract.View {

    @BindView(R.id.rvData)
    RecyclerView rvData;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private int type;
    private List<SceneBean> mSceneData;

    private SceneAdapter mSceneAdapter;

    private boolean checked; //区分自动开关提示文案
    private ItemTouchHelper itemTouchHelper;
    private boolean isSorting;

    public static SceneItemFragment getInstance(int type, List<SceneBean> sceneData) {
        SceneItemFragment sceneItemFragment = new SceneItemFragment();
        Bundle args = new Bundle();
        args.putInt(IntentConstant.TYPE, type);
        args.putSerializable(IntentConstant.BEAN_LIST, (Serializable) sceneData);
        sceneItemFragment.setArguments(args);
        return sceneItemFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_scene_item;
    }

    @Override
    protected void initUI() {
        super.initUI();
        type = getArguments().getInt(IntentConstant.TYPE);
        mSceneData = (List<SceneBean>) getArguments().getSerializable(IntentConstant.BEAN_LIST);
        rvData.setLayoutManager(new LinearLayoutManager(getContext()));
        mSceneAdapter = new SceneAdapter(type);
        rvData.setAdapter(mSceneAdapter);
        mSceneAdapter.setNewData(mSceneData);
        itemTouchHelper = new ItemTouchHelper(new MyItemTouchHelper(mSceneAdapter, false));
        refreshLayout.setEnableRefresh(!HomeUtil.tokenIsInvalid);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                EventBus.getDefault().post(new RefreshSceneEvent());
            }
        });
        setAdapterListener();
        resetStatus();
    }

    /**
     * 设置适配器事件
     */
    private void setAdapterListener() {
        mSceneAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (isSorting) return;
                SceneBean sceneBean = mSceneAdapter.getItem(position);
                if (SceneFragment.hasUpdateP) { // 有权限才进
                    if (HomeUtil.notLoginAndSAEnvironment()) {
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putInt(IntentConstant.ID, sceneBean.getId());
                    bundle.putBoolean(IntentConstant.REMOVE_SCENE, SceneFragment.hasDelP);
                    switchToActivity(SceneDetailActivity.class, bundle);
                } else {
                    ToastUtil.show(getResources().getString(R.string.scene_no_modify_permission));
                }
            }
        });

        mSceneAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                SceneBean sceneBean = mSceneAdapter.getItem(position);
                if (isSorting) return;
                if (view.getId() == R.id.tvPerform) { // 执行
                    if (sceneBean.isControl_permission()) {
                        type = 0;
                        sceneBean.setPerforming(true);
                        mSceneAdapter.notifyItemChanged(position);
                        mPresenter.perform(sceneBean.getId(), position, true);
                    } else {
                        ToastUtil.show(getResources().getString(R.string.scene_no_control_permission));
                    }
                } else if (view.getId() == R.id.ivSwitch) { // 无权限开关
                    ToastUtil.show(getResources().getString(R.string.scene_no_control_permission));
                } else if (view.getId() == R.id.llSwitch) { // 开关
                    sceneBean.setIs_on(!sceneBean.isIs_on());
                    sceneBean.setPerforming(true);
                    mSceneAdapter.notifyItemChanged(position);
                    checked = sceneBean.isIs_on();
                    mPresenter.perform(sceneBean.getId(), position, sceneBean.isIs_on());
                }
            }
        });
    }

    /**
     * 刷新数据
     *
     * @param sceneData
     */
    public void notifyDataChange(List<SceneBean> sceneData) {
        if (mSceneAdapter != null) {
            mSceneData = sceneData;
            mSceneAdapter.setNewData(mSceneData);
            resetStatus();
        }
    }

    /**
     * 是否可操作状态
     */
    private void resetStatus() {
        boolean status = UserUtils.isLogin() ? true : HomeUtil.isSAEnvironment();
        if (mSceneAdapter != null) {
            mSceneAdapter.setStatus(status);
        }
    }

    /**
     * 执行成功
     */
    @Override
    public void performSuccess(int position) {
        String tip = UiUtil.getString(R.string.scene_perform_success);
        if (type == 1) {
            tip = checked ? UiUtil.getString(R.string.scene_perform_has_been_opened) : UiUtil.getString(R.string.scene_perform_has_been_closed);
        }
        resetPerformStatus(position, true);
        ToastUtil.show(tip);
    }

    /**
     * 执行失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void performFail(int errorCode, int position, String msg) {
        resetPerformStatus(position, false);
        ToastUtil.show(msg);
    }

    @Override
    public void orderSceneSuccess() {
        setRefreshLayoutEnable();
        ToastUtil.show(UiUtil.getString(R.string.scene_order_success));
    }

    @Override
    public void orderSceneFail(int errorCode, String msg) {
        setRefreshLayoutEnable();
        ToastUtil.show(UiUtil.getString(R.string.scene_order_fail));
    }

    public void setRefreshLayoutEnable() {
        if (refreshLayout != null) {
            refreshLayout.setEnableRefresh(true);
            refreshLayout.finishRefresh();
        }
    }

    /**
     * 刷新执行状态
     *
     * @param position
     * @param success
     */
    private void resetPerformStatus(int position, boolean success) {
        if (type == 1) {
            if (mSceneAdapter != null && CollectionUtil.isNotEmpty(mSceneAdapter.getData())) {
                SceneBean sceneBean = mSceneAdapter.getItem(position);
                sceneBean.setPerforming(false);
                if (!success) {
                    LogUtil.e("结果：false");
                    sceneBean.setIs_on(!sceneBean.isIs_on());
                }
                mSceneAdapter.notifyItemChanged(position);
            }
        } else {
            SceneBean sceneBean = mSceneAdapter.getItem(position);
            sceneBean.setPerforming(false);
            mSceneAdapter.notifyItemChanged(position);
        }
    }

    /**
     * 排序
     *
     * @param isSorting
     */
    public void setSorting(boolean isSorting) {
        this.isSorting = isSorting;
        refreshLayout.setEnableRefresh(!isSorting);
        if (mSceneAdapter != null) {
            mSceneAdapter.setSorting(isSorting);
            itemTouchHelper.attachToRecyclerView(isSorting ? rvData : null);
            if (!isSorting) {
                List<Integer> scene_ids = mSceneAdapter.getIdList();
                String body = "{\"scene_ids\":" + scene_ids.toString() + "}";
                mPresenter.orderScene(body);
            }
        }
    }
}
