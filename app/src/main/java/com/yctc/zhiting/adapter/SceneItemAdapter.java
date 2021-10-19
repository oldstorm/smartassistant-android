package com.yctc.zhiting.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.scene.SceneItemBean;

import java.util.List;

public class SceneItemAdapter extends BaseQuickAdapter<SceneItemBean, BaseViewHolder> {

    public SceneItemAdapter() {
        super(R.layout.layout_scene_device);
    }

    @Override
    protected void convert(BaseViewHolder helper, SceneItemBean sceneItemBean) {
        ImageView ivCover = helper.getView(R.id.ivCover);
        TextView tvStatus = helper.getView(R.id.tvStatus);
        /**
         * 执行任务类型;1为设备,2 3 4为场景
         */
        switch (sceneItemBean.getType()){
            case 1:
                ivCover.setScaleType(ImageView.ScaleType.FIT_CENTER);
                GlideUtil.load(sceneItemBean.getLogo_url()).into(ivCover);
                /**
                 * 设备状态:1为正常,2为已删除,3为离线
                 */
                if (sceneItemBean.getStatus() == 1){
                    tvStatus.setText(mContext.getResources().getString(R.string.scene_normal));
                    tvStatus.setVisibility(View.GONE);
                }else   if (sceneItemBean.getStatus() == 2){
                    tvStatus.setText(mContext.getResources().getString(R.string.scene_removed));
                    tvStatus.setVisibility(View.VISIBLE);
                }else   if (sceneItemBean.getStatus() == 3){
                    tvStatus.setText(mContext.getResources().getString(R.string.scene_offline));
                    tvStatus.setVisibility(View.VISIBLE);
                }
                ivCover.setPadding(UiUtil.getDimens(R.dimen.dp_4), UiUtil.getDimens(R.dimen.dp_4), UiUtil.getDimens(R.dimen.dp_4), UiUtil.getDimens(R.dimen.dp_4));
                ivCover.setBackgroundResource(R.drawable.shape_stroke_eeeeee_c4);

                break;

            case 2:
            case 3:
            case 4:
                ivCover.setScaleType(ImageView.ScaleType.FIT_XY);
                ivCover.setPadding(UiUtil.getDimens(R.dimen.dp_0), UiUtil.getDimens(R.dimen.dp_0), UiUtil.getDimens(R.dimen.dp_0), UiUtil.getDimens(R.dimen.dp_0));
                ivCover.setImageResource(R.drawable.icon_scene);
                ivCover.setBackgroundResource(R.drawable.shape_white);
                /**
                 * 场景状态:1为正常,2为已删除
                 */
                if (sceneItemBean.getStatus() == 1){
                    tvStatus.setText(mContext.getResources().getString(R.string.scene_normal));
                    tvStatus.setVisibility(View.GONE);
                }else   if (sceneItemBean.getStatus() == 2){
                    tvStatus.setText(mContext.getResources().getString(R.string.scene_removed));
                    tvStatus.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
