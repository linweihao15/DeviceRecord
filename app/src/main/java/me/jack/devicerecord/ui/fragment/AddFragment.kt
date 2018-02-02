package me.jack.devicerecord.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_detail.*
import me.jack.devicerecord.R
import me.jack.devicerecord.data.DataHelper
import me.jack.devicerecord.data.Device
import me.jack.devicerecord.extension.*
import me.jack.devicerecord.ui.dialog.Constants
import me.jack.devicerecord.ui.dialog.SelectorCallback
import me.jack.devicerecord.ui.dialog.SelectorDialog
import me.jack.devicerecord.util.FragmentUtils
import me.jack.devicerecord.util.POIUtils
import me.jack.kotlin.library.extension.hideSoftInput
import me.jack.kotlin.library.util.ToolbarInterface
import org.jetbrains.anko.find

/**
 * Created by Jack on 2017/12/1.
 */
class AddFragment : BaseFragment(), ToolbarInterface, TextWatcher, View.OnClickListener,
        SelectorCallback {

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    private val saveBtn by lazy { toolbar.menu.findItem(R.id.action_save) }

    private var isTextChanged = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.fragment_detail, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolBar()
        initWidgets()
    }

    override fun onBackPressed() {
        if (isTextChanged) {
            //show dialog to save change
            val dialog = AlertDialog.Builder(activity)
            dialog.setMessage(R.string.message_save_change)
            dialog.setPositiveButton(R.string.button_yes) { _, _ ->
                save()
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

    private fun initToolBar() {
        toolbarTitle = getString(R.string.add)
        showToolbarBackBtn { onBackPressed() }
        setupMenu(R.menu.menu_add) {
            when (it.itemId) {
                R.id.action_save -> onSaveButtonClick()
            }
        }
    }

    private fun initWidgets() {
        val rootView = view as ViewGroup
        rootView.enableEdit()
        rootView.addTextChangedListener(this)
        brand.enableEdit()
        brand.setOnClickListener(this)
        model.enableEdit()
        model.setOnClickListener(this)
        team.enableEdit()
        team.setOnClickListener(this)
        location.enableEdit()
        location.setOnClickListener(this)
        buyer.enableEdit()
        buyer.setOnClickListener(this)
        timeLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
        contentPanel.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
    }

    private fun onSaveButtonClick() {
        save()
    }

    private fun onSetClick(v: View, set: Set<String>, keyword: String) {
        val dialog = SelectorDialog()
        dialog.setTargetFragment(this, Constants.SELECTOR_CODE.ordinal)
        val bundle = Bundle()
        bundle.putSerializable(me.jack.devicerecord.data.Constants.DATA.name, set.toHashSet())
        bundle.putString(me.jack.devicerecord.data.Constants.KEYWORD.name, keyword)
        bundle.putInt(me.jack.devicerecord.data.Constants.ID.name, v.id)
        dialog.arguments = bundle
        dialog.show(fragmentManager, SelectorDialog::class.java.name)
    }

    private fun detectChange() {
        if (model.text.isNotBlank() && brand.text.isNotBlank() &&
                (serialNumber.text.isNotBlank() || imei.text.isNotBlank()) &&
                owner.text.isNotBlank() && adminRecord.text.isNotBlank()) {
            isTextChanged = true
            saveBtn.isEnabled = true
            saveBtn.setIcon(R.drawable.ic_save)
        } else {
            isTextChanged = false
            saveBtn.isEnabled = false
            saveBtn.setIcon(R.drawable.ic_save_disabled)
        }
    }

    private fun save() {
        val helper = DataHelper(POIUtils.instance.record())
        helper.addDevice(currentDevice())
        FragmentUtils.instance.back(activity, Bundle())
    }

    private fun currentDevice(): Device {
        val size = POIUtils.instance.record().list.size
        return Device(size,
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
                currentTimeWithFormat(),
                emptyMap<String, String>().toSortedMap()).format()
    }
}