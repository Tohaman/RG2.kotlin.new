package ru.tohaman.testempty.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tohaman.testempty.databinding.GameMenuItemBinding
import ru.tohaman.testempty.dbase.entitys.MainDBItem

class GamesAdapter: RecyclerView.Adapter<GamesAdapter.GamesHolder>() {
    private var items: List<MainDBItem> = ArrayList()
    private var onClickCallBack: OnClickCallBack? = null

    fun attachCallBack(onClickCallBack: OnClickCallBack) {
        this.onClickCallBack = onClickCallBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesHolder {
        return GamesHolder.from(parent)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: GamesHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onClickCallBack)
    }

    fun refreshItems(list: List<MainDBItem>) {
        this.items = list
        notifyDataSetChanged()
    }

    class GamesHolder private constructor(private val binding: GameMenuItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MainDBItem, onClickCallBack: OnClickCallBack?) {
            binding.viewMenuItem = item
            binding.clickListener = onClickCallBack
            //Метод executePendingBindings используется, чтобы биндинг не откладывался, а выполнился как можно быстрее. Это критично в случае с RecyclerView.
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : GamesHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding =  GameMenuItemBinding.inflate(inflater, parent, false)

                return GamesHolder(binding)
            }
        }
    }

    interface OnClickCallBack {
        fun clickItem(menuItem: MainDBItem)
        fun clickSettings(menuItem: MainDBItem)
        fun clickHelp(menuItem: MainDBItem)
    }

}