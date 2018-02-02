package me.jack.devicerecord.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatRadioButton
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import me.jack.devicerecord.R
import me.jack.devicerecord.data.Constants
import me.jack.kotlin.library.util.ToolbarInterface
import org.jetbrains.anko.find
import org.jetbrains.anko.forEachChild

/**
 * Created by Jack on 2018/1/13.
 */
class SelectorDialog : BaseDialog(), ToolbarInterface {

    override val toolbar by lazy { dialog.find<Toolbar>(R.id.toolbar) }

    private val radioGroup by lazy { dialog.find<RadioGroup>(R.id.radioGroup) }

    private var lastCheckedId = -1

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity, R.style.BaseFullscreenDialog)
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_selector, null)
        dialog.setContentView(view)
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolbar()
        initRadioGroup()
    }

    private fun initToolbar() {
        showToolbarBackBtn { dismiss() }
    }

    private fun initRadioGroup() {
        val data = arguments.getSerializable(Constants.DATA.name) as Set<*>
        val keyword = arguments.getString(Constants.KEYWORD.name)
        val id = arguments.getInt(Constants.ID.name)
        val param = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        data.forEach {
            val button = createRadioButton(it as String)
            radioGroup.addView(button, param)
        }
        val othersButton = createRadioButton(getString(R.string.others))
        radioGroup.addView(othersButton, param)
        radioGroup.forEachChild {
            val button = it as RadioButton
            if (button.text.toString() == keyword) {
                lastCheckedId = button.id
                button.isChecked = true
                return@forEachChild
            }
        }
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == -1) return@setOnCheckedChangeListener
            val button = radioGroup.find<RadioButton>(checkedId)
            if (!button.isChecked) return@setOnCheckedChangeListener
            if (checkedId == lastCheckedId) return@setOnCheckedChangeListener
            if (checkedId == othersButton.id) {
                showEditDialog(id)
            } else {
                onItemSelected(id, button.text.toString())
                dismiss()
            }
        }

    }

    @SuppressLint("InflateParams")
    private fun showEditDialog(id: Int) {
        val builder = AlertDialog.Builder(activity)
        val view = LayoutInflater.from(activity).inflate(R.layout.view_edit, null)
        val editor = view.find<EditText>(R.id.edit)
        builder.setView(view)
        builder.setPositiveButton(R.string.button_ok) { _, _ ->
            onItemSelected(id, editor.text.toString())
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.button_cancel) { _, _ ->
            if (lastCheckedId != -1)
                radioGroup.check(lastCheckedId)
            else
                radioGroup.clearCheck()
        }
        builder.setOnCancelListener {
            if (lastCheckedId != -1)
                radioGroup.check(lastCheckedId)
            else
                radioGroup.clearCheck()
        }
        builder.show()
    }

    @SuppressLint("InflateParams")
    private fun createRadioButton(label: String): RadioButton {
        val button = LayoutInflater.from(activity).inflate(R.layout.radiobutton, null) as AppCompatRadioButton
        button.text = label
        return button
    }

    private fun onItemSelected(id: Int, item: String) {
        val f = targetFragment
        if (f is SelectorCallback) {
            f.onItemSelected(id, item)
        }
    }

}