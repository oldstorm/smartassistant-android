package com.yctc.zhiting.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.imageutil.GlideUtil;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.mine.FeedbackDetailBean;

/**
 * 反馈详情图片
 */
public class FeedbackDetailPicAdapter extends BaseQuickAdapter<FeedbackDetailBean.FilesBean, BaseViewHolder> {

    public FeedbackDetailPicAdapter() {
        super(R.layout.item_feedback_detail_pic);
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedbackDetailBean.FilesBean item) {
        ConstraintLayout clParent = helper.getView(R.id.clParent);
        clParent.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams clParams = (ViewGroup.LayoutParams) clParent.getLayoutParams();
                clParams.width = (int) (UiUtil.getScreenWidth() * 0.14);
                clParams.height = (int) (UiUtil.getScreenWidth() * 0.14);
                clParent.setLayoutParams(clParams);
            }
        });

        String fileUrl = item.getFile_url();
        if (fileUrl.contains("?")) {
            String[] videoUrlArr = fileUrl.split("\\?");
            fileUrl = videoUrlArr[0];
        }
        item.setFile_url(fileUrl);
        ImageView ivPic = helper.getView(R.id.ivPic);
        GlideUtil.loadRound(fileUrl, UiUtil.dip2px(4)).into(ivPic);

        ImageView ivPlay = helper.getView(R.id.ivPlay);
        ivPlay.setVisibility(item.getFile_type().equals(Constant.VIDEO) ? View.VISIBLE : View.GONE);
    }
}
