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
import com.yctc.zhiting.activity.contract.DisposablePwdContract;
import com.yctc.zhiting.activity.presenter.DisposablePwdPresenter;
import com.yctc.zhiting.adapter.DisposablePwdAdapter;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.dialog.RemovedTipsDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *  一次性密码
 */
public class DisposablePwdActivity extends MVPBaseActivity<DisposablePwdContract.View, DisposablePwdPresenter> implements DisposablePwdContract.View {

    @BindView(R.id.rvPwd)
    RecyclerView rvPwd;

    private DisposablePwdAdapter mDisposablePwdAdapter;
    private CenterAlertDialog mRemovePwdDialog;
    private RemovedTipsDialog mFullTipDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_disposable_pwd;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.home_disposable_pwd));
        initRv();
    }

    @OnClick({R.id.tvAdd})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tvAdd:
//                switchToActivity(DisposablePwdAddActivity.class);
                showFullTipDialog();
                break;
        }
    }

    /**
     *  初始化列表
     */
    private void initRv() {
        rvPwd.setLayoutManager(new LinearLayoutManager(this));
        mDisposablePwdAdapter = new DisposablePwdAdapter();
        rvPwd.setAdapter(mDisposablePwdAdapter);
        List<String> data = new ArrayList<>();
        for (int i=0; i<3; i++) {
            data.add("");
        }
        mDisposablePwdAdapter.setNewData(data);
        mDisposablePwdAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int viewId = view.getId();
                switch (viewId) {
                    case R.id.tvRemove: // 删除
                        showRemoveDialog();
                        break;
                }
            }
        });
    }

    /**
     * 删除密码确认弹窗
     */
    private void showRemoveDialog() {
        if (mRemovePwdDialog == null) {
            mRemovePwdDialog = CenterAlertDialog.newInstance(UiUtil.getString(R.string.common_tips), UiUtil.getString(R.string.home_dl_remove_ask),
                    UiUtil.getString(R.string.cancel), UiUtil.getString(R.string.home_dl_remove));
        }
        if (mRemovePwdDialog!=null && !mRemovePwdDialog.isShowing()) {
            mRemovePwdDialog.show(this);
        }
    }

    /**
     * 密码数量已满
     */
    private void showFullTipDialog() {
        if (mFullTipDialog == null) {
            mFullTipDialog = new RemovedTipsDialog(UiUtil.getString(R.string.home_dl_disposable_full));
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