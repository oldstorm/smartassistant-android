package com.yctc.zhiting.adapter;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.scene.SceneBean;
import com.yctc.zhiting.entity.scene.SceneItemBean;
import com.yctc.zhiting.utils.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 场景
 */
public class SceneAdapter extends BaseQuickAdapter<SceneBean, BaseViewHolder> {

    private int type;
    private boolean connected;
    private boolean isSorting;

    public SceneAdapter(int type) {
        super(R.layout.item_scene);
        this.type = type;
    }

    public void setStatus(boolean connected){
        this.connected = connected;
        notifyDataSetChanged();
    }

    /**
     * 排序
     * @param isSorting
     */
    public void setSorting(boolean isSorting) {
        this.isSorting = isSorting;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, SceneBean item) {
        helper.addOnClickListener(R.id.tvPerform);
        helper.addOnClickListener(R.id.ivSwitch);
        helper.addOnClickListener(R.id.llSwitch);
        ImageView ivType = helper.getView(R.id.ivType);
        ImageView ivLink = helper.getView(R.id.ivLink);
        ImageView ivSwitch = helper.getView(R.id.ivSwitch);
        ImageView ivSort = helper.getView(R.id.ivSort);
        RecyclerView rvDevice = helper.getView(R.id.rvDevice);
        rvDevice.setOnTouchListener((v, event) -> helper.getView(R.id.clParent).onTouchEvent(event));
        rvDevice.setLayoutManager(new GridLayoutManager(mContext, type == 1 ? 5 : 7));
        if (rvDevice.getItemDecorationCount()<=0) {
            HashMap<String, Integer> spaceValue = new HashMap<>();
            spaceValue.put(SpacesItemDecoration.LEFT_SPACE, 0);
            spaceValue.put(SpacesItemDecoration.TOP_SPACE, 0);
            spaceValue.put(SpacesItemDecoration.RIGHT_SPACE, 9);
            spaceValue.put(SpacesItemDecoration.BOTTOM_SPACE, 10);
            SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(spaceValue);
            rvDevice.addItemDecoration(spacesItemDecoration);
        }
        SceneItemAdapter sceneItemAdapter = new SceneItemAdapter();
        rvDevice.setAdapter(sceneItemAdapter);
        ivType.setVisibility(type == 1 ? View.VISIBLE : View.GONE);
        ivLink.setVisibility(type == 1 ? View.VISIBLE : View.GONE);
        SceneItemBean condition = item.getCondition();

        // 条件类型图标
        if (type == 1 && condition!=null){
            if (condition.getType() == 1){  // 定时
                ivType.setScaleType(ImageView.ScaleType.CENTER);
                ivType.setPadding(0, 0, 0, 0);
                ivType.setImageResource(R.drawable.icon_scene_timing);
                ivType.setBackgroundResource(R.drawable.shape_white);
            }else if (condition.getType() == 2){  // 设备状态变化
                GlideUtil.load(condition.getLogo_url()).into(ivType);
                int padding = UiUtil.getDimens(R.dimen.dp_4);
                ivType.setPadding(padding, padding, padding, padding);
                ivType.setScaleType(ImageView.ScaleType.FIT_CENTER);
                ivType.setBackgroundResource(R.drawable.shape_stroke_eeeeee_c4);
            }
        }
        LinearLayout llPerform = helper.getView(R.id.llPerform);
        TextView tvPerform = helper.getView(R.id.tvPerform);
        TextView tvName = helper.getView(R.id.tvName);
        ProgressBar rbPerform = helper.getView(R.id.rbPerform);
        LinearLayout llSwitch = helper.getView(R.id.llSwitch);
        ProgressBar rbSwitch = helper.getView(R.id.rbSwitch);

        tvName.setText(item.getName());

        llPerform.setVisibility(!isSorting && type == 0 ? View.VISIBLE : View.GONE);
        tvPerform.setVisibility(type == 0 && !item.isPerforming() ? View.VISIBLE : View.GONE);
        rbPerform.setVisibility(type == 0 && item.isPerforming() ? View.VISIBLE : View.GONE);

        // 执行按钮
        tvPerform.setEnabled(connected);
        tvPerform.setBackgroundResource((connected && item.isControl_permission()) ? R.drawable.shape_f1f4fc_c4 : R.drawable.shape_eeeeee_c4);
        tvPerform.setTextColor((connected && item.isControl_permission()) ? UiUtil.getColor(R.color.color_2da3f6) : UiUtil.getColor(R.color.white));

        // 开关按钮
        llSwitch.setEnabled(item.isControl_permission() && connected);
        rbSwitch.setVisibility(item.isPerforming() ? View.VISIBLE : View.GONE);
        llSwitch.setGravity(item.isIs_on() ? Gravity.RIGHT|Gravity.CENTER_VERTICAL : Gravity.LEFT|Gravity.CENTER_VERTICAL);
        llSwitch.setBackgroundResource(item.isIs_on() ? R.drawable.shape_2da3f6_c30 : R.drawable.shape_cfd6e0_c30);

        // 自动执行
        if (type == 1) {
            if (isSorting) {
                llSwitch.setVisibility(View.GONE);
                ivSwitch.setVisibility(View.GONE);
            } else {
                if (item.isControl_permission() && connected) {  // 有权限且已连接sa
                    llSwitch.setVisibility(View.VISIBLE);
                    ivSwitch.setVisibility(View.GONE);
                } else {
                    llSwitch.setVisibility(View.GONE);
                    ivSwitch.setEnabled(connected);
                    ivSwitch.setVisibility(View.VISIBLE);
                    ivSwitch.setImageResource(item.isIs_on() ? R.drawable.icon_on_disable : R.drawable.icon_off_disable);
                }
            }
        }
        ivSort.setVisibility(isSorting ? View.VISIBLE : View.GONE);
        sceneItemAdapter.setNewData(item.getItems());
    }

    public List<Integer> getIdList() {
        List<Integer> idList = new ArrayList<>();
        for (SceneBean sceneBean : getData()) {
            idList.add(sceneBean.getId());
        }
        return idList;
    }
}
