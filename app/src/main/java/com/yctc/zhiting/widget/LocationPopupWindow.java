package com.yctc.zhiting.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.adapter.PopupLocationAdapter;
import com.yctc.zhiting.entity.mine.LocationBean;

import java.util.List;

public class LocationPopupWindow extends PopupWindow {

    private RecyclerView rvLocation;
    private List<LocationBean> data;
    private PopupLocationAdapter popupLocationAdapter;

    public LocationPopupWindow(Context context, List<LocationBean> data) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) UiUtil.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.popup_location, null,false);
        rvLocation = mView.findViewById(R.id.rvLocation);
        rvLocation.setLayoutManager(new LinearLayoutManager(context));
        popupLocationAdapter = new PopupLocationAdapter();
        rvLocation.setAdapter(popupLocationAdapter);
        popupLocationAdapter.setNewData(data);
        popupLocationAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LocationBean locationBean= popupLocationAdapter.getItem(position);
                for (LocationBean l : popupLocationAdapter.getData()){
                    l.setCheck(false);
                }
                locationBean.setCheck(true);
                popupLocationAdapter.notifyDataSetChanged();
                if (locationSelectListener!=null){
                    locationSelectListener.onLocationSelect(locationBean.getId(), locationBean.getName());
                }
            }
        });

        setContentView(mView);
        setWidth(UiUtil.getDimens(R.dimen.dp_200));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        ColorDrawable dw = new ColorDrawable(UiUtil.getColor(R.color.color_00000000));
        setBackgroundDrawable(dw);
        setFocusable(true);
        setOutsideTouchable(true);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void show(View view){
        showAtLocation(view, Gravity.TOP|Gravity.RIGHT,UiUtil.dip2px(10),UiUtil.dip2px(60));
    }

    private OnLocationSelectListener locationSelectListener;

    public OnLocationSelectListener getLocationSelectListener() {
        return locationSelectListener;
    }

    public void setLocationSelectListener(OnLocationSelectListener locationSelectListener) {
        this.locationSelectListener = locationSelectListener;
    }

    public interface OnLocationSelectListener{
        void onLocationSelect(int id, String name);
    }
}
