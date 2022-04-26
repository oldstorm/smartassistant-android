package com.yctc.zhiting.adapter;


import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.UserBean;
import com.yctc.zhiting.utils.CollectionUtil;

import java.util.List;

/**
 * 家庭/公司详情
 */
public class MemberAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {

    public MemberAdapter() {
        super(R.layout.item_member);
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
        ImageView ciAvatar = helper.getView(R.id.ciAvatar);
        GlideUtil.load(item.getAvatar_url()).userHead().into(ciAvatar);
        ImageView ivMedal = helper.getView(R.id.ivMedal);
        ivMedal.setVisibility(item.isIs_manager() ? View.VISIBLE : View.GONE);
        helper.setText(R.id.tvName, item.getNickname())
                .setText(R.id.tvRole, stringBuffer.toString());
    }
}
