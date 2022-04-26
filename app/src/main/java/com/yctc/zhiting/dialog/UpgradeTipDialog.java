package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.BindView;

public class UpgradeTipDialog extends CommonBaseDialog {

    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.tvLeft)
    TextView tvLeft;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.llBtn)
    LinearLayout llBtn;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.tvProgress)
    TextView tvProgress;
    @BindView(R.id.tvCancel)
    TextView tvCancel;

    public static final String TIP_TXT = "tipTxt";
    public static final String LEFT_TXT = "leftTxt";
    public static final String RIGHT_TXT = "rightTxt";

    private String tipTxt;  // 提示文案
    private String leftTxt; // 左边按钮文本
    private String rightTxt;  // 右边按钮文本
    private DownloadStatus downloadStatus; // 下载状态


    public static UpgradeTipDialog getInstance(String tipTxt, String leftTxt, String rightTxt) {
        UpgradeTipDialog tipDialog = new UpgradeTipDialog();
        Bundle arguments = new Bundle();
        arguments.putString(TIP_TXT, tipTxt);
        arguments.putString(LEFT_TXT, leftTxt);
        arguments.putString(RIGHT_TXT, rightTxt);
        tipDialog.setArguments(arguments);
        return tipDialog;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        tipTxt = arguments.getString(TIP_TXT);
        leftTxt = arguments.getString(LEFT_TXT);
        rightTxt = arguments.getString(RIGHT_TXT);
    }

    @Override
    protected void initView(View view) {
        setCancelable(false);
        tvContent.setText(tipTxt);
        tvLeft.setText(leftTxt);
        tvRight.setText(rightTxt);
        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCallback!=null) {
                    clickCallback.onClickLeft();
                }
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCallback!=null) {
                    clickCallback.onClickRight();
                }
            }
        });

        tvProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCallback!=null) {
                    clickCallback.onClickCenter(downloadStatus);
                }
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickCallback!=null) {
                    clickCallback.onClickCancel();
                }
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_upgrade_tips;
    }

    @Override
    protected int obtainWidth() {
        return dp2px(300);
    }

    @Override
    protected int obtainHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.CENTER;
    }

    /**
     * 开始下载
     */
    public void startDownloading() {
        downloadStatus = DownloadStatus.PENDING_STATUS;
        progressbar.setProgress(0);
        llBtn.setVisibility(View.GONE);
        tvCancel.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);
        tvProgress.setVisibility(View.VISIBLE);
    }

    /**
     * 更新进度条
     * @param progress
     */
    public void updateProgress(int progress) {
        downloadStatus = DownloadStatus.DOWNLOADING_STATUS;
        progressbar.setProgress(progress);
        String downloadTxt = String.format(UiUtil.getString(R.string.home_download_progress), progress+"%");
        tvProgress.setText(downloadTxt);
    }

    public void downloadCompleted() {
        progressbar.setVisibility(View.GONE);
        downloadStatus =  DownloadStatus.DOWNLOAD_FINISH;
        tvCancel.setVisibility(View.VISIBLE);
        tvProgress.setText(UiUtil.getString(R.string.home_install));
    }

    /**
     * 出错
     */
    public void setError() {
        downloadStatus = DownloadStatus.DOWNLOAD_ERROR;
        progressbar.setProgress(0);
        tvCancel.setVisibility(View.VISIBLE);
        tvProgress.setText(UiUtil.getString(R.string.home_downloading_error));
    }

    private OnClickCallback clickCallback;

    public void setClickCallback(OnClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }


    public abstract static class OnClickCallback {
        public void onClickLeft() {}
        public void onClickRight(){}
        public void onClickCenter(DownloadStatus downloadStatus){}
        public void onClickCancel(){};
    }

    public enum DownloadStatus{
        PENDING_STATUS (0),
        DOWNLOADING_STATUS(1),
        DOWNLOAD_FINISH(2),
        DOWNLOAD_ERROR(-1)
        ;

        private int status;

        DownloadStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
