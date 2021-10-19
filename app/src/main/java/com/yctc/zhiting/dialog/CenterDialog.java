package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

/**
 *
 * date : 2021/4/3013:05
 * desc :
 */
public class CenterDialog extends CommonBaseDialog {

    public static CenterDialog newInstance() {
//        Bundle args = new Bundle();
//        args.putInt(ERROR_CODE, errorCode);
//        args.putString(MSG, alertMsg);

        CenterDialog fragment = new CenterDialog();
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_home;
    }

    @Override
    protected int obtainWidth() {
        return dp2px(300);
    }

    @Override
    protected int obtainHeight() {
        return dp2px(225);
    }

    @Override
    protected int obtainGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {

    }
}
