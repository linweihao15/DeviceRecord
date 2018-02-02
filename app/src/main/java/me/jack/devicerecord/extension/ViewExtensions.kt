package me.jack.devicerecord.extension

import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import me.jack.devicerecord.R
import me.jack.kotlin.library.extension.translucentStatus
import org.jetbrains.anko.forEachChild

/**
 * Created by Jack on 2017/11/28.
 */
fun Window.translucentStatus() {
    translucentStatus(context.resources.getColor(R.color.colorPrimaryDark))
}

fun View.slideExit() {
    if (translationY == 0f) animate().translationY(-height.toFloat())
}

fun View.slideEnter() {
    if (translationY < 0f) animate().translationY(0f)
}

fun ViewGroup.enableEdit() {
    this.forEachChild {
        when (it) {
            is ViewGroup -> it.enableEdit()
            is EditText -> it.isEnabled = true
        }
    }
}

fun ViewGroup.disableEdit() {
    this.forEachChild {
        when (it) {
            is ViewGroup -> it.disableEdit()
            is EditText -> it.isEnabled = false
        }
    }
}

fun ViewGroup.addTextChangedListener(textWatcher: TextWatcher) {
    this.forEachChild {
        when (it) {
            is ViewGroup -> it.addTextChangedListener(textWatcher)
            is EditText -> it.addTextChangedListener(textWatcher)
        }
    }
}

fun ViewGroup.removeTextChangedListener(textWatcher: TextWatcher) {
    this.forEachChild {
        when (it) {
            is ViewGroup -> it.removeTextChangedListener(textWatcher)
            is EditText -> it.removeTextChangedListener(textWatcher)
        }
    }
}

fun TextView.enableEdit() {
    isEnabled = true
    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_next, 0)
}

fun TextView.disableEdit() {
    isEnabled = false
    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
}

var TextView.textColor
    get() = currentTextColor
    set(value) = setTextColor(value)
