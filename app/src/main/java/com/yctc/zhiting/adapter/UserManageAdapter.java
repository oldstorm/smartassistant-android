package com.yctc.zhiting.adapter;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.DLUserDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class UserManageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public UserManageAdapter() {
        super(R.layout.item_user_manage);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        RecyclerView rvUser = helper.getView(R.id.rvUser);
        rvUser.setLayoutManager(new LinearLayoutManager(mContext));
        UserManageSonAdapter userManageSonAdapter = new UserManageSonAdapter();
        rvUser.setAdapter(userManageSonAdapter);
        List<String> data = new ArrayList<>();
        for (int i=0; i<4; i++) {
            data.add("");
        }
        userManageSonAdapter.setNewData(data);
        userManageSonAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(mContext, DLUserDetailActivity.class);
                mContext.startActivity(intent);
            }
        });
    }
}
