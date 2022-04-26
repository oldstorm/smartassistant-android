package com.yctc.zhiting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.app.main.framework.baseutil.UiUtil;
import com.yctc.zhiting.R;

import java.util.ArrayList;
import java.util.Iterator;

public class DLStatusView extends View {

    private int DEFAULT_COLOR = Color.parseColor("#89C3FF");

    // 门锁状态图片
    private int mDLStatusDrawable;
    // 门锁两边的线颜色
    private int mSurroundLineColor;
    //小圆圈的颜色
    private int mDotColor = Color.parseColor("#1EE8D1");
    //小圆圈数量，随机产生
    private int mDotNum = 6;
    private boolean isShowArc;
    // 是否显示小圆圈
    private boolean isShowDot;
    //小圆圈显示和消失的速度
    private float mFlicker = 3.0f;

    private Paint mPaint; // 图片画笔
    private Paint mDotPaint;// 小圆圈的画笔
    private Paint mArcPaint; // 门锁两边的线的画笔

    private Bitmap mBitmap;
    private Context mContext;

    //保存水滴数据
    private ArrayList<Dot> mDots = new ArrayList<>();

    public int getDLStatusDrawable() {
        return mDLStatusDrawable;
    }

    public void setDLStatusDrawable(int dlStatusDrawable) {
        this.mDLStatusDrawable = dlStatusDrawable;
    }

    public int getSurroundLineColor() {
        return mSurroundLineColor;
    }

    public int getDotColor() {
        return mDotColor;
    }

    public void setDotColor(int dotColor) {
        this.mDotColor = dotColor;
    }

    public void setSurroundLineColor(int surroundLineColor) {
        this.mSurroundLineColor = surroundLineColor;
    }

    public int getDotNum() {
        return mDotNum;
    }

    public void setDotNum(int dotNum) {
        this.mDotNum = dotNum;
    }

    public boolean isShowArc() {
        return isShowArc;
    }

    public void setShowArc(boolean showArc) {
        isShowArc = showArc;
    }

    public boolean isShowDot() {
        return isShowDot;
    }

    public void setShowDot(boolean showDot) {
        isShowDot = showDot;
    }

    public float getFlicker() {
        return mFlicker;
    }

    public void setFlicker(float flicker) {
        this.mFlicker = flicker;
    }

    public DLStatusView(Context context) {
        this(context, null);
    }

    public DLStatusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DLStatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mContext = context;
        if (attrs != null) {
            TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.DLStatusView);
            mDLStatusDrawable = mTypedArray.getInt(R.styleable.DLStatusView_dl_status_resource, R.drawable.icon_door_closed);
            mSurroundLineColor = mTypedArray.getColor(R.styleable.DLStatusView_dl_surround_line_color, DEFAULT_COLOR);
            mDotColor = mTypedArray.getColor(R.styleable.DLStatusView_dl_dot_color, Color.parseColor("#1EE8D1"));
            mDotNum = mTypedArray.getInt(R.styleable.DLStatusView_dl_dot_num, mDotNum);
            isShowArc = mTypedArray.getBoolean(R.styleable.DLStatusView_dl_show_arc, false);
            isShowDot = mTypedArray.getBoolean(R.styleable.DLStatusView_dl_show_dot, false);
            mFlicker = mTypedArray.getFloat(R.styleable.DLStatusView_dl_flicker, mFlicker);
            if (mFlicker <= 0) {
                mFlicker = 3;
            }
            mTypedArray.recycle();
            mBitmap = BitmapFactory.decodeResource(context.getResources(), mDLStatusDrawable);
        }
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        // 弧线
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStrokeWidth(1f);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setColor(mSurroundLineColor);
        // 小圆圈
        mDotPaint = new Paint();
        mDotPaint.setStyle(Paint.Style.FILL);
        mDotPaint.setAntiAlias(true);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //计算圆的半径
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        int radius = Math.min(width, height) / 2;
        int bitWidth = mBitmap.getWidth() / 2;
        //计算圆的圆心
        int cx = getPaddingLeft() + (getWidth() - getPaddingLeft() - getPaddingRight()) / 2;
        int cy = getPaddingTop() + (getHeight() - getPaddingTop() - getPaddingBottom()) / 2;
        canvas.drawBitmap(mBitmap, cx-bitWidth, cy-bitWidth, mPaint);
        if (isShowArc) {
            drawArc(canvas, cx, cy);
        }

        if (isShowDot) {
            drawDot(canvas, cx, cy, radius);
        }
    }

    /**
     * 画弧线
     * @param canvas
     * @param cx
     * @param cy
     */
    private void drawArc(Canvas canvas, float cx, float cy) {

        int colorSweep[] = {UiUtil.changeAlpha(mSurroundLineColor, 130),
                UiUtil.changeAlpha(mSurroundLineColor, 0), UiUtil.changeAlpha(mSurroundLineColor, 130),
                UiUtil.changeAlpha(mSurroundLineColor, 0),UiUtil.changeAlpha(mSurroundLineColor, 130) };
        float position[]={
                0.0f,0.25f, 0.5f,  0.75f,  1f };
        SweepGradient sweepGradient=new SweepGradient(cx, cy, colorSweep, position);
        mArcPaint.setShader(sweepGradient);

        int with = mBitmap.getWidth() / 2;
        RectF oval = new  RectF( cx-with-50, cy-with-50,
                cx+with +50, cy+with +50);
        canvas.drawArc(oval, 315, 90, false, mArcPaint);
        canvas.drawArc(oval, 135, 90, false, mArcPaint);
        RectF oval2 = new RectF( cx-with-100, cy-with-100,
                cx+with +100, cy+with +100);
        canvas.drawArc(oval2, 315, 90, false, mArcPaint);
        canvas.drawArc(oval2, 135, 90, false, mArcPaint);
    }

    /**
     * 画小圆圈(就是在扫描的过程中随机出现的点)。
     */
    private void drawDot(Canvas canvas, int cx, int cy, int radius) {


        generateRaindrop(cx, cy, radius);
        for (Dot raindrop : mDots) {
            mDotPaint.setColor(raindrop.changeAlpha());
            canvas.drawCircle(raindrop.x, raindrop.y, raindrop.radius, mDotPaint);
            //水滴的扩散和透明的渐变效果
            raindrop.radius += 1.0f * 10 / 60 / mFlicker;
            raindrop.alpha -= 1.0f * 255 / 60 / mFlicker;
        }
        removeDot();
        //触发View重新绘制，通过不断的绘制View的扫描动画效果
        invalidate();
    }

    /**
     * 生成小圆圈。小圆圈的生成是随机的，并不是每次调用都会生成一个小圆圈。
     */
    private void generateRaindrop(int cx, int cy, int radius) {

        // 最多只能同时存在mDotNum个小圆圈。
        if (mDots.size() < mDotNum) {
            radius = radius+100;
            // 随机一个20以内的数字，如果这个数字刚好是0，就生成一个小圆圈。
            // 用于控制小圆圈生成的概率。
            boolean probability = (int) (Math.random() * 20) == 0;
            if (probability) {
                int x = 0;
                int y = 0;
                int xOffset = (int) (Math.random() * (radius));
                int yOffset = (int) (Math.random() * (int) Math.sqrt(1.0 * (radius) * (radius) - xOffset * xOffset));

                if ((int) (Math.random() * 2) == 0) {
                    x = cx - xOffset;
                } else {
                    x = cx + xOffset;
                }

                if ((int) (Math.random() * 2) == 0) {
                    y = cy - yOffset;
                } else {
                    y = cy + yOffset;
                }

                mDots.add(new Dot(x, y, 0, mDotColor));
            }
        }
    }

    /**
     * 删除水滴
     */
    private void removeDot() {
        Iterator<Dot> iterator = mDots.iterator();

        while (iterator.hasNext()) {
            Dot dot = iterator.next();
            if (dot.radius > 20 || dot.alpha < 0) {
                iterator.remove();
            }
        }
    }

    /**
     * 是否展示弧线和小圆圈
     * @param show
     */
    public void showArcAndDot(boolean show) {
        isShowArc = show;
        isShowDot = show;
        invalidate();
    }

    /**
     * 设置状态图片
     * @param open
     */
    public void setStatusDrawable(boolean open) {
        mDLStatusDrawable = open ? R.drawable.icon_door_opened : R.drawable.icon_door_closed;
        mSurroundLineColor = open ? Color.parseColor("#FF7F7F") : Color.parseColor("#89C3FF");
        mDotColor = open ? Color.parseColor("#FF7F7F") : Color.parseColor("#1EE8D1");
        mArcPaint.setColor(mSurroundLineColor);
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), mDLStatusDrawable);
        invalidate();
    }
}
