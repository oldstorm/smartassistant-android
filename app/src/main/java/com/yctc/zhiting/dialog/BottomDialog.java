package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

/**
 *
 * date : 2021/4/3013:05
 * desc :
 */
public class BottomDialog extends CommonBaseDialog {

    public static BottomDialog newInstance() {
//        Bundle args = new Bundle();
//        args.putInt(ERROR_CODE, errorCode);
//        args.putString(MSG, alertMsg);

        BottomDialog fragment = new BottomDialog();
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_bottom;
    }

    @Override
    protected int obtainWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int obtainHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {

    }
}
