package ru.tohaman.rg2.ui.learn.list_items

import androidx.databinding.ObservableBoolean
import ru.tohaman.rg2.BR
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.adapters.universal.RecyclerItemComparator
import ru.tohaman.rg2.dataSource.entitys.RecyclerItem
import ru.tohaman.rg2.dbase.entitys.MainDBItem
import ru.tohaman.rg2.dbase.entitys.PllGameItem
import timber.log.Timber

class LeftMenuItemViewModel(val mainDBItem: MainDBItem): RecyclerItemComparator {
    var clickHandler: OnClickByLeftMenuItem? = null

    override fun isSameItem(other: Any): Boolean {
        if (this === other) return true
        if (javaClass != other.javaClass) return false

        other as LeftMenuItemViewModel
        return (this.mainDBItem.id == other.mainDBItem.id) and (this.mainDBItem.phase == other.mainDBItem.phase)
    }

    override fun isSameContent(other: Any): Boolean {
        other as LeftMenuItemViewModel
        return this.mainDBItem == other.mainDBItem
    }

    fun onClick() {
        clickHandler?.onItemClick(mainDBItem)
    }

    fun toRecyclerItem() = RecyclerItem(
        data = this,
        layoutId = R.layout.item_left_menu,
        variableId = BR.leftMenuItemViewModel
    )
}

interface OnClickByLeftMenuItem {
    fun onItemClick(mainDBItem: MainDBItem)
}