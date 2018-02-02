package me.jack.devicerecord.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log

/**
 * Created by Jack on 2017/12/11.
 */
class PermissionUtils {

    companion object {
        val instance by lazy { PermissionUtils() }
        val REQUEST_CODE = 1001
    }

    private val mPermissions by lazy { ArrayList<String>() }
    private var success: SuccessCallback? = null
    private var failure: FailureCallback? = null
    private var end: EndCallback? = null

    fun checkPermission(ctx: Context, permissions: Array<String>): Boolean {
        return permissions.none { ContextCompat.checkSelfPermission(ctx, it) != PackageManager.PERMISSION_GRANTED }
    }

    fun noLongerAskPermission(activity: Activity, permission: String): Boolean {
        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    fun requestPermissions(vararg permissions: String): PermissionUtils {
        permissions.map { mPermissions.add(it) }
        return this
    }

    fun onSuccess(callback: SuccessCallback): PermissionUtils {
        this.success = callback
        return this
    }

    fun onFailure(callback: FailureCallback): PermissionUtils {
        this.failure = callback
        return this
    }

    fun onEnd(callback: EndCallback): PermissionUtils {
        this.end = callback
        return this
    }

    fun run(activity: Activity) {
        if (mPermissions.isEmpty()) {
            reset()
            return
        }
        val permissions = mPermissions.toTypedArray()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !checkPermission(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE)
        } else {
            success?.onSuccess(permissions)
            val array = IntArray(permissions.size) { PackageManager.PERMISSION_GRANTED }
            end?.onEnd(permissions, array)
            reset()
        }

    }

    fun handlePermissionsResult(permissions: Array<out String>, result: IntArray) {
        val state = result.none { it != PackageManager.PERMISSION_GRANTED }
        when (state) {
            true -> success?.onSuccess(permissions)
            false -> failure?.onFailure(permissions, result)
        }
        end?.onEnd(permissions, result)
        reset()
    }

    private fun reset() {
        mPermissions.clear()
        success = null
        failure = null
        end = null
    }

    interface SuccessCallback {
        fun onSuccess(permissions: Array<out String>)
    }

    interface FailureCallback {
        fun onFailure(permissions: Array<out String>, result: IntArray)
    }

    interface EndCallback {
        fun onEnd(permissions: Array<out String>, result: IntArray)
    }

}
