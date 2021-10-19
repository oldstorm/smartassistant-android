package com.app.main.framework.widget;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.main.framework.R;
import com.app.main.framework.baseutil.LogUtil;


/**
 * on 2019/4/18
 */
public class ReplaceViewHelper {
    private static ReplaceViewHelper instance = null;
    private View mTargetView = null;
    private View mReplaceView = null;
    private Context mContext;
    private int mResource = 0;//图片资源
    private String mDesc = "";//文本显示

    public static ReplaceViewHelper getInstance(Activity activity) {
//        if (instance == null) {
//            synchronized (ReplaceViewHelper.class) {
//                if (instance == null) {
//                    instance = new ReplaceViewHelper(activity);
//                }
//            }
//        }
        instance = new ReplaceViewHelper(activity);
        return instance;

    }

    public ReplaceViewHelper(Context context) {
        mContext = context;
    }

    OnClickListener listener;

    public interface OnClickListener {
        void onClick();
    }

    public ReplaceViewHelper setOnClickListener(OnClickListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * @return
     */
    public ReplaceViewHelper show() {
        replaceView(mTargetView, mReplaceView);
        return this;
    }

    /**
     * 默认有图
     *
     * @param targetView
     * @return
     */
    public ReplaceViewHelper replaceView(View targetView) {
        View view = View.inflate(mContext, R.layout.layout_empty_default, null);
//        mReplaceView = view;
//        mTargetView = targetView;
        replaceView(targetView, view);
        return this;
    }

    /**
     * 设置图片
     *
     * @param resource
     * @return
     */
    public ReplaceViewHelper image(int resource) {
        mResource = resource;
        ImageView imageView = mReplaceView.findViewById(R.id.imageView);
        if (mResource != 0) {
            imageView.setImageResource(mResource);
        }
        return this;
    }

    /**
     * 设置文本
     *
     * @param text
     * @return
     */
    public ReplaceViewHelper descText(String text) {
        mDesc = text;
        TextView textView = mReplaceView.findViewById(R.id.textView);
        if (!TextUtils.isEmpty(mDesc)) {
            textView.setText(mDesc);
        }
        return this;
    }

    /**
     * 设置空白页的高度
     * @param
     * @return
     */
    public ReplaceViewHelper showTop() {
        TextView tvTop = mReplaceView.findViewById(R.id.tvTop);
        tvTop.setVisibility(View.GONE);
        tvTop.invalidate();
        return this;
    }
    /**
     * 工作文本
     *
     * @param text
     * @return
     */
    public ReplaceViewHelper actionText(String text) {
        mDesc = text;
        TextView textView = mReplaceView.findViewById(R.id.tv_action);
        if (!TextUtils.isEmpty(mDesc)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(mDesc);
        }
        return this;
    }

    public ReplaceViewHelper action(String text, final OnClickListener listener) {
        mDesc = text;
        TextView textView = mReplaceView.findViewById(R.id.tv_action);
        if (!TextUtils.isEmpty(mDesc)) {
            textView.setText(mDesc);
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick();
            }
        });
        return this;
    }

    /**
     * 需要设置动作文本
     *
     * @param listener
     * @return
     */
    public ReplaceViewHelper action(final OnClickListener listener) {

        LinearLayout ll_root = mReplaceView.findViewById(R.id.ll_root);
        ll_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick();
            }
        });
        return this;
    }

    /**
     * 用来替换某个View，比如你可以用一个空页面去替换某个View
     *
     * @param targetView       被替换的那个View
     * @param replaceViewResId 要替换进去的布局LayoutId
     * @return
     */
    public ReplaceViewHelper replaceView(View targetView, final int replaceViewResId) {
        replaceView(targetView, View.inflate(mContext, replaceViewResId, null));
        return this;
    }

    /**
     * 用来替换某个View，比如你可以用一个空页面去替换某个View
     *
     * @param targetView  被替换的那个View
     * @param replaceView 要替换进去的那个View
     * @return
     */
    public ReplaceViewHelper replaceView(View targetView, final View replaceView) {
        mTargetView = targetView;
        if (mTargetView == null) {
            return this;
        } else if (!(mTargetView.getParent() instanceof ViewGroup)) {
            return this;
        }

        ViewGroup parentViewGroup = (ViewGroup) mTargetView.getParent();
        int index = parentViewGroup.indexOfChild(mTargetView);
        if (mReplaceView != null) {
            parentViewGroup.removeView(mReplaceView);
        }
        mReplaceView = replaceView;
        mReplaceView.setLayoutParams(mTargetView.getLayoutParams());

        parentViewGroup.addView(mReplaceView, index);

        //RelativeLayout时别的View可能会依赖这个View的位置，所以不能GONE
        if (parentViewGroup instanceof RelativeLayout) {
            mTargetView.setVisibility(View.INVISIBLE);
        } else {
            mTargetView.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 移除你替换进来的View
     */
    public final ReplaceViewHelper hide() {
        LogUtil.e("ReplaceViewHelper=0=" + mReplaceView);
        LogUtil.e("ReplaceViewHelper=1=" + mTargetView);
        if (mReplaceView != null && mTargetView != null) {
            if (mTargetView.getParent() instanceof ViewGroup) {
                ViewGroup parentViewGroup = (ViewGroup) mTargetView.getParent();
                parentViewGroup.removeView(mReplaceView);
                mTargetView.setVisibility(View.VISIBLE);
            }
        }
        return this;
    }

    /**
     * @return 返回你替换进来的View
     */
    public final View getView() {
        return mReplaceView;
    }
}

