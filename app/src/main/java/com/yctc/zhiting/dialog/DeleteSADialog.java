package com.yctc.zhiting.dialog;

import static com.yctc.zhiting.config.Constant.CurrentHome;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.BaseActivity;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.LoginActivity;
import com.yctc.zhiting.utils.UserUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class DeleteSADialog extends CommonBaseDialog {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.login)
    TextView login;
    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.tvCloudHomeDeleteTip)
    TextView tvCloudHomeDeleteTip;
    @BindView(R.id.tvLoginTip)
    TextView tvLoginTip;
    @BindView(R.id.tvCreateCloud)
    TextView tvCreateCloud;
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.llCreateCloudHome)
    LinearLayout llCreateCloudHome;
    @BindView(R.id.llNoLogin)
    LinearLayout llNoLogin;
    @BindView(R.id.llLogin)
    LinearLayout llLogin;
    @BindView(R.id.llCloudDisk)
    LinearLayout llCloudDisk;
    @BindView(R.id.ivDotCreateHomeOrCompany)
    ImageView ivDotCreateHomeOrCompany;
    @BindView(R.id.ivDotDeleteCloud)
    ImageView ivDotDeleteCloud;
    private boolean isDisk;//是否有网盘

    public static DeleteSADialog newInstance(boolean isHasDisk) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isDisk", isHasDisk);
        DeleteSADialog fragment = new DeleteSADialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_delete_sa;
    }

    @Override
    protected int obtainWidth() {
        return dp2px(300);
    }

    @Override
    protected int obtainHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        isDisk = arguments.getBoolean("isDisk");
    }

    @Override
    protected void initView(View view) {
        boolean isCloudHome = CurrentHome.getArea_id() > 0 && CurrentHome.getId() > 0;
        String type = CurrentHome.getArea_type() == 1 ? UiUtil.getString(R.string.home_delete_home) : UiUtil.getString(R.string.home_delete_company);
        tvContent.setText(String.format(UiUtil.getString(R.string.home_delete_local_sa), type, CurrentHome.getName()));
        String typeLoginTip = CurrentHome.getArea_type() == 1 ? UiUtil.getString(R.string.home_name) : UiUtil.getString(R.string.home_company_name);
        tvLoginTip.setText(String.format(UiUtil.getString(R.string.home_login_tip), typeLoginTip));
        tvCloudHomeDeleteTip.setText(String.format(UiUtil.getString(R.string.home_cloud_delete_tip), typeLoginTip));
        tvCreateCloud.setText(String.format(UiUtil.getString(R.string.home_create_cloud_home), typeLoginTip));

         if (isCloudHome) {
            llLogin.setVisibility(View.VISIBLE);
            llNoLogin.setVisibility(View.GONE);
            llCreateCloudHome.setVisibility(View.VISIBLE);
        } else {
            llLogin.setVisibility(View.GONE);
            if (!UserUtils.isLogin())
                llNoLogin.setVisibility(View.VISIBLE);
        }
        llCloudDisk.setVisibility(isDisk ? View.VISIBLE : View.GONE);

        llCloudDisk.setOnTouchListener((v, event) -> {
            ivDotDeleteCloud.setSelected(!ivDotDeleteCloud.isSelected());
            return false;
        });

        llCreateCloudHome.setOnTouchListener((v, event) -> {
            ivDotCreateHomeOrCompany.setSelected(!ivDotCreateHomeOrCompany.isSelected());
            return false;
        });
    }

    @OnClick({R.id.tvCancel, R.id.tvConfirm, R.id.login})
    public void onClick(View view) {
        dismiss();
        if (view.getId() == R.id.tvConfirm) {
            if (mConfirmListener != null) {
                mConfirmListener.onConfirm(ivDotCreateHomeOrCompany.isSelected(), ivDotDeleteCloud.isSelected());
            }
        } else if (view.getId() == R.id.login) {
            ((BaseActivity) (LibLoader.getCurrentActivity())).switchToActivity(LoginActivity.class);
        }
    }

    private OnConfirmListener mConfirmListener;

    public void setConfirmListener(OnConfirmListener mConfirmListener) {
        this.mConfirmListener = mConfirmListener;
    }

    public interface OnConfirmListener {
        void onConfirm(boolean isDeleteCloudDisk, boolean isCreateCloudHome);
    }
}
