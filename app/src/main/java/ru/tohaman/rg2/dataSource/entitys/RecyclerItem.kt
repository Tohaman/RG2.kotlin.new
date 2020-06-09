package ru.tohaman.rg2.dataSource.entitys

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

data class RecyclerItem(
    val data: Any,
    @LayoutRes val layoutId: Int,
    val variableId: Int,
    val callback: Any,
    val callBackId: Int = 0
) {
    fun bind(binding: ViewDataBinding) {
        binding.setVariable(variableId, data)
        binding.setVariable(callBackId, callback)
    }
}