package com.yctc.zhiting.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;

public class CenterAlertDialog extends CommonBaseDialog {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvTip)
    TextView tvTip;
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.rbConfirm)
    ProgressBar rbConfirm;
    @BindView(R.id.llDelFolder)
    LinearLayout llDelFolder;

    private String title; // 标题
    private String tip;  // 提示
    private String cancelStr;  // 取消文案
    private String confirmStr;  // 确认文案
    private boolean showLoading;  // 是否显示加载
    private boolean mShowDelFolder;  // 是否显示删除家庭时是否删除云盘文件

    public static CenterAlertDialog newInstance(String title, String tip, String cancelStr, String confirmStr) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("tip", tip);
        args.putString("cancelStr", cancelStr);
        args.putString("confirmStr", confirmStr);

        CenterAlertDialog fragment = new CenterAlertDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static CenterAlertDialog newInstance(String title, String tip) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("tip", tip);
        CenterAlertDialog fragment = new CenterAlertDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static CenterAlertDialog newInstance(String title, String tip, boolean showLoading) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("tip", tip);
        args.putBoolean("showLoading", showLoading);
        CenterAlertDialog fragment = new CenterAlertDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static CenterAlertDialog newInstance(String title, String cancelStr, String confirmStr, boolean showLoading) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("cancelStr", cancelStr);
        args.putString("confirmStr", confirmStr);
        args.putBoolean("showLoading", showLoading);
        CenterAlertDialog fragment = new CenterAlertDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static CenterAlertDialog newInstance(String title, String tip, String cancelStr, String confirmStr, boolean showLoading) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("tip", tip);
        args.putString("cancelStr", cancelStr);
        args.putString("confirmStr", confirmStr);
        args.putBoolean("showLoading", showLoading);
        CenterAlertDialog fragment = new CenterAlertDialog();
        fragment.setArguments(args);
        return fragment;
    }


    public static CenterAlertDialog newInstance(String title, String tip, boolean showLoading, boolean showDelFolder) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("tip", tip);
        args.putBoolean("showLoading", showLoading);
        args.putBoolean("showDelFolder", showDelFolder);
        CenterAlertDialog fragment = new CenterAlertDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_center_alert;
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
        title = arguments.getString("title");
        tip = arguments.getString("tip");
        showLoading = arguments.getBoolean("showLoading", false);
        mShowDelFolder = arguments.getBoolean("showDelFolder", false);
        cancelStr = arguments.getString("cancelStr");
        confirmStr = arguments.getString("confirmStr");
    }

    @Override
    protected void initView(View view) {
        tvTitle.setText(title);
        tvTip.setText(tip);
        tvTip.setVisibility(TextUtils.isEmpty(tip) ? View.GONE : View.VISIBLE);
        llDelFolder.setVisibility(mShowDelFolder ? View.VISIBLE : View.GONE);
        if (!TextUtils.isEmpty(cancelStr))
            tvCancel.setText(cancelStr);
        if (!TextUtils.isEmpty(confirmStr))
            tvConfirm.setText(confirmStr);

    }


    @OnClick(R.id.tvCancel)
    void onClickCancel() {
        if (cancelListener != null) {
            cancelListener.onCancel();
        }
        dismiss();
    }

    @OnClick(R.id.tvConfirm)
    void onClickConfirm() {
        if (confirmListener != null) {
            if (showLoading) {
                tvConfirm.setVisibility(View.GONE);
                rbConfirm.setVisibility(View.VISIBLE);
            }
            confirmListener.onConfirm(llDelFolder.isSelected());
        }
    }

    @OnClick(R.id.llDelFolder)
    void onClickDelFolder() {
        llDelFolder.setSelected(!llDelFolder.isSelected());
    }


    private OnConfirmListener confirmListener;
    private OnCancelListener cancelListener;

    public OnConfirmListener getConfirmListener() {
        return confirmListener;
    }

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public OnCancelListener getCancelListener() {
        return cancelListener;
    }

    public void setCancelListener(OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (showLoading) {
            tvConfirm.setVisibility(View.VISIBLE);
            rbConfirm.setVisibility(View.GONE);
        }
        if (dismissListener != null) {
            dismissListener.onDismiss();
        }
    }

    public interface OnConfirmListener {
        void onConfirm(boolean del);
    }

    public interface OnCancelListener {
        void onCancel();
    }

    private OnDismissListener dismissListener;

    public OnDismissListener getDismissListener() {
        return dismissListener;
    }

    public void setDismissListener(OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
