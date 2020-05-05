package ru.tohaman.testempty.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tohaman.testempty.databinding.ItemGameMenuBinding
import ru.tohaman.testempty.databinding.ItemTimerResultBinding
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.dbase.entitys.TimeNoteItem

class TimerResultAdapter: RecyclerView.Adapter<TimerResultAdapter.TimeNoteHolder>() {
    private var items: List<TimeNoteItem> = ArrayList()
    private var onClickCallBack: OnClickCallBack? = null

    fun attachCallBack(onClickCallBack: OnClickCallBack) {
        this.onClickCallBack = onClickCallBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeNoteHolder {
        return TimeNoteHolder.from(parent)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TimeNoteHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onClickCallBack)
    }

    fun refreshItems(list: List<TimeNoteItem>) {
        this.items = list
        notifyDataSetChanged()
    }

    class TimeNoteHolder private constructor(private val binding: ItemTimerResultBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TimeNoteItem, onClickCallBack: OnClickCallBack?) {
            binding.timeNote = item
            binding.clickListener = onClickCallBack
            //Метод executePendingBindings используется, чтобы биндинг не откладывался, а выполнился как можно быстрее. Это критично в случае с RecyclerView.
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : TimeNoteHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding =  ItemTimerResultBinding.inflate(inflater, parent, false)

                return TimeNoteHolder(binding)
            }
        }
    }

    interface OnClickCallBack {
        fun clickItem(item: TimeNoteItem)
    }

}