package com.app.main.framework.baseview;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.main.framework.R;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public abstract class BasePermissionsFragment extends Fragment implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    protected Activity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();

    }
    public final int WRITE_STORAGE_PERMISSION_REQUEST = 200;
    public final int WRITE_ACCESS_FINE_LOCATION_PERMISSION_REQUEST = 300;

    protected String[] ACCESS_FINE_LOCATION = {android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CHANGE_NETWORK_STATE,Manifest.permission.CHANGE_WIFI_STATE};
    protected String[] STORAGE = {android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public OnPermissionListener mPermissionListener;

    public void checkFineLocationTask(OnPermissionListener listener) {
        mPermissionListener = listener;
        checkFineLocationTask();
    }

    public void checkWriteStorageTask(OnPermissionListener listener) {
        mPermissionListener = listener;
        writeStorageTask();
    }

    @AfterPermissionGranted(WRITE_ACCESS_FINE_LOCATION_PERMISSION_REQUEST)
    public void checkFineLocationTask() {
        if (hasAccessFineLocationPermission()) {
            // Have permissions, do the thing!
            if (mPermissionListener != null)
                mPermissionListener.onSuccess();
        } else {
            // Ask for ACCESS_FINE_LOCATION permissions
            EasyPermissions.requestPermissions(
                    this,
                    getResources().getString(R.string.access_fine_location_apply),
                    WRITE_ACCESS_FINE_LOCATION_PERMISSION_REQUEST,
                    ACCESS_FINE_LOCATION);
        }
    }

    @AfterPermissionGranted(WRITE_STORAGE_PERMISSION_REQUEST)
    public void writeStorageTask() {
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

    public boolean hasAccessFineLocationPermission() {
        return EasyPermissions.hasPermissions(mActivity, ACCESS_FINE_LOCATION);
    }
    private boolean hasWriteStoragePermission() {
        return EasyPermissions.hasPermissions(mActivity, STORAGE);
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



    public interface OnPermissionListener {
        void onSuccess();
    }

    protected void hasPermissionTodo() {

    }

    protected void noPermissionTodo() {

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
        noPermissionTodo();
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
