package com.app.main.framework.view.defaultview;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import com.app.main.framework.R;
import com.app.main.framework.baseutil.UiUtil;

/**
 * 加载无数据显示布局
 */
public class LoadDefaultView{

    public static View loadNoDataView(String hint, ViewGroup.LayoutParams layoutParams){
        View view = UiUtil.inflate(R.layout.no_data_view);
        if (layoutParams != null)
            view.setLayoutParams(layoutParams);
        if (!TextUtils.isEmpty(hint)) {
            TextView hintText = view.findViewById(R.id.tvNoDataHint);
            hintText.setText(hint);
        }
        return view;
    }

    /**
     * 获取空数据界面
     * @param noDataBean
     * @return
     */
    public View loadNoDataView(NoDataBean noDataBean){
        return loadNoDataView(noDataBean,R.layout.default_view);
    }

    /**
     * 获取空数据界面
     * @param noDataBean
     * @return
     */
    public View loadNoDataView(NoDataBean noDataBean, @LayoutRes int layoutId){
        View inflate = UiUtil.inflate(layoutId);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, noDataBean.getHeight());
        inflate.setLayoutParams(params);
        ImageView ivDefaultLcon = inflate.findViewById(R.id.iv_default_lcon);
        TextView tvHint = inflate.findViewById(R.id.tv_hint);
        TextView tvLoad = inflate.findViewById(R.id.tv_load);
        tvLoad.setClickable(true);
        ivDefaultLcon.setImageResource(noDataBean.getImageResourceId());
        tvHint.setText(noDataBean.getTextResourceId());
        if (noDataBean.isReflash() || noDataBean.getReflashClickListener() != null){
            tvLoad.setOnClickListener(noDataBean.getReflashClickListener());
        }else
            tvLoad.setVisibility(View.GONE);
        return inflate;
    }

    public static View loadNoNetWorkView(){
        return UiUtil.inflate(R.layout.default_view);
    }
}
