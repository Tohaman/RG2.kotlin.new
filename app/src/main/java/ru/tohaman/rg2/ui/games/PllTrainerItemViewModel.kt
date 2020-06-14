package ru.tohaman.rg2.ui.games

import androidx.databinding.ObservableBoolean
import ru.tohaman.rg2.BR
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.adapters.universal.RecyclerItemComparator
import ru.tohaman.rg2.dataSource.entitys.RecyclerItem
import ru.tohaman.rg2.dbase.entitys.PllGameItem
import timber.log.Timber

class PllTrainerItemViewModel(val pllGameItem: PllGameItem): RecyclerItemComparator {
    var clickHandler: OnClickByPllTrainerItem? = null
    var isChecked = ObservableBoolean (pllGameItem.isChecked)

    override fun isSameItem(other: Any): Boolean {
        if (this === other) return true
        if (javaClass != other.javaClass) return false

        other as PllTrainerItemViewModel
        return this.pllGameItem.id == other.pllGameItem.id
    }

    override fun isSameContent(other: Any): Boolean {
        other as PllTrainerItemViewModel
        return this.pllGameItem == other.pllGameItem
    }

    fun onClick() {
        clickHandler?.onItemClick(pllGameItem)
    }

    fun onCheckChange(value: Boolean) {
        clickHandler?.let {
            val checked = it.onCheckedChange(value, pllGameItem.id)
            isChecked.set(checked)
        }
    }

    fun toRecyclerItem() = RecyclerItem(
        data = this,
        layoutId = R.layout.item_algorithms_properties,
        variableId = BR.pllTrainerItemViewModel
    )
}

interface OnClickByPllTrainerItem {
    fun onItemClick(pllGameItem: PllGameItem)
    fun onCheckedChange(value: Boolean, id: Int): Boolean
}