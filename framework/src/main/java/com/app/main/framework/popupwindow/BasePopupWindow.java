package com.app.main.framework.popupwindow;

import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.app.main.framework.R;
import com.app.main.framework.baseview.BaseActivity;

public abstract class BasePopupWindow extends PopupWindow implements View.OnClickListener {

    public View view;
    public BaseActivity mContext;

    protected abstract int getLayoutId();

    public BasePopupWindow(BaseActivity context) {
        mContext = context;
        setWidthHeight(LayoutStyle.WRAP_ALL);//设置布局样式
        setFocusable(true);
        setOutsideTouchable(true);//设置popuwindow的弹出窗体是否可以点击
        setBackgroundDrawable(new ColorDrawable(0x00000000));//设置透明背景
        view = View.inflate(mContext, getLayoutId(), null);
        setContentView(view);//设置布局文件
        //setAnimationStyles(AnimStyle.BOTTOM);//设置动画
        //setShadowLevel(1.0f);//设置阴影
        measureWidthAndHeight(view);
        update(); // 刷新状态
    }


    /**
     * 设置遮罩的透明度
     *
     * @param level 0.0-1.0 越小越暗
     */
    public BasePopupWindow setShadowLevel(float level) {
        Window window = mContext.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.alpha = level;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(params);
        return this;
    }

    /**
     * 设置动画样式
     *
     * @param style
     */
    public BasePopupWindow setAnimationStyles(AnimStyle style) {
        int animStyle = R.style.CommonAnimCenter;
        if (style == AnimStyle.TOP) {
            animStyle = R.style.CommonAnimTop;
        } else if (style == AnimStyle.RIGHT) {
            animStyle = R.style.CommonAnimRight;
        } else if (style == AnimStyle.BOTTOM) {
            animStyle = R.style.CommonAnimBottom;
        } else if (style == AnimStyle.LEFT) {
            animStyle = R.style.CommonAnimLeft;
        } else if (style == AnimStyle.CENTER) {
            animStyle = R.style.CommonAnimCenter;
        }
        setAnimationStyle(animStyle);
        return this;
    }

    /**
     * 设置狂傲
     *
     * @param type
     */
    public BasePopupWindow setWidthHeight(LayoutStyle type) {
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (type == LayoutStyle.WRAP_ALL) {
            width = ViewGroup.LayoutParams.WRAP_CONTENT;
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else if (type == LayoutStyle.WRAP_WIDTH) {
            width = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else if (type == LayoutStyle.WRAP_HEIGHT) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
            width = ViewGroup.LayoutParams.MATCH_PARENT;
        } else if(type== LayoutStyle.MATCH_ALL){
            width = ViewGroup.LayoutParams.MATCH_PARENT;
            height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        setWidth(width);//设置弹出的popuwindow的宽
        setHeight(height); //设置弹出的popuwndow的高
        return this;
    }

    /**
     * 向下
     *
     * @param view
     */
    public void showAsDown(View view) {
        showAsDropDown(view);
    }

    /**
     * 向上
     *
     * @param view
     */
    public void showAsUp(View view) {
        showAsDropDown(view, 0, -(this.getHeight() + view.getMeasuredHeight()));
    }

    /**
     * 向左边
     *
     * @param view
     */
    public void showAsLeft(View view, PopupWindow popupWindow) {
        showAsDropDown(view, -popupWindow.getWidth(), -view.getHeight());
    }

    /**
     * 向右边
     *
     * @param view
     */
    public void showAsRight(View view) {
        showAsDropDown(view, view.getWidth(), -view.getHeight());
    }

    /**
     * 中间显示
     *
     * @param view
     */
    public void showAtCenter(View view) {
        showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    /**
     * 底部弹出
     * @param view
     */
    public void showAtBottom(View view) {
        if(!isShowing())
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 测量View的宽高
     *
     * @param view View
     */
    public void measureWidthAndHeight(View view) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public void dismiss() {
        super.dismiss();
        setShadowLevel(1.0f);
    }

    @Override
    public int getWidth() {
        return view.getMeasuredWidth();
    }

    @Override
    public int getHeight() {
        return view.getMeasuredHeight();
    }

    public enum LayoutStyle {
        WRAP_ALL, WRAP_WIDTH, WRAP_HEIGHT, MATCH_ALL
    }

    /**
     * 枚举类型
     */
    public enum AnimStyle {
        TOP,//上向下
        BOTTOM, //下向上
        LEFT,//右向左
        RIGHT, //左向右
        CENTER//中间
    }
}
