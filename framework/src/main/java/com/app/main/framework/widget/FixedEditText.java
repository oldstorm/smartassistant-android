package com.app.main.framework.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.AppCompatEditText;

import com.app.main.framework.R;
import com.app.main.framework.baseutil.UiUtil;

public class FixedEditText extends AppCompatEditText implements View.OnFocusChangeListener {

    private String fixText;
    @ColorInt
    private int fixColor = 0;//固定文本颜色

    public FixedEditText(Context context) {
        super(context);
        init(null);
    }

    public FixedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FixedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        if (attrs != null){
            @SuppressLint({"Recycle", "CustomViewStyleable"}) TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.FixedEditTextStyleable);
            fixColor = array.getColor(R.styleable.FixedEditTextStyleable_fixedTextColor,0);
            fixText = array.getString(R.styleable.FixedEditTextStyleable_fixedText);
            setFixedText(fixText,null);
        }
        this.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        //this.setLongClickable(false);需要支持长按弹出全选、复制框
        this.setOnFocusChangeListener(this);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        int fixTextLength = fixText != null ? fixText.length() : 0;
        // 若选中的光标位置在固定文本内，则重置光标位置到固定文本之后;
        if (selStart < fixTextLength) {
            selStart = selStart < fixText.length() ? fixText.length() : selStart;
            selEnd = selStart;
            if (getText().toString().length() != 0) {
                this.setSelection(selStart, selEnd);
            }
        }
        super.onSelectionChanged(selStart, selEnd);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == 67) {//删除键
            //当标记为不能删除的时候拦截操作
            if (!isDelete()) {
                return !isDelete();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus && getText().toString().length() <= fixText.length()) {
            // 在获取焦点的时候，去设置头部文本；(默认进入页面不获取焦点，这样可以显示默认文本)
            if (fixColor != 0) {
                setFixedText(fixText,null);
            } else {
                this.setText(fixText);
            }
        }
    }

    /**
     * 输入固定文本
     * @param fixText：固定文本前缀
     */
    public void setFixedText(String fixText,String showText) {
        this.fixColor = UiUtil.getColor(R.color.appBlack);
        this.fixText = TextUtils.isEmpty(fixText) ? "" : fixText;
        setFixedText(fixText,fixColor,showText);
    }

    /**
     * 设置固定文本的颜色
     * @param fixText
     */
    public void setFixedText(String fixText, int fixColor,String showText) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        ForegroundColorSpan fixTextSpan = new ForegroundColorSpan(fixColor);
        builder.append(fixText);
        builder.setSpan(fixTextSpan, 0, fixText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        if (!TextUtils.isEmpty(showText))
            builder.append(showText);
        this.setText(builder);
        this.setSelection(fixText.length());
    }

    /**
     * 设置Hint文本
     * @param fixText：固定头部文本
     * @param hintText：输入提示文字
     */
    public void setEdtHint(String fixText, String hintText) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        ForegroundColorSpan fixTextSpan = new ForegroundColorSpan(fixColor);
        builder.append(fixText);
        builder.setSpan(fixTextSpan, 0, fixText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append(hintText);
        this.setHint(builder);
    }

    /**
     * 判断除了固定头，是否有其他输入文本
     * @return
     */
    public boolean isEdtContentEmpty() {
        return getText().toString().trim().length() <= fixText.length();
    }

    /**
     * 获取输入文本内容
     * @param symbol：输入的符号
     * @return
     */
    public String getContentText(String symbol) {
        String content = getText().toString().trim();
        int index = content.indexOf(symbol, 1);
        return content.substring(index + 1).trim();
    }

    private boolean isDelete() {
        int fixTextLength = fixText != null ? fixText.length() : 0;
        if (getSelectionStart() == getSelectionEnd()) {
            // 单个删除
            return getSelectionStart() > fixTextLength;
        }
        // 长按弹出选中删除
        return getSelectionStart() >= fixTextLength;
    }
}