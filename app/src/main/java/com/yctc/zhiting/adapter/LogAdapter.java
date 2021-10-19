package com.yctc.zhiting.adapter;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.scene.LogBean;

import java.util.List;

public class LogAdapter extends BaseQuickAdapter<LogBean, BaseViewHolder> {

    public LogAdapter() {
        super(R.layout.item_log);
    }

    @Override
    protected void convert(BaseViewHolder helper, LogBean item) {
        String month = "";
        String[] date = item.getDate().split("-");
        if (date.length>1){
            month = date[1];
        }
        helper.setText(R.id.tvMonth, month);


        /************************** 展示列表数据 ***********************************/
        RecyclerView rvData = helper.getView(R.id.rvData);
        rvData.setLayoutManager(new LinearLayoutManager(mContext));
        LogSonAdapter logSonAdapter = new LogSonAdapter();
        rvData.setAdapter(logSonAdapter);
        logSonAdapter.setNewData(item.getItems());
    }
}
