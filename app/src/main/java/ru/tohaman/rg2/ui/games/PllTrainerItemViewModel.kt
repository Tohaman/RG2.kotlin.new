package ru.tohaman.rg2.ui.games

import ru.tohaman.rg2.BR
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.adapters.universal.RecyclerItemComparator
import ru.tohaman.rg2.dataSource.entitys.RecyclerItem
import ru.tohaman.rg2.dbase.entitys.PllGameItem
import timber.log.Timber

class PllTrainerItemViewModel(val pllGameItem: PllGameItem): RecyclerItemComparator, OnClick {

    override fun isSameItem(other: Any): Boolean {
        if (this === other) return true
        if (javaClass != other.javaClass) return false

        other as PllTrainerItemViewModel
        return this.pllGameItem.id == other.pllGameItem.id
    }

    override fun isSameContent(other: Any): Boolean {
        other as PllTrainerItemViewModel
        val isSame = ((this.pllGameItem.internationalName == other.pllGameItem.internationalName) and
                (this.pllGameItem.currentName == other.pllGameItem.currentName) and
                (this.pllGameItem.isChecked == other.pllGameItem.isChecked))
        Timber.d("$TAG .isSameContent $isSame = other = [${other.pllGameItem.currentName}] and this = [${this.pllGameItem.currentName}]")
        return isSame

    }

    override fun onClick() {
        Timber.d("$TAG .onClick ")
    }

    fun onCheckChange(value: Boolean) {
        Timber.d("$TAG .onCheckChange value = [${value}], ${pllGameItem.id}")
    }

    fun toRecyclerItem() = RecyclerItem(
        data = this,
        layoutId = R.layout.item_algorithms_properties,
        variableId = BR.pllTrainerItemViewModel
    )
}

interface OnClick {
    fun onClick()
}