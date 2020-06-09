package ru.tohaman.rg2.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.smarteist.autoimageslider.SliderViewAdapter
import ru.tohaman.rg2.dataSource.entitys.TipsItem
import ru.tohaman.rg2.databinding.ItemSliderImageBinding

class SliderAdapter() : SliderViewAdapter<SliderAdapter.ViewHolder>() {
    private var items: List<TipsItem> = ArrayList()

    fun refreshItems(items: List<TipsItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = items[position]
        viewHolder.bind(item)
    }


    class ViewHolder private constructor(private val binding: ItemSliderImageBinding): SliderViewAdapter.ViewHolder(binding.root) {
        fun bind(item: TipsItem){
            binding.sliderImage.images.setImageResource(item.imageRes)
            binding.sliderImage.imageComment.text = item.imageComment
            //Метод executePendingBindings используется, чтобы биндинг не откладывался, а выполнился как можно быстрее. Это критично в случае с RecyclerView.
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemSliderImageBinding.inflate(inflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}