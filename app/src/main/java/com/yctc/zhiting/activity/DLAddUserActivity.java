package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DLAddUserContract;
import com.yctc.zhiting.activity.presenter.DLAddUserPresenter;
import com.yctc.zhiting.adapter.DLAddUserAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 门锁添加用户
 */
public class DLAddUserActivity extends MVPBaseActivity<DLAddUserContract.View, DLAddUserPresenter> implements DLAddUserContract.View {

    @BindView(R.id.rvType)
    RecyclerView rvType;

    private DLAddUserAdapter mDLAddUserAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dladd_user;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.home_add_user));
        initRv();
    }

    /**
     * 初始化列表
     */
    private void initRv() {
        rvType.setLayoutManager(new LinearLayoutManager(this));
        mDLAddUserAdapter = new DLAddUserAdapter();
        rvType.setAdapter(mDLAddUserAdapter);
        List<String> data = new ArrayList<>();
        for (int i=0; i<5; i++) {
            data.add("");
        }
        mDLAddUserAdapter.setNewData(data);
    }
}