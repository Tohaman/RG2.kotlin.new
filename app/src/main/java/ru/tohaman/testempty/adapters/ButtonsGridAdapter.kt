package ru.tohaman.testempty.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import ru.tohaman.testempty.databinding.ItemBlindButtonGridBinding


/**
 * Created by anton on 29.04.20 Для GridView адаптер наследуем от BaseAdapter и
 * для упрощения используем биндинг, с ним можно легко обойтись без всяких Холдеров.
 */


class ButtonsGridAdapter : BaseAdapter() {
    private var items: List<String> = listOf()
    private var onClickCallBack: OnClickCallBack? = null

    fun attachCallBack(onClickCallBack: OnClickCallBack) {
        this.onClickCallBack = onClickCallBack
    }

    fun refreshItems(list: List<String>) {
        items = list
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemBlindButtonGridBinding
        binding = if (convertView == null) {
            val inflater = LayoutInflater.from(parent.context)
            ItemBlindButtonGridBinding.inflate(inflater, parent, false)
        } else {
            DataBindingUtil.getBinding(convertView) ?: throw IllegalStateException()
        }

        with (binding) {
            item = items[position]
            clickListener = onClickCallBack
            id = position
            executePendingBindings()
        }
        return binding.root
    }

    override fun getItem(position: Int): String {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

    interface OnClickCallBack {
        fun clickItem(letter: String, id: Int, view: View)
    }


}