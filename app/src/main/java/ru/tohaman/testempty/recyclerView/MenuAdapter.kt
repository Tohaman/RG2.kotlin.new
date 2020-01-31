package ru.tohaman.testempty.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tohaman.testempty.R
import ru.tohaman.testempty.dbase.MainDBItem

class MenuAdapter(private val items: List<MainDBItem>) : RecyclerView.Adapter<MenuAdapter.MenuHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.main_menu_item, parent, false)
        return MenuHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        val currentItem = items[position]
        holder.bindTo(currentItem)
    }

    class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView = itemView.findViewById<TextView>(R.id.main_menu_title)
        private val commentView = itemView.findViewById<TextView>(R.id.main_menu_comment)
        private val imageView = itemView.findViewById<ImageView>(R.id.main_menu_image)

        fun bindTo(menuItem: MainDBItem) {
            titleView.text = menuItem.title
            commentView.text = menuItem.comment
            val icon = menuItem.icon
            imageView.setImageResource(icon)
        }
    }

}