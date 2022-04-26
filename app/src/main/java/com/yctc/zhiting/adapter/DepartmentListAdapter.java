package com.yctc.zhiting.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.main.framework.baseutil.UiUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.LocationBean;

/**
 * 部门列表
 */
public class DepartmentListAdapter extends BaseQuickAdapter<LocationBean, BaseViewHolder> {

    private boolean isEdit;

    public DepartmentListAdapter() {
        super(R.layout.item_department_list);
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, LocationBean item) {
        ConstraintLayout clParent = helper.getView(R.id.clParent);
        clParent.setBackgroundColor(UiUtil.getColor(R.color.white));
        ImageView imageView = helper.getView(R.id.ivIndicator);
        imageView.setImageResource(isEdit ? R.drawable.icon_mine_edit : R.drawable.icon_gray_right_arrow);
        TextView tvNum = helper.getView(R.id.tvNum);
        int count = item.getUser_count();
        tvNum.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        tvNum.setText(isEdit ? "" : String.format(UiUtil.getString(R.string.mine_people), count));
        helper.setText(R.id.tvName, item.getName());
    }
}
