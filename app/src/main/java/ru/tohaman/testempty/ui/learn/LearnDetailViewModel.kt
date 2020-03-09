package ru.tohaman.testempty.ui.learn

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.entitys.MainDBItem

class LearnDetailViewModel(context: Context) : ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()
    private val ctx = context
    private var currentID = 0
    private var currentItems: List<MainDBItem> = listOf()

    //private var phaseItems : MutableLiveData<List<MainDBItem>> =

    fun setCurrentItem (id: Int, phase: String) {
        viewModelScope.launch {
            currentID = id
            currentItems = repository.getPhaseFromMain(phase)
        }
    }

}