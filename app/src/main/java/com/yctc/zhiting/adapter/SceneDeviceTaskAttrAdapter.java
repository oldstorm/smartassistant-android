package com.yctc.zhiting.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
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
        Object val = item.getVal();
        String valStr = "";
        int min = 0;
        int max = 0;
        if (item.getMin()!=null){
            min = item.getMin();
        }
        if (item.getMax()!=null){
            max = item.getMax();
        }
        if (val!=null) {
            switch (item.getAttribute()) {
                case "power":  // 开关
                    valStr = StringUtil.switchStatus2String((String) val, mContext);
                    break;

                case "color_temp":  // 色温
                case "brightness":  // 亮度
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
            }
        }
        helper.setText(R.id.tvDeviceName, StringUtil.attr2String(item.getAttribute(), mContext))
                .setText(R.id.tvDeviceValue, valStr);
    }
}
