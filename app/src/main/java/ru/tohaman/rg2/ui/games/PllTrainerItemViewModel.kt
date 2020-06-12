package ru.tohaman.rg2.ui.games

import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.adapters.universal.RecyclerItemComparator
import ru.tohaman.rg2.dbase.entitys.PllGameItem
import timber.log.Timber

class PllTrainerItemViewModel(val pllGameItem: PllGameItem): RecyclerItemComparator {

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
        Timber.d("$TAG .onClick ")
    }

    fun onCheckChange(value: Boolean) {
        Timber.d("$TAG .onCheckChange value = [${value}], ${pllGameItem.id}")
    }


}