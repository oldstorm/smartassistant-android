package com.app.main.framework.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import com.app.main.framework.R;
import com.app.main.framework.baseutil.UiUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonEditText extends FrameLayout {
    private static final String TYPE_PASSWORD = UiUtil.getString(R.string.inputTypePassword); //密码类型
    private static final String TYPE_NUMBER = UiUtil.getString(R.string.inputTypeNumber); //数字类型
    private static final String TYPE_MOBILE = UiUtil.getString(R.string.inputTypeMobile); //电话号码类型
    private static final int DEFAULT_MAX_LENGTH = 20; //默认输入文本长度
    private static final float DEFAULT_HINT_TEXT_SIZE = 12; //默认提示文本大小
    private static final float DEFAULT_TEXT_SIZE = 18; //默认输入文本大小
    private static final int DEFAULT_TEXT_COLOR = UiUtil.getColor(R.color.appBlack); //默认文本颜色
    private static final int DEFAULT_TITLE_TEXT_COLOR = UiUtil.getColor(R.color.appGray); //默认标题文本颜色
    private static final int DEFAULT_ERROR_TEXT_COLOR = UiUtil.getColor(R.color.appOrange); //默认错误文本颜色
    private static final int STATE_NULL_COLOR = UiUtil.getColor(R.color.appLightGray); //文本框为空且无焦点时

    private EditText etInputText; //正文输入框
    private TextView tvTitleText; //顶上提示
    private TextView tvErrorText; //错误提示
    private ImageView ivPasswordToggle; //密码显示
    private ImageView ivClearInputText; //清除输入框文本
    private TextView tvMobilePrefix; //手机号前缀

    private int errorTextColor;
    private int inputTextColor;
    private String inputType;
    private boolean isShowPassword = false;

    public CommonEditText(Context context) {
        super(context);
        initEditText(context,null);
    }

    public CommonEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEditText(context,attrs);
    }

    public CommonEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initEditText(context,attrs);
    }

    private void initEditText(Context context,AttributeSet attrs) {
        initUI();
        initAction();
        if (attrs != null){
            @SuppressLint("CustomViewStyleable")
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CommonEditTextStyleable);
            //输入框类型
            inputType = array.getString(R.styleable.CommonEditTextStyleable_cetInputType);
            //显示文本
            String text = array.getString(R.styleable.CommonEditTextStyleable_catInputText);
            //提示文本
            String hintText = array.getString(R.styleable.CommonEditTextStyleable_catInputHintText);
            //文本输入限制
            String digits = array.getString(R.styleable.CommonEditTextStyleable_catInputDigits);
            //文本限制长度
            int maxLength = array.getInteger(R.styleable.CommonEditTextStyleable_catInputMaxLength, DEFAULT_MAX_LENGTH);
            //文本颜色
            inputTextColor = array.getColor(R.styleable.CommonEditTextStyleable_catInputTextColor, DEFAULT_TEXT_COLOR);
            //文本大小
            float inputTextSize = array.getDimension(R.styleable.CommonEditTextStyleable_catInputTextSize, DEFAULT_TEXT_SIZE);
            setInputType(inputType);
            setInputText(text);
            setInputHintText(hintText);
            setInputTextMaxLength(maxLength);
            setInputDigits(digits);
            if (TYPE_MOBILE.equals(inputType))
                setInputTextColor(inputTextColor);
            else
                setInputTextColor(STATE_NULL_COLOR);
            setInputTextSize(inputTextSize);

            //错误文本尺寸
            float errorTextSize = array.getDimension(R.styleable.CommonEditTextStyleable_cetErrorTextSize, DEFAULT_HINT_TEXT_SIZE);
            //错误文本颜色
            errorTextColor = array.getColor(R.styleable.CommonEditTextStyleable_cetErrorTextColor, DEFAULT_ERROR_TEXT_COLOR);
            //错误文本
            String errorText = array.getString(R.styleable.CommonEditTextStyleable_cetErrorText);
            setErrorTextSize(errorTextSize);
            setErrorTextColor(errorTextColor);
            setErrorText(errorText);

            //顶部文本尺寸
            float topTextSize = array.getDimension(R.styleable.CommonEditTextStyleable_cetTitleTextSize, DEFAULT_HINT_TEXT_SIZE);
            //顶部文本颜色
            int topTextColor = array.getColor(R.styleable.CommonEditTextStyleable_cetTitleTextColor, DEFAULT_TITLE_TEXT_COLOR);
            //顶部文本
            String topText = array.getString(R.styleable.CommonEditTextStyleable_cetTitleText);
            setTitleTextSize(topTextSize);
            setTitleTextColor(topTextColor);
            setTitleText(topText);
            array.recycle();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        etInputText.setEnabled(enabled);
    }

    public EditText getInputText(String transitionName){
        if (!TextUtils.isEmpty(transitionName))
            etInputText.setTransitionName(transitionName);
        return etInputText;
    }

    public void setInputDigits(String digits) {
        if (!TextUtils.isEmpty(digits))
            etInputText.setKeyListener(DigitsKeyListener.getInstance(digits));
    }

    private void initAction() {
        ivPasswordToggle.setOnClickListener(v->{
            if (isShowPassword) {
                isShowPassword = false;
                ivPasswordToggle.setImageResource(R.mipmap.m_eye_close);
                etInputText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }else{
                isShowPassword = true;
                ivPasswordToggle.setImageResource(R.mipmap.m_eye_open);
                etInputText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            Editable text = etInputText.getText();
            if (text != null && text.length() > 0)
                etInputText.setSelection(text.length());
        });
        ivClearInputText.setOnClickListener(v-> etInputText.setText(""));
        etInputText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                setInputTextColor(inputTextColor);
            else {
                if (etInputText.getText().length() > 0){
                    etInputText.setTextColor(inputTextColor);
                    etInputText.getBackground().setColorFilter(STATE_NULL_COLOR, PorterDuff.Mode.SRC_ATOP);
                }else
                    setInputTextColor(STATE_NULL_COLOR);
            }
        });
    }

    private void initUI() {
        View rootView = UiUtil.inflate(R.layout.layout_input_text, this);
        etInputText = rootView.findViewById(R.id.etInputText);
        tvMobilePrefix = rootView.findViewById(R.id.tvMobilePrefix);
        tvTitleText = rootView.findViewById(R.id.tvTitleText);
        tvErrorText = rootView.findViewById(R.id.tvErrorText);
        ivPasswordToggle = rootView.findViewById(R.id.ivPasswordToggle);
        ivClearInputText = rootView.findViewById(R.id.ivClearInputText);
        addView(rootView);
    }

    public void setInputType(String inputType){
        if (TYPE_PASSWORD.equals(inputType)){
            etInputText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etInputText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivPasswordToggle.setVisibility(VISIBLE);
        }else if (TYPE_NUMBER.equals(inputType)){
            etInputText.setInputType(InputType.TYPE_CLASS_NUMBER);
            etInputText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivPasswordToggle.setVisibility(GONE);
        }else if (TYPE_MOBILE.equals(inputType)){
            etInputText.setInputType(InputType.TYPE_CLASS_TEXT);
            etInputText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            tvMobilePrefix.setVisibility(VISIBLE);
            etInputText.setPadding(UiUtil.getDimens(R.dimen.dp_40),0,0,0);
            ivPasswordToggle.setVisibility(GONE);
        }else {
            etInputText.setInputType(InputType.TYPE_CLASS_TEXT);
            etInputText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivPasswordToggle.setVisibility(GONE);
        }
    }

    public void setInputText(String inputText){
        if (!TextUtils.isEmpty(inputText)){
            etInputText.setTextColor(inputTextColor);
            etInputText.setText(inputText);
            etInputText.setSelection(inputText.length());
        }
    }

    public void addTextChangeListener(TextWatcher listener){
        etInputText.addTextChangedListener(listener);
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener listener){
        etInputText.setOnEditorActionListener(listener);
    }

    public void setInputHintText(String hintText){
        if (!TextUtils.isEmpty(hintText))
            etInputText.setHint(hintText);
    }

    public void setInputTextColor(@ColorInt int color){
        etInputText.setTextColor(color);
        etInputText.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        tvMobilePrefix.setTextColor(color);
    }

    public void setInputTextSize(float textSize){
        etInputText.setTextSize(textSize);
    }

    public void setInputTextMaxLength(int maxLength){
        InputFilter[] filters = etInputText.getFilters();
        List<InputFilter> filterList = new ArrayList<>(Arrays.asList(filters));
        filterList.add(new InputFilter.LengthFilter(maxLength));
        etInputText.setFilters(filterList.toArray(new InputFilter[0]));
    }

    public void setTitleText(String titleText){
        tvTitleText.setText(titleText);
    }

    public void setTitleTextColor(@ColorInt int color){
        tvTitleText.setTextColor(color);
    }

    public void setTitleTextSize(float textSize){
        tvTitleText.setTextSize(textSize);
    }

    public void setErrorText(String errorText){
        if (!TextUtils.isEmpty(errorText))
            tvErrorText.setText(errorText);
    }

    public void setErrorTextColor(@ColorInt int color){
        tvErrorText.setTextColor(color);
    }

    public void setErrorTextSize(float textSize){
        tvErrorText.setTextSize(textSize);
    }

    public void showError(String errorText){
        setInputTextColor(errorTextColor);
        tvErrorText.setVisibility(VISIBLE);
        if (!TextUtils.isEmpty(errorText))
        tvErrorText.setText(errorText);
    }

    public void hideError(){
        setInputTextColor(inputTextColor);
        tvErrorText.setVisibility(GONE);
        tvErrorText.setText("");
    }

    public String getText(){
        if (TYPE_MOBILE.equals(inputType))
            return tvMobilePrefix.getText() + etInputText.getText().toString();
        return etInputText.getText().toString().trim();
    }
}
