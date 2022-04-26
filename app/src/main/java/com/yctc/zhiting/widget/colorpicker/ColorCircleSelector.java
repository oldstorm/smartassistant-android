package com.yctc.zhiting.widget.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ColorCircleSelector extends View {

    private Paint selectorPaint;
    private float selectorRadiusPx = 9 * 3;
    private PointF currentPoint = new PointF();

    public ColorCircleSelector(Context context) {
        this(context, null);
    }

    public ColorCircleSelector(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorCircleSelector(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        selectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectorPaint.setColor(Color.parseColor("#FFFFFFFF"));
        selectorPaint.setStyle(Paint.Style.STROKE);
        selectorPaint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //1-圆弧的位置:整圆,再绘制进度圆弧
        //屏幕宽度
        int width = getMeasuredWidth();
        RectF rectF = new RectF();
        rectF.left = currentPoint.x-10;//左上角X
        rectF.top = currentPoint.y-10;//左上角Y
        rectF.right = currentPoint.x+10;//右上角X
        rectF.bottom = currentPoint.y+10;//右上角Y
        if ((rectF.right - rectF.left) > (rectF.bottom- rectF.top)){//正方形矩形,保证画出的圆不会变成椭圆
            float space = (rectF.right - rectF.left) - (rectF.bottom- rectF.top);
            rectF.left += space/2;
            rectF.right -= space/2;
        }
        canvas.drawArc(rectF,270,360,false,selectorPaint);//第2个参数:时钟3点处为0度,逆时针为正方向
    }

    public void setSelectorRadiusPx(float selectorRadiusPx) {
        this.selectorRadiusPx = selectorRadiusPx;
    }

    public void setCurrentPoint(PointF currentPoint) {
        this.currentPoint = currentPoint;
        invalidate();
    }
}
