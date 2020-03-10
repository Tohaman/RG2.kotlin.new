package ru.tohaman.testempty.ui.learn

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.utils.toMutableLiveData

class LearnDetailViewModel(context: Context) : ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()
    private val ctx = context
    private var currentID = 0
    val mutableCurrentPhase = MutableLiveData<String>()
    private var currentItems: List<MainDBItem> = listOf()
    private var mutableCurrentItems : MutableLiveData<List<MainDBItem>> = currentItems.toMutableLiveData()


    //private var phaseItems : MutableLiveData<List<MainDBItem>> =

    fun setCurrentItems (id: Int, phase: String) {
        viewModelScope.launch {
            currentID = id
            currentItems = repository.getDetailsItems(phase)
            mutableCurrentPhase.postValue(phase)
        }
    }

}