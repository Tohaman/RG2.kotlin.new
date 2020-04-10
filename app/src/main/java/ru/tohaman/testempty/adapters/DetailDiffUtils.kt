package ru.tohaman.testempty.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.tohaman.testempty.dbase.entitys.MainDBItem

class DetailDiffUtils(val oldList: List<MainDBItem>, val newList: List<MainDBItem>): DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].phase == newList[newItemPosition].phase) and
                (oldList[oldItemPosition].id == newList[newItemPosition].id)
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].isFavourite == newList[newItemPosition].isFavourite)
    }
}