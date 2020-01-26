package ru.tohaman.testempty.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.tohaman.testempty.R
import ru.tohaman.testempty.dbase.ListPagerDBItem


class MenuViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup) {

    private val titleView = viewGroup.findViewById<TextView>(R.id.main_menu_title)
    private val commentView = viewGroup.findViewById<TextView>(R.id.main_menu_comment)
    private val imageView = viewGroup.findViewById<ImageView>(R.id.main_menu_image)
    var menuItem: ListPagerDBItem? = null


//    init {
//        viewGroup.setOnClickListener {
//            Log.d("DEB", "${menuItem}")
//        }
//    }

    fun bindTo(menuItem: ListPagerDBItem?) {
        this.menuItem = menuItem
        //Поскольку menuItem может быть null, то прописываем, умолчательные значения в этом случае.
        titleView.text = menuItem?.title ?: "SomeTitle"
        commentView.text = menuItem?.comment ?: ""
        val icon = menuItem?.icon ?: R.drawable.ic_menu_help
        imageView.setImageResource(icon)

    }

    companion object {
        fun create(parent: ViewGroup): MenuViewHolder {
            return MenuViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.main_menu_item, parent, false)
                        as ViewGroup)
        }
    }
}