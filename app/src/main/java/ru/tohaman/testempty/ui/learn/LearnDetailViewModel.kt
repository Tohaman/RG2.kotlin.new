package ru.tohaman.testempty.ui.learn

import android.content.Context
import androidx.lifecycle.*
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
    private var count = 10
    val mutableCurrentPhase = MutableLiveData<String>()

    private var detailsArray = Array<MediatorLiveData<MainDBItem>>(count) { MediatorLiveData() }
    val detailsLiveArray: Array<LiveData<MainDBItem>>
        get() = Array<LiveData<MainDBItem>>(count) {detailsArray[it]}

    private var currentItems: List<MainDBItem> = listOf()
    private var mutableCurrentItems : MutableLiveData<List<MainDBItem>> = currentItems.toMutableLiveData()
    val liveCurrentItems
        get() = mutableCurrentItems


    fun setCurrentItems (id: Int, phase: String) {
        runBlocking {
            currentID = id
            currentItems = repository.getDetailsItems(phase)
            mutableCurrentItems.postValue(currentItems)
            mutableCurrentPhase.postValue(phase)
            count = currentItems.size
            Timber.d("$TAG curItem Initiated $currentItems")
        }
    }

    fun getItemByNum(num: Int): MutableLiveData<MainDBItem> {
        return currentItems[num].toMutableLiveData()
    }

    fun getCurrentItems(): List<MainDBItem> = currentItems

}