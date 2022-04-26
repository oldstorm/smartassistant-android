package com.yctc.zhiting.activity;


import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.main.framework.baseutil.NetworkUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.espressif.provisioning.DeviceConnectionEvent;
import com.espressif.provisioning.ESPConstants;
import com.espressif.provisioning.ESPProvisionManager;
import com.espressif.provisioning.listeners.ProvisionListener;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.ApNetworkContract;
import com.yctc.zhiting.activity.presenter.ApNetworkPresenter;
import com.yctc.zhiting.utils.StringUtil;
import com.yctc.zhiting.utils.confignetwork.ConfigNetworkCallback;
import com.yctc.zhiting.utils.confignetwork.softap.EspressifConfigNetworkUtil;
import com.yctc.zhiting.utils.udp.ByteUtil;
import com.yctc.zhiting.utils.udp.UDPSocket;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * AP配网
 */
public class ApNetworkActivity extends MVPBaseActivity<ApNetworkContract.View, ApNetworkPresenter> implements ApNetworkContract.View {

    @BindView(R.id.etWifi)
    EditText etWifi;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvOther)
    TextView tvOther;
    @BindView(R.id.tvNext)
    TextView tvNext;
    @BindView(R.id.ivCover)
    ImageView ivCover;
    @BindView(R.id.ivVisible)
    ImageView ivVisible;
    @BindView(R.id.llNext)
    LinearLayout llNext;
    @BindView(R.id.rlParent)
    RelativeLayout rlParent;
    @BindView(R.id.rbNext)
    ProgressBar rbNext;

    private boolean showPwd;

    DatagramSocket socket = null;
    InetAddress serverAddress = null;
    private static final int SEND_BUFF_LEN = 512;

    //屏幕高度
    private int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;
    private UDPSocket udpSocket;
    private String ipAddress = "192.168.4.1";
    private int portNum = 54322;

    private ESPProvisionManager provisionManager;

    private EspressifConfigNetworkUtil espressifConfigNetworkUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ap_network;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.home_connect_router));
        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight / 3;
        setKeyBoard();
        provisionManager = ESPProvisionManager.getInstance(getApplicationContext());
        espressifConfigNetworkUtil = new EspressifConfigNetworkUtil(getApplicationContext());
        udpSocket = new UDPSocket(ipAddress, portNum, new UDPSocket.OnReceiveCallback(){

            @Override
            public void onReceiveByteData(String address, int port, byte[] data, int length) {

            }

            @Override
            public void onReceive(String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(msg);
                    }
                });
            }
        });
//        udpSocket.startUDPSocket();
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        espressifConfigNetworkUtil.disconnectDevice();
        if (provisionManager.getEspDevice() != null) {
            provisionManager.getEspDevice().disconnectDevice();
        }
        if (udpSocket!=null){
            udpSocket.stopUDPSocket();
        }
    }

    private void setKeyBoard(){
        rlParent.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {//认为软键盘将Activity向上推的高度超过了屏幕高度的1/3，就是软键盘弹起了，这个时候隐藏底部的提交按钮
                    //延迟100ms设置不可见性是避免view还没计算好自己的宽高，设置可见不可见性失效。
                    llNext.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            llNext.setVisibility(View.GONE);
                            tvOther.setVisibility(View.GONE);
                        }
                    }, 100);

                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {//认为软键盘将Activity向下推的高度超过了屏幕高度的1/3，就是软键盘隐藏了，这个时候显示底部的提交按钮

                    llNext.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            llNext.setVisibility(View.VISIBLE);
                            tvOther.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                }
            }
        });
    }


    @OnTextChanged(R.id.etPassword)
    void onChanged(){
        ivVisible.setVisibility(TextUtils.isEmpty(etPassword.getText().toString().trim()) ? View.GONE : View.VISIBLE);
    }

    private void sendMsg(){
        String wifiName = etWifi.getText().toString();
        String wifiPwd = etPassword.getText().toString();
        String formatStr = UiUtil.getString(R.string.distribution_network);
        String stringBody = String.format(formatStr, wifiName, wifiPwd);
        byte[] byteBody = stringBody.getBytes();
        byte[] bodyLength = ByteUtil.intToByte2(byteBody.length);
        byte[] data = ByteUtil.byteMergerAll(ByteUtil.intToByte2(0x55ee),bodyLength, byteBody);
        byte[] crcByte = ByteUtil.intToByte2(ByteUtil.alex_crc16(data, data.length)) ;
        byte[] sendData = ByteUtil.byteMergerAll( data , crcByte);
        udpSocket.sendMessage(sendData, ipAddress);
    }

    /**
     * softAP置网
     */
    private void softAp(){
        if (NetworkUtil.getNetworkerStatus() != 1){
            ToastUtil.show(getResources().getString(R.string.home_please_connect_wifi_of_device));
            return;
        }
        accessFineLocationTask();  // 是否有定位权限
    }


    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d("TAG", "onPermissionsDenied:" + requestCode + ":" + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    /**
     * 有定位权限
     */
    @SuppressLint("MissingPermission")
    @Override
    protected void hasPermissionTodo() {
        super.hasPermissionTodo();
//        provisionManager.createESPDevice(ESPConstants.TransportType.TRANSPORT_SOFTAP, ESPConstants.SecurityType.SECURITY_1); // 创建设备
//        provisionManager.getEspDevice().connectWiFiDevice(); // 连接设备
        espressifConfigNetworkUtil.createDevice("abcd1234");
        espressifConfigNetworkUtil.connectDevice();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceConnectionEvent event) {

        Log.d(TAG, "On Device Prov Event RECEIVED : " + event.getEventType());

        switch (event.getEventType()) {

            case ESPConstants.EVENT_DEVICE_CONNECTED:
                toConfigNetwork();
                break;

            case ESPConstants.EVENT_DEVICE_CONNECTION_FAILED:
                ToastUtil.show(getResources().getString(R.string.error_device_connect_failed));
                setNextEnabled(true);
                break;
        }
    }

    /**
     * 配网操作
     */
    private void toConfigNetwork(){
        String ssidValue = etWifi.getText().toString();
        String passphraseValue = etPassword.getText().toString();
        String pop = getResources().getString(R.string.proof_of_possesion);
        espressifConfigNetworkUtil.configNetwork(ssidValue, passphraseValue, new ConfigNetworkCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show("配网成功");
                        setNextEnabled(true);
                    }
                });
            }

            @Override
            public void onFailed(int errorCode, Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (errorCode){
                            case ConfigNetworkCallback.CREATE_SESSION_FAILED:
                                ToastUtil.show(getResources().getString(R.string.error_session_creation));
                                break;
                            case ConfigNetworkCallback.WIFI_CONFIG_FAILED:
                                ToastUtil.show(getResources().getString(R.string.error_prov_step_1));
                                break;

                            case ConfigNetworkCallback.WIFI_CONFIG_APPLIED_FAILED:
                                ToastUtil.show(getResources().getString(R.string.error_prov_step_2));

                                break;

                            case ConfigNetworkCallback.ERROR_AUTHENTICATION_FAILED:
                                ToastUtil.show(getResources().getString(R.string.error_authentication_failed));
                                break;

                            case ConfigNetworkCallback.ERROR_NETWORK_NOT_FOUND:
                                ToastUtil.show(getResources().getString(R.string.error_network_not_found));
                                break;

                            case ConfigNetworkCallback.TO_PROV_DEVICE_FAILED:
                                ToastUtil.show(getResources().getString(R.string.error_prov_step_3));
                                break;
                        }
                        setNextEnabled(true);
                    }
                });
            }
        });
//        provisionManager.getEspDevice().setProofOfPossession(pop);
//        provisionManager.getEspDevice().provision(ssidValue, passphraseValue, new ProvisionListener() {
//
//            @Override
//            public void createSessionFailed(Exception e) {
//
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        ToastUtil.show(getResources().getString(R.string.error_session_creation));
//                        setNextEnabled(true);
//                    }
//                });
//            }
//
//            @Override
//            public void wifiConfigSent() {
//
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                    }
//                });
//            }
//
//            @Override
//            public void wifiConfigFailed(Exception e) {
//
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        ToastUtil.show(getResources().getString(R.string.error_prov_step_1));
//                        setNextEnabled(true);
//                    }
//                });
//            }
//
//            @Override
//            public void wifiConfigApplied() {
//
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                    }
//                });
//            }
//
//            @Override
//            public void wifiConfigApplyFailed(Exception e) {
//
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        ToastUtil.show(getResources().getString(R.string.error_prov_step_2));
//                        setNextEnabled(true);
//                    }
//                });
//            }
//
//            @Override
//            public void provisioningFailedFromDevice(final ESPConstants.ProvisionFailureReason failureReason) {
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        switch (failureReason) {
//                            case AUTH_FAILED:
//                                ToastUtil.show(getResources().getString(R.string.error_authentication_failed));
//                                break;
//                            case NETWORK_NOT_FOUND:
//                                ToastUtil.show(getResources().getString(R.string.error_network_not_found));
//                                break;
//                            case DEVICE_DISCONNECTED:
//                            case UNKNOWN:
//                                ToastUtil.show(getResources().getString(R.string.error_prov_step_3));
//                                break;
//                        }
//                        setNextEnabled(true);
//                    }
//                });
//            }
//
//            @Override
//            public void deviceProvisioningSuccess() {
//
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        ToastUtil.show("配网成功");
//                    }
//                });
//            }
//
//            @Override
//            public void onProvisioningFailed(Exception e) {
//
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        ToastUtil.show(getResources().getString(R.string.error_prov_step_3));
//                        setNextEnabled(true);
//                    }
//                });
//            }
//        });
    }


    /**
     * 设置下一步的状态
     * @param enabled
     */
    private void setNextEnabled(boolean enabled){
        llNext.setEnabled(enabled);
        llNext.setAlpha(enabled ? 1f : 0.5f);
        rbNext.setVisibility(enabled ? View.GONE : View.VISIBLE);
        tvNext.setVisibility(enabled ? View.VISIBLE : View.GONE);
        etWifi.setEnabled(enabled);
        etPassword.setEnabled(enabled);
    }


    @OnClick({R.id.ivVisible, R.id.llNext, R.id.tvOther})
    void onClick(View view){
        switch (view.getId()) {
            case R.id.ivVisible:  // 密码是否可见
                showPwd = !showPwd;
                ivVisible.setImageResource(showPwd ? R.drawable.icon_password_visible : R.drawable.icon_password_invisible);
                if (showPwd){
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                etPassword.setSelection(etPassword.getText().length());
                break;
            case R.id.llNext:  // 下一步
//                sendMsg();
                if (TextUtils.isEmpty(etWifi.getText().toString().trim())){
                    ToastUtil.show("请输入wifi名称");
                    return;
                }
                if (TextUtils.isEmpty(etPassword.getText().toString().trim())){
                    ToastUtil.show("请输入wifi密码");
                    return;
                }
                setNextEnabled(false);
                softAp();
                break;

            case R.id.tvOther:  // 连接其他wifi

                break;
        }
    }
}