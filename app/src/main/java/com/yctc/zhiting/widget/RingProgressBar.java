package com.yctc.zhiting.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 环形进度
 */
public class RingProgressBar extends View {

    private Context mContext;
    /**
     * 文本画笔
     */
    private Paint mTextPaint;
    /**
     * 圆弧画笔
     */
    private Paint mArcCirclePaint;
    /**
     * 宽度
     */
    private int mWidth = 100;
    /**
     * 文本
     */
    private String mText  ="0%";
    /**
     * 弧度
     */
    private int mAngleValue = 0;
    /**
     * 圆的背景色:默认浅绿色
     */
    private int mCircleBackgroundColor = 0xffDDE5EB;
    /**
     * 进度的颜色,默认白色
     */
    private int mProgressColor = 0xff2DA3F6;

    /**
     * 文本的颜色,默认白色
     */
    private int mTextColor = 0xffffffff;
    /**
     * 边宽
     */
    private int mStrokeWidth = 10;
    /**
     * 进度圆边宽
     */
    private int mInnerStrokeWidth = 9;
    /**
     * 文本大小
     */
    private int mTextSize = 12;

    //扫描的转速，表示几秒转一圈
    private float mSpeed = 1f;

    private float mDegrees = 270; //旋转角度。
    private boolean rotating;  // 设置旋转


    public RingProgressBar(Context context) {
        this(context, null);
    }

    public RingProgressBar(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingProgressBar(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;

        mTextPaint  = new Paint();
        //设置抗锯齿
        mTextPaint.setAntiAlias(true);
        //使文本看起来更清晰
        mTextPaint.setLinearText(true);

        mArcCirclePaint  = new Paint();
        mArcCirclePaint.setAntiAlias(true);
        mArcCirclePaint.setStyle(Paint.Style.STROKE);

    }


    public void setText(String text){
        mText = text;
        invalidate();
    }




    public void setTextColor(int textColor) {
        this.mTextColor = mContext.getResources().getColor(textColor);
        invalidate();
    }


    public void setProgressColor(int progressColor) {
        this.mProgressColor = mContext.getResources().getColor(progressColor);
        invalidate();
    }

    public void setCircleBackgroundColor(int circleBackgroundColor) {
        this.mCircleBackgroundColor = mContext.getResources().getColor(circleBackgroundColor);
        invalidate();
    }

    public void setStrokeWidth(int strokeWidth){
        this.mStrokeWidth = strokeWidth;
        invalidate();
    }

    public void setInnerStrokeWidth(int innerStrokeWidth){
        this.mInnerStrokeWidth = innerStrokeWidth;
        invalidate();
    }

    public void setTextSize(int textSize){
        this.mTextSize = textSize;
        invalidate();
    }



    /**
     * 设置进度
     * @param progress
     */
    public void setProgress(float progress){
        int angleValue = (int) ((progress * 1.0)/100 * 360);
        if (angleValue != 0 && progress <= 100){
            mAngleValue  = angleValue;
            mText = String.valueOf(progress)+"%";
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getSize(widthMeasureSpec);
        int height = getSize(heightMeasureSpec);
        if (width<height){
            height = width;
        }else {
            width = height;
        }
        mWidth = Math.min(width, height);
        mStrokeWidth = mWidth/10;
        mInnerStrokeWidth = mStrokeWidth - mStrokeWidth/10;
        setMeasuredDimension(width, height);
    }

    private int getSize(int measureSpec){
        int viewSize = mWidth;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode){
            case MeasureSpec.UNSPECIFIED:
                viewSize = mWidth;
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

        //1-圆弧的位置:整圆,再绘制进度圆弧
        mArcCirclePaint.setColor(mCircleBackgroundColor);
        mArcCirclePaint.setStrokeWidth(mStrokeWidth);
        //屏幕宽度
        int width = getMeasuredWidth();
        RectF rectF = new RectF();
        rectF.left = (width-mWidth)/2;//左上角X
        rectF.top = mWidth*0.1f;//左上角Y
        rectF.right = (width-mWidth)/2+mWidth;//右上角X
        rectF.bottom = mWidth*0.9f;//右上角Y
        if ((rectF.right - rectF.left) > (rectF.bottom- rectF.top)){//正方形矩形,保证画出的圆不会变成椭圆
            float space = (rectF.right - rectF.left) - (rectF.bottom- rectF.top);
            rectF.left += space/2;
            rectF.right -= space/2;
        }
        canvas.drawArc(rectF,270,360,false,mArcCirclePaint);//第2个参数:时钟3点处为0度,逆时针为正方向

        mArcCirclePaint.setColor(mProgressColor);
        //设置边角为圆
        mArcCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mArcCirclePaint.setStrokeWidth(mInnerStrokeWidth);
        drawValueArc(canvas, rectF);

//        //2-文本的位置:居中显示
//        int centerX = width/2;
//        //计算文本宽度
//        int textWidth = (int) mTextPaint.measureText(mText, 0, mText.length());
//        //计算baseline:垂直方向居中
//        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
//        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
//        int textX = centerX-textWidth/2;
//        mTextPaint.setColor(mTextColor);
//        mTextPaint.setTextSize(mTextSize);
//        canvas.drawText(mText,textX,baseline,mTextPaint);

    }

    private void drawValueArc(Canvas canvas, RectF rectF){
        canvas.drawArc(rectF,mDegrees,mAngleValue,false,mArcCirclePaint);
        if (rotating){
            mDegrees = (mDegrees + (360 / mSpeed / 60)) % 360;
            invalidate();
        }
    }

    public boolean isRotating() {
        return rotating;
    }

    public void setRotating(boolean rotating) {
        this.rotating = rotating;
    }
}
