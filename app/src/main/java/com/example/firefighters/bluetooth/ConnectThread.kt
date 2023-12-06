package com.example.firefighters.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import timber.log.Timber
import java.io.IOException
import java.util.UUID

//Class that will open the BT Socket to the Arduino BT Module
//Given a BT device, the UUID and a Handler to set the results
@SuppressLint("MissingPermission")
class ConnectThread constructor(
    device: BluetoothDevice,
    MY_UUID: UUID?,
    handler: Handler
) : Thread() {
    @JvmField
    val mmSocket: BluetoothSocket?

    init {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        var tmp: BluetoothSocket? = null
        mHandler = handler
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID)
        } catch (e: IOException) {
            Timber.d("Socket's create() method failed")
        }
        mmSocket = tmp
    }

    override fun run() {
        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket?.connect()
        } catch (connectException: IOException) {
            // Unable to connect; close the socket and return.
            mHandler.obtainMessage(ERROR_READ, "Unable to connect to the BT device").sendToTarget()
            Timber.d( "connectException: $connectException")
            try {
                mmSocket!!.close()
            } catch (closeException: IOException) {
                Timber.d( "Could not close the client socket")
            }
            return
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        //manageMyConnectedSocket(mmSocket);
    }

    // Closes the client socket and causes the thread to finish.
    fun cancel() {
        try {
            mmSocket!!.close()
        } catch (e: IOException) {
            Timber.d( "Could not close the client socket")
        }
    }

    companion object {
        private const val TAG = "FrugalLogs"
        lateinit var mHandler: Handler
        private const val ERROR_READ = 0
    }
}
