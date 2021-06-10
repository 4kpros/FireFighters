package com.example.firefighters.tools;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsManager {

    private static PermissionsManager instance;

    public static PermissionsManager getInstance() {
        if (instance == null)
            instance = new PermissionsManager();
        return instance;
    }

    public boolean isLocationPermissions(Activity activity) {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ConstantsValues.LOCATION_PERMISSION_CODE);
    }

    public boolean isCallPermissions(Activity activity) {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isBluetoothPermissions(Activity activity) {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestCallPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, ConstantsValues.CALL_PERMISSION_CODE);
    }

    public void requestBluetoothPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH}, ConstantsValues.BLUETOOTH_PERMISSION_CODE);
    }
}
