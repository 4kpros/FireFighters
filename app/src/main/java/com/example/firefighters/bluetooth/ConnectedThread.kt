package com.example.firefighters.bluetooth

import android.bluetooth.BluetoothSocket
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

//Class that given an open BT Socket will
//Open, manage and close the data Stream from the Arduino BT device
class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

    private val mInStream: InputStream?
    private val mOutStream: OutputStream?

    var receivedMessage: String? = null
        private set

    init {
        var tmpIn: InputStream? = null
        var tmpOut: OutputStream? = null

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = mmSocket.inputStream
        } catch (e: IOException) {
            Timber.e(e, "Error occurred when creating input stream")
        }
        try {
            tmpOut = mmSocket.outputStream
        } catch (e: IOException) {
            Timber.e(e, "Error occurred when creating output stream")
        }
        //Input and Output streams members of the class
        //We wont use the Output stream of this project
        mInStream = tmpIn
        mOutStream = tmpOut
    }

    fun sendValue(value: String) {
        try {
            mOutStream!!.write(value.toByteArray())
        } catch (e: IOException) {
            //
        }
    }

    override fun run() {
        val buffer = ByteArray(1024)
        var bytes = 0 // bytes returned from read()
        var numberOfReadings = 0 //to control the number of readings from the Arduino

        // Keep listening to the InputStream until an exception occurs.
        //We just want to get 1 temperature readings from the Arduino
        while (numberOfReadings < 1) {
            try {
                buffer[bytes] = mInStream!!.read().toByte()
                var readMessage: String
                // If I detect a "\n" means I already read a full measurement
                if (buffer[bytes] == '\n'.code.toByte()) {
                    readMessage = String(buffer, 0, bytes)
                    Timber.e(readMessage)
                    //Value to be read by the Observer streamed by the Obervable
                    receivedMessage = readMessage
                    bytes = 0
                    numberOfReadings++
                } else {
                    bytes++
                }
            } catch (e: IOException) {
                Timber.d(e, "Input stream was disconnected")
                break
            }
        }
    }

    // Call this method from the main activity to shut down the connection.
    fun cancel() {
        try {
            mmSocket.close()
        } catch (e: IOException) {
            Timber.e(e, "Could not close the connect socket")
        }
    }

    companion object {
        private const val TAG = "FrugalLogs"
    }
}