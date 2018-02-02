package me.jack.devicerecord.ui.fragment

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.jack.devicerecord.R
import me.jack.devicerecord.util.FragmentUtils
import me.jack.kotlin.library.util.ToolbarInterface
import org.jetbrains.anko.find

/**
 * Created by Jack on 2017/12/1.
 */
class InfoFragment : BaseFragment(), ToolbarInterface {

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.fragment_info, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolbar()
    }

    override fun onBackPressed() {
        FragmentUtils.instance.back(activity, Bundle())
    }

    private fun initToolbar() {
        toolbarTitle = getString(R.string.info)
        showToolbarBackBtn { onBackPressed() }
    }
}