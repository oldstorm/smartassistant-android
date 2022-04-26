package com.yctc.zhiting.popup_window;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.BaseActivity;
import com.app.main.framework.popupwindow.BasePopupWindow;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.adapter.AreaCodeAdapter;
import com.yctc.zhiting.entity.AreaCodeBean;
import com.yctc.zhiting.utils.CollectionUtil;

import java.util.List;

public class AreaCodePopupWindow extends BasePopupWindow {

    private RecyclerView rvAreaCode;
    private AreaCodeAdapter mAreaCodeAdapter;

    public AreaCodePopupWindow(BaseActivity context) {
        this(context, null);
    }

    public AreaCodePopupWindow(BaseActivity context, List<AreaCodeBean> areaCodeData) {
        super(context);
        rvAreaCode = view.findViewById(R.id.rvAreaCode);
        rvAreaCode.setLayoutManager(new LinearLayoutManager(context));
        mAreaCodeAdapter = new AreaCodeAdapter();
        rvAreaCode.setAdapter(mAreaCodeAdapter);
        mAreaCodeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AreaCodeBean areaCodeBean = mAreaCodeAdapter.getItem(position);
                if (!areaCodeBean.getCode().equals("86")){
                    ToastUtil.show(UiUtil.getString(R.string.mine_area_not_support));
                    return;
                }
                for (AreaCodeBean acb : mAreaCodeAdapter.getData()){
                    acb.setSelected(false);
                }
                areaCodeBean.setSelected(true);
                mAreaCodeAdapter.notifyDataSetChanged();
                if (selectedAreaCodeListener != null) {
                    selectedAreaCodeListener.selectedAreaCode(areaCodeBean);
                }
            }
        });
        mAreaCodeAdapter.setNewData(areaCodeData);
    }


    public void setAreaCodeData(List<AreaCodeBean> areaCodeData){
        if (CollectionUtil.isNotEmpty(areaCodeData)){
            mAreaCodeAdapter.setNewData(areaCodeData);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.popup_area_code;
    }

    @Override
    public void onClick(View v) {

    }

    private OnSelectedAreaCodeListener selectedAreaCodeListener;

    public void setSelectedAreaCodeListener(OnSelectedAreaCodeListener selectedAreaCodeListener) {
        this.selectedAreaCodeListener = selectedAreaCodeListener;
    }

    public interface OnSelectedAreaCodeListener {
        void selectedAreaCode(AreaCodeBean areaCodeBean);
    }
}
