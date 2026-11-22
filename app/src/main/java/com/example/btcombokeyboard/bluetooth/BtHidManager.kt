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

    var isRegistered: Boolean = false
        private set

    var onStatus: (String) -> Unit = {}

    val isConnected: Boolean get() = connectedHost != null

    private val executor = Executors.newSingleThreadExecutor()

    private val serviceListener = object : BluetoothProfile.ServiceListener {
        @SuppressLint("MissingPermission")
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                hidDevice = proxy as BluetoothHidDevice
                onStatus("HID profile connected, registering app...")
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
            val stateName = when (state) {
                BluetoothProfile.STATE_CONNECTED -> "Connected"
                BluetoothProfile.STATE_CONNECTING -> "Connecting"
                BluetoothProfile.STATE_DISCONNECTED -> "Disconnected"
                BluetoothProfile.STATE_DISCONNECTING -> "Disconnecting"
                else -> "State $state"
            }
            onStatus("$stateName: ${device.name ?: device.address}")
            Log.d("BtHidManager", "Connection state: $state with device: ${device.name}")
        }

        override fun onAppStatusChanged(device: BluetoothDevice?, registered: Boolean) {
            isRegistered = registered
            onStatus(if (registered) "HID app registered${device?.let { " for ${it.name ?: it.address}" } ?: ""}" else "HID app unregistered")
            Log.d("BtHidManager", "App status: registered=$registered device=${device?.name}")
        }
    }

    @SuppressLint("MissingPermission")
    fun init() {
        if (bluetoothAdapter == null) {
            onStatus("Bluetooth adapter not available")
            return
        }
        try {
            onStatus("Requesting HID profile proxy...")
            bluetoothAdapter.getProfileProxy(context, serviceListener, BluetoothProfile.HID_DEVICE)
        } catch (e: SecurityException) {
            onStatus("Missing permission: ${e.message}")
            Log.e("BtHidManager", "Missing permission for HID profile", e)
        } catch (e: Exception) {
            onStatus("Init failed: ${e.message}")
            Log.e("BtHidManager", "Init failed", e)
        }
    }

    @SuppressLint("MissingPermission")
    fun getBondedDevices(): List<BluetoothDevice> {
        return try {
            bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
        } catch (e: SecurityException) {
            Log.e("BtHidManager", "Missing permission for bonded devices", e)
            emptyList()
        }
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice): Boolean {
        return try {
            hidDevice?.connect(device) ?: false
        } catch (e: SecurityException) {
            Log.e("BtHidManager", "Missing permission for connect", e)
            false
        } catch (e: Exception) {
            Log.e("BtHidManager", "Connect failed", e)
            false
        }
    }

    @SuppressLint("MissingPermission")
    fun startDiscovery(): Boolean {
        return try {
            bluetoothAdapter?.cancelDiscovery()
            bluetoothAdapter?.startDiscovery() ?: false
        } catch (e: SecurityException) {
            Log.e("BtHidManager", "Missing permission for discovery", e)
            false
        } catch (e: Exception) {
            Log.e("BtHidManager", "Discovery failed", e)
            false
        }
    }

    @SuppressLint("MissingPermission")
    fun stopDiscovery() {
        try { bluetoothAdapter?.cancelDiscovery() } catch (_: Exception) {}
    }

    @SuppressLint("MissingPermission")
    fun pairDevice(device: BluetoothDevice): Boolean {
        return try {
            device.createBond()
        } catch (e: Exception) {
            Log.e("BtHidManager", "Pairing failed", e)
            false
        }
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

        try {
            val ok = hidDevice?.registerApp(sdpSettings, null, qosSettings, executor, hidCallback)
            onStatus("registerApp called: $ok")
        } catch (e: Exception) {
            onStatus("registerApp failed: ${e.message}")
            Log.e("BtHidManager", "registerApp failed", e)
        }
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
