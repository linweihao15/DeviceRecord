package me.jack.devicerecord.data

import me.jack.devicerecord.extension.isNotNA
import me.jack.devicerecord.util.POIUtils

/**
 * Created by Jack on 2017/12/29.
 */
class DataHelper(private val record: DeviceRecord) {

    fun brandSet() = record.list.map { it.brand }.toSet()

    fun modelSetByBrand(brand: String) = record.list.filter { it.brand == brand }.map { it.model }.toSet()

    fun teamSet() = record.list.map { it.team }.toSet()

    fun locationSet() = record.list.map { it.location }.toSet()

    fun buyerSet() = record.list.map { it.buyer }.toSet()

    fun updateDevice(device: Device): Boolean {
        with(record.list) {
            val d = indexOfFirst {
                it.index == device.index
            }
            if (d != -1) {
                set(d, device)
                POIUtils.instance.modified = true
                return true
            }
            return false
        }
    }

    fun addDevice(device: Device): Boolean {
        if (isNotExist(device)) {
            record.list.add(device)
            POIUtils.instance.modified = true
            return true
        }
        return false
    }

    private fun isNotExist(device: Device): Boolean {
        return record.list.none {
            (device.recordId.isNotNA() && it.recordId == device.recordId) ||
                    (device.inventoryNumber.isNotNA() && it.inventoryNumber == device.inventoryNumber) ||
                    (device.serialNumber.isNotNA() && it.serialNumber == device.serialNumber) ||
                    (device.imei.isNotNA() && it.imei == device.imei)
        }
    }
}