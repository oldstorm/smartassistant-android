package com.yctc.zhiting.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.main.framework.baseutil.UiUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.UserBean;
import com.yctc.zhiting.utils.CollectionUtil;

/**
 * 选择部门主管时的成员
 */
public class DMMemberAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {

    public DMMemberAdapter() {
        super(R.layout.item_dm_member);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBean item) {
        StringBuffer stringBuffer = new StringBuffer();
        if(CollectionUtil.isNotEmpty(item.getRole_infos())) {
            for (int i = 0; i < item.getRole_infos().size(); i++) {
                UserBean.RoleInfosBean roleInfosBean = item.getRole_infos().get(i);
                stringBuffer.append(roleInfosBean.getName());
                if (i < item.getRole_infos().size() - 1) {
                    stringBuffer.append("、");
                }
            }
        }
        ImageView ivSelected = helper.getView(R.id.ivSelected);
        ivSelected.setSelected(item.isSelected());
        View viewLine = helper.getView(R.id.viewLine);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(0, UiUtil.dip2px(0.5f));
        layoutParams.topToBottom = R.id.cvAvatar;
        if (helper.getAdapterPosition() == getData().size()-1){
            layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.setMargins(0, UiUtil.dip2px(14), 0, 0);
        }else {
            layoutParams.leftToRight = R.id.cvAvatar;
            layoutParams.setMargins(UiUtil.dip2px(15), UiUtil.dip2px(14), 0, 0);
        }

        viewLine.setLayoutParams(layoutParams);
        helper.setText(R.id.tvName, item.getNickname())
                .setText(R.id.tvRole, stringBuffer.toString());
    }

    public UserBean getSelectedUser(){
        for (UserBean userBean : getData()){
            if (userBean.isSelected()){
                return userBean;
            }
        }
        return null;
    }
}
