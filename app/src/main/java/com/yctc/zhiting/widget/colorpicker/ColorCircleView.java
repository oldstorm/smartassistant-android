package com.yctc.zhiting.widget.colorpicker;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.app.main.framework.baseutil.LogUtil;

import java.util.Locale;

/**
 * HSV color wheel
 */
public class ColorCircleView extends FrameLayout {

    private float radius;
    private float centerX;
    private float centerY;

    private float selectorRadiusPx = 3 * 3;

    private PointF currentPoint = new PointF();
    private int currentColor = Color.MAGENTA;
    private float hue = 0;
    private float saturation = 0;
    private boolean onlyUpdateOnTouchEventUp;

    private ColorCircleSelector selector;


    public ColorCircleView(Context context) {
        this(context, null);
    }

    public ColorCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        selectorRadiusPx = 9 * getResources().getDisplayMetrics().density;

        {
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ColorCirclePalette palette = new ColorCirclePalette(context);
            int padding = (int) selectorRadiusPx;
            palette.setPadding(padding, padding, padding, padding);
            addView(palette, layoutParams);
        }

        {
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            selector = new ColorCircleSelector(context);
            selector.setSelectorRadiusPx(selectorRadiusPx);
            addView(selector, layoutParams);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;
        width = height = Math.min(maxWidth, maxHeight);
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int netWidth = w - getPaddingLeft() - getPaddingRight();
        int netHeight = h - getPaddingTop() - getPaddingBottom();
        radius = Math.min(netWidth, netHeight) * 0.5f - selectorRadiusPx;
        if (radius < 0) return;
        centerX = netWidth * 0.5f;
        centerY = netHeight * 0.5f;
        setColor(currentColor, false);
    }

    private int minInterval = 1000 / 60;
    private long lastPassedEventTime = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                long current = System.currentTimeMillis();
                if (current - lastPassedEventTime <= minInterval) {
                    return true;
                }
                lastPassedEventTime = current;
                update(event);
                return true;
            case MotionEvent.ACTION_UP:
                update(event);
                return true;
        }
        return super.onTouchEvent(event);
    }


    public void update(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        boolean isTouchUpEvent = event.getActionMasked() == MotionEvent.ACTION_UP;
        if (!onlyUpdateOnTouchEventUp || isTouchUpEvent) {
            LogUtil.e("color:"+getColorAtPoint(x, y));
        }
        updateSelector(x, y);
        int color = getColorAtPoint(x, y);
        if (colorPickerListener!=null){
            colorPickerListener.onPicker(colorHex(color), color, hue, saturation);
        }
    }

    private int getColorAtPoint(float eventX, float eventY) {
        float x = eventX - centerX;
        float y = eventY - centerY;
        double r = Math.sqrt(x * x + y * y);
        float[] hsv = {0, 0, 1};
        hsv[0] = (float) (Math.atan2(y, -x) / Math.PI * 180f) + 180;
        hsv[1] = Math.max(0f, Math.min(1f, (float) (r / radius)));
        hue = hsv[0];
        saturation = hsv[1];
        return Color.HSVToColor(hsv);
    }

    public void setOnlyUpdateOnTouchEventUp(boolean onlyUpdateOnTouchEventUp) {
        this.onlyUpdateOnTouchEventUp = onlyUpdateOnTouchEventUp;
    }

    public void setColor(int color, boolean shouldPropagate) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        float r = hsv[1] * radius;
        float radian = (float) (hsv[0] / 180f * Math.PI);
        updateSelector((float) (r * Math.cos(radian) + centerX), (float) (-r * Math.sin(radian) + centerY));
        currentColor = color;
//        if (!onlyUpdateOnTouchEventUp) {
//            System.out.println("color:"+currentColor);
//        }
        if (colorPickerListener!=null){
            colorPickerListener.onPicker(colorHex(currentColor), currentColor, hue, saturation);
        }
    }

    private void updateSelector(float eventX, float eventY) {
        float x = eventX - centerX;
        float y = eventY - centerY;
        double r = Math.sqrt(x * x + y * y);
        if (r > radius) {
            x *= radius / r;
            y *= radius / r;
        }
        currentPoint.x = x + centerX;
        currentPoint.y = y + centerY;
        selector.setCurrentPoint(currentPoint);
    }

    private String colorHex(int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return String.format(Locale.getDefault(), "#%02X%02X%02X",  r, g, b);
    }

    private OnColorPickerListener colorPickerListener;

    public void setColorPickerListener(OnColorPickerListener colorPickerListener) {
        this.colorPickerListener = colorPickerListener;
    }

    public interface OnColorPickerListener{
        void onPicker(String colorStr, int color, float hue, float saturation);
    }
}
