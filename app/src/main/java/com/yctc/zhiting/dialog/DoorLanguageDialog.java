package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.BindView;

/**
 * Author by Ouyangle, Date on 2022/4/11.
 * PS: Not easy to write code, please indicate.
 */
public class DoorLanguageDialog extends CommonBaseDialog {
    @BindView(R.id.ivClose)
    ImageView ivClose;
    @BindView(R.id.rbCn)
    RadioButton rbCn;
    @BindView(R.id.rbEs)
    RadioButton rbEs;

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {
        ivClose.setOnClickListener(v -> dismiss());
        rbCn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                rbEs.setChecked(false);
            }
        });
        rbEs.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                rbCn.setChecked(false);
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_door_language;
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
