package com.yctc.zhiting.adapter;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.scene.SceneConditionAttrEntity;
import com.yctc.zhiting.entity.scene.SceneConditionEntity;
import com.yctc.zhiting.utils.AttrUtil;
import com.yctc.zhiting.utils.StringUtil;


public class SceneDeviceTaskAttrAdapter extends BaseQuickAdapter<SceneConditionAttrEntity, BaseViewHolder> {

    public SceneDeviceTaskAttrAdapter() {
        super(R.layout.item_scene_device_task_attr);
    }

    @Override
    protected void convert(BaseViewHolder helper, SceneConditionAttrEntity item) {
        View viewColor = helper.getView(R.id.viewColor);
        TextView tvDeviceValue = helper.getView(R.id.tvDeviceValue);
        Object val = item.getVal();
        String valStr = "";
        String color = "#FFFFFF";
        int min = 0;
        int max = 0;
        if (item.getMin()!=null){
            min = item.getMin();
        }
        if (item.getMax()!=null){
            max = item.getMax();
        }
        if (val!=null) {
            switch (item.getType()) {
                case Constant.ON_OFF:  // 开关
//                case Constant.powers_1:  // 开关
//                case Constant.powers_2:  // 开关
//                case Constant.powers_3:  // 开关
                    valStr = StringUtil.switchStatus2String((String) val, mContext);
                    break;

                case Constant.color_temp:  // 色温
                case Constant.brightness:  // 亮度
                    int valInt = 0;
                    try {
                        if (val!=null){
                            valInt = AttrUtil.getPercentVal(min, max, (Double) val);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    valStr = valInt > 0 ? valInt + "%" : "";
                    break;


                case Constant.rgb: // 彩色
                    color = val.toString();
                    break;

                case Constant.target_state:
                    int target_state = 0;
                    if (val != null) {
                        double valDou = ((Double)val).doubleValue();
                        target_state = (int) valDou;
                    }
                    valStr = StringUtil.targetStatStr(mContext, target_state);
                    break;

                case Constant.target_position:
                    int target_position = 0;
                    if (val != null) {
                        double valDou = ((Double)val).doubleValue();
                        target_position = (int) valDou;
                    }
                    valStr = StringUtil.targetPositionStr(mContext, min, max, target_position,"");
                    break;
            }
            if (item.getType().equals(Constant.rgb)){
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(UiUtil.dip2px(4));
                drawable.setColor(Color.parseColor(color));
                viewColor.setBackground(drawable);
                viewColor.setVisibility(View.VISIBLE);
                tvDeviceValue.setVisibility(View.INVISIBLE);
            } else {
                viewColor.setVisibility(View.GONE);
                tvDeviceValue.setVisibility(View.VISIBLE);
            }
        }else {
            viewColor.setVisibility(View.GONE);
            tvDeviceValue.setVisibility(View.VISIBLE);
        }
        helper.setText(R.id.tvDeviceName, StringUtil.attr2String(item.getType(), mContext))
                .setText(R.id.tvDeviceValue, valStr);
    }
}
