package com.app.main.framework.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.app.main.framework.R;
import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.UiUtil;

import java.util.List;

public class SelectPopupWindow extends PopupWindow {
    private Button btn_cancel;
    private View mMenuView;
    private ListView lvPopupMenuList;
    private RelativeLayout llPopupDialog;

    public SelectPopupWindow setDatas(List<String> datas) {
        if (lvPopupMenuList != null){
            lvPopupMenuList.setAdapter(new ArrayAdapter<>(UiUtil.getContext(), R.layout.item_popupdialog, R.id.tvPopupDialogItemName, datas));
        }
        return this;
    }

    public SelectPopupWindow(){
        this(null);
    }

    @SuppressLint("ClickableViewAccessibility")
    public SelectPopupWindow(List<String> datas) {
        super(UiUtil.getContext());
        LayoutInflater inflater = (LayoutInflater) UiUtil.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.dialog_popupmenu, null,false);
        lvPopupMenuList = mMenuView.findViewById(R.id.lvPopupMenuList);
        llPopupDialog = mMenuView.findViewById(R.id.llPopupDialog);
        if (datas != null)
            setDatas(datas);
        btn_cancel = mMenuView.findViewById(R.id.btn_cancel);
        //取消按钮
        btn_cancel.setOnClickListener(v -> {
            //销毁弹出框
            dismiss();
        });
        llPopupDialog.setOnTouchListener((v, event) -> {
            int height = lvPopupMenuList.getTop();
            int y=(int) event.getY();
            if(event.getAction()==MotionEvent.ACTION_UP){
                if(y<height){
                    dismiss();
                }
            }
            return true;
        });
        setContentView(mMenuView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(UiUtil.getColor(R.color.popup_dialog_out_background));
        setBackgroundDrawable(dw);
        setOutsideTouchable(true);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public SelectPopupWindow setOnItemClicklistener(AdapterView.OnItemClickListener onItemClicklistener) {
        if (lvPopupMenuList != null)
            lvPopupMenuList.setOnItemClickListener(onItemClicklistener);
        return this;
    }

    public void show(){
        Activity currentActivity = LibLoader.getCurrentActivity();
        showAtLocation(currentActivity.getWindow().getDecorView(), Gravity.BOTTOM,0,0);
    }
}
