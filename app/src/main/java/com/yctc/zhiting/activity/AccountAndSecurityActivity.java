package com.yctc.zhiting.activity;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.AccountAndSecurityContract;
import com.yctc.zhiting.activity.presenter.AccountAndSecurityPresenter;
import com.yctc.zhiting.adapter.AccountSecurityAdapter;
import com.yctc.zhiting.entity.mine.ABFunctionBean;

import butterknife.BindView;

/**
 * 账号与安全
 */
public class AccountAndSecurityActivity extends MVPBaseActivity<AccountAndSecurityContract.View, AccountAndSecurityPresenter> implements AccountAndSecurityContract.View {

    @BindView(R.id.rvFunction)
    RecyclerView rvFunction;

    private AccountSecurityAdapter mAccountSecurityAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_account_and_security;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.mine_account_and_security));
        initRv();
    }

    private void initRv() {
        mAccountSecurityAdapter = new AccountSecurityAdapter();
        rvFunction.setLayoutManager(new LinearLayoutManager(this));
        rvFunction.setAdapter(mAccountSecurityAdapter);
        mAccountSecurityAdapter.setNewData(ABFunctionBean.getData());
        mAccountSecurityAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ABFunctionBean functionBean = mAccountSecurityAdapter.getItem(position);
                switch (functionBean) {
                    case PWD_MODIFY:
                        switchToActivity(PwdModifyActivity.class);
                        break;

                    case ACCOUNT_CANCELLATION:
                        switchToActivity(AccountCancellationActivity.class);
                        break;
                }
            }
        });
    }
}