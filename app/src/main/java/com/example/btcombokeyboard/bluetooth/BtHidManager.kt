package com.example.btcombokeyboard.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.concurrent.Executors

@RequiresApi(Build.VERSION_CODES.P)
class BtHidManager(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var hidDevice: BluetoothHidDevice? = null
    private var connectedHost: BluetoothDevice? = null

    val isConnected: Boolean get() = connectedHost != null

    private val executor = Executors.newSingleThreadExecutor()

    private val serviceListener = object : BluetoothProfile.ServiceListener {
        @SuppressLint("MissingPermission")
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                hidDevice = proxy as BluetoothHidDevice
                registerHidApp()
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                hidDevice = null
                connectedHost = null
            }
        }
    }

    private val hidCallback = object : BluetoothHidDevice.Callback() {
        override fun onConnectionStateChanged(device: BluetoothDevice, state: Int) {
            connectedHost = if (state == BluetoothProfile.STATE_CONNECTED) device else null
            Log.d("BtHidManager", "Connection state: $state with device: ${device.name}")
        }
    }

    @SuppressLint("MissingPermission")
    fun init() {
        bluetoothAdapter?.getProfileProxy(context, serviceListener, BluetoothProfile.HID_DEVICE)
    }

    @SuppressLint("MissingPermission")
    private fun registerHidApp() {
        val sdpSettings = BluetoothHidDeviceAppSdpSettings(
            "Cyber Remote Combo",
            "Keyboard & Trackpad Combo",
            "BT Keyboards Inc",
            BluetoothHidDevice.SUBCLASS1_COMBO,
            HidDescriptors.COMBO_REPORT_DESCRIPTOR
        )

        val qosSettings = BluetoothHidDeviceAppQosSettings(
            BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT,
            800, 9, 0, 11250, 11250
        )

        hidDevice?.registerApp(sdpSettings, null, qosSettings, executor, hidCallback)
    }

    @SuppressLint("MissingPermission")
    fun sendKeystroke(modifier: Int, keycode: Int) {
        val host = connectedHost ?: return
        val device = hidDevice ?: return

        val keyDown = byteArrayOf(modifier.toByte(), 0x00, keycode.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00)
        device.sendReport(host, 1, keyDown)

        val keyRelease = ByteArray(8)
        device.sendReport(host, 1, keyRelease)
    }

    @SuppressLint("MissingPermission")
    fun sendMouseMovement(buttons: Int, dx: Int, dy: Int, wheel: Int = 0) {
        val host = connectedHost ?: return
        val device = hidDevice ?: return

        val mouseReport = byteArrayOf(buttons.toByte(), dx.toByte(), dy.toByte(), wheel.toByte())
        device.sendReport(host, 2, mouseReport)
    }

    @SuppressLint("MissingPermission")
    fun sendConsumerKey(usage: Int) {
        val host = connectedHost ?: return
        val device = hidDevice ?: return
        val report = byteArrayOf((usage and 0xFF).toByte(), ((usage shr 8) and 0xFF).toByte())
        device.sendReport(host, 3, report)
        device.sendReport(host, 3, byteArrayOf(0, 0))
    }
}
