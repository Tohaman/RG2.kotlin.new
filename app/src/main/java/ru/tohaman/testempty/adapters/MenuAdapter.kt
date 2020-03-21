package ru.tohaman.testempty.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tohaman.testempty.databinding.MainMenuItemBinding
import ru.tohaman.testempty.dbase.entitys.MainDBItem

interface OnClickCallBack {
    fun openItem(menuItem: MainDBItem, view: View): Boolean
    fun favouriteChange(menuItem: MainDBItem): Boolean
    //fun longClick(menuItem: MainDBItem, view: View): Boolean
}

class MenuAdapter() : RecyclerView.Adapter<MenuAdapter.MenuHolder>() {
    //тут храним список, который надо отобразить
    private var items: List<MainDBItem> = ArrayList()
    private var curPhase: String = ""
    private var onClickCallBack: OnClickCallBack? = null

    fun attachCallBack(onClickCallBack: OnClickCallBack) {
        this.onClickCallBack = onClickCallBack
    }

    //создает ViewHolder и инициализирует views для списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        return MenuHolder.from(parent)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        //тут обновляем данные ячейки (вызываем биндер холдера) передаем туда MainDBItem и onClickListener
        val item = items[position]
        holder.bind(item, curPhase, onClickCallBack)
    }

    fun refreshItems(items: List<MainDBItem>, phase: String = items[0].phase) {
        this.items = items
        this.curPhase = phase
        notifyDataSetChanged()
    }

//    class OnClickListener(val clickListener: (MainDBItem, View) -> Unit) {
//        fun onClick(menuItem: MainDBItem, view: View) = clickListener(menuItem, view)
//    }

    class MenuHolder private constructor(private val binding: MainMenuItemBinding)
            : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MainDBItem, phase: String, onClickCallBack: OnClickCallBack?) {
            binding.viewMenuItem = item
            binding.clickListener = onClickCallBack
            //Метод executePendingBindings используется, чтобы биндинг не откладывался, а выполнился как можно быстрее. Это критично в случае с RecyclerView.
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : MenuHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding =  MainMenuItemBinding.inflate(inflater, parent, false)

                return MenuHolder(binding)
            }
        }
    }

}