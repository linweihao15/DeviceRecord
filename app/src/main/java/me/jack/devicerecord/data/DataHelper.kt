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

    fun sumOfBrand(): Map<String, Int> {
        val map = HashMap<String, Int>()
        record.list.forEach {
            val value = map[it.brand] ?: 0
            map.put(it.brand, value + 1)
        }
        return map
    }

    fun sumOfmodelByBrand(brand: String): Map<String, Int> {
        val map = HashMap<String, Int>()
        record.list.filter { it.brand == brand }
                .forEach {
                    val value = map[it.model] ?: 0
                    map.put(it.model, value + 1)
                }
        return map
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