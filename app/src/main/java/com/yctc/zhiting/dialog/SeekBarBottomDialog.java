package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;
import com.yctc.zhiting.bean.ListBottomBean;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SeekBarBottomDialog extends CommonBaseDialog {

    @BindView(R.id.tvLess)
    TextView tvLess;
    @BindView(R.id.tvEqual)
    TextView tvEqual;
    @BindView(R.id.tvGreater)
    TextView tvGreater;
    @BindView(R.id.tvValue)
    TextView tvValue;
    @BindView(R.id.seekbar)
    SeekBar seekBar;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.llOperator)
    LinearLayout llOperator;


    private String operator = "<";
    private String operatorName = "";
    private int val=0;

    /**
     * 1.亮度
     * 2.色温
     */
    private int type;
    private String title;
    private boolean showOperator;

    public SeekBarBottomDialog() {
    }

    public SeekBarBottomDialog(int type, String title, boolean showOperator) {
        this.type = type;
        this.title = title;
        this.showOperator = showOperator;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_seek_bar_bottom;
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

    @Override
    protected void initArgs(Bundle arguments) {
        val = arguments.getInt("val", 0);
        operator = arguments.getString("operator");
    }

    @Override
    protected void initView(View view) {
        tvTitle.setText(title);
        tvValue.setText(val+"");
        seekBar.setProgress(val);
        if (TextUtils.isEmpty(operator)){
            operator = "<";
            operatorName = getResources().getString(R.string.scene_less);
            resetOperator(1);
        }else {
            switch (operator) {
                case "<":
                    operatorName = getResources().getString(R.string.scene_less);
                    resetOperator(1);
                    break;

                case "=":
                    operatorName = getResources().getString(R.string.scene_equal);
                    resetOperator(2);
                    break;


                case ">":
                    operatorName = getResources().getString(R.string.scene_greater);
                    resetOperator(3);
                    break;
            }
        }
//        tvLess.setSelected(true);
        llOperator.setVisibility(showOperator ? View.VISIBLE : View.GONE);
        seekBar.setProgressDrawable(type == 1 ? UiUtil.getDrawable(R.drawable.seekbar_shape_2) : UiUtil.getDrawable(R.drawable.seekbar_shape));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                val = progress;
                tvValue.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @OnClick({R.id.tvLess, R.id.tvEqual, R.id.tvGreater})
    void onClickOperator(View view){
        switch (view.getId()){
            case R.id.tvLess:
                operatorName = getResources().getString(R.string.scene_less);
                resetOperator(1);
                break;

            case R.id.tvEqual:
                operatorName = getResources().getString(R.string.scene_equal);
                resetOperator(2);
                break;

            case R.id.tvGreater:
                operatorName = getResources().getString(R.string.scene_greater);
                resetOperator(3);
                break;
        }
    }

    /**
     * 重置操作符
     * @param type
     */
    private void resetOperator(int type){
        switch (type){
            case 1:
                operator = "<";
                resetTextView(tvLess);
                break;

            case 2:
                operator = "=";
                resetTextView(tvEqual);
                break;

            case 3:
                operator = ">";
                resetTextView(tvGreater);
                break;
        }
    }

    /**
     * 重置文本显示
     * @param textView
     */
    private void resetTextView(TextView textView){
        tvLess.setSelected(false);
        tvEqual.setSelected(false);
        tvGreater.setSelected(false);
        textView.setSelected(true);
    }

    @OnClick(R.id.ivClose)
    void onClickClose(){
        dismiss();
    }

    @OnClick(R.id.tvTodo)
    void onClickTodo(){
        if (clickTodoListener!=null){
            clickTodoListener.onTodo(operator, operatorName, val);
        }
    }

    private OnClickTodoListener clickTodoListener;

    public OnClickTodoListener getClickTodoListener() {
        return clickTodoListener;
    }

    public void setClickTodoListener(OnClickTodoListener clickTodoListener) {
        this.clickTodoListener = clickTodoListener;
    }

    public interface  OnClickTodoListener{
        void onTodo(String operator, String operatorName,  int val);
    }
}
