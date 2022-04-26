package com.yctc.zhiting.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.UserBean;
import com.yctc.zhiting.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加成员列表
 */
public class AddMemberAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {

    public AddMemberAdapter() {
        super(R.layout.item_add_member);
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

        helper.setText(R.id.tvName, item.getNickname())
                .setText(R.id.tvRole, stringBuffer.toString());
    }

    /**
     * 已选择的数据
     * @return
     */
    public List<UserBean> getSelectedData(){
        List<UserBean> selectedData = new ArrayList<>();
        for (UserBean userBean : getData()){
            if (userBean.isSelected()){
                selectedData.add(userBean);
            }
        }
        return selectedData;
    }
}
