package com.yctc.zhiting.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.BaseActivity;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.yctc.zhiting.R;
import com.yctc.zhiting.adapter.DevicesAdapter;
import com.yctc.zhiting.entity.home.TestBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 设备列表
 */
public class DevicesActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    List<TestBean> list = new ArrayList<>();
    private DevicesAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_devices;
    }

    @Override
    protected void initUI() {
        super.initUI();
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                ToastUtil.show("加载更多");
                refreshLayout.finishRefresh(3000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                ToastUtil.show("刷新数据");
                refreshLayout.finishLoadMore(3000);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        for (int i = 0; i < 10; i++) {
            TestBean bean = new TestBean();
            bean.setOpen(i / 2 == 0);
            bean.setName("设备" + i);
            list.add(bean);
        }

        adapter = new DevicesAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}