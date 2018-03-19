package me.jack.devicerecord.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.*
import kotlinx.android.synthetic.main.item_menu.view.*
import me.jack.devicerecord.R
import me.jack.kotlin.library.extension.ctx

/**
 * Created by Jack on 2017/12/2.
 */
class MenuAdapter(private val menu: Menu, private val itemClick: (Int) -> Unit)
    : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.parseMenuItem(menu.getItem(position))
    }

    override fun getItemCount() = menu.size()

    class ViewHolder(view: View, private val itemClick: (Int) -> Unit)
        : RecyclerView.ViewHolder(view) {

        fun parseMenuItem(item: MenuItem) {
            with(item) {
                itemView.menuIcon.setImageDrawable(icon)
                itemView.menuText.text = title
                itemView.setOnClickListener {
                    itemClick(itemId)
                }
            }
        }
    }
}