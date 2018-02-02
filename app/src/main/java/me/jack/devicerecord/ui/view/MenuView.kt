package me.jack.devicerecord.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.MenuRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import me.jack.devicerecord.R
import me.jack.devicerecord.ui.adapter.MenuAdapter

/**
 * Created by Jack on 2017/12/2.
 */
class MenuView(private val ctx: Context) : PopupWindow(ctx) {

    private val content = LayoutInflater.from(ctx).inflate(R.layout.layout_menu, null) as RecyclerView

    init {
        contentView = content
        isFocusable = true
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        animationStyle = R.style.MenuFadeAnimation
    }

    fun inflate(@MenuRes menuIdRes: Int, itemClick: (Int) -> Unit): MenuView {
        val menu = PopupMenu(ctx, content)
        menu.menuInflater.inflate(menuIdRes, menu.menu)
        content.layoutManager = LinearLayoutManager(ctx)
        content.adapter = MenuAdapter(menu.menu, itemClick)
        return this
    }

    fun show(view: View) {
        this.showAsDropDown(view)
    }

}