package ru.tohaman.testempty.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import ru.tohaman.testempty.R
import ru.tohaman.testempty.dataSource.cubeColor
import ru.tohaman.testempty.databinding.ItemGameAzbukaGridBinding
import ru.tohaman.testempty.dataSource.entitys.AzbukaSimpleItem


/**
 * Created by anton on 29.04.20 Для GridView адаптер наследуем от BaseAdapter и
 * для упрощения используем биндинг, с ним можно легко обойтись без всяких Холдеров.
 */


class AzbukaGridAdapter : BaseAdapter() {
    private var items: List<AzbukaSimpleItem> = listOf()
    private var onClickCallBack: OnClickCallBack? = null

    fun attachCallBack(onClickCallBack: OnClickCallBack) {
        this.onClickCallBack = onClickCallBack
    }

    fun refreshItems(list: List<AzbukaSimpleItem>) {
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
            clickListener = onClickCallBack
            id = position
            val color = items[position].color
            innerSqLayout.setBackgroundResource(cubeColor[color])
            if (items[position].value != "") {
                outerLayout.setBackgroundResource(R.color.black)
            }
            //if((position == 53) or (position == 54)) {outOutLayout.setBackgroundResource(R.color.red)} //подсветка буфера
            executePendingBindings()
        }

        return binding.root
    }

    override fun getItem(position: Int): AzbukaSimpleItem {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

    interface OnClickCallBack {
        fun clickItem(azbuka: AzbukaSimpleItem, id: Int, view: View)
    }


}