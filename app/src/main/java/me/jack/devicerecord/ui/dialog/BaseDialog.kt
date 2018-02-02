package me.jack.devicerecord.ui.dialog

import android.app.DialogFragment
import android.os.Bundle
import me.jack.devicerecord.extension.translucentStatus

/**
 * Created by Jack on 2017/12/10.
 */
open class BaseDialog : DialogFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.translucentStatus()
    }

}