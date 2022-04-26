package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DisposablePwdAddContract;
import com.yctc.zhiting.activity.contract.DisposablePwdContract;
import com.yctc.zhiting.activity.presenter.DisposablePwdAddPresenter;
import com.yctc.zhiting.widget.CustomEditTextView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *  添加一次性密码
 */
public class DisposablePwdAddActivity extends MVPBaseActivity<DisposablePwdAddContract.View, DisposablePwdAddPresenter> implements DisposablePwdContract.View {

    @BindView(R.id.cetv)
    CustomEditTextView cetv;
    @BindView(R.id.ivEye)
    ImageView ivEye;
    @BindView(R.id.tvPwd)
    TextView tvPwd;
    @BindView(R.id.tvCopy)
    TextView tvCopy;

    private boolean mHidePwd; // 是否隐藏密码

    @Override
    protected int getLayoutId() {
        return R.layout.activity_disposable_pwd_add;
    }

    @OnClick({R.id.ivEye, R.id.tvGenerate})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId){

            case R.id.ivEye:
                mHidePwd = !mHidePwd;
                ivEye.setImageResource(mHidePwd ? R.drawable.icon_password_visible : R.drawable.icon_password_invisible);
                cetv.setmHideText(mHidePwd);
                break;

            case R.id.tvGenerate:
                setPwdVisible();
                break;
        }
    }

    private void setPwdVisible() {
        cetv.setVisibility(View.GONE);
        tvPwd.setVisibility(View.VISIBLE);
        tvCopy.setVisibility(View.VISIBLE);
    }
}