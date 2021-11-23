package com.yctc.zhiting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.app.main.framework.baseutil.UiUtil;
import com.yctc.zhiting.R;

public class CustomEditTextView extends LinearLayout {

    private Context mContext;
    private OnInputCompleteListener inputCompleteListener;  // 输入完成监听

    private int mEtCount; // 输入个数
    private int mEtWidth; // 输入框宽度
    private int mTextColor; //文本颜色
    private float mTextSize; // 文本大小
    private int mEtBg; // 输入框背景
    private int mSpacing; // 输入框间距
    private int mEqualSpacing; // 平分间距
    private boolean isEqual; // 是否平分
    private int mViewWidth; // 输入框总宽度

    public OnInputCompleteListener getInputCompleteListener() {
        return inputCompleteListener;
    }

    public void setInputCompleteListener(OnInputCompleteListener inputCompleteListener) {
        this.inputCompleteListener = inputCompleteListener;
    }

    public int getEtCount() {
        return mEtCount;
    }

    public void setEtCount(int mEtCount) {
        this.mEtCount = mEtCount;
    }

    public int getEtWidth() {
        return mEtWidth;
    }

    public void setEtWidth(int mEtWidth) {
        this.mEtWidth = mEtWidth;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
    }

    public int getEtBg() {
        return mEtBg;
    }

    public void setEtBg(int mEtBg) {
        this.mEtBg = mEtBg;
    }

    public int getSpacing() {
        return mSpacing;
    }

    public void setSpacing(int mSpacing) {
        this.mSpacing = mSpacing;
    }

    public int getEqualSpacing() {
        return mEqualSpacing;
    }

    public void setEqualSpacing(int mEqualSpacing) {
        this.mEqualSpacing = mEqualSpacing;
    }

    public boolean isEqual() {
        return isEqual;
    }

    public void setEqual(boolean equal) {
        isEqual = equal;
    }

    public CustomEditTextView(Context context) {
        this(context, null);
    }

    public CustomEditTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomEditTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomEditTextView);
        mEtCount = ta.getInteger(R.styleable.CustomEditTextView_cet_count, 8);
        mEtWidth = ta.getDimensionPixelSize(R.styleable.CustomEditTextView_cet_width, dip2px(30));
        mTextColor = ta.getColor(R.styleable.CustomEditTextView_cet_text_color, Color.BLACK);
        mTextSize = ta.getDimensionPixelSize(R.styleable.CustomEditTextView_cet_text_size, 16);
        mEtBg = ta.getResourceId(R.styleable.CustomEditTextView_cet_bg, R.drawable.selector_stroke_blue_gray);
        isEqual = ta.hasValue(R.styleable.CustomEditTextView_cet_spacing);
        if (isEqual){
            mSpacing = ta.getDimensionPixelSize(R.styleable.CustomEditTextView_cet_spacing, 0);
        }
        initView();
        ta.recycle();
    }
    /**
     * dip转换px
     */
    public  int dip2px(int dip) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }


    private void initView(){
        for (int i=0; i<mEtCount; i++){
            EditText editText = new EditText(mContext);
            editText.setLayoutParams(getCETLayoutParams(i));
            editText.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            editText.setGravity(Gravity.CENTER);
            editText.setId(i);
            editText.setCursorVisible(false);
            editText.setTypeface(Typeface.DEFAULT_BOLD);
            editText.setMaxEms(1);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setPadding(0, 0, 0, 0);
            editText.setBackgroundResource(mEtBg);
            setEditListener(editText);
            addView(editText);
            if (i==0){
                editText.setFocusable(true);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        updateEtMargin();
    }

    private void updateEtMargin(){
        for (int i=0; i<mEtCount; i++){
            EditText editText = (EditText) getChildAt(i);
            editText.setLayoutParams(getCETLayoutParams(i));
        }
    }

    /**
     * 键盘、文本变化和焦点事件
     * @param editText
     */
    private void setEditListener(EditText editText){
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN){
                    backFocus();
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()!=0){
                    focus();
                }
                if (inputCompleteListener!=null){
                    inputCompleteListener.onTextChange(CustomEditTextView.this, getResult());
                    EditText lastEditText = (EditText)getChildAt(mEtCount-1);
                    if (lastEditText.getText().length()>0){
                        inputCompleteListener.onComplete(CustomEditTextView.this, getResult());
                    }
                }
            }
        });
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    focus();
                }
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        int childCount = getChildCount();
        for (int i=0; i<childCount; i++){
            View child = getChildAt(i);
            child.setEnabled(enabled);
        }
    }

    /**
     * 获取焦点
     */
    private void focus(){
        int count = getChildCount();
        EditText editText;
        for (int i=0; i<count; i++){
            editText = (EditText) getChildAt(i);
            if (editText.getText().length()<1){
                editText.requestFocus();
                return;
            }else {
                if (i == count-1){
                    editText.requestFocus();
                }
            }
        }
    }

    /**
     * 回退焦点
     */
    private void backFocus(){
        EditText editText;
        for (int i=mEtCount-1; i>=0; i--){
            editText = (EditText)getChildAt(i);
            if (editText.getText().length()>=1){
                editText.setText("");
                editText.requestFocus();
                return;
            }
        }
    }

    /**
     * 文本结果
     * @return
     */
    private String getResult(){
        StringBuilder stringBuilder = new StringBuilder();
        EditText editText;
        for (int i=0; i<mEtCount; i++){
            editText = (EditText) getChildAt(i);
            stringBuilder.append(editText.getText());
        }
        return stringBuilder.toString();
    }

    /**
     * 单个EditText的布局参数
     * @param i
     * @return
     */
    public LinearLayout.LayoutParams getCETLayoutParams(int i){
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(mEtWidth, mEtWidth+dip2px(10));
        if (!isEqual){
            mEqualSpacing = (mViewWidth-mEtCount*mEtWidth)/(mEtCount+1);
            if (i==0){
                llp.leftMargin = mEqualSpacing;
                llp.rightMargin = mEqualSpacing/2;
            }else if (i == mEtCount-1){
                llp.leftMargin = mEqualSpacing/2;
                llp.rightMargin = mEqualSpacing;
            }else {
                llp.leftMargin = mEqualSpacing/2;
                llp.rightMargin = mEqualSpacing/2;
            }
        }else {
            llp.leftMargin = mSpacing/2;
            llp.rightMargin = mSpacing/2;
        }
        llp.gravity = Gravity.CENTER;
        return llp;
    }

    public void clearText(){
        EditText editText;
        for (int i=mEtCount-1; i>=0; i--){
            editText = (EditText)getChildAt(i);
            editText.setText("");
            if (i==0){
                editText.requestFocus();
            }
        }
    }

    public interface OnInputCompleteListener{
        /**
         * 文本改变
         * @param view
         * @param text
         */
        void onTextChange(View view, String text);

        /**
         * 输完了
         * @param view
         * @param text
         */
        void onComplete(View view, String text);
    }
}
