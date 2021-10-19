package com.app.main.framework.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.main.framework.R;
import com.app.main.framework.baseutil.UiUtil;


public class VerifyCodeView extends RelativeLayout {
    private EditText editText;
    private TextView[] textViews;
    private View[] views;
    private int MAX = 4;
    private String inputContent;
    private LinearLayout llTextInput,llIndictor;

    public VerifyCodeView(Context context) {
        this(context, null);
    }

    public VerifyCodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerifyCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.layout_sms_code, this);
        @SuppressLint({"Recycle", "CustomViewStyleable"}) TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.VerifyCodeViewStyleable);
        if (array != null){
            MAX = array.getInteger(R.styleable.VerifyCodeViewStyleable_verifyCodeLenght,4);
        }
        llTextInput = findViewById(R.id.llTextInput);
        llIndictor = findViewById(R.id.llIndictor);
        views = new View[MAX];
        textViews = new TextView[MAX];
        initView();
        editText = findViewById(R.id.edit_text_view);
        editText.setCursorVisible(false);//隐藏光标
        editText.setMaxEms(MAX);
        setEditTextListener();
        selectTextIndex(0);
    }

    public void clearText(){
        selectTextIndex(0);
        for (TextView textView:textViews){
            textView.setText("");
            inputContent = "";
            editText.setText("");
        }
    }

    public void getFocus(){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    private void initView() {
        for (int i = 0; i < MAX; i++) {
            llTextInput.addView(getInputText(i));
            llIndictor.addView(getIndictorView(i));
            if (i != (MAX-1)){
                llTextInput.addView(getSpaceView());
                llIndictor.addView(getSpaceView());
            }
        }
    }

    private TextView getInputText(int index){
        textViews[index] = new TextView(getContext());
        textViews[index].setBackgroundResource(android.R.color.transparent);
        textViews[index].setGravity(Gravity.CENTER);
        textViews[index].setTextColor(UiUtil.getColor(R.color.appBlack));
        textViews[index].setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        textViews[index].setTextSize(34);
//        textViews[index].setTextAppearance(R.style.SmsTextViewStyle);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UiUtil.getDimens(R.dimen.dp_40),UiUtil.getDimens(R.dimen.dp_40));
        textViews[index].setLayoutParams(params);
        textViews[index].setGravity(Gravity.CENTER);
        return textViews[index];
    }

    private View getIndictorView(int index){
        views[index] = new View(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UiUtil.getDimens(R.dimen.dp_40),UiUtil.getDimens(R.dimen.dp_2));
        views[index].setLayoutParams(params);
        views[index].setBackgroundColor(UiUtil.getColor(R.color.appLightGray));
        return views[index];
    }

    private View getSpaceView(){
        View view = new View(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UiUtil.getDimens(R.dimen.dp_20), ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return view;
    }

    public void setMAX(int MAX) {
        this.MAX = MAX;
    }

    private void setEditTextListener() {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int index = i - i1;
                if (index < MAX && index > -1) {
                    selectTextIndex(index);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                inputContent = editText.getText().toString();
                for (int i = 0; i < MAX; i++) {
                    if (i < inputContent.length()) {
                        textViews[i].setText(String.valueOf(inputContent.charAt(i)));
                    } else {
                        textViews[i].setText("");
                    }
                }
                if (inputCompleteListener != null) {
                    if (!TextUtils.isEmpty(inputContent) && inputContent.length() >= MAX) {
                        inputCompleteListener.inputComplete();
                        return;
                    } else {
                        inputCompleteListener.invalidContent();
                    }
                }
            }
        });
    }

    private void selectTextIndex(int index) {
        for (int i = 0; i < views.length; i++) {
            views[i].setBackgroundColor(UiUtil.getColor(R.color.appLightGray));
        }
        views[index].setBackgroundColor(UiUtil.getColor(R.color.appBlack));
    }


    private InputCompleteListener inputCompleteListener;

    public void setInputCompleteListener(InputCompleteListener inputCompleteListener) {
        this.inputCompleteListener = inputCompleteListener;
    }

    public interface InputCompleteListener {

        void inputComplete();

        void invalidContent();
    }

    public String getEditContent() {
        return inputContent;
    }

}