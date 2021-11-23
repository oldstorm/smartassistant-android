package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.AgreementAndPolicyContract;
import com.yctc.zhiting.activity.presenter.AgreementAndPolicyPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.utils.IntentConstant;

import butterknife.OnClick;

/**
 * 用户协议和隐私整肠丸
 */
public class AgreementAndPolicyActivity extends MVPBaseActivity<AgreementAndPolicyContract.View, AgreementAndPolicyPresenter> implements AgreementAndPolicyContract.View {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_agreement_and_policy;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.mine_user_agreement_and_privacy_policy));
    }

    @OnClick({R.id.tvAgreement, R.id.tvPolicy})
    void onClick(View view){
        int viewId = view.getId();
        if (viewId == R.id.tvAgreement){
            Bundle bundle = new Bundle();
            bundle.putString(IntentConstant.TITLE, UiUtil.getString(R.string.user_agreement));
            bundle.putString(IntentConstant.URL, Constant.AGREEMENT_URL);
            switchToActivity(NormalWebActivity.class, bundle);
        }else if (viewId == R.id.tvPolicy){
            Bundle bundle = new Bundle();
            bundle.putString(IntentConstant.TITLE, UiUtil.getString(R.string.privacy_policy));
            bundle.putString(IntentConstant.URL, Constant.POLICY_URL);
            switchToActivity(NormalWebActivity.class, bundle);
        }
    }
}