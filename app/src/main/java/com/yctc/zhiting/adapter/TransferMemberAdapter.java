package com.yctc.zhiting.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.UserBean;
import com.yctc.zhiting.utils.CollectionUtil;

public class TransferMemberAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {

    public TransferMemberAdapter() {
        super(R.layout.item_transfer_member);
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
        helper.setText(R.id.tvName, item.getNickname())
                .setText(R.id.tvRole, stringBuffer.toString());
        ImageView ivArrow = helper.getView(R.id.ivArrow);
        ivArrow.setSelected(item.isSelected());
    }
}
