package me.jack.devicerecord.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_search_result.view.*
import me.jack.devicerecord.R
import me.jack.devicerecord.data.Device
import me.jack.devicerecord.extension.isNotBlankAndNA
import me.jack.kotlin.library.extension.ctx
import me.jack.kotlin.library.extension.highLight

/**
 * Created by Jack on 2017/12/29.
 */
class SearchResultAdapter(private val deviceList: List<Device>,
                          private val keyword: String,
                          private val itemClick: (Device) -> Unit)
    : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.ctx).inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindDevice(deviceList[position], keyword)
    }

    override fun getItemCount() = deviceList.size

    class ViewHolder(view: View, private val itemClick: (Device) -> Unit)
        : RecyclerView.ViewHolder(view) {

        private val mHighLightColor = view.ctx.resources.getColor(R.color.colorAccent)

        fun bindDevice(device: Device, keyword: String) {
            with(device) {
                itemView.resultModel.text = model.highLight(keyword, mHighLightColor)
                itemView.resultOwner.text = owner.highLight(keyword, mHighLightColor)
                itemView.resultDescription.text = getDeviceKeywordInfo(device, keyword).highLight(keyword, mHighLightColor)
                itemView.setOnClickListener { itemClick(this) }
            }
        }

        private fun getDeviceKeywordInfo(device: Device, keyword: String) = when {
            device.recordId.isNotBlankAndNA() && device.recordId.contains(keyword, true) -> device.recordId
            device.inventoryNumber.isNotBlankAndNA() && device.inventoryNumber.contains(keyword, true) -> device.inventoryNumber
            device.serialNumber.isNotBlankAndNA() && device.serialNumber.contains(keyword, true) -> device.serialNumber
            device.imei.isNotBlankAndNA() && device.imei.contains(keyword, true) -> device.imei
            else -> if (device.serialNumber.isNotBlankAndNA()) device.serialNumber else device.imei
        }

    }
}