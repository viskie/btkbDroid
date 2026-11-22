package com.example.btcombokeyboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.bluetooth.BluetoothDevice
import androidx.compose.ui.unit.sp
import com.example.btcombokeyboard.bluetooth.HidDescriptors

val DarkBackground = Color(0xFF0F111A)
val SurfaceColor = Color(0xFF1E2235)
val CyberCyan = Color(0xFF00E5FF)
val NeonPurple = Color(0xFFBD00FF)

@Composable
fun BeautifulComboScreen(
    isConnected: Boolean,
    connectedDeviceName: String?,
    bondedDevices: List<BluetoothDevice>,
    discoveredDevices: List<BluetoothDevice>,
    showDevices: Boolean,
    onToggleDevices: () -> Unit,
    onRefreshDevices: () -> Unit,
    onStartScan: () -> Unit,
    onPairDevice: (BluetoothDevice) -> Unit,
    onConnectDevice: (BluetoothDevice) -> Unit,
    onMakeDiscoverable: () -> Unit,
    onKeyPress: (Int, Int) -> Unit,
    onMouseMove: (Int, Int, Int, Int) -> Unit,
    onMediaKey: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("CYBER COMBO REMOTE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isConnected) "● ${connectedDeviceName ?: "CONNECTED"}" else "○ DISCONNECTED",
                    color = if (isConnected) Color.Green else Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onToggleDevices) { Text("Devices", color = CyberCyan, fontSize = 12.sp) }
            }
        }

        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Left Half: Keyboard
            Column(modifier = Modifier.weight(1.3f).fillMaxHeight()) {
                Row(modifier = Modifier.weight(1f)) {
                    Keycap("1") { onKeyPress(0x00, 0x1E) }
                    Keycap("2") { onKeyPress(0x00, 0x1F) }
                    Keycap("3") { onKeyPress(0x00, 0x20) }
                    Keycap("4") { onKeyPress(0x00, 0x21) }
                    Keycap("⌫") { onKeyPress(0x00, 0x2A) }
                }
                Row(modifier = Modifier.weight(1f)) {
                    Keycap("Q") { onKeyPress(0x00, 0x14) }
                    Keycap("W") { onKeyPress(0x00, 0x1A) }
                    Keycap("E") { onKeyPress(0x00, 0x08) }
                    Keycap("R") { onKeyPress(0x00, 0x15) }
                    Keycap("T") { onKeyPress(0x00, 0x17) }
                }
                Row(modifier = Modifier.weight(1f)) {
                    Keycap("A") { onKeyPress(0x00, 0x04) }
                    Keycap("S") { onKeyPress(0x00, 0x16) }
                    Keycap("D") { onKeyPress(0x00, 0x07) }
                    Keycap("F") { onKeyPress(0x00, 0x09) }
                    Keycap("G") { onKeyPress(0x00, 0x0A) }
                }
                Row(modifier = Modifier.weight(1f)) {
                    Keycap("Z") { onKeyPress(0x00, 0x1D) }
                    Keycap("X") { onKeyPress(0x00, 0x1B) }
                    Keycap("C") { onKeyPress(0x00, 0x06) }
                    Keycap("V") { onKeyPress(0x00, 0x19) }
                    Keycap("B") { onKeyPress(0x00, 0x05) }
                }
                Row(modifier = Modifier.weight(1f)) {
                    Keycap("TAB") { onKeyPress(0x00, 0x2B) }
                    Keycap("SPACE", modifier = Modifier.weight(2f)) { onKeyPress(0x00, 0x2C) }
                    Keycap("ENTER ↵") { onKeyPress(0x00, 0x28) }
                }
                Row(modifier = Modifier.weight(1f)) {
                    Keycap("←") { onKeyPress(0x00, 0x50) }
                    Keycap("↑") { onKeyPress(0x00, 0x52) }
                    Keycap("↓") { onKeyPress(0x00, 0x51) }
                    Keycap("→") { onKeyPress(0x00, 0x4F) }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Half: Trackpad + mouse buttons
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(
                            brush = Brush.radialGradient(colors = listOf(Color(0xFF192038), SurfaceColor)),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(1.dp, CyberCyan, RoundedCornerShape(16.dp))
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Main)
                                    val changes = event.changes
                                    if (changes.size == 1) {
                                        val c = changes.first()
                                        if (c.pressed) {
                                            val dx = (c.position.x - c.previousPosition.x).coerceIn(-127f, 127f).toInt()
                                            val dy = (c.position.y - c.previousPosition.y).coerceIn(-127f, 127f).toInt()
                                            if (dx != 0 || dy != 0) {
                                                onMouseMove(HidDescriptors.MOUSE_BUTTON_NONE, dx, dy, 0)
                                                c.consume()
                                            }
                                        }
                                    } else if (changes.size >= 2) {
                                        val avgDy = changes.map { it.position.y - it.previousPosition.y }.average().toFloat()
                                        if (avgDy != 0f) {
                                            val wheel = (-avgDy).coerceIn(-127f, 127f).toInt()
                                            if (wheel != 0) {
                                                onMouseMove(HidDescriptors.MOUSE_BUTTON_NONE, 0, 0, wheel)
                                            }
                                        }
                                        changes.forEach { it.consume() }
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("TRACKPAD\n1 finger move\n2 fingers scroll", color = Color.Gray, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.height(44.dp).fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(SurfaceColor, RoundedCornerShape(8.dp))
                            .border(1.dp, NeonPurple, RoundedCornerShape(8.dp))
                            .clickable {
                                onMouseMove(HidDescriptors.MOUSE_BUTTON_LEFT, 0, 0, 0)
                                onMouseMove(HidDescriptors.MOUSE_BUTTON_NONE, 0, 0, 0)
                            },
                        contentAlignment = Alignment.Center
                    ) { Text("LEFT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp) }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(SurfaceColor, RoundedCornerShape(8.dp))
                            .border(1.dp, NeonPurple, RoundedCornerShape(8.dp))
                            .clickable {
                                onMouseMove(HidDescriptors.MOUSE_BUTTON_RIGHT, 0, 0, 0)
                                onMouseMove(HidDescriptors.MOUSE_BUTTON_NONE, 0, 0, 0)
                            },
                        contentAlignment = Alignment.Center
                    ) { Text("RIGHT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Media controls row
        Row(modifier = Modifier.height(44.dp).fillMaxWidth()) {
            MediaKey("⏮", HidDescriptors.MEDIA_PREV, onMediaKey)
            MediaKey("⏯", HidDescriptors.MEDIA_PLAY_PAUSE, onMediaKey)
            MediaKey("⏭", HidDescriptors.MEDIA_NEXT, onMediaKey)
            MediaKey("🔉", HidDescriptors.MEDIA_VOL_DOWN, onMediaKey)
            MediaKey("🔊", HidDescriptors.MEDIA_VOL_UP, onMediaKey)
            MediaKey("🔇", HidDescriptors.MEDIA_MUTE, onMediaKey)
        }

        if (showDevices) {
            AlertDialog(
                onDismissRequest = onToggleDevices,
                title = { Text("Bluetooth Devices", color = Color.White) },
                text = {
                    Column {
                        Text("Make this device discoverable, then pair/connect from the host.", color = Color.Gray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onMakeDiscoverable) { Text("Make Discoverable (5 min)") }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onRefreshDevices) { Text("Refresh bonded devices") }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onStartScan) { Text("Scan for nearby devices") }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (discoveredDevices.isNotEmpty()) {
                            Text("Discovered:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            discoveredDevices.forEach { dev ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(dev.name ?: "Unknown", color = Color.White, fontSize = 13.sp)
                                        Text(dev.address, color = Color.Gray, fontSize = 11.sp)
                                    }
                                    Button(onClick = { onPairDevice(dev) }) { Text("Pair") }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        bondedDevices.forEach { dev ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(dev.name ?: "Unknown", color = Color.White, fontSize = 13.sp)
                                    Text(dev.address, color = Color.Gray, fontSize = 11.sp)
                                }
                                Button(onClick = { onConnectDevice(dev) }) { Text("Connect") }
                            }
                        }
                        if (bondedDevices.isEmpty()) {
                            Text("No bonded devices. Pair from system settings or make discoverable.", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = { TextButton(onClick = onToggleDevices) { Text("Close", color = CyberCyan) } }
            )
        }
    }
}

@Composable
fun RowScope.Keycap(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(2.dp)
            .background(SurfaceColor, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun RowScope.MediaKey(label: String, usage: Int, onMediaKey: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(2.dp)
            .background(SurfaceColor, RoundedCornerShape(8.dp))
            .border(1.dp, CyberCyan, RoundedCornerShape(8.dp))
            .clickable { onMediaKey(usage) },
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
