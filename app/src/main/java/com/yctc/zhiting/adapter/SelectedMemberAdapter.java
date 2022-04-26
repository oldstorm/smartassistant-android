package com.yctc.zhiting.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.UserBean;

/**
 * 添加成员时显示已选择的成员
 */
public class SelectedMemberAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {

    public SelectedMemberAdapter() {
        super(R.layout.item_selected_member);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBean item) {
        helper.addOnClickListener(R.id.ivClose);
        helper.setText(R.id.tvName, item.getNickname());
    }
}
