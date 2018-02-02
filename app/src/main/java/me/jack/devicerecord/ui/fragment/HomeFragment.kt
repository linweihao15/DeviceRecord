package me.jack.devicerecord.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import me.jack.devicerecord.R
import me.jack.devicerecord.data.Device
import me.jack.devicerecord.ui.dialog.Constants
import me.jack.devicerecord.ui.dialog.LoadingDialog
import me.jack.devicerecord.ui.dialog.SearchDialog
import me.jack.devicerecord.ui.dialog.SearchResultCallback
import me.jack.devicerecord.ui.view.MenuView
import me.jack.devicerecord.util.FragmentUtils
import me.jack.devicerecord.util.POIUtils
import me.jack.devicerecord.util.PermissionUtils
import me.jack.kotlin.library.util.ToolbarInterface
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import me.jack.devicerecord.data.Constants as DataConstants

/**
 * Created by Jack on 2017/11/28.
 */
class HomeFragment : BaseFragment(), ToolbarInterface, SearchResultCallback {

    private var mPressedTime = 0L
    private val mLoadingDialog by lazy { LoadingDialog() }

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.fragment_home, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolbar()
        start()
    }

    override fun onBackPressed() {
        if (POIUtils.instance.modified) {
            showDialog()
            return
        }
        val currentTime = System.currentTimeMillis()
        if (currentTime - mPressedTime > 500) {
            mPressedTime = currentTime
        } else {
            activity.finish()
        }
    }

    override fun onItemSelected(device: Device) {
        val bundle = Bundle()
        bundle.putSerializable(DataConstants.DEVICE.name, device)
        FragmentUtils.instance.show(activity, DetailFragment::class.java.name, bundle)
    }

    private fun initToolbar() {
        toolbarTitle = getString(R.string.app_name)
        setupMenu(R.menu.menu_home) {
            when (it.itemId) {
                R.id.action_search -> {
                    val dialog = SearchDialog()
                    dialog.setTargetFragment(this, Constants.SEARCH_CODE.ordinal)
                    dialog.show(fragmentManager, SearchDialog::class.java.name)
                }
                R.id.action_add ->
                    FragmentUtils.instance.show(activity, AddFragment::class.java.name, Bundle())
                R.id.action_filter ->
                    FragmentUtils.instance.show(activity, FilterFragment::class.java.name, Bundle())
                R.id.action_info ->
                    FragmentUtils.instance.show(activity, InfoFragment::class.java.name, Bundle())
                R.id.action_import -> toast("Import")
                R.id.action_export -> toast("Export")
            }
        }
    }

    private fun start() {
        //Request permission
        PermissionUtils.instance.requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .onSuccess(object : PermissionUtils.SuccessCallback {
                    override fun onSuccess(permissions: Array<out String>) {
                        loadData()
                    }
                })
                .onFailure(object : PermissionUtils.FailureCallback {
                    override fun onFailure(permissions: Array<out String>, result: IntArray) {
                        toast("failure")
                    }
                })
                .run(activity)
    }

    private fun loadData() {
//        mLoadingDialog.show(activity.fragmentManager, LoadingDialog::class.java.name)
//        doAsync {
//            val record = POIUtils.instance.record()
//            runOnUiThread {
//                if (record.title.isNotBlank()) {
//                    //show data
//                    toast(record.title)
//                } else {
//                    //show error message and file selector
//                    toast("No exist excel")
//                }
//                mLoadingDialog.dismiss()
//            }
//        }
        async(UI) {
            mLoadingDialog.show(activity.fragmentManager, LoadingDialog::class.java.name)
            val record = bg { POIUtils.instance.record() }
            if (record.await().title.isNotBlank()) {
                //show data
            } else {
                //show error message and file selector
                toast("No exist excel")
            }
            mLoadingDialog.dismiss()
        }
    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(activity)
        dialog.setMessage(R.string.message_export_change)
        dialog.setPositiveButton(R.string.button_yes) { _, _ ->
            async(UI) {
                val record = POIUtils.instance.record()
                val result = bg { POIUtils.instance.exportExcel(record) }
                if (result.await()) {
                    activity.finish()
                    toast("export success")
                } else {
                    toast("export failed")
                }
            }
        }
        dialog.setNegativeButton(R.string.button_no) { _, _ ->
            activity.finish()
        }
        dialog.show()
    }


    /**
     * Custom menu
     */
    private fun showMenu(view: View) {
        val menuView = MenuView(activity)
        menuView.inflate(R.menu.menu_more) {
            when (it) {
                R.id.action_add -> toast("Add")
                R.id.action_info -> toast("Info")
            }
            menuView.dismiss()
        }
        menuView.show(view)
    }

}