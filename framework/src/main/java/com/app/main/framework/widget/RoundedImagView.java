package com.app.main.framework.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.app.main.framework.R;

public class RoundedImagView extends AppCompatImageView {
    private int cornerSize = 30;
    private Paint paint;
    private boolean exceptLeftTop = false, exceptRightTop = false, exceptLeftBottom = false, exceptRightBotoom = false;
    private int cornerColor;

    public RoundedImagView(Context context) {
        super(context);
        init(context,null);
    }

    public RoundedImagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public RoundedImagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        if (attrs != null){
            @SuppressLint({"Recycle", "CustomViewStyleable"}) TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.RoundImageViewStyleable);
            exceptLeftTop = array.getBoolean(R.styleable.RoundImageViewStyleable_roundImageCornerLeftTop,false);
            exceptRightTop = array.getBoolean(R.styleable.RoundImageViewStyleable_roundImageCornerRightTop,false);
            exceptRightBotoom = array.getBoolean(R.styleable.RoundImageViewStyleable_roundImageCornerRightBottom,false);
            exceptLeftBottom = array.getBoolean(R.styleable.RoundImageViewStyleable_roundImageCornerLeftBottom,false);
            cornerColor = array.getColor(R.styleable.RoundImageViewStyleable_roundImageCornerBackground,Color.WHITE);
            boolean corner = array.getBoolean(R.styleable.RoundImageViewStyleable_roundImageCornerAll,false);
            int cornerRadius = array.getDimensionPixelSize(R.styleable.RoundImageViewStyleable_roundImageCornerRadius,0);
            if (cornerRadius > 0)
                setCornerSize(cornerRadius);
            if (corner){
                exceptLeftBottom = true;
                exceptRightBotoom = true;
                exceptRightTop = true;
                exceptLeftTop = true;
            }
            array.recycle();
        }
        paint = new Paint();
        paint.setColor(cornerColor);
        paint.setAntiAlias(true);//消除锯齿
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (exceptLeftTop)
            drawLeftTop(canvas);
        if (exceptRightTop)
            drawRightTop(canvas);
        if (exceptLeftBottom)
            drawLeftBottom(canvas);
        if (exceptRightBotoom)
            drawRightBottom(canvas);
    }

    public void setExceptLeftTop(boolean exceptLeftTop) {
        this.exceptLeftTop = exceptLeftTop;
    }

    public void setExceptRightTop(boolean exceptRightTop) {
        this.exceptRightTop = exceptRightTop;
    }

    public void setExceptLeftBottom(boolean exceptLeftBottom) {
        this.exceptLeftBottom = exceptLeftBottom;
    }

    public void setExceptRightBotoom(boolean exceptRightBotoom) {
        this.exceptRightBotoom = exceptRightBotoom;
    }

    private void drawLeftTop(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, cornerSize);
        path.lineTo(0, 0);
        path.lineTo(cornerSize, 0);
        path.arcTo(new RectF(0, 0, cornerSize * 2, cornerSize * 2), -90, -90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawLeftBottom(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, getHeight() - cornerSize);
        path.lineTo(0, getHeight());
        path.lineTo(cornerSize, getHeight());
        path.arcTo(new RectF(0, // x
                getHeight() - cornerSize * 2,// y
                cornerSize * 2,// x
                getHeight()// getWidth()// y
        ), 90, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightBottom(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth() - cornerSize, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight() - cornerSize);
        RectF oval = new RectF(getWidth() - cornerSize * 2, getHeight() - cornerSize * 2, getWidth(), getHeight());
        path.arcTo(oval, 0, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightTop(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth(), cornerSize);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth() - cornerSize, 0);
        path.arcTo(new RectF(getWidth() - cornerSize * 2, 0, getWidth(),
                0 + cornerSize * 2), -90, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    public int getCornerSize() {
        return cornerSize;
    }

    public void setCornerSize(int cornerSize) {
        this.cornerSize = cornerSize;
    }
}
