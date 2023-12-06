package com.example.firefighters.ui.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.firefighters.R
import com.example.firefighters.databinding.ActivityBluetoothBinding
import com.example.firefighters.utils.InjectorUtils
import com.example.firefighters.viewmodels.activities.BluetoothActivityViewModel

class BluetoothActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityBluetoothBinding

    //View models
    private val mBluetoothActivityViewModel by viewModels<BluetoothActivityViewModel> {
        InjectorUtils.provideBluetoothViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set content view with data binding util
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_bluetooth)
        //Init bluetooth adapter
        mBluetoothActivityViewModel.initBluetoothManager(this@BluetoothActivity)
        //Check interactions
        checkInteractions()
    }

    private fun checkInteractions() {
        mDataBinding.buttonScanDevices.setOnClickListener {
            mBluetoothActivityViewModel.scanDevices(this@BluetoothActivity)
        }
        mDataBinding.buttonScanPairedDevices.setOnClickListener {
            mBluetoothActivityViewModel.scanPairedDevices(this)
        }
        mDataBinding.buttonShareLocation.setOnClickListener {
            mBluetoothActivityViewModel.shareLocation()
        }
        mDataBinding.buttonBack.setOnClickListener {
            backToPreviousPage(this@BluetoothActivity)
        }
    }

    private fun backToPreviousPage(activity: Activity?) {
        activity?.onBackPressed()
    }

    companion object {
        private const val TAG = "BluetoothActivity"
    }
}