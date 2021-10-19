package com.app.main.framework.baseutil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtil {
    private static final int updateLocationMinTime = 60*1000; //每分钟更新一次定位
    private static final int updateLocationMinDiatance = 100; //没100米更新一次定位
    private static LocationUtil instance = new LocationUtil();
    private LocationManager locationManager;
    private String locationProvider;
    private Location location;
    private LocationListener mListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        // 如果位置发生变化，重新显示
        @Override
        public void onLocationChanged(Location locations) {
            location = locations;
            LogUtil.i("Latitude() == " + location.getLatitude() + ",Longitude() == " + location.getLongitude());
        }
    };

    @SuppressLint("MissingPermission")
    public LocationUtil(){
        if (locationManager == null) {
            locationManager = (LocationManager) UiUtil.getContext().getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);
            if (providers.contains(LocationManager.NETWORK_PROVIDER)){
                //如果是网络定位
                locationProvider = LocationManager.NETWORK_PROVIDER;
            }else if (providers.contains(LocationManager.GPS_PROVIDER)){
                //如果是GPS定位
                locationProvider = LocationManager.GPS_PROVIDER;
            }else {
                return;
            }
            //3.获取上次的位置，一般第一次运行，此值为null
            location = locationManager.getLastKnownLocation(locationProvider);
            if (location!=null){
                // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
                locationManager.requestLocationUpdates(locationProvider, updateLocationMinTime, updateLocationMinDiatance,mListener);
            }
        }
    }

    public static LocationUtil getInstance() {
        return instance;
    }

    public Location getLocation() {
        LogUtil.i("Latitude() == " + location.getLatitude() + ",Longitude() == " + location.getLongitude());
        return location;
    }

    public String getAddress(Location location) {
        Geocoder geocoder = new Geocoder(UiUtil.getContext());
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //根据经纬度获取地理位置信息
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    stringBuilder.append(address.getAddressLine(i)).append("\n");
                }
                stringBuilder.append(address.getCountryName()).append("_");//国家
                stringBuilder.append(address.getFeatureName()).append("_");//周边地址
                stringBuilder.append(address.getLocality()).append("_");//市
                stringBuilder.append(address.getPostalCode()).append("_");
                stringBuilder.append(address.getCountryCode()).append("_");//国家编码
                stringBuilder.append(address.getAdminArea()).append("_");//省份
                stringBuilder.append(address.getSubAdminArea()).append("_");
                stringBuilder.append(address.getThoroughfare()).append("_");//道路
                stringBuilder.append(address.getSubLocality()).append("_");//香洲区
                stringBuilder.append(address.getLatitude()).append("_");//经度
                stringBuilder.append(address.getLongitude());//维度
                System.out.println(stringBuilder.toString());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LogUtil.i(stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     *根据地址获取经纬度
     * @param context
     * @param str
     * @return
     */
    public Address getGeoPointBystr(Context context,String str) {
        Address address_temp = null;
        if (str != null) {
            Geocoder gc = new Geocoder(context, Locale.CHINA);
            List<Address> addressList = null;
            try {
                addressList = gc.getFromLocationName(str, 1);
                if (!addressList.isEmpty()) {
                    address_temp = addressList.get(0);
                    double Latitude = address_temp.getLatitude();
                    double Longitude = address_temp.getLongitude();
                    Log.d("zxc003",str+" Latitude = "+Latitude+" Longitude = "+Longitude);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return address_temp;
    }
}
