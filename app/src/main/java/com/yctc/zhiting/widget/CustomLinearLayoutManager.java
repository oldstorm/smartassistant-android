package com.yctc.zhiting.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomLinearLayoutManager extends LinearLayoutManager {

    private static final String TAG = CustomLinearLayoutManager.class.getSimpleName();

    private int mMaxHeight;

    public CustomLinearLayoutManager(Context context, int maxHeight) {
        super(context);
        mMaxHeight = maxHeight;
    }

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }



    @Override
    public void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
        super.setMeasuredDimension(childrenBounds, wSpec, View.MeasureSpec.makeMeasureSpec(mMaxHeight, View.MeasureSpec.AT_MOST));
    }
}
