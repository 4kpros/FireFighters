package com.example.firefighters

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.firefighters.models.EmergencyModel
import com.example.firefighters.ui.fragments.MainFragment
import com.example.firefighters.utils.ConstantsValues
import com.example.firefighters.utils.FirebaseUtils
import com.example.firefighters.utils.InjectorUtils
import com.example.firefighters.viewmodels.models.EmergencyViewModel
import com.example.firefighters.viewmodels.models.UserViewModel
import com.facebook.drawee.backends.pipeline.Fresco

class MainActivity : AppCompatActivity() {

    private val mUserViewModel by viewModels<UserViewModel> {
        InjectorUtils.provideUserViewModel()
    }
    private val mEmergencyViewModel by viewModels<EmergencyViewModel> {
        InjectorUtils.provideEmergencyViewModel()
    }

    private var mNotificationManagerCompat: NotificationManagerCompat? = null
    private var mFirsTimeOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialize fresco
        Fresco.initialize(this)
        //We don't have any data to handle so no need Data binding
        setContentView(R.layout.activity_main)

        //Set theme preferences
        val sharedPref = getPreferences(MODE_PRIVATE)
        val theme = sharedPref.getString(getString(R.string.save_theme), "")
        if (theme == "dark") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        //Initialize data
        initData()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ConstantsValues.LOCATION_PERMISSION_CODE) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Location permissions refused !", Toast.LENGTH_SHORT).show()
            } else {
                //Work
            }
        }
        if (requestCode == ConstantsValues.CALL_PERMISSION_CODE) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Call permissions refused !", Toast.LENGTH_SHORT).show()
            } else {
                //Work
            }
        }
        if (requestCode == ConstantsValues.SMS_PERMISSION_CODE) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Sms permissions refused !", Toast.LENGTH_SHORT).show()
            } else {
                //Work
            }
        }
        if (requestCode == ConstantsValues.AUDIO_RECORD_PERMISSION_CODE) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Record audio permissions refused !", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //Work
            }
        }
        if (requestCode == ConstantsValues.CAMERA_PERMISSION_CODE) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Camera permissions refused !", Toast.LENGTH_SHORT).show()
            } else {
                //Work
            }
        }
        if (requestCode == ConstantsValues.BLUETOOTH_PERMISSION_CODE) {
            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Bluetooth permissions refused !", Toast.LENGTH_SHORT).show()
            } else {
                //Work
            }
        }
    }

    private fun startListener() {
        mEmergencyViewModel.getEmergenciesQuerySnapshot(null, null)
            .observe(this) {
                if (mFirsTimeOpened) {
                    if (it != null) {
                        if (it.documentChanges.size > 0) {
                            for (document in it.documentChanges) {
                                val emergencyModel = document.document.toObject(
                                    EmergencyModel::class.java
                                )
                                notifyUser(emergencyModel)
                            }
                        }
                    }
                } else {
                    mFirsTimeOpened = true
                }
            }
    }

    private fun notifyUser(emergencyModel: EmergencyModel) {
        mNotificationManagerCompat = NotificationManagerCompat.from(this)
        val tempMessage = "EM ${emergencyModel.id}"
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("New emergency !")
            .setContentText(tempMessage)
            .setSmallIcon(R.drawable.ic_baseline_fireplace_24)
            .setChannelId(CHANNEL_ID)
            .setAutoCancel(true)
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT > 26) {
            val channel =
                NotificationChannel(CHANNEL_ID, "Firefighters", NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }
        manager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun initData() {
        if (FirebaseUtils.instance?.currentAuthUser == null) {
            setupFragments()
        } else {
            mUserViewModel.loadUserModel(
                FirebaseUtils.instance?.currentAuthUser?.email
            ).observe(this) {
                ConstantsValues.isIsFirefighter = it?.isFireFighter ?: false
                ConstantsValues.isIsChief = it?.isChief ?: false
                ConstantsValues.unit = it?.unit
                setupFragments()
                startListener()
            }
        }
    }

    private fun setupFragments() {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.anim_scale_in, R.anim.anim_scale_in)
        ft.replace(R.id.main_frame_layout, MainFragment()).addToBackStack(null)
        ft.commit()
    }

    companion object {
        const val NOTIFICATION_ID = 2131
        const val CHANNEL_ID = "FIRE_FIGHTER_CHANNEL_1"
    }
}