package com.yctc.zhiting.adapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;

import java.util.ArrayList;
import java.util.List;

/**
 *  门锁日志适配器
 */
public class DLLogParentAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public DLLogParentAdapter() {
        super(R.layout.item_dl_log_parent);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        RecyclerView rvLog = helper.getView(R.id.rvLog);
        rvLog.setLayoutManager(new LinearLayoutManager(mContext));
        DLLogAdapter dlLogAdapter = new DLLogAdapter(true);
        rvLog.setAdapter(dlLogAdapter);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            data.add("");
        }
        dlLogAdapter.setNewData(data);
    }
}
