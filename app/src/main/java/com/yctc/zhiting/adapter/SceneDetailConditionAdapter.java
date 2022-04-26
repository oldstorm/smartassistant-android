package com.yctc.zhiting.adapter;

import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimajia.swipe.SwipeLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.scene.SceneConditionAttrEntity;
import com.yctc.zhiting.entity.scene.SceneConditionEntity;
import com.yctc.zhiting.entity.scene.SceneDeviceInfoEntity;
import com.yctc.zhiting.utils.AttrUtil;
import com.yctc.zhiting.utils.StringUtil;
import com.yctc.zhiting.utils.TimeUtil;

/**
 * 场景详情 条件适配器
 */
public class SceneDetailConditionAdapter extends BaseQuickAdapter<SceneConditionEntity, BaseViewHolder> {

    private boolean swipe = true;

    public SceneDetailConditionAdapter() {
        super(R.layout.item_scene_detail_condition);
    }

    public void setSwipe(boolean swipe) {
        this.swipe = swipe;
    }

    @Override
    protected void convert(BaseViewHolder helper, SceneConditionEntity item) {
        helper.addOnClickListener(R.id.llContent);
        helper.addOnClickListener(R.id.tvDel);
        boolean notLast = helper.getAdapterPosition() < getData().size() - 1;
        SwipeLayout swipeLayout = helper.getView(R.id.swipeLayout);
        swipeLayout.setSwipeEnabled(swipe);
        TextView tvTitle = helper.getView(R.id.tvTitle);
        TextView tvSubtitle = helper.getView(R.id.tvSubtitle);
        TextView tvLocation = helper.getView(R.id.tvLocation);
        ImageView ivCover = helper.getView(R.id.ivCover);
        LinearLayout llContent = helper.getView(R.id.llContent);
        TextView tvDel = helper.getView(R.id.tvDel);
        View viewLine = helper.getView(R.id.viewLine);
        llContent.setBackgroundResource(notLast ? R.drawable.shape_white : R.drawable.shape_white_bottom_c10);
        tvDel.setBackgroundResource(notLast ? R.drawable.shape_red : R.drawable.shape_red_bottom_right_c10);
        viewLine.setVisibility(notLast ? View.VISIBLE : View.GONE);

        int type = item.getCondition_type();
        tvSubtitle.setVisibility(type > 0 ? View.VISIBLE : View.GONE);
        boolean hasSetTitle = false;
        switch (type) {
            case 0: // 手动
                tvTitle.setText(mContext.getResources().getString(R.string.scene_manual_perform));
                ivCover.setBackgroundResource(R.color.white);
                ivCover.setImageResource(R.drawable.icon_scene_manual);
                break;

            case 1:  // 定时
                tvTitle.setText(TimeUtil.getTodayHMS(item.getTiming()));
                tvSubtitle.setText(mContext.getResources().getString(R.string.scene_timing));
                ivCover.setBackgroundResource(R.color.white);
                ivCover.setImageResource(R.drawable.icon_scene_timing);
                break;

            case 2: // 设备状态变化时
                SceneDeviceInfoEntity deviceInfo = item.getDevice_info();
                SceneConditionAttrEntity attr = item.getCondition_attr();
                String title = "";  // 标题

                if (attr != null) {  // 设备属性
                    Object val = attr.getVal();
                    int min = 0;
                    int max = 0;
                    if (attr.getMin() != null) {
                        min = attr.getMin();
                    }
                    if (attr.getMax() != null) {
                        max = attr.getMax();
                    }
                    try {
                        String operator = "";
                        if (!TextUtils.isEmpty(item.getOperator())) {
                            operator = StringUtil.operator2String(item.getOperator(), mContext); // 操作符文案
                        }

                        switch (attr.getType()) {
                            case Constant.ON_OFF:  // 开关
                                title = StringUtil.switchStatus2String((String) val, mContext);
                                break;

                            case Constant.color_temp:  // 色温
                                int colorTemp = 0;
                                if (val != null) {
                                    colorTemp = AttrUtil.getPercentVal(min, max, (Double) val);
                                }
                                title = mContext.getResources().getString(R.string.scene_color_temperature) + operator + colorTemp + "%";
                                break;

                            case Constant.brightness:  // 亮度
                                int brightness = 0;
                                if (val != null) {
                                    brightness = AttrUtil.getPercentVal(min, max, (Double) val);
                                }
                                title = mContext.getResources().getString(R.string.scene_brightness) + operator + brightness + "%";
                                break;

                            case Constant.leak_detected:  // 水浸
                                title = mContext.getResources().getString(R.string.scene_check_watering);
                                break;

                            case Constant.temperature:  // 温度
                                int temperature = 0;
                                if (val != null) {
                                    double valDou = ((Double)val).doubleValue();
                                    temperature = (int) valDou;
                                }
                                title = mContext.getResources().getString(R.string.scene_temperature) + operator + temperature + mContext.getResources().getString(R.string.scene_temperature_unit);
                                break;

                            case Constant.humidity:  // 湿度
                                int humidity = 0;
                                if (val != null) {
                                    humidity = AttrUtil.getPercentVal(min, max, (Double) val);
                                }
                                title = mContext.getResources().getString(R.string.scene_humidity) + operator + humidity + "%";
                                break;

                            case Constant.window_door_close:  // 门窗
                                int window_door_close = 0;
                                if (val != null) {
                                    double vaDou = ((Double)val).doubleValue();
                                    window_door_close = (int)vaDou;
                                }
                                title = StringUtil.doorWindowStatus2String(mContext, window_door_close);
                                break;

                            case Constant.detected: // 人体
                                title = mContext.getResources().getString(R.string.scene_check_moving);
                                break;

                            case Constant.powers_1:  // 开关 (一键)
                                title = mContext.getString(R.string.scene_powers_1)+StringUtil.switchStatus2String((String) val, mContext);
                                break;

                            case Constant.powers_2:  // 开关（二键）
                                title = mContext.getString(R.string.scene_powers_2)+StringUtil.switchStatus2String((String) val, mContext);
                                break;

                            case Constant.powers_3:  // 开关（三键）
                                title = mContext.getString(R.string.scene_powers_3) + StringUtil.switchStatus2String((String) val, mContext);
                                break;

                            case Constant.rgb:  // 彩色
                                String color = "#FFFFFF";
                                if (val != null){
                                    color = (String)  val;
                                }
                                title = mContext.getString(R.string.home_color)+"■";
                                tvTitle.setText(StringUtil.changeTextColor(title, Color.parseColor(color)));
                                hasSetTitle = true;
                                break;

                            case Constant.target_state:  // 网关守护状态
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
                                title = StringUtil.targetPositionStr(mContext, min, max, target_position, operator);
                                break;

                            case Constant.switch_event:  // 无线开关（一二长按）
                                int switchType = 0;
                                if (val != null) {
                                    double valDou = ((Double)val).doubleValue();
                                    switchType = (int) valDou;
                                }
                                title = StringUtil.switchEventStr(mContext, switchType);
                                break;
                        }
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }

                }
                if (!hasSetTitle) {
                    tvTitle.setText(title);
                }
                if (deviceInfo != null) {  // 设备
                    tvSubtitle.setText(deviceInfo.getName());
                    GlideUtil.load(deviceInfo.getLogo_url()).into(ivCover);
                }

                ivCover.setBackgroundResource(R.drawable.shape_stroke_eeeeee_c4);

                break;
        }
    }
}
