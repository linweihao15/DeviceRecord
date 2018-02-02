package me.jack.devicerecord.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.fragment_filter.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import me.jack.devicerecord.R
import me.jack.devicerecord.data.DataHelper
import me.jack.devicerecord.data.DeviceRecord
import me.jack.devicerecord.ui.view.FlowLayout
import me.jack.devicerecord.util.FragmentUtils
import me.jack.devicerecord.util.POIUtils
import me.jack.kotlin.library.util.ToolbarInterface
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.dip
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.io.File
import java.util.*

/**
 * Created by Jack on 2018/1/21.
 */
class FilterFragment : BaseFragment(), ToolbarInterface, CompoundButton.OnCheckedChangeListener {

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    private val brandList = ArrayList<String>()
    private val modelList = ArrayList<String>()
    private val teamList = ArrayList<String>()
    private val locationList = ArrayList<String>()
    private val buyerList = ArrayList<String>()

    enum class Type {
        BRAND,
        MODEL,
        TEAM,
        LOCATION,
        BUYER
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.fragment_filter, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolbar()
        loadData()
    }

    override fun onBackPressed() {
        FragmentUtils.instance.back(activity, Bundle())
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        buttonView?.let {
            val type = buttonView.tag
            when (type) {
                Type.BRAND -> {
                    onCheckBoxChecked(buttonView, isChecked, brandList)
                    val helper = DataHelper(POIUtils.instance.record())
                    if (isChecked) {
                        addViews(helper.modelSetByBrand(buttonView.text.toString()), Type.MODEL, modelPanel, true)
                    } else {
                        val set = helper.modelSetByBrand(buttonView.text.toString())
                        modelList.removeAll(set)
                        Log.d(javaClass.simpleName, "Model list: $modelList")
                        return
                    }
                }
                Type.MODEL -> onCheckBoxChecked(buttonView, isChecked, modelList)
                Type.TEAM -> onCheckBoxChecked(buttonView, isChecked, teamList)
                Type.LOCATION -> onCheckBoxChecked(buttonView, isChecked, locationList)
                Type.BUYER -> onCheckBoxChecked(buttonView, isChecked, buyerList)
            }
        }
    }

    private fun initToolbar() {
        toolbarTitle = getString(R.string.filter)
        showToolbarBackBtn { onBackPressed() }
        setupMenu(R.menu.menu_filter) {
            when (it.itemId) {
                R.id.action_export -> export()
            }
        }
    }

    private fun loadData() {
        val record = POIUtils.instance.record()
        val helper = DataHelper(record)
        val brands = helper.brandSet()
        addViews(brands, Type.BRAND, brandPanel)
        addViews(helper.modelSetByBrand(brands.first()), Type.MODEL, modelPanel)
        addViews(helper.teamSet(), Type.TEAM, teamPanel)
        addViews(helper.locationSet(), Type.LOCATION, locationPanel)
        addViews(helper.buyerSet(), Type.BUYER, buyerPanel)
    }

    @SuppressLint("InflateParams")
    private fun addViews(set: Set<String>, type: Type, panel: FlowLayout, checked: Boolean = false) {
        panel.removeAllViews()
        set.forEach {
            val checkbox = LayoutInflater.from(activity).inflate(R.layout.checkbox, null) as CheckBox
            checkbox.text = it
            checkbox.tag = type
            checkbox.setOnCheckedChangeListener(this)
            checkbox.isChecked = checked
            val param = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            param.setMargins(dip(5), dip(5), dip(5), dip(5))
            panel.addView(checkbox, param)
        }
    }

    private fun onCheckBoxChecked(button: CompoundButton, isChecked: Boolean, list: MutableList<String>) {
        if (isChecked) {
            list.add(button.text.toString())
        } else {
            list.remove(button.text.toString())
        }
        Log.d(javaClass.simpleName, "List: $list")
    }

    private fun export() {
        async(UI) {
            val record = POIUtils.instance.record()
            val list = record.list.filter {
                (brandList.isEmpty() || brandList.contains(it.brand)) &&
                        (modelList.isEmpty() || modelList.contains(it.model)) &&
                        (teamList.isEmpty() || teamList.contains(it.team)) &&
                        (locationList.isEmpty() || locationList.contains(it.location)) &&
                        (buyerList.isEmpty() || buyerList.contains(it.buyer))
            }.toMutableList()
            Log.d(javaClass.simpleName, "List: $list")
            val exportRecord = DeviceRecord("export", list)
            val file = POIUtils.DEFAULT_PATH + File.separator + "ExportRecord.xlsx"
            val result = bg { POIUtils.instance.exportExcel(exportRecord, File(file)) }
            if (result.await()) {
                toast("export success")
            } else {
                toast("export failed")
            }
        }
    }

}