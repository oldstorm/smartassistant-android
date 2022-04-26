package com.yctc.zhiting.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.scene.SceneConditionAttrEntity;
import com.yctc.zhiting.entity.scene.SceneControlSceneInfoEntity;
import com.yctc.zhiting.entity.scene.SceneDeviceInfoEntity;
import com.yctc.zhiting.entity.scene.SceneTaskEntity;
import com.yctc.zhiting.utils.AttrUtil;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.StringUtil;
import com.yctc.zhiting.utils.TimeUtil;

import java.util.List;

/**
 * 场景详情 执行任务列表适配器
 */
public class SceneDetailTaskAdapter extends BaseQuickAdapter<SceneTaskEntity, BaseViewHolder> {

    public SceneDetailTaskAdapter() {
        super(R.layout.item_scene_detail_task);
    }

    @Override
    protected void convert(BaseViewHolder helper, SceneTaskEntity item) {
        helper.addOnClickListener(R.id.llContent);
        helper.addOnClickListener(R.id.tvDel);
        boolean notLast = helper.getAdapterPosition()<getData().size()-1;
        int color = 0;
        boolean hasColor = false;
        /***********************获取控件************************/
        LinearLayout llContent = helper.getView(R.id.llContent);
        TextView tvDel = helper.getView(R.id.tvDel);
        View viewLine = helper.getView(R.id.viewLine);
        llContent.setBackgroundResource(notLast ? R.drawable.shape_white : R.drawable.shape_white_bottom_c10 );
        tvDel.setBackgroundResource(notLast ? R.drawable.shape_red : R.drawable.shape_red_bottom_right_c10);
        viewLine.setVisibility(notLast ? View.VISIBLE : View.GONE);
        TextView tvTitle = helper.getView(R.id.tvTitle);
        TextView tvSubtitle = helper.getView(R.id.tvSubtitle);
        TextView tvLocation = helper.getView(R.id.tvLocation);
        TextView tvTime = helper.getView(R.id.tvTime);
        ImageView ivCover = helper.getView(R.id.ivCover);

        /***************************标题显示***************************/
        // 1:控制设备；2:手动执行场景;3:开启自动执行场景;4:关闭自动执行场景
        int type = item.getType();
        SceneDeviceInfoEntity deviceInfoEntity = item.getDevice_info(); // 设备信息
        List<SceneConditionAttrEntity> attributes = item.getAttributes(); // 设备属性信息
        SceneControlSceneInfoEntity sceneControlSceneInfoEntity = item.getControl_scene_info();  // 场景控制信息
        String title = ""; // 标题
        String subTitle = ""; // 右边文案
        if (type == 1){  // 设备
            if (CollectionUtil.isNotEmpty(attributes)){  // 设备属性不为空
                int attrSize = attributes.size();  // 设备属性个数
                for (int i=0; i<attrSize; i++){
                    SceneConditionAttrEntity sceneConditionAttrEntity = attributes.get(i);
                    if (sceneConditionAttrEntity!=null){
                    Object val = sceneConditionAttrEntity.getVal();
                    int min = 0;
                    int max = 0;
                    if (sceneConditionAttrEntity.getMin()!=null){
                        min = sceneConditionAttrEntity.getMin();
                    }
                    if (sceneConditionAttrEntity.getMax()!=null){
                        max = sceneConditionAttrEntity.getMax();
                    }
                    if (val!=null) {
                        try {
                            switch (sceneConditionAttrEntity.getType()) {
                                case Constant.ON_OFF: // 开关
//                                case Constant.powers_1:
//                                case Constant.powers_2:
//                                case Constant.powers_3:
                                    String switched = StringUtil.switchStatus2String((String) val, mContext);
                                    title = title + switched + (i < attrSize - 1 ? "、" : "");
                                    break;

                                case Constant.brightness:  // 亮度
                                    int brightness = 0;
                                    if (val != null) {
                                        brightness = AttrUtil.getPercentVal(min, max, (Double) val);
                                    }
                                    title = title + mContext.getResources().getString(R.string.scene_brightness) + brightness + "%" + (i < attrSize - 1 ? "、" : "");
                                    break;

                                case Constant.color_temp:  // 色温
                                    int colorTemp = 0;
                                    if (val != null) {
                                        colorTemp = AttrUtil.getPercentVal(min, max, (Double) val);
                                    }
                                    title = title + mContext.getResources().getString(R.string.scene_color_temperature) + colorTemp + "%" + (i < attrSize - 1 ? "、" : "");
                                    break;

                                case Constant.rgb: // 彩色
                                    color = Color.parseColor((String)  val);
                                    hasColor = true;
                                    title = title + mContext.getString(R.string.home_color)+"■" + (i < attrSize - 1 ? "、" : "");
                                    break;

                                case Constant.target_state:
                                    int target_state = 0;
                                    if (val != null) {
                                        double valDou = ((Double)val).doubleValue();
                                        target_state = (int) valDou;
                                    }
                                    title = StringUtil.targetStatStr(mContext, target_state);
                                    break;

                                case Constant.target_position: // 窗帘状态
                                    int target_position = 0;
                                    if (val != null) {
                                        double valDou = ((Double)val).doubleValue();
                                        target_position = (int) valDou;
                                    }
                                    title = StringUtil.targetPositionStr(mContext, min, max, target_position, "");
                                    break;

                            }
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                            ToastUtil.show(UiUtil.getString(R.string.common_server_error));
                        }
                    }
                    }
                }
            }
            // 设备信息不为空
            if (deviceInfoEntity!=null){
                subTitle = deviceInfoEntity.getName();
                GlideUtil.load(deviceInfoEntity.getLogo_url()).into(ivCover);
                if (deviceInfoEntity.getStatus() == 2){ // 设备已删除
                    tvLocation.setVisibility(View.VISIBLE);
                    tvLocation.setText(mContext.getResources().getString(R.string.scene_device_removed));
                    tvLocation.setTextColor(UiUtil.getColor(R.color.color_F6AE1E));
                }else { // 设备没删除
                    String location = Constant.AREA_TYPE == 2 ? deviceInfoEntity.getDepartment_name() : deviceInfoEntity.getLocation_name();
                    tvLocation.setVisibility(TextUtils.isEmpty(location) ? View.GONE : View.VISIBLE);
                    tvLocation.setText(location);
                    tvLocation.setTextColor(UiUtil.getColor(R.color.color_94A5BE));
                }
            }
        }else {  // 场景
            switch (type){
                case 2:  // 手动执行场景
                    title = mContext.getResources().getString(R.string.scene_perform);
                    break;

                case 3:  // 开启自动执行场景
                    title = mContext.getResources().getString(R.string.scene_turn_on);
                    break;

                case 4:  // 关闭自动执行场景
                    title = mContext.getResources().getString(R.string.scene_turn_off);
                    break;
            }
            if (sceneControlSceneInfoEntity!=null){  // 场景信息不为空
                subTitle = sceneControlSceneInfoEntity.getName();
                tvLocation.setVisibility(sceneControlSceneInfoEntity.getStatus() == 1 ? View.GONE : View.VISIBLE);
                tvLocation.setText(mContext.getResources().getString(R.string.scene_scene_removed));
                tvLocation.setTextColor(UiUtil.getColor(R.color.color_F6AE1E));
            }
            ivCover.setImageResource(R.drawable.icon_scene);
        }
        if (hasColor){  // 如果有彩色
            tvTitle.setText(StringUtil.changeTextColor(title, color));
        }else {
            tvTitle.setText(title);
        }
        tvSubtitle.setText(subTitle);

        /****************** 延时 *******************/
        tvTime.setVisibility(item.getDelay_seconds()>0 ? View.VISIBLE : View.GONE);
        int delaySeconds = item.getDelay_seconds();
        int hour = delaySeconds/3600;
        int minute =  (delaySeconds % 3600) / 60;
        int seconds = (delaySeconds % 3600) % 60;
        String time = TimeUtil.toHMSString(hour, minute, seconds, mContext);
        tvTime.setText(String.format(mContext.getResources().getString(R.string.scene_delay_after), time));
    }
}
