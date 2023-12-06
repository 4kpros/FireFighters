package com.example.firefighters.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionsManager {
    fun isLocationPermissions(activity: Activity?): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun isCallPermissions(activity: Activity?): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isBluetoothPermissions(activity: Activity?): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun isMessagePermissions(activity: Activity?): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isAudioRecordingPermissions(activity: Activity?): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isCameraPermissions(activity: Activity?): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission(activity: Activity?) {
        ActivityCompat.requestPermissions(
            activity!!, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            ConstantsValues.LOCATION_PERMISSION_CODE
        )
    }

    fun requestCallPermission(activity: Activity?) {
        ActivityCompat.requestPermissions(
            activity!!, arrayOf(
                Manifest.permission.CALL_PHONE
            ),
            ConstantsValues.CALL_PERMISSION_CODE
        )
    }

    fun requestMessagePermission(activity: Activity?) {
        ActivityCompat.requestPermissions(
            activity!!, arrayOf(
                Manifest.permission.SEND_SMS
            ),
            ConstantsValues.SMS_PERMISSION_CODE
        )
    }

    fun requestBluetoothPermission(activity: Activity?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                activity!!, arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                ),
                ConstantsValues.BLUETOOTH_PERMISSION_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                activity!!, arrayOf(
                    Manifest.permission.BLUETOOTH
                ),
                ConstantsValues.BLUETOOTH_PERMISSION_CODE
            )
        }
    }

    fun requestAudioRecordPermission(activity: Activity?) {
        ActivityCompat.requestPermissions(
            activity!!, arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            ConstantsValues.AUDIO_RECORD_PERMISSION_CODE
        )
    }

    fun requestCameraPermission(activity: Activity?) {
        ActivityCompat.requestPermissions(
            activity!!, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            ConstantsValues.CAMERA_PERMISSION_CODE
        )
    }
}
