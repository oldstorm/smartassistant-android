package com.yctc.zhiting.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.httputil.NameValuePair;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.HCDetailContract;
import com.yctc.zhiting.activity.contract.LogContract;
import com.yctc.zhiting.activity.presenter.HCDetailPresenter;
import com.yctc.zhiting.activity.presenter.LogPresenter;
import com.yctc.zhiting.adapter.LogAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.scene.LogBean;
import com.yctc.zhiting.utils.CollectionUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 执行日志
 */
public class LogActivity extends MVPBaseActivity<LogContract.View, LogPresenter> implements LogContract.View {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.layoutNull)
    View layoutNull;
    @BindView(R.id.ivEmpty)
    ImageView ivEmpty;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;

    private LogAdapter logAdapter;
    private int currentPage=0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_log;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.scene_perform_log));
        ivEmpty.setImageResource(R.drawable.icon_empty_log);
        tvEmpty.setText(getResources().getString(R.string.scene_no_log));
        initRv();
    }

    private void initRv(){
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                currentPage++;
                getData(false);
            }

            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                currentPage = 0;
                getData(false);
            }
        });
        logAdapter = new LogAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(logAdapter);
        getData(true);
    }

    /**
     * 获取数据
     */
    private void getData(boolean showLoading){
        List<NameValuePair> requestData = new ArrayList<>();
        requestData.add(new NameValuePair(Constant.START, String.valueOf(currentPage*40)));
        requestData.add(new NameValuePair(Constant.SIZE, String.valueOf(40)));
        mPresenter.getLogList(showLoading, requestData);
    }

    /**
     * 成功
     * @param logBeans
     */
    @Override
    public void getLogListSuccess(List<LogBean> logBeans) {
        setFinish();
        if (CollectionUtil.isNotEmpty(logBeans)){
            setNullView(false);
            if (checkMore(logBeans)){
                setNoMore();
            }else {
                if(currentPage == 0){
                    refreshLayout.resetNoMoreData();
                }
            }
            if (currentPage==0) {
                logAdapter.setNewData(logBeans);
            }else {
                List<LogBean> logList = logAdapter.getData();
                LogBean logBean = logList.get(logList.size()-1);
                for (LogBean lb : logBeans){
                    if (logBean.getDate().equals(lb.getDate())){
                        logBean.getItems().addAll(lb.getItems());
                    }else {
                        logAdapter.getData().add(lb);
                    }
                }
                logAdapter.notifyDataSetChanged();
            }
        }else {
            if(currentPage == 0){
                setNullView(true);
            }else {
                setNoMore();
                currentPage--;
            }
        }
    }

    /**
     * 检查日志条数是否小于40
     */
    private boolean checkMore(List<LogBean> logBeans){
        int count=0;
        for (LogBean logBean : logBeans){
            count = count+logBean.getItems().size();
        }
        return count<40;
    }

    /**
     * 失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getLogListFail(int errorCode, String msg) {
        setFinish();
        ToastUtil.show(msg);
    }

    private void setFinish(){
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    /**
     * 有无无数据
     */
    private void setNullView(boolean visible){
        recyclerView.setVisibility(visible ? View.GONE : View.VISIBLE);
        layoutNull.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 没有更多数据
     */
    private void setNoMore(){
        refreshLayout.finishLoadMoreWithNoMoreData();
    }

}