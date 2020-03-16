package ru.tohaman.testempty.ui.learn

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.testempty.DebugTag
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.utils.toMutableLiveData
import timber.log.Timber

class LearnDetailViewModel(context: Context) : ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()
    private val ctx = context
    private var currentID = 0
    private var count = 0

    private var currentItems: List<MainDBItem> = listOf()
    private var mutableCurrentItems : MutableLiveData<List<MainDBItem>> = currentItems.toMutableLiveData()
    val liveCurrentItems: LiveData<List<MainDBItem>>
        get() = mutableCurrentItems


    fun setCurrentItems (id: Int, phase: String) {
            currentID = id
            runBlocking (Dispatchers.IO) { currentItems = repository.getDetailsItems(phase)}
            mutableCurrentItems.value = currentItems
            mutableCurrentItems.postValue(currentItems)
            count = currentItems.size
            Timber.d("$TAG curItem Initiated count=$count items=$currentItems")
    }

    fun getItemNum(id: Int): Int {
        currentItems.indices.map { i ->
            if (currentItems[i].id == id) { return i }
        }
        return 0 //Если не нашлось значение (хотя и не должны сюда попадать)
    }

    fun getCurrentItems(): List<MainDBItem> = currentItems

}