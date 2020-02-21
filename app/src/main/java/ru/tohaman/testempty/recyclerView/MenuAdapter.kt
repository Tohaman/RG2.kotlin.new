package ru.tohaman.testempty.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.main_menu_item.view.*
import ru.tohaman.testempty.R
import ru.tohaman.testempty.dbase.MainDBItem

class MenuAdapter() : RecyclerView.Adapter<MenuAdapter.MenuHolder>() {

    private var items: List<MainDBItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.main_menu_item, parent, false)
        return MenuHolder(itemView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        val currentItem = items[position]
        holder.bindTo(currentItem)
    }

    fun refreshItems(items : List<MainDBItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindTo(menuItem: MainDBItem) = with(itemView){
            val icon = menuItem.icon
            main_menu_image.setImageResource(icon)
            main_menu_title.text = menuItem.title
            main_menu_comment.text = menuItem.comment
        }
    }

}