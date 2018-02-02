package me.jack.devicerecord.data

import android.util.Log
import me.jack.devicerecord.extension.currentTimeWithFormat
import me.jack.devicerecord.extension.firstLetterUpperCase
import java.io.Serializable
import java.util.*

/**
 * Created by Jack on 2017/11/28.
 */
data class Device(private val map: MutableMap<String, Any?>) : Serializable {
    var index: Int by map //index
    var recordId: String by map
    var inventoryNumber: String by map
    var model: String by map
    var serialNumber: String by map
    var imei: String by map
    var brand: String by map //Samsung, Amazon
    var description: String by map //SIZE, RAM, ROM, COLOR etc
    var password: String by map
    var owner: String by map
    var team: String by map
    var location: String by map
    var buyer: String by map
    var adminRecord: String by map
    var time: String by map // 2017/12/12
    var remark: String by map
    var history: SortedMap<String, String> by map // time:people

    constructor(index: Int,
                recordId: String,
                inventoryNumber: String,
                model: String,
                brand: String,
                serialNumber: String,
                imei: String,
                description: String,
                password: String,
                owner: String,
                team: String,
                location: String,
                buyer: String,
                adminRecord: String,
                remark: String,
                time: String,
                history: SortedMap<String, String>)
            : this(emptyMap<String, String>().toMutableMap()) {
        this.index = index
        this.recordId = recordId
        this.inventoryNumber = inventoryNumber
        this.model = model
        this.brand = brand
        this.serialNumber = serialNumber
        this.imei = imei
        this.description = description
        this.password = password
        this.owner = owner
        this.team = team
        this.location = location
        this.buyer = buyer
        this.adminRecord = adminRecord
        this.remark = remark
        this.time = time
        this.history = history
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Device) {
            this.equals(other)
        } else {
            false
        }
    }

    private fun equals(device: Device): Boolean {
        return recordId.equals(device.recordId, true) &&
                inventoryNumber.equals(device.inventoryNumber, true) &&
                model.equals(device.model, true) &&
                serialNumber.equals(device.serialNumber, true) &&
                imei.equals(device.imei, true) &&
                brand.equals(device.brand, true) &&
                description.equals(device.description, true) &&
                password.equals(device.password, true) &&
                owner.equals(device.owner, true) &&
                team.equals(device.team, true) &&
                location.equals(device.location, true) &&
                buyer.equals(device.buyer, true) &&
                adminRecord.equals(device.adminRecord, true) &&
                remark.equals(device.remark, true)
    }

    fun update(device: Device) {
        if (!this.owner.equals(device.owner, true)) {
            //different owner, change time and history
            with(history) {
                if (!containsKey(time) && size >= 5) {
                    val key = remove(firstKey())
                    Log.d(javaClass.simpleName, ">> First key: $key")
                }
                put(time, owner)
            }
            time = currentTimeWithFormat()
        }
        recordId = device.recordId
        inventoryNumber = inventoryNumber
        model = device.model
        brand = device.brand
        serialNumber = device.serialNumber
        imei = device.imei
        description = device.description
        password = device.password
        owner = device.owner
        team = device.team
        location = device.location
        buyer = device.buyer
        adminRecord = device.adminRecord
        remark = device.remark
    }

    fun format(): Device {
        owner = owner.formatName()
        recordId = recordId.toUpperCase()
        inventoryNumber = inventoryNumber.toUpperCase()
        serialNumber = serialNumber.toUpperCase()
        imei = imei.toLowerCase()
        location = location.toUpperCase()
        adminRecord = adminRecord.formatName()
        return this
    }

    private fun String.formatName(): String {
        val a = this.split(" ")
        val builder = StringBuffer()
        a.forEachIndexed { index, s ->
            if (index == 0) {
                builder.append(s.toLowerCase().firstLetterUpperCase()).append(" ")
            } else {
                builder.append(s.toUpperCase()).append(" ")
            }
        }
        return builder.toString().trim()
    }
}

data class DeviceRecord(val title: String, val list: MutableList<Device>)