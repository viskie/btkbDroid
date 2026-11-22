package com.example.btcombokeyboard.bluetooth

object HidDescriptors {

    val COMBO_REPORT_DESCRIPTOR = byteArrayOf(
        // --- REPORT ID 1: KEYBOARD ---
        0x05.toByte(), 0x01.toByte(), // USAGE_PAGE (Generic Desktop)
        0x09.toByte(), 0x06.toByte(), // USAGE (Keyboard)
        0xA1.toByte(), 0x01.toByte(), // COLLECTION (Application)
        0x85.toByte(), 0x01.toByte(), //   REPORT_ID (1)
        0x05.toByte(), 0x07.toByte(), //   USAGE_PAGE (Keyboard)
        0x19.toByte(), 0xE0.toByte(), //   USAGE_MINIMUM (Left Control)
        0x29.toByte(), 0xE7.toByte(), //   USAGE_MAXIMUM (Right GUI)
        0x15.toByte(), 0x00.toByte(), //   LOGICAL_MINIMUM (0)
        0x25.toByte(), 0x01.toByte(), //   LOGICAL_MAXIMUM (1)
        0x75.toByte(), 0x01.toByte(), //   REPORT_SIZE (1)
        0x95.toByte(), 0x08.toByte(), //   REPORT_COUNT (8)
        0x81.toByte(), 0x02.toByte(), //   INPUT (Data,Var,Abs)
        0x95.toByte(), 0x01.toByte(), //   REPORT_COUNT (1)
        0x75.toByte(), 0x08.toByte(), //   REPORT_SIZE (8)
        0x81.toByte(), 0x03.toByte(), //   INPUT (Cnst,Var,Abs)
        0x95.toByte(), 0x06.toByte(), //   REPORT_COUNT (6)
        0x75.toByte(), 0x08.toByte(), //   REPORT_SIZE (8)
        0x15.toByte(), 0x00.toByte(), //   LOGICAL_MINIMUM (0)
        0x25.toByte(), 0x65.toByte(), //   LOGICAL_MAXIMUM (101)
        0x05.toByte(), 0x07.toByte(), //   USAGE_PAGE (Keyboard)
        0x19.toByte(), 0x00.toByte(), //   USAGE_MINIMUM (0)
        0x29.toByte(), 0x65.toByte(), //   USAGE_MAXIMUM (101)
        0x81.toByte(), 0x00.toByte(), //   INPUT (Data,Ary,Abs)
        0xC0.toByte(),                 // END_COLLECTION

        // --- REPORT ID 2: MOUSE / TRACKPAD ---
        0x05.toByte(), 0x01.toByte(), // USAGE_PAGE (Generic Desktop)
        0x09.toByte(), 0x02.toByte(), // USAGE (Mouse)
        0xA1.toByte(), 0x01.toByte(), // COLLECTION (Application)
        0x85.toByte(), 0x02.toByte(), //   REPORT_ID (2)
        0x09.toByte(), 0x01.toByte(), //   USAGE (Pointer)
        0xA1.toByte(), 0x00.toByte(), //   COLLECTION (Physical)
        0x05.toByte(), 0x09.toByte(), //     USAGE_PAGE (Button)
        0x19.toByte(), 0x01.toByte(), //     USAGE_MINIMUM (Button 1)
        0x29.toByte(), 0x03.toByte(), //     USAGE_MAXIMUM (Button 3)
        0x15.toByte(), 0x00.toByte(), //     LOGICAL_MINIMUM (0)
        0x25.toByte(), 0x01.toByte(), //     LOGICAL_MAXIMUM (1)
        0x75.toByte(), 0x01.toByte(), //     REPORT_SIZE (1)
        0x95.toByte(), 0x03.toByte(), //     REPORT_COUNT (3)
        0x81.toByte(), 0x02.toByte(), //     INPUT (Data,Var,Abs)
        0x75.toByte(), 0x05.toByte(), //     REPORT_SIZE (5)
        0x95.toByte(), 0x01.toByte(), //     REPORT_COUNT (1)
        0x81.toByte(), 0x03.toByte(), //     INPUT (Cnst,Var,Abs) - Padding
        0x05.toByte(), 0x01.toByte(), //     USAGE_PAGE (Generic Desktop)
        0x09.toByte(), 0x30.toByte(), //     USAGE (X)
        0x09.toByte(), 0x31.toByte(), //     USAGE (Y)
        0x09.toByte(), 0x38.toByte(), //     USAGE (Wheel)
        0x15.toByte(), 0x81.toByte(), //     LOGICAL_MINIMUM (-127)
        0x25.toByte(), 0x7F.toByte(), //     LOGICAL_MAXIMUM (127)
        0x75.toByte(), 0x08.toByte(), //     REPORT_SIZE (8)
        0x95.toByte(), 0x03.toByte(), //     REPORT_COUNT (3)
        0x81.toByte(), 0x06.toByte(), //     INPUT (Data,Var,Rel)
        0xC0.toByte(),                 //   END_COLLECTION
        0xC0.toByte(),                 // END_COLLECTION

        // --- REPORT ID 3: CONSUMER CONTROL (MEDIA) ---
        0x05.toByte(), 0x0C.toByte(), // USAGE_PAGE (Consumer)
        0x09.toByte(), 0x01.toByte(), // USAGE (Consumer Control)
        0xA1.toByte(), 0x01.toByte(), // COLLECTION (Application)
        0x85.toByte(), 0x03.toByte(), //   REPORT_ID (3)
        0x05.toByte(), 0x0C.toByte(), //   USAGE_PAGE (Consumer)
        0x15.toByte(), 0x00.toByte(), //   LOGICAL_MINIMUM (0)
        0x26.toByte(), 0x9C.toByte(), 0x02.toByte(), // LOGICAL_MAXIMUM (668)
        0x19.toByte(), 0x00.toByte(), //   USAGE_MINIMUM (0)
        0x2A.toByte(), 0x9C.toByte(), 0x02.toByte(), // USAGE_MAXIMUM (668)
        0x75.toByte(), 0x10.toByte(), //   REPORT_SIZE (16)
        0x95.toByte(), 0x01.toByte(), //   REPORT_COUNT (1)
        0x81.toByte(), 0x00.toByte(), //   INPUT (Data,Ary,Abs)
        0xC0.toByte()                  // END_COLLECTION
    )

    // Button bitmasks for Report ID 2
    const val MOUSE_BUTTON_NONE: Int = 0x00
    const val MOUSE_BUTTON_LEFT: Int = 0x01
    const val MOUSE_BUTTON_RIGHT: Int = 0x02

    // Consumer (media) control usages for Report ID 3
    const val MEDIA_PLAY_PAUSE: Int = 0xCD
    const val MEDIA_NEXT: Int = 0xB5
    const val MEDIA_PREV: Int = 0xB6
    const val MEDIA_VOL_UP: Int = 0xE9
    const val MEDIA_VOL_DOWN: Int = 0xEA
    const val MEDIA_MUTE: Int = 0xE2
}
