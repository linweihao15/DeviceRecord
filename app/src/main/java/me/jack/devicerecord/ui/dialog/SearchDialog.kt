package me.jack.devicerecord.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import me.jack.devicerecord.R
import me.jack.devicerecord.data.Device
import me.jack.devicerecord.extension.isNotBlankAndNA
import me.jack.devicerecord.ui.adapter.SearchResultAdapter
import me.jack.devicerecord.ui.view.RecyclerViewDivider
import me.jack.devicerecord.util.POIUtils
import me.jack.kotlin.library.extension.hideSoftInput
import me.jack.kotlin.library.util.ToolbarInterface
import org.jetbrains.anko.find

/**
 * Created by Jack on 2017/12/26.
 */
class SearchDialog : BaseDialog(), ToolbarInterface {

    private lateinit var searchView: EditText
    private lateinit var searchButton: View

    override val toolbar by lazy { dialog.find<Toolbar>(R.id.toolbar) }

    private val noResultTips by lazy { dialog.find<TextView>(R.id.noResultTips) }
    private val searchResultListView by lazy { dialog.find<RecyclerView>(R.id.searchResultListView) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity, R.style.BaseFullscreenDialog)
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_search, null)
        dialog.setContentView(view)
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolbar()
        initListView()
    }


    private fun initToolbar() {
        showToolbarBackBtn { dismiss() }
        addSearchView()
    }

    private fun addSearchView() {
        val view = LayoutInflater.from(activity).inflate(R.layout.view_search, null)
        searchView = view.find(R.id.searchView)
        searchButton = view.find(R.id.searchButton)
        searchButton.setOnClickListener { search(searchView.text.toString().trim()) }
        toolbar.addView(view, Toolbar.LayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)))
    }

    private fun initListView() {
        searchResultListView.layoutManager = LinearLayoutManager(activity)
        searchResultListView.addItemDecoration(RecyclerViewDivider(activity, LinearLayoutManager.VERTICAL))
    }

    private fun search(keyword: String) {
        dialog.window.decorView.hideSoftInput()
        val results = findResultByKeyword(keyword)
        noResultTips.visibility = if (results.isEmpty()) View.VISIBLE else View.GONE
        val adapter = SearchResultAdapter(results, keyword) {
            onItemClick(it)
        }
        searchResultListView.adapter = adapter
    }

    private fun findResultByKeyword(keyword: String): List<Device> {
        if (keyword.isBlank()) return emptyList()
        val record = POIUtils.instance.record()
        return record.list.filter {
            (it.recordId.isNotBlankAndNA() && it.recordId.contains(keyword, true)) ||
                    (it.inventoryNumber.isNotBlankAndNA() && it.inventoryNumber.contains(keyword, true)) ||
                    (it.model.isNotBlankAndNA() && it.model.contains(keyword, true)) ||
                    (it.serialNumber.isNotBlankAndNA() && it.serialNumber.contains(keyword, true)) ||
                    (it.imei.isNotBlankAndNA() && it.imei.contains(keyword, true)) ||
                    (it.owner.isNotBlankAndNA() && it.owner.contains(keyword, true))
        }
    }

    private fun onItemClick(device: Device) {
        val f = targetFragment
        if (f is SearchResultCallback) {
            f.onItemSelected(device)
        }
        dismiss()
    }
}