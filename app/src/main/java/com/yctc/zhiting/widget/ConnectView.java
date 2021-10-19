package com.yctc.zhiting.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.yctc.zhiting.R;


/**
 * 设备连接
 */
public class ConnectView extends View {

    //默认的主题颜色
    private int DEFAULT_COLOR = Color.parseColor("#91D7F4");
    // 圆环开始颜色
    private int dashStartColor = DEFAULT_COLOR;
    // 圆环中间颜色
    private int dashCntColor = DEFAULT_COLOR;
    // 圆环结束颜色
    private int dashStopColor = DEFAULT_COLOR;
    // 外圈的颜色
    private int outDashColor = Color.parseColor("#FFCFD2E6");
    // 内圈的颜色
    private int inDashColor = Color.parseColor("#FFDDE5EB");
    // 进度的颜色
    private int progressColor = Color.parseColor("#2DA3F6");
    // 中间文本颜色
    private int textColor = Color.parseColor("#3F4663");
    // 刻度宽度
    private float dashRingWidth;
    // 刻度长度
    private float dashRingHeight;
    // 圆环半径
    private float circleRadius;
    // 画笔
    private Paint mPaint;
    private Paint mTextPaint;


    private int defaultSize = 100;
    private int width = 0;
    private int height = 0;
    private int circleAlpha;
    private float textSize=36;
    private  int currentProgress = 0;

    private ValueAnimator animator;

    public ConnectView(Context context) {
        this(context, null);
    }

    public ConnectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConnectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
    }

    /**
     * 设置属性
     * @param context
     * @param attrs
     */
    private void initAttrs(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ConnectView);
        dashStartColor = typedArray.getColor(R.styleable.ConnectView_dashStartColor, DEFAULT_COLOR);
        dashCntColor = typedArray.getColor(R.styleable.ConnectView_dashCntColor, DEFAULT_COLOR);
        dashStopColor = typedArray.getColor(R.styleable.ConnectView_dashStopColor, DEFAULT_COLOR);
        outDashColor = typedArray.getColor(R.styleable.ConnectView_outDashColor, Color.parseColor("#FFCFD2E6"));
        inDashColor = typedArray.getColor(R.styleable.ConnectView_inDashColor, Color.parseColor("#FFDDE5EB"));
        progressColor = typedArray.getColor(R.styleable.ConnectView_progressColor, DEFAULT_COLOR);
        circleRadius = typedArray.getDimensionPixelSize(R.styleable.ConnectView_dashCircleRadius, 110);
        dashRingWidth = typedArray.getDimensionPixelSize(R.styleable.ConnectView_dashRingWidth, 2);
        dashRingHeight = typedArray.getDimensionPixelSize(R.styleable.ConnectView_dashRingHeight, 30);
        textColor = typedArray.getColor(R.styleable.ConnectView_progressColor, Color.parseColor("#3F4663"));
        textSize = typedArray.getDimensionPixelSize(R.styleable.ConnectView_textSize, 36);
        typedArray.recycle();
    }

    /**
     * 初始化
     */
    private void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(dashRingWidth);

        mTextPaint = new Paint();
        mPaint.setAntiAlias(true);

        animator = ValueAnimator.ofInt(0, 255);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.RESTART);
        animator.setRepeatCount(-1);
        animator.setDuration(3000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                circleAlpha = (int) animation.getAnimatedValue();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getSize(widthMeasureSpec);
        height = getSize(heightMeasureSpec);
//        if (width< UiUtil.dip2px(250)){
//            width = UiUtil.dip2px(250);
//        }
//        if (height<UiUtil.dip2px(250)){
//            height = UiUtil.dip2px(250);
//        }
        if (width<height){
            height = width;
        }else {
            width = height;
        }
        setMeasuredDimension(width, height);
    }

    private int getSize(int measureSpec){
        int viewSize = defaultSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode){
            case MeasureSpec.UNSPECIFIED:
                viewSize = defaultSize;
                bringToFront();
                break;

            case MeasureSpec.AT_MOST:
                viewSize = size;
                break;

            case MeasureSpec.EXACTLY:
                viewSize = size;
                break;
        }
        return viewSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawCircle(canvas);
        drawDash(canvas);
        drawDash2(canvas);
        drawProgress(canvas);

    }

    private void drawCircle(Canvas canvas){
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ff99cc00"));
        canvas.drawCircle(width/2, height/2, circleRadius, mPaint);

    }

    private void drawDash(Canvas canvas){
        mPaint.setColor(outDashColor);
        canvas.translate(width/2, height/2);

        //100等分
        for(int i=0;  i<100; i++){
            canvas.save();
            canvas.rotate(360+i*3.6f);
            int alpha = (int)((i/60f*255+circleAlpha)%255);
//            mPaint.setAlpha(alpha);
            canvas.translate(circleRadius*2+10, 0);
            canvas.drawLine(0,0, dashRingHeight,0, mPaint);
            canvas.restore();
        }
    }

    private void drawDash2(Canvas canvas){
        mPaint.setColor(inDashColor);
//        canvas.translate(width/2, height/2);

        //60等分
        for(int i=0;  i<100; i++){
            canvas.save();
            canvas.rotate(360+i*3.6f);
            int alpha = (int)((i/60f*255+circleAlpha)%255);
//            mPaint.setAlpha(alpha);
            canvas.translate(circleRadius*2+10-dashRingHeight, 0);
            canvas.drawLine(0,0, dashRingHeight/2,0, mPaint);
            canvas.restore();
        }
    }

    /**
     * 画进度
     * @param canvas
     */
    private void drawProgress(Canvas canvas){
        // 3. 画文本
        String text = String.valueOf(currentProgress*100/100);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        // 要重新设置宽度为0
        mTextPaint.setStrokeWidth(0);
        mTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        // 得到指定文本边界的指定大小
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), bounds);
        Rect unitBounds = new Rect();
        mTextPaint.getTextBounds("%", 0, "%".length(), unitBounds);
        canvas.drawText(text,  -(bounds.width()+unitBounds.width())/2+(text.length()<2 ? 16 : 10),   bounds.height() / 2, mTextPaint);
        float textWidth = mPaint.measureText(text);
        mTextPaint.setTextSize(textSize/2);

        canvas.drawText("%",bounds.width()/2-unitBounds.width()/2+(text.length()==2 ? 20 : 28), bounds.height() / 2, mTextPaint);

        //60等分
        mPaint.setColor(progressColor);
        for(int i=0;  i<currentProgress; i++){
            canvas.save();
            canvas.rotate(360+i*3.6f);
            int alpha = (int)((i/60f*255+circleAlpha)%255);
//            mPaint.setAlpha(alpha);
            canvas.translate(circleRadius*2+10, 0);
            canvas.drawLine(0,0, dashRingHeight,0, mPaint);
            canvas.restore();
        }
    }

    /**
     * 设置进度
     */
    public void setProgress(int progress){
        this.currentProgress = progress;
        invalidate();

    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        if (animator!=null){
//            animator.start();
//        }
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        if (animator!=null){
//            animator.cancel();
//        }
//    }
}
