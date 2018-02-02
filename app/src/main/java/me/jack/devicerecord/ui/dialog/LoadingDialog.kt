package me.jack.devicerecord.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import me.jack.devicerecord.R

/**
 * Created by Jack on 2017/12/10.
 */
class LoadingDialog : BaseDialog() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity, R.style.BaseDialog)
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_loading, null)
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

}