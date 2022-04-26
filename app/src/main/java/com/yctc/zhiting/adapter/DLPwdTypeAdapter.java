package com.yctc.zhiting.adapter;

import android.util.SparseArray;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;

import java.util.ArrayList;
import java.util.List;

public class DLPwdTypeAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private boolean mIsEdit;
    private SparseArray<DLPwdAdapter> mAdapterSA = new SparseArray<>();

    public DLPwdTypeAdapter() {
        super(R.layout.item_dl_pwd_type);
    }

    public void setIsEdit(boolean isEdit) {
        this.mIsEdit = isEdit;
        if (mAdapterSA!=null && mAdapterSA.size()>0) {
            for (int i=0; i<mAdapterSA.size(); i++) {
                int key = mAdapterSA.keyAt(i);
                DLPwdAdapter dlPwdAdapter = mAdapterSA.get(key);
                dlPwdAdapter.setIsEdit(mIsEdit);
            }
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.addOnClickListener(R.id.ivAdd);
        RecyclerView rvPwd = helper.getView(R.id.rvPwd);
        rvPwd.setLayoutManager(new LinearLayoutManager(mContext));
        DLPwdAdapter dlPwdAdapter = new DLPwdAdapter();
        mAdapterSA.put(helper.getAdapterPosition(), dlPwdAdapter);
        rvPwd.setAdapter(dlPwdAdapter);
        List<String> data = new ArrayList<>();
        data.add("");
        data.add("");
        dlPwdAdapter.setNewData(data);
        dlPwdAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int viewId = view.getId();
                switch (viewId) {
                    case R.id.tvRemove:
                        if (sonAdapterItemChildListener != null) {
                            sonAdapterItemChildListener.onRemove();
                        }
                        break;

                    case R.id.tvEdit:
                        if (sonAdapterItemChildListener != null) {
                            sonAdapterItemChildListener.onEdit();
                        }
                        break;
                }
            }
        });
    }

    private OnSonAdapterItemChildListener sonAdapterItemChildListener;

    public void setSonAdapterItemChildListener(OnSonAdapterItemChildListener sonAdapterItemChildListener) {
        this.sonAdapterItemChildListener = sonAdapterItemChildListener;
    }

    public interface OnSonAdapterItemChildListener {
        void onRemove();
        void onEdit();
    }
}
