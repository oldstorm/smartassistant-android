package com.yctc.zhiting.activity;


import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.main.framework.baseutil.DateUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DLAddPwdContract;
import com.yctc.zhiting.activity.presenter.DLAddPwdPresenter;
import com.yctc.zhiting.bean.ValidPeriodDialog;
import com.yctc.zhiting.dialog.CalendarSelectDialog;
import com.yctc.zhiting.widget.CustomEditTextView;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class DLAddPwdActivity extends MVPBaseActivity<DLAddPwdContract.View, DLAddPwdPresenter> implements DLAddPwdContract.View {

    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.ivEye)
    ImageView ivEye;
    @BindView(R.id.cetv)
    CustomEditTextView cetv;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvPeriod)
    TextView tvPeriod;

    private ValidPeriodDialog mValidPeriodDialog;
    private CalendarSelectDialog mCalendarSelectDialog;

    private boolean mHidePwd; // 是否隐藏密码

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dl_add_pwd;
    }


    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.home_dl_add_password));
    }

    @OnClick({R.id.ivEye, R.id.tvSave, R.id.clDate, R.id.clPeriod})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.ivEye:
                mHidePwd = !mHidePwd;
                ivEye.setImageResource(mHidePwd ? R.drawable.icon_password_visible : R.drawable.icon_password_invisible);
                cetv.setmHideText(mHidePwd);
                break;

            case R.id.tvSave:
//                switchToActivity(DLAddPwdFailActivity.class);
                switchToActivity(DLAddPwdSuccessActivity.class);
                break;

            case R.id.clDate:
                showCalendarSelectDialog();
                break;

            case R.id.clPeriod:
                showValidPeriodDialog();
                break;
        }
    }

    /**
     *  日历弹窗
     */
    private void showCalendarSelectDialog(){
        if (mCalendarSelectDialog == null) {
            mCalendarSelectDialog = new CalendarSelectDialog();
            mCalendarSelectDialog.setListener(new CalendarSelectDialog.OnDateSelectedListener() {
                @Override
                public void onDateSelected(Date dateIn, Date dateOut) {
                    String beginDay = DateUtil.dateToString(dateIn, DateUtil.DATE_FORMAT_YMD_SPLIT_DOT);
                    String endDay = DateUtil.dateToString(dateOut, DateUtil.DATE_FORMAT_YMD_SPLIT_DOT);
                    tvDate.setText(beginDay + "-" + endDay);
                }
            });
        }
        mCalendarSelectDialog.show(this);
    }

    /**
     * 时段弹窗
     */
    private void showValidPeriodDialog() {
        if (mValidPeriodDialog == null) {
            mValidPeriodDialog = new ValidPeriodDialog();
            mValidPeriodDialog.setConfirmListener(new ValidPeriodDialog.OnConfirmListener() {
                @Override
                public void onConfirm(String beginTime, String endTime) {
                    tvPeriod.setText(beginTime + "-" + endTime);
                    mValidPeriodDialog.dismiss();
                }
            });
        }
        mValidPeriodDialog.show(this);
    }
}