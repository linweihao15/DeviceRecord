package me.jack.devicerecord.ui.dialog

import me.jack.devicerecord.data.Device

/**
 * Created by Jack on 2017/12/28.
 */
interface SearchResultCallback {
    fun onItemSelected(device: Device)
}