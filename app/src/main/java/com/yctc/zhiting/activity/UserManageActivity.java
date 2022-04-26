package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.UserManageContract;
import com.yctc.zhiting.activity.presenter.UserManagePresenter;
import com.yctc.zhiting.adapter.UserManageAdapter;
import com.yctc.zhiting.dialog.RemovedTipsDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *  门锁用户管理
 */
public class UserManageActivity extends MVPBaseActivity<UserManageContract.View, UserManagePresenter> implements UserManageContract.View {

    @BindView(R.id.rvManager)
    RecyclerView rvManager;

    UserManageAdapter mUserManageAdapter;

    private RemovedTipsDialog mFullTipDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_manage;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.home_user_manage));
        initRv();
    }

    private void initRv() {
        rvManager.setLayoutManager(new LinearLayoutManager(this));
        mUserManageAdapter = new UserManageAdapter();
        rvManager.setAdapter(mUserManageAdapter);
        List<String> data = new ArrayList<>();
        for (int i=0; i<4; i++) {
            data.add("");
        }
        mUserManageAdapter.setNewData(data);
        mUserManageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
    }

    @OnClick({R.id.tvBind, R.id.tvAdd})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tvBind:
                switchToActivity(DLAuthWayActivity.class);
                break;

            case R.id.tvAdd:
//                switchToActivity(DLAddUserActivity.class);
                showFullTipDialog();
                break;
        }
    }

    /**
     * 密码数量已满
     */
    private void showFullTipDialog() {
        if (mFullTipDialog == null) {
            mFullTipDialog = new RemovedTipsDialog(UiUtil.getString(R.string.home_dl_disposable_visitor_full));
            mFullTipDialog.setKnowListener(new RemovedTipsDialog.OnKnowListener() {
                @Override
                public void onKnow() {
                    mFullTipDialog.dismiss();
                }
            });
        }
        if (mFullTipDialog != null && !mFullTipDialog.isShowing()) {
            mFullTipDialog.show(this);
        }
    }
}