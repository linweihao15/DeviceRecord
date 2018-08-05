package me.jack.devicerecord

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.jack.devicerecord.extension.translucentStatus
import me.jack.devicerecord.ui.fragment.BaseFragment
import me.jack.devicerecord.ui.fragment.HomeFragment
import me.jack.devicerecord.util.FragmentUtils
import me.jack.kotlin.library.util.PermissionHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.translucentStatus()
        showDefaultFragment()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PermissionHelper.REQUEST_CODE) {
            PermissionHelper.instance.handlePermissionsResult(permissions, grantResults)
        }
    }

    override fun onBackPressed() {
        val f = FragmentUtils.instance.currentFragment(this)
        if (f is BaseFragment) {
            f.onBackPressed()
        } else {
            finish()
        }
    }

    private fun showDefaultFragment() {
        FragmentUtils.instance.show(this, HomeFragment::class.java.name, Bundle())
    }

}
