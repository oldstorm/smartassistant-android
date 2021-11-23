package com.yctc.zhiting.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanSettings;
import android.util.Log;

import com.app.main.framework.baseutil.LogUtil;

public class BluetoothUtil {

//    public static final String BLUFI_PREFIX = "BLUFI";
    public static final String BLUFI_PREFIX = "ZT";

    public static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    /**
     * 是否支持蓝牙
     * @return
     */
    public static boolean hasBlueTooth(){
        return bluetoothAdapter!=null;
    }

    /**
     * 蓝牙是否打开
     * @return
     */
    public static boolean isEnabled(){
        return bluetoothAdapter!=null && bluetoothAdapter.isEnabled();
    }

    /**
     * 扫描蓝牙设备
     * @param scanCallback
     */
    public static void startScanBluetooth(ScanCallback scanCallback){
        if (isEnabled()) {
            BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
            if (scanner == null) {
                LogUtil.e("蓝牙不可用");
                return;
            }
            LogUtil.e("开始扫描");
            scanner.startScan(null,
                    new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build(),
                    scanCallback);
        }
    }

    /**
     * 停止扫描蓝牙设备
     * @param scanCallback
     */
    public static void stopScanBluetooth(ScanCallback scanCallback){
        if (isEnabled()) {
            BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
            if (scanner != null) {
                scanner.stopScan(scanCallback);
            }
            LogUtil.e("停止扫描");
        }
    }
}
