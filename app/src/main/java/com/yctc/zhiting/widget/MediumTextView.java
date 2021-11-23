package com.yctc.zhiting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.yctc.zhiting.R;

public class MediumTextView extends AppCompatTextView {
    private float mStrokeWidth = 0.9f;

    public MediumTextView(Context context) {
        super(context);
    }

    public MediumTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MediumTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MediumTextView,defStyleAttr,0);
        mStrokeWidth = array.getFloat(R.styleable.MediumTextView_strokeWidth,mStrokeWidth);
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //获取当前控件的画笔
        TextPaint paint = getPaint();
        //设置画笔的描边宽度值
        paint.setStrokeWidth(mStrokeWidth);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        super.onDraw(canvas);
    }
}
