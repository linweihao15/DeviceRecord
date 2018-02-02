package me.jack.devicerecord.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_detail.*
import me.jack.devicerecord.R
import me.jack.devicerecord.data.DataHelper
import me.jack.devicerecord.data.Device
import me.jack.devicerecord.data.HistoryMapper
import me.jack.devicerecord.extension.*
import me.jack.devicerecord.ui.dialog.Constants
import me.jack.devicerecord.ui.dialog.SelectorCallback
import me.jack.devicerecord.ui.dialog.SelectorDialog
import me.jack.devicerecord.util.FragmentUtils
import me.jack.devicerecord.util.POIUtils
import me.jack.kotlin.library.extension.hideSoftInput
import me.jack.kotlin.library.util.ToolbarInterface
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import me.jack.devicerecord.data.Constants as DataConstants

/**
 * Created by Jack on 2017/12/1.
 */
class DetailFragment : BaseFragment(), ToolbarInterface, TextWatcher, View.OnClickListener,
        SelectorCallback {

    private var isTextChanged = false

    private val editBtn by lazy { toolbar.menu.findItem(R.id.action_edit) }
    private val saveBtn by lazy { toolbar.menu.findItem(R.id.action_save) }

    private lateinit var device: Device

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.fragment_detail, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolbar()
        initWidget()
        device = arguments.getSerializable(DataConstants.DEVICE.name) as Device
        loadData(device)
    }

    override fun onBackPressed() {
        if (isTextChanged) {
            //show dialog to save change
            val dialog = AlertDialog.Builder(activity)
            dialog.setMessage(R.string.message_save_change)
            dialog.setPositiveButton(R.string.button_yes) { _, _ ->
                saveChange()
                FragmentUtils.instance.back(activity, Bundle())
            }
            dialog.setNegativeButton(R.string.button_no) { _, _ ->
                FragmentUtils.instance.back(activity, Bundle())
            }
            dialog.show()
        } else {
            FragmentUtils.instance.back(activity, Bundle())
        }
        view.hideSoftInput()
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        detectChange()
    }

    override fun onClick(v: View?) {
        val helper = DataHelper(POIUtils.instance.record())
        when (v?.id) {
            R.id.brand -> onSetClick(v, helper.brandSet(), brand.text.toString())
            R.id.model -> onSetClick(v, helper.modelSetByBrand(brand.text.toString()), model.text.toString())
            R.id.team -> onSetClick(v, helper.teamSet(), team.text.toString())
            R.id.location -> onSetClick(v, helper.locationSet(), location.text.toString())
            R.id.buyer -> onSetClick(v, helper.buyerSet(), buyer.text.toString())
        }
    }

    override fun onItemSelected(id: Int, item: String) {
        when (id) {
            R.id.brand -> brand.text = brand.text.toIf(item) { item.isNotBlank() }.trim()
            R.id.model -> model.text = model.text.toIf(item) { item.isNotBlank() }.trim()
            R.id.team -> team.text = team.text.toIf(item) { item.isNotBlank() }.trim()
            R.id.location -> location.text = location.text.toIf(item) { item.isNotBlank() }.trim()
            R.id.buyer -> buyer.text = buyer.text.toIf(item) { item.isNotBlank() }.trim()
        }
        detectChange()
    }

    private fun initToolbar() {
        toolbarTitle = getString(R.string.detail)
        showToolbarBackBtn { onBackPressed() }
        setupMenu(R.menu.menu_detail) {
            when (it.itemId) {
                R.id.action_edit -> onEditButtonClick()
                R.id.action_save -> onSaveButtonClick()
            }
        }
    }

    private fun initWidget() {
        brand.setOnClickListener(this)
        model.setOnClickListener(this)
        team.setOnClickListener(this)
        location.setOnClickListener(this)
        buyer.setOnClickListener(this)
    }

    private fun loadData(d: Device) {
        owner.setText(d.owner)
        brand.text = d.brand
        model.text = d.model
        recordId.setText(d.recordId)
        inventoryNumber.setText(d.inventoryNumber)
        serialNumber.setText(d.serialNumber)
        imei.setText(d.imei)
        description.setText(d.description)
        password.setText(d.password)
        team.text = d.team
        location.text = d.location
        buyer.text = d.buyer
        adminRecord.setText(d.adminRecord)
        remark.setText(d.remark)
        time.text = d.time
        history.text = HistoryMapper().convertToStringFromHistory(d.history)
    }

    private fun onEditButtonClick() {
        editBtn.isVisible = false
        saveBtn.isVisible = true
        saveBtn.isEnabled = false
        saveBtn.setIcon(R.drawable.ic_save_disabled)
        val rootView = view as ViewGroup
        rootView.enableEdit()
        rootView.addTextChangedListener(this)
        brand.enableEdit()
        model.enableEdit()
        team.enableEdit()
        location.enableEdit()
        buyer.enableEdit()
        timeLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
        contentPanel.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
    }

    private fun onSaveButtonClick() {
        editBtn.isVisible = true
        saveBtn.isVisible = false
        val rootView = view as ViewGroup
        rootView.disableEdit()
        rootView.removeTextChangedListener(this)
        brand.disableEdit()
        model.disableEdit()
        team.disableEdit()
        location.disableEdit()
        buyer.disableEdit()
        timeLayout.visibility = View.VISIBLE
        historyLayout.visibility = View.VISIBLE
        contentPanel.showDividers = LinearLayout.SHOW_DIVIDER_NONE
        saveChange()
        loadData(device)
    }

    private fun detectChange() {
        if (currentDevice() != device) {
            isTextChanged = true
            saveBtn.isEnabled = true
            saveBtn.setIcon(R.drawable.ic_save)
        } else {
            isTextChanged = false
            saveBtn.isEnabled = false
            saveBtn.setIcon(R.drawable.ic_save_disabled)
        }
    }

    private fun saveChange() {
        toast("Saved")
        isTextChanged = false
        device.update(currentDevice())
        val helper = DataHelper(POIUtils.instance.record())
        helper.updateDevice(device)
    }

    private fun currentDevice(): Device {
        return Device(device.index,
                recordId.text.toString().ensureNotBlankAndTrim(),
                inventoryNumber.text.toString().ensureNotBlankAndTrim(),
                model.text.toString().ensureNotBlankAndTrim(),
                brand.text.toString().ensureNotBlankAndTrim(),
                serialNumber.text.toString().ensureNotBlankAndTrim(),
                imei.text.toString().ensureNotBlankAndTrim(),
                description.text.toString().ensureNotBlankAndTrim(),
                password.text.toString().ensureNotBlankAndTrim(),
                owner.text.toString().ensureNotBlankAndTrim(),
                team.text.toString().ensureNotBlankAndTrim(),
                location.text.toString().ensureNotBlankAndTrim(),
                buyer.text.toString().ensureNotBlankAndTrim(),
                adminRecord.text.toString().ensureNotBlankAndTrim(),
                remark.text.toString().ensureNotBlankAndTrim(),
                time.text.toString().ensureNotBlankAndTrim(),
                HistoryMapper().convertToHistoryFromString(history.text.toString())).format()
    }

    private fun onSetClick(v: View, set: Set<String>, keyword: String) {
        val dialog = SelectorDialog()
        dialog.setTargetFragment(this, Constants.SELECTOR_CODE.ordinal)
        val bundle = Bundle()
        bundle.putSerializable(DataConstants.DATA.name, set.toHashSet())
        bundle.putString(DataConstants.KEYWORD.name, keyword)
        bundle.putInt(DataConstants.ID.name, v.id)
        dialog.arguments = bundle
        dialog.show(fragmentManager, SelectorDialog::class.java.name)
    }
}