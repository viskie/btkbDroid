package com.example.btcombokeyboard

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.example.btcombokeyboard.bluetooth.BtHidManager
import com.example.btcombokeyboard.ui.BeautifulComboScreen

class MainActivity : ComponentActivity() {

    private lateinit var btHidManager: BtHidManager

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val permissions: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_SCAN
        )
    } else {
        arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
    }

    private val bondedDevicesState = mutableStateOf<List<BluetoothDevice>>(emptyList())
    private val statusMessageState = mutableStateOf<String?>(null)
    private val discoveredDevicesState = mutableStateOf<List<BluetoothDevice>>(emptyList())
    private val connectedNameState = mutableStateOf<String?>(null)
    private val showDevicesState = mutableStateOf(false)
    private val hasPermissionsState = mutableStateOf(false)

    private val discoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                        } else {
                            @Suppress("DEPRECATION")
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        }
                    device?.let { d ->
                        if (discoveredDevicesState.value.none { it.address == d.address }) {
                            discoveredDevicesState.value = discoveredDevicesState.value + d
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    // no-op
                }
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.all { it }) {
            hasPermissionsState.value = true
            btHidManager.init()
            refreshDevices()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(discoveryReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(discoveryReceiver, filter)
        }

        btHidManager = BtHidManager(this)
        btHidManager.onStatus = { msg -> statusMessageState.value = msg }

        if (permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
            hasPermissionsState.value = true
            btHidManager.init()
            refreshDevices()
        } else {
            permissionLauncher.launch(permissions)
        }

        setContent {
            BeautifulComboScreen(
                isConnected = btHidManager.isConnected,
                statusMessage = statusMessageState.value,
                connectedDeviceName = connectedNameState.value,
                bondedDevices = bondedDevicesState.value,
                discoveredDevices = discoveredDevicesState.value,
                showDevices = showDevicesState.value,
                onToggleDevices = { showDevicesState.value = !showDevicesState.value },
                onRefreshDevices = { refreshDevices() },
                onStartScan = {
                    discoveredDevicesState.value = emptyList()
                    btHidManager.startDiscovery()
                },
                onPairDevice = { dev ->
                    btHidManager.pairDevice(dev)
                },
                onConnectDevice = { dev ->
                    if (!btHidManager.isRegistered) {
                        statusMessageState.value = "HID app not registered yet; wait and retry"
                    } else if (dev.bondState != android.bluetooth.BluetoothDevice.BOND_BONDED) {
                        btHidManager.pairDevice(dev)
                        statusMessageState.value = "Pairing initiated with ${dev.name ?: dev.address}; connect after bonded"
                    } else {
                        val ok = btHidManager.connectToDevice(dev)
                        if (ok) {
                            connectedNameState.value = dev.name ?: dev.address
                            statusMessageState.value = "Connect requested to ${dev.name ?: dev.address}"
                        } else {
                            statusMessageState.value = "Connect failed/returned false for ${dev.name ?: dev.address} (host must accept HID)"
                        }
                    }
                    showDevicesState.value = false
                },
                onMakeDiscoverable = { makeDiscoverable() },
                onKeyPress = { modifier, keycode ->
                    btHidManager.sendKeystroke(modifier, keycode)
                },
                onMouseMove = { buttons, dx, dy, wheel ->
                    btHidManager.sendMouseMovement(buttons, dx, dy, wheel)
                },
                onMediaKey = { usage ->
                    btHidManager.sendConsumerKey(usage)
                }
            )
        }
    }

    private fun refreshDevices() {
        bondedDevicesState.value = btHidManager.getBondedDevices()
    }

    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(discoveryReceiver) } catch (_: Exception) {}
        btHidManager.stopDiscovery()
    }

    private fun makeDiscoverable() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        startActivity(intent)
    }
}
