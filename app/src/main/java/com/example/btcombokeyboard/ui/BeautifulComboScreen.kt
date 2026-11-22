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
    statusMessage: String?,
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
    var capsLock by remember { mutableStateOf(false) }
    var shiftOn by remember { mutableStateOf(false) }
    var ctrlOn by remember { mutableStateOf(false) }
    var altOn by remember { mutableStateOf(false) }

    fun sendKey(code: Int, isLetter: Boolean) {
        var mod = 0
        if (ctrlOn) mod = mod or 0x01
        if (altOn) mod = mod or 0x04
        val useShift = shiftOn || (capsLock && isLetter)
        if (useShift) mod = mod or 0x02
        onKeyPress(mod, code)
        if (shiftOn) shiftOn = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("CYBER COMBO REMOTE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isConnected) "● ${connectedDeviceName ?: "CONNECTED"}" else "○ DISCONNECTED",
                    color = if (isConnected) Color.Green else Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                TextButton(onClick = onToggleDevices) { Text("Devices", color = CyberCyan, fontSize = 11.sp) }
            }
        }

        // Top area: trackpad on top-right 60%
        Row(modifier = Modifier.weight(0.32f).fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(0.4f))
            Column(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(
                            brush = Brush.radialGradient(colors = listOf(Color(0xFF192038), SurfaceColor)),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, CyberCyan, RoundedCornerShape(12.dp))
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
                    Text("TRACKPAD", color = Color.Gray, fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.height(32.dp).fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(SurfaceColor, RoundedCornerShape(6.dp))
                            .border(1.dp, NeonPurple, RoundedCornerShape(6.dp))
                            .clickable {
                                onMouseMove(HidDescriptors.MOUSE_BUTTON_LEFT, 0, 0, 0)
                                onMouseMove(HidDescriptors.MOUSE_BUTTON_NONE, 0, 0, 0)
                            },
                        contentAlignment = Alignment.Center
                    ) { Text("L", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(SurfaceColor, RoundedCornerShape(6.dp))
                            .border(1.dp, NeonPurple, RoundedCornerShape(6.dp))
                            .clickable {
                                onMouseMove(HidDescriptors.MOUSE_BUTTON_RIGHT, 0, 0, 0)
                                onMouseMove(HidDescriptors.MOUSE_BUTTON_NONE, 0, 0, 0)
                            },
                        contentAlignment = Alignment.Center
                    ) { Text("R", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Full keyboard
        Column(modifier = Modifier.weight(0.62f).fillMaxWidth()) {
            val keyH = Modifier.height(IntrinsicSize.Min)
            // Row 1
            Row(Modifier.weight(1f).fillMaxWidth()) {
                K("`", 0x35, 1f, false, ::sendKey)
                K("1", 0x1E, 1f, false, ::sendKey)
                K("2", 0x1F, 1f, false, ::sendKey)
                K("3", 0x20, 1f, false, ::sendKey)
                K("4", 0x21, 1f, false, ::sendKey)
                K("5", 0x22, 1f, false, ::sendKey)
                K("6", 0x23, 1f, false, ::sendKey)
                K("7", 0x24, 1f, false, ::sendKey)
                K("8", 0x25, 1f, false, ::sendKey)
                K("9", 0x26, 1f, false, ::sendKey)
                K("0", 0x27, 1f, false, ::sendKey)
                K("-", 0x2D, 1f, false, ::sendKey)
                K("=", 0x2E, 1f, false, ::sendKey)
                K("⌫", 0x2A, 1.4f, false, ::sendKey)
            }
            // Row 2
            Row(Modifier.weight(1f).fillMaxWidth()) {
                K("Tab", 0x2B, 1.4f, false, ::sendKey)
                K("Q", 0x14, 1f, true, ::sendKey)
                K("W", 0x1A, 1f, true, ::sendKey)
                K("E", 0x08, 1f, true, ::sendKey)
                K("R", 0x15, 1f, true, ::sendKey)
                K("T", 0x17, 1f, true, ::sendKey)
                K("Y", 0x1C, 1f, true, ::sendKey)
                K("U", 0x18, 1f, true, ::sendKey)
                K("I", 0x0C, 1f, true, ::sendKey)
                K("O", 0x12, 1f, true, ::sendKey)
                K("P", 0x13, 1f, true, ::sendKey)
                K("[", 0x2F, 1f, false, ::sendKey)
                K("]", 0x30, 1f, false, ::sendKey)
                K("\\", 0x31, 1.2f, false, ::sendKey)
            }
            // Row 3
            Row(Modifier.weight(1f).fillMaxWidth()) {
                ModifierKey("Caps", capsLock) { capsLock = !capsLock }
                K("A", 0x04, 1f, true, ::sendKey)
                K("S", 0x16, 1f, true, ::sendKey)
                K("D", 0x07, 1f, true, ::sendKey)
                K("F", 0x09, 1f, true, ::sendKey)
                K("G", 0x0A, 1f, true, ::sendKey)
                K("H", 0x0B, 1f, true, ::sendKey)
                K("J", 0x0D, 1f, true, ::sendKey)
                K("K", 0x0E, 1f, true, ::sendKey)
                K("L", 0x0F, 1f, true, ::sendKey)
                K(";", 0x33, 1f, false, ::sendKey)
                K("'", 0x34, 1f, false, ::sendKey)
                K("Enter", 0x28, 1.6f, false, ::sendKey)
            }
            // Row 4
            Row(Modifier.weight(1f).fillMaxWidth()) {
                ModifierKey("Shift", shiftOn) { shiftOn = !shiftOn }
                K("Z", 0x1D, 1f, true, ::sendKey)
                K("X", 0x1B, 1f, true, ::sendKey)
                K("C", 0x06, 1f, true, ::sendKey)
                K("V", 0x19, 1f, true, ::sendKey)
                K("B", 0x05, 1f, true, ::sendKey)
                K("N", 0x11, 1f, true, ::sendKey)
                K("M", 0x10, 1f, true, ::sendKey)
                K(",", 0x36, 1f, false, ::sendKey)
                K(".", 0x37, 1f, false, ::sendKey)
                K("/", 0x38, 1f, false, ::sendKey)
                ModifierKey("Shift", shiftOn) { shiftOn = !shiftOn }
            }
            // Row 5
            Row(Modifier.weight(1f).fillMaxWidth()) {
                ModifierKey("Ctrl", ctrlOn) { ctrlOn = !ctrlOn }
                ModifierKey("Alt", altOn) { altOn = !altOn }
                K("Space", 0x2C, 4f, false, ::sendKey)
                K("←", 0x50, 1f, false, ::sendKey)
                K("↑", 0x52, 1f, false, ::sendKey)
                K("↓", 0x51, 1f, false, ::sendKey)
                K("→", 0x4F, 1f, false, ::sendKey)
                ModifierKey("Alt", altOn) { altOn = !altOn }
                ModifierKey("Ctrl", ctrlOn) { ctrlOn = !ctrlOn }
            }
        }

        if (statusMessage != null) {
            Text(
                text = statusMessage,
                color = Color.Yellow,
                fontSize = 10.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(modifier = Modifier.height(36.dp).fillMaxWidth()) {
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
fun RowScope.K(label: String, code: Int, weight: Float, isLetter: Boolean, onKey: (Int, Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .weight(weight)
            .fillMaxHeight()
            .padding(1.dp)
            .background(SurfaceColor, RoundedCornerShape(6.dp))
            .clickable { onKey(code, isLetter) },
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
    }
}

@Composable
fun RowScope.ModifierKey(label: String, active: Boolean, onToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(1.dp)
            .background(if (active) NeonPurple else SurfaceColor, RoundedCornerShape(6.dp))
            .border(1.dp, CyberCyan, RoundedCornerShape(6.dp))
            .clickable { onToggle() },
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
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
