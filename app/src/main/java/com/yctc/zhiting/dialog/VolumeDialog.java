package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;
import com.yctc.zhiting.widget.VolumeProgressBar;

import butterknife.BindView;

/**
 * Author by Ouyangle, Date on 2022/4/11.
 * PS: Not easy to write code, please indicate.
 */
public class VolumeDialog extends CommonBaseDialog {
    @BindView(R.id.ivClose)
    ImageView ivClose;
    @BindView(R.id.volumeProgressBar)
    VolumeProgressBar volumeProgressBar;
    private VolumeProgressBar.OnVolumeChangeListener volumeChangeListener;

    public VolumeDialog(VolumeProgressBar.OnVolumeChangeListener listener) {
        this.volumeChangeListener = listener;
    }

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {
        ivClose.setOnClickListener(v -> dismiss());
        volumeProgressBar.setVolumeChangeListener(volumeChangeListener);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_door_volume;
    }

    @Override
    protected int obtainWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int obtainHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.BOTTOM;
    }
}
