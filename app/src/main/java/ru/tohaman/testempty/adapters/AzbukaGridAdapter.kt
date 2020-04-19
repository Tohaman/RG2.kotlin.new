package ru.tohaman.testempty.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import ru.tohaman.testempty.databinding.ItemGameAzbukaGridBinding
import ru.tohaman.testempty.dbase.entitys.AzbukaItem


/**
 * Created by anton on 29.04.20 Для GridView адаптер наследуем от BaseAdapter и
 * для упрощения используем биндинг, с ним можно легко обойтись без всяких Холдеров.
 */


class AzbukaGridAdapter : BaseAdapter() {
    private var items: List<AzbukaItem> = listOf()

    fun refreshItems(list: List<AzbukaItem>) {
        items = list
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemGameAzbukaGridBinding
        binding = if (convertView == null) {
            val inflater = LayoutInflater.from(parent.context)
            ItemGameAzbukaGridBinding.inflate(inflater, parent, false)
        } else {
            DataBindingUtil.getBinding(convertView) ?: throw IllegalStateException()
        }

        with (binding) {
            item = items[position]
            executePendingBindings()
        }

        return binding.root
    }

    override fun getItem(position: Int): AzbukaItem {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

}