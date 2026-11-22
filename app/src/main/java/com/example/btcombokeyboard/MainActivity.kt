package com.example.btcombokeyboard

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.btcombokeyboard.bluetooth.BtHidManager
import com.example.btcombokeyboard.ui.BeautifulComboScreen

class MainActivity : ComponentActivity() {

    private lateinit var btHidManager: BtHidManager

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btHidManager = BtHidManager(this)
        btHidManager.init()

        setContent {
            BeautifulComboScreen(
                isConnected = btHidManager.isConnected,
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
}
