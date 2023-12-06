package com.example.firefighters.viewmodels.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.ParcelUuid
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firefighters.bluetooth.ConnectThread
import com.example.firefighters.bluetooth.ConnectedThread
import com.example.firefighters.utils.PermissionsManager
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.UUID

class BluetoothActivityViewModel: ViewModel() {

    //Mutable live data
    val scannedDevice = MutableLiveData<ScanResult?>(null)
    val scannedDevices = MutableLiveData<List<ScanResult>?>(null)
    val pairedDevices = MutableLiveData<Set<BluetoothDevice>?>(null)
    val receivedMessage = MutableLiveData<String?>(null)

    //Bluetooth
    private var mBluetoothDevice: BluetoothDevice? = null
    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothLeScanner: BluetoothLeScanner? = null
    //Handler
    private var mConnectToBTObservable: Observable<String?>? = null
    private var mConnectThread: ConnectThread? = null
    private var mHandler: Handler? = null

    fun initBluetoothManager(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothManager = activity.getSystemService(
                BluetoothManager::class.java
            )
        }else {
            //
        }
        mBluetoothAdapter = mBluetoothManager?.adapter
        mBluetoothLeScanner = mBluetoothAdapter?.bluetoothLeScanner
        connectToBluetoothObservable()
        initializeHandler()
    }
    private fun initializeHandler() {
        mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                if (msg.what == ERROR_READ) {
                    val resultMsg = msg.obj.toString()
                    if (resultMsg.contains("@@@")) {
                        receivedMessage.value = resultMsg
                    }
                }
            }
        }
    }
    private fun connectToBluetoothObservable() {
        // Create an Observable from RxAndroid
        //The code will be executed when an Observer subscribes to the the Observable
        mConnectToBTObservable = Observable.create { emitter: ObservableEmitter<String?> ->
            //Call the constructor of the ConnectThread class
            //Passing the Arguments: an Object that represents the BT device,
            // the UUID and then the handler to update the UI
            mConnectThread = ConnectThread(mBluetoothDevice!!,
                DEVICE_UUI, mHandler!!)
            mConnectThread?.start()
            //Check if Socket connected
            if (mConnectThread?.mmSocket?.isConnected == true) {
                //The pass the Open socket as arguments to call the constructor of ConnectedThread
                val connectedThread = ConnectedThread(mConnectThread?.mmSocket!!)
                connectedThread.start()
                if (connectedThread.receivedMessage != null) {
                    // If we have read a value from the Arduino
                    // we call the onNext() function
                    //This value will be observed by the observer
                    emitter.onNext(connectedThread.receivedMessage!!)
                }
                //We just want to stream 1 value, so we close the BT stream
                connectedThread.cancel()
            }
            // SystemClock.sleep(5000); // simulate delay
            //Then we close the socket connection
            mConnectThread?.cancel()
            //We could Override the onComplete function
            emitter.onComplete()
        }
    }

    @SuppressLint("MissingPermission")
    fun scanDevices(activity: Activity) {
        //Check BT enabled. If disabled, we ask the user to enable BT
        if(!PermissionsManager.isBluetoothPermissions(activity)) {
            PermissionsManager.requestBluetoothPermission(activity)
            return
        }
        mBluetoothLeScanner?.startScan(buildScanFilters(), buildScanSettings(), SampleScanCallback())
    }
    private fun buildScanSettings(): ScanSettings {
        val builder = ScanSettings.Builder()
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER) // Low_Power mode conserves battery power.Change to SCAN_MODE_LOW_LATENCY for more aggressive scanning
        return builder.build()
    }
    private fun buildScanFilters(): List<ScanFilter> {
        val scanFilters: MutableList<ScanFilter> = ArrayList()
        val builder = ScanFilter.Builder()
        /* Comment out the below line to see all BLE devices around you
        the Service_UUID is the UUID in the advertisement packet*/
        builder.setServiceUuid(
            ParcelUuid.fromString(
                STRING_UUI
            )
        )
        scanFilters.add(builder.build())
        return scanFilters
    }

    @SuppressLint("MissingPermission")
    fun scanPairedDevices(activity: Activity) {
        //Check BT enabled. If disabled, we ask the user to enable BT
        if(!PermissionsManager.isBluetoothPermissions(activity)) {
            PermissionsManager.requestBluetoothPermission(activity)
            return
        }
        pairedDevices.value = mBluetoothAdapter?.bondedDevices
    }

    fun shareLocation() {
        sendDataToPairedBluetooth()
    }
    private fun sendDataToPairedBluetooth() {
        if (mConnectThread != null && mConnectThread?.mmSocket?.isConnected == true) {
            //The pass the Open socket as arguments to call the constructor of ConnectedThread
            val connectedThread = ConnectedThread(mConnectThread?.mmSocket!!)
            connectedThread.sendValue("alert")
        }
    }

    fun connectToDevice() {
        //We subscribe to the observable until the onComplete() is called
        //We also define control the thread management with
        // subscribeOn:  the thread in which you want to execute the action
        // observeOn: the thread in which you want to get the response
        mConnectToBTObservable?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe {
                    valueRead: String? ->
                //
            }
    }

    private inner class SampleScanCallback : ScanCallback() {
        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            //Handle when multiple devices are found
            scannedDevices.value = results
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            // Handle when a single device is found
            scannedDevice.value = result
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            scannedDevice.value = null
            scannedDevices.value = null
        }
    }

    class Factory(
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BluetoothActivityViewModel() as T
        }
    }
    companion object {
        private const val TAG = "BluetoothViewModel"

        const val STRING_UUI = "00001101-0000-1000-8000-00805F9B34FB"
        val DEVICE_UUI: UUID = UUID.fromString(STRING_UUI)

        private const val ERROR_READ = 0 // used in bluetooth handler to identify message update
    }
}