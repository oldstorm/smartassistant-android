package com.app.main.framework.baseview;

import android.Manifest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.main.framework.R;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class BasePermissionsAndStackActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    private boolean isInitFocus = false;  //记录是否已经初始化获得了焦点，PopupWindow必须在Activity获取焦点后才能显示。
    private boolean isDestroyed = false;    //记录activity是否已经被销毁了
    /*权限相关*/
    public final int BASE_PERMISSION_REQUEST = 100;
    public final int WRITE_STORAGE_PERMISSION_REQUEST = 200;
    public final int WRITE_ACCESS_FINE_LOCATION_PERMISSION_REQUEST = 300;
    public final int INSTALL_PACKAGES_PERMISSION_REQUEST = 400;
    protected String[] permissions = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    protected String[] STORAGE = {android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    protected String[] ACCESS_FINE_LOCATION = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
    protected String[] INSTALL_PACKAGES = {android.Manifest.permission.FOREGROUND_SERVICE, android.Manifest.permission.REQUEST_INSTALL_PACKAGES};

    public OnPermissionListener mPermissionListener;

    public interface OnPermissionListener {
        void onSuccess();
    }

    private boolean hasPermissions() {
        return EasyPermissions.hasPermissions(this, permissions);
    }

    private boolean hasWriteStoragePermission() {
        return EasyPermissions.hasPermissions(this, STORAGE);
    }

    private boolean hasInstallApkPermission() {
        return EasyPermissions.hasPermissions(this, INSTALL_PACKAGES);
    }

    private boolean hasAccessFineLocationPermission() {
        return EasyPermissions.hasPermissions(this, ACCESS_FINE_LOCATION);
    }

    @AfterPermissionGranted(BASE_PERMISSION_REQUEST)
    public void checkPermissionTask() {
        if (hasPermissions()) {
            // Have permissions, do the thing!
            hasPermissionTodo();
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    this,
                    getResources().getString(R.string.permission_apply),
                    BASE_PERMISSION_REQUEST,
                    permissions);
        }
    }

    @AfterPermissionGranted(WRITE_STORAGE_PERMISSION_REQUEST)
    public void writeStorageTask() {
        if (hasWriteStoragePermission()) {
            // Have permissions, do the thing!
            hasPermissionTodo();
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    this,
                    getResources().getString(R.string.storage_permission_apply),
                    WRITE_STORAGE_PERMISSION_REQUEST,
                    STORAGE);
        }
    }

    @AfterPermissionGranted(WRITE_STORAGE_PERMISSION_REQUEST)
    public void checkWriteStorageTask() {
        if (hasWriteStoragePermission()) {
            // Have permissions, do the thing!
            if (mPermissionListener != null)
                mPermissionListener.onSuccess();
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    this,
                    getResources().getString(R.string.storage_permission_apply),
                    WRITE_STORAGE_PERMISSION_REQUEST,
                    STORAGE);
        }
    }

    @AfterPermissionGranted(INSTALL_PACKAGES_PERMISSION_REQUEST)
    public void checkInstallApkTask() {
        if (hasInstallApkPermission()) {
            // Have permissions, do the thing!
            if (mPermissionListener != null)
                mPermissionListener.onSuccess();
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    this,
                    getResources().getString(R.string.install_apk_apply),
                    INSTALL_PACKAGES_PERMISSION_REQUEST,
                    INSTALL_PACKAGES);
        }
    }

    public void checkInstallApkTask(OnPermissionListener listener) {
        mPermissionListener = listener;
        checkInstallApkTask();
    }

    public void checkWriteStorageTask(OnPermissionListener listener) {
        mPermissionListener = listener;
        writeStorageTask();
    }

    @AfterPermissionGranted(WRITE_ACCESS_FINE_LOCATION_PERMISSION_REQUEST)
    public void accessFineLocationTask() {
        if (hasAccessFineLocationPermission()) {
            // Have permissions, do the thing!
            hasPermissionTodo();
        } else {
            // Ask for ACCESS_FINE_LOCATION permissions
            EasyPermissions.requestPermissions(
                    this,
                    getResources().getString(R.string.access_fine_location_apply),
                    WRITE_ACCESS_FINE_LOCATION_PERMISSION_REQUEST,
                    ACCESS_FINE_LOCATION);
        }
    }

    protected void hasPermissionTodo() {

    }

    protected boolean checkPermissions() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            isInitFocus = true;
        }
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    public boolean isInitFocus() {
        return isInitFocus;
    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d("TAG", "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d("TAG", "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
