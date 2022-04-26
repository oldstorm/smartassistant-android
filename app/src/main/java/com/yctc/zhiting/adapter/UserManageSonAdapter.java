package com.yctc.zhiting.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

public class UserManageSonAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public UserManageSonAdapter() {
        super(R.layout.item_user_manage_son);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        TagFlowLayout tfl = helper.getView(R.id.tfl);
        String[] data = new String[] {"指纹1", "密码2", "NFC3"};
        final LayoutInflater mInflater = LayoutInflater.from(mContext);
        tfl.setAdapter(new TagAdapter<String>(data) {

            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.layout_tag,
                        tfl, false);
                tv.setText(s);
                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_gray_finger_print, 0, 0, 0);
                return tv;
            }
        });
    }
}
