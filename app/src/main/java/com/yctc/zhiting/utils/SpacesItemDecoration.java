package com.yctc.zhiting.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration{

    private HashMap<String, Integer> mSpaceValue;

    public static final String LEFT_SPACE = "left_space";
    public static final String TOP_SPACE = "top_space";
    public static final String RIGHT_SPACE = "right_space";
    public static final String BOTTOM_SPACE = "bottom_space";

    public SpacesItemDecoration(HashMap<String, Integer> spaceValue){
        this.mSpaceValue = spaceValue;
    }

    @Override
    public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mSpaceValue.get(TOP_SPACE) != null)
            outRect.top = mSpaceValue.get(TOP_SPACE);
        if (mSpaceValue.get(LEFT_SPACE) != null)
            outRect.left = mSpaceValue.get(LEFT_SPACE);
        if (mSpaceValue.get(RIGHT_SPACE) != null)
            outRect.right = mSpaceValue.get(RIGHT_SPACE);
        if (mSpaceValue.get(BOTTOM_SPACE) != null)
            outRect.bottom = mSpaceValue.get(BOTTOM_SPACE);
    }
}
