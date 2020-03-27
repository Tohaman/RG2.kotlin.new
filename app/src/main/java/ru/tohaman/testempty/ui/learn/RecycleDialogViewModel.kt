package ru.tohaman.testempty.ui.learn

import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.testempty.dataSource.ItemsRepository

class RecycleDialogViewModel: ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()
    var type = "BASIC3X3"



}