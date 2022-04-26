package com.yctc.zhiting.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class CustomImageSpan extends ImageSpan {

    private final int mMarginLeft;
    private final int mMarginRight;
    private WeakReference<Drawable> mDrawableWr;

    public CustomImageSpan(@NonNull Drawable drawable) {
        this(drawable, 0, 0);
    }

    public CustomImageSpan(@NonNull Drawable drawable, int marginLeft, int marginRight) {
        super(drawable);
        mMarginLeft = marginLeft;
        mMarginRight = marginRight;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        Drawable drawable = getDrawable();
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        x = mMarginLeft + x;
        int transY = (y + fm.descent + y + fm.ascent) / 2 - drawable.getBounds().bottom / 2;
        canvas.save();
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return mMarginLeft + super.getSize(paint, text, start, end, fm) + mMarginRight;
    }

    private Drawable getCacheDrawable() {
        WeakReference<Drawable> wr = mDrawableWr;
        Drawable drawable = null;
        if (wr != null) {
            drawable = wr.get();
        }
        if (drawable == null) {
            drawable = getDrawable();
            mDrawableWr = new WeakReference<>(drawable);
        }
        return drawable;
    }
}
