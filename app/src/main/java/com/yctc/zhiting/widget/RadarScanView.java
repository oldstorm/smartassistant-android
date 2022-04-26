package com.yctc.zhiting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;


import com.app.main.framework.baseutil.UiUtil;
import com.yctc.zhiting.R;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * 雷达扫描
 */
public class RadarScanView extends View {

    /********* 属性  ********/
    //默认的主题颜色
    private int DEFAULT_COLOR = Color.parseColor("#2DA3F6");
    // 圆圈和交叉线的颜色
    private int mCircleColor = DEFAULT_COLOR;
    // 直线的颜色
    private int mLineColor = DEFAULT_COLOR;
    // 直线的宽度
    private int mLineWidth = 2;
    // 中心点颜色
    private int mCenterDotColor = DEFAULT_COLOR;
    // 中心点半径
    private int mCenterDotR=10;
    //圆圈的数量 不能小于1
    private int mCircleNum = 3;
    //扫描的颜色 RadarView会对这个颜色做渐变透明处理
    private int mSweepColor = DEFAULT_COLOR;
    //小圆圈的颜色
    private int mDotColor = DEFAULT_COLOR;
    //小圆圈数量，随机产生
    private int mDotNum = 4;
    // 是否显示中心点
    private boolean isShowCntDot = true;
    //是否显示交叉线
    private boolean isShowCross = false;
    // 是否显示直线
    private boolean isShowLine = true;
    //是否显示小圆圈
    private boolean isShowDot = true;
    //扫描的转速，表示几秒转一圈
    private float mSpeed = 3.0f;
    //小圆圈显示和消失的速度
    private float mFlicker = 3.0f;


    /********* 画笔  ********/
    private Paint mCirclePaint;// 圆的画笔
    private Paint mSweepPaint; //扫描效果的画笔
    private Paint mDotPaint;// 小圆圈的画笔
    private Paint mLinePaint; // 直线的画笔
    private Paint mCntDotPaint; // 中心点画笔


    /********* 其他 ********/
    private float mDegrees; //扫描时的扫描旋转角度。
    private boolean isScanning = false;//是否扫描
    //保存水滴数据
    private ArrayList<Dot> mDots = new ArrayList<>();

    /********* 构造方法  ********/
    public RadarScanView(Context context) {
        this(context, null);
    }

    public RadarScanView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarScanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    /**
     * 获取自定义属性值
     *
     * @param context
     * @param attrs
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RadarScanView);
            mCenterDotColor = mTypedArray.getColor(R.styleable.RadarScanView_cntDotColor, DEFAULT_COLOR);
            mLineColor = mTypedArray.getColor(R.styleable.RadarScanView_lineColor, DEFAULT_COLOR);
            mLineWidth = mTypedArray.getInt(R.styleable.RadarScanView_lineWidth, 2);
            mCircleColor = mTypedArray.getColor(R.styleable.RadarScanView_circleColor, DEFAULT_COLOR);
            mCircleNum = mTypedArray.getInt(R.styleable.RadarScanView_circleNum, mCircleNum);
            if (mCircleNum < 1) {
                mCircleNum = 3;
            }
            mSweepColor = mTypedArray.getColor(R.styleable.RadarScanView_sweepColor, DEFAULT_COLOR);
            mDotColor = mTypedArray.getColor(R.styleable.RadarScanView_dotColor, DEFAULT_COLOR);
            mDotNum = mTypedArray.getInt(R.styleable.RadarScanView_dotNum, mDotNum);
            mCenterDotR = mTypedArray.getInt(R.styleable.RadarScanView_cntDotR, mCenterDotR);
            isShowCntDot = mTypedArray.getBoolean(R.styleable.RadarScanView_showCntDot, true);
            isShowCross = mTypedArray.getBoolean(R.styleable.RadarScanView_showCross, false);
            isShowLine = mTypedArray.getBoolean(R.styleable.RadarScanView_showLine, true);
            isShowDot = mTypedArray.getBoolean(R.styleable.RadarScanView_showRaindrop, true);
            mSpeed = mTypedArray.getFloat(R.styleable.RadarScanView_speed, mSpeed);
            if (mSpeed <= 0) {
                mSpeed = 3;
            }
            mFlicker = mTypedArray.getFloat(R.styleable.RadarScanView_flicker, mFlicker);
            if (mFlicker <= 0) {
                mFlicker = 3;
            }
            mTypedArray.recycle();
        }
    }


    /**
     * 初始化画笔
     */
    private void initPaint() {
        // 初始化画笔几个圆圈
        mCirclePaint = new Paint();
        mCirclePaint.setColor(UiUtil.changeAlpha(DEFAULT_COLOR, 100));
        mCirclePaint.setStrokeWidth(1);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setAntiAlias(true);

        // 小圆圈
        mDotPaint = new Paint();
        mDotPaint.setStyle(Paint.Style.FILL);
        mDotPaint.setAntiAlias(true);

        // 扫描
        mSweepPaint = new Paint();
        mSweepPaint.setAntiAlias(true);

        // 直线
        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(mLineColor);

        // 中心圆点
        mCntDotPaint = new Paint();
        mCntDotPaint.setColor(mCenterDotColor);
        mCntDotPaint.setStyle(Paint.Style.FILL);
        mCntDotPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置宽高,默认200dp
        int defaultSize = dp2px(getContext(), 200);
        setMeasuredDimension(measureWidth(widthMeasureSpec, defaultSize),
                measureHeight(heightMeasureSpec, defaultSize));
    }




    /**
     * 测量宽
     *
     * @param measureSpec
     * @param defaultSize
     * @return
     */
    private int measureWidth(int measureSpec, int defaultSize) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        result = Math.max(result, getSuggestedMinimumWidth());
        return result;
    }

    /**
     * 测量高
     *
     * @param measureSpec
     * @param defaultSize
     * @return
     */
    private int measureHeight(int measureSpec, int defaultSize) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        result = Math.max(result, getSuggestedMinimumHeight());
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //计算圆的半径
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        int radius = Math.min(width, height) / 2;

        //计算圆的圆心
        int cx = getPaddingLeft() + (getWidth() - getPaddingLeft() - getPaddingRight()) / 2;
        int cy = getPaddingTop() + (getHeight() - getPaddingTop() - getPaddingBottom()) / 2;

        drawCircle(canvas, cx, cy, radius);

        // 显示中心圆点
        if (isShowCntDot){
            canvas.drawCircle(cx , cy, mCenterDotR, mCntDotPaint);
        }

        // 显示横竖线
        if (isShowCross) {
            drawCross(canvas, cx, cy, radius);
        }
        drawScan(isScanning, canvas, cx, cy, radius);

    }

    /**
     * 扫描
     * @param scan
     */
    private void drawScan(boolean scan, Canvas canvas, int cx, int cy, int radius){


        if (isShowDot && scan) {
            drawDot(canvas, cx, cy, radius);
        }
        if (isShowLine){
            drawLine(canvas, cx, cy, cx+radius, cy);
        }
        drawSweep(canvas, cx, cy, radius);
        if (scan) {

            //计算雷达扫描的旋转角度
            mDegrees = (mDegrees + (360 / mSpeed / 60)) % 360;
            if (scanListener!=null){
                scanListener.onStart();
            }
        }

        //触发View重新绘制，通过不断的绘制View的扫描动画效果
        invalidate();
    }


    /**
     * 画圆
     */
    private void drawCircle(Canvas canvas, int cx, int cy, int radius) {
        //画mCircleNum个半径不等的圆圈。
        for (int i = 0; i < mCircleNum; i++) {
            canvas.drawCircle(cx, cy, radius - (radius / mCircleNum * i), mCirclePaint);
        }
    }

    /**
     * 画交叉线
     */
    private void drawCross(Canvas canvas, int cx, int cy, int radius) {
        //水平线
        canvas.drawLine(cx - radius, cy, cx + radius, cy, mCirclePaint);

        //垂直线
        canvas.drawLine(cx, cy - radius, cx, cy + radius, mCirclePaint);
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
    }

    /**
     * 生成小圆圈。小圆圈的生成是随机的，并不是每次调用都会生成一个小圆圈。
     */
    private void generateRaindrop(int cx, int cy, int radius) {

        // 最多只能同时存在mDotNum个小圆圈。
        if (mDots.size() < mDotNum) {
            // 随机一个20以内的数字，如果这个数字刚好是0，就生成一个小圆圈。
            // 用于控制小圆圈生成的概率。
            boolean probability = (int) (Math.random() * 20) == 0;
            if (probability) {
                int x = 0;
                int y = 0;
                int xOffset = (int) (Math.random() * (radius - 20));
                int yOffset = (int) (Math.random() * (int) Math.sqrt(1.0 * (radius - 20) * (radius - 20) - xOffset * xOffset));

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
     * 画扫描效果
     */
    private void drawSweep(Canvas canvas, int cx, int cy, int radius) {
        //扇形的透明的渐变效果
        SweepGradient sweepGradient = new SweepGradient(cx, cy,
                new int[]{Color.TRANSPARENT, UiUtil.changeAlpha(mSweepColor, 0), UiUtil.changeAlpha(mSweepColor, 120),
                        UiUtil.changeAlpha(mSweepColor, 130), UiUtil.changeAlpha(mSweepColor, 130)
                }, new float[]{0.0f, 0.9f, 0.99f, 0.998f, 1f});
        mSweepPaint.setShader(sweepGradient);
        //先旋转画布，再绘制扫描的颜色渲染，实现扫描时的旋转效果。
        canvas.drawCircle(cx, cy, radius, mSweepPaint);
    }

    /**
     * 画线
     * @param canvas
     * @param startX 开始 x 坐标
     * @param startY 开始 y 坐标
     * @param stopX  结束 x 坐标
     * @param stopY  结束 y 坐标
     */
    private void drawLine(Canvas canvas, int startX, int startY, int stopX, int stopY){
        canvas.rotate(-90 + mDegrees, startX, startY);
        canvas.drawLine(startX, startY, stopX, stopY, mLinePaint);
    }

    /**
     * 开始扫描
     */
    public void start() {
        if (!isScanning) {
            isScanning = true;
            invalidate();
        }
    }

    /**
     * 停止扫描
     */
    public void stop() {
        if (isScanning) {
            isScanning = false;
            mDots.clear();
//            mDegrees = 0.0f;
            if (scanListener!=null){
                scanListener.onStop();
            }
        }
    }

    /**
     * dp转px
     */
    private static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }


    private OnScanListener scanListener;

    public OnScanListener getScanListener() {
        return scanListener;
    }

    public void setScanListener(OnScanListener scanListener) {
        this.scanListener = scanListener;
    }

    public interface OnScanListener{
        void onStart();
        void onStop();
    }
}
