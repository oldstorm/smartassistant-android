package com.yctc.zhiting.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.google.gson.reflect.TypeToken;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SetHomekitContract;
import com.yctc.zhiting.activity.presenter.SetHomekitPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.WSBaseResponseBean;
import com.yctc.zhiting.entity.home.DeviceBean;
import com.yctc.zhiting.entity.home.SocketDeviceInfoBean;
import com.yctc.zhiting.event.DeviceRefreshEvent;
import com.yctc.zhiting.event.FinishSetHomekit;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.websocket.IWebSocketListener;
import com.yctc.zhiting.websocket.WSocketManager;
import com.yctc.zhiting.widget.CustomEditTextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import okhttp3.WebSocket;

/**
 * 设置homekit
 */
public class SetHomeKitActivity extends MVPBaseActivity<SetHomekitContract.View, SetHomekitPresenter> implements SetHomekitContract.View {

    @BindView(R.id.cetv)
    CustomEditTextView cetv;
    @BindView(R.id.tvError)
    TextView tvError;

    private long homeId;
    private int mDeviceId;//设备id
    private String mDeviceName;//设备名称
    private DeviceBean mDeviceBean;//设备对象
    private int instance_id;

    private IWebSocketListener mIWebSocketListener;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_home_kit;
    }


    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.home_homekit_set_code));
//        initWebSocket();

    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        homeId = intent.getLongExtra(IntentConstant.ID, -1);
        mDeviceId = intent.getIntExtra(IntentConstant.ID, 0);
        mDeviceName = intent.getStringExtra(IntentConstant.NAME);
        mDeviceBean = (DeviceBean) intent.getSerializableExtra(IntentConstant.BEAN);
        instance_id = intent.getIntExtra(IntentConstant.INSTANCE_ID, 1);

        cetv.setInputCompleteListener(new CustomEditTextView.OnInputCompleteListener() {
            @Override
            public void onTextChange(View view, String text) {
                LogUtil.e("输入中：" + text);
            }

            @Override
            public void onComplete(View view, String text) {
//                String formatStr = UiUtil.getString(R.string.device_set_attr);
//                String deviceJson = String.format(formatStr, text, instance_id, Constant.PIN, 1, mDeviceBean.getIdentity(), mDeviceBean.getPluginId());
//                UiUtil.starThread(() -> WSocketManager.getInstance().sendMessage(deviceJson));
                Bundle bundle = new Bundle();
                bundle.putLong(IntentConstant.ID, homeId);
                bundle.putString(IntentConstant.HOMEKIT_CODE, text);
                bundle.putSerializable(IntentConstant.BEAN, mDeviceBean);
                switchToActivityForResult(DeviceConnectActivity.class, bundle, 100);
//                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FinishSetHomekit event) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100){
            cetv.clearText();
            if (data!=null) {
                String error = data.getStringExtra(IntentConstant.HOMEKIT_CODE_ERROR);
                tvError.setText(error);
                tvError.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * WebSocket初始化、添加监听
     */
    private void initWebSocket() {
        mIWebSocketListener = new IWebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                WSBaseResponseBean<Object> data = GsonConverter.getGson().fromJson(text, new TypeToken<WSBaseResponseBean<Object>>(){}.getType());
                if (data.isSuccess()){
//                    Bundle bundle = new Bundle();
//                    bundle.putInt(IntentConstant.ID, mDeviceId);
//                    bundle.putString(IntentConstant.NAME, mDeviceName);
//                    switchToActivity(SetDevicePositionActivity.class, bundle);
                    Bundle bundle = new Bundle();
                    bundle.putLong(IntentConstant.ID, homeId);
                    bundle.putSerializable(IntentConstant.BEAN, mDeviceBean);
                    switchToActivity(DeviceConnectActivity.class, bundle);
                    finish();
                }else {
                   tvError.setText(getResources().getString(R.string.home_wrong_code));
                }
            }
        };
        WSocketManager.getInstance().addWebSocketListener(mIWebSocketListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WSocketManager.getInstance().removeWebSocketListener(mIWebSocketListener);
    }
}