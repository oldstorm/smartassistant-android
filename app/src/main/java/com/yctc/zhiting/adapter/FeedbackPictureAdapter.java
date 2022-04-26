package com.yctc.zhiting.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.FeedbackPictureBean;

import java.util.List;

public class FeedbackPictureAdapter extends BaseMultiItemQuickAdapter<FeedbackPictureBean, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public FeedbackPictureAdapter(List<FeedbackPictureBean> data) {
        super(data);
        addItemType(FeedbackPictureBean.PICTURE, R.layout.item_feedback_picture);
        addItemType(FeedbackPictureBean.ADD, R.layout.item_feedback_add_picture);
    }

    @Override
    protected void convert(BaseViewHolder helper, FeedbackPictureBean item) {
        switch (item.getItemType()) {
            case FeedbackPictureBean.PICTURE:
                ConstraintLayout clPictureParent = helper.getView(R.id.clPictureParent);
                clPictureParent.post(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams clParams = (ViewGroup.LayoutParams) clPictureParent.getLayoutParams();
                        clParams.width = (int) (UiUtil.getScreenWidth()*0.13);
                        clParams.height = (int) (UiUtil.getScreenWidth()*0.13);
                        clPictureParent.setLayoutParams(clParams);
                    }
                });
                ImageView ivPic = helper.getView(R.id.ivPic);
                GlideUtil.load(item.getUrl()).into(ivPic);
                helper.addOnClickListener(R.id.ivClose);
                break;

            case FeedbackPictureBean.ADD:
                LinearLayout llAddPicParent = helper.getView(R.id.llAddPicParent);
                llAddPicParent.post(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams llParams = (ViewGroup.LayoutParams) llAddPicParent.getLayoutParams();
                        llParams.width = (int) (UiUtil.getScreenWidth()*0.13);
                        llParams.height = (int) (UiUtil.getScreenWidth()*0.13);
                        llAddPicParent.setLayoutParams(llParams);
                    }
                });
                break;
        }
    }
}
