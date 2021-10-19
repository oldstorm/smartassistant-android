package com.app.main.framework.baseutil;

import android.Manifest;

import com.app.main.framework.R;

import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;

public class PermissionUtil {
    private static final int BASE_PERMISSION_REQUEST = 100;
    private static String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static String[] permissionsAudio = {Manifest.permission.RECORD_AUDIO};

    public static void checkLocationPermission(){
        EasyPermissions.requestPermissions(Objects.requireNonNull(LibLoader.getCurrentActivity()),
                UiUtil.getString(R.string.base_permisson_request),
                BASE_PERMISSION_REQUEST,
                permissions);
    }

    public static void checkAudioPermission(){
        EasyPermissions.requestPermissions(Objects.requireNonNull(LibLoader.getCurrentActivity()),
                UiUtil.getString(R.string.permisson_request_audio),
                BASE_PERMISSION_REQUEST,
                permissionsAudio);
    }
}
