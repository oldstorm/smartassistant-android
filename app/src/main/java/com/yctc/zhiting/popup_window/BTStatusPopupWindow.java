package com.yctc.zhiting.popup_window;

import android.view.View;
import android.widget.TextView;

import com.app.main.framework.baseview.BaseActivity;
import com.app.main.framework.popupwindow.BasePopupWindow;
import com.yctc.zhiting.R;

/**
 * 蓝牙状态说明
 */
public class BTStatusPopupWindow extends BasePopupWindow {

    private TextView tvStatus;

    public BTStatusPopupWindow(BaseActivity context) {
        super(context);
        tvStatus = view.findViewById(R.id.tvStatus);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.popup_bt_status;
    }

    @Override
    public void onClick(View view) {

    }

    public void setStatusText(String status) {
        tvStatus.setText(status);
    }
}
