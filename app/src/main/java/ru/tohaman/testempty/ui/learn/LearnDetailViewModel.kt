package ru.tohaman.testempty.ui.learn

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.entitys.BasicMove
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.utils.getPhasesToTypesMap
import timber.log.Timber

class LearnDetailViewModel(context: Context) : ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()
    private val ctx = context
    var currentId = 0

    private var currentItems: MutableList<MainDBItem> = mutableListOf()
    private var _currentItems : MutableLiveData<List<MainDBItem>> = MutableLiveData()
    val liveCurrentItems: LiveData<List<MainDBItem>>
        get() = _currentItems

    private var phasesToTypes: MutableMap<String, String> = mutableMapOf()
    private var cubeTypes : List<BasicMove> = listOf()
    private var mutableCubeTypes : MutableLiveData<List<BasicMove>> = MutableLiveData()
    val liveDataCubeTypes: LiveData<List<BasicMove>> get() = mutableCubeTypes

    private var favouritesList: MutableList<MainDBItem> = mutableListOf()
    private var mutableFavouritesList: MutableLiveData<List<MainDBItem>> = MutableLiveData()
    val liveDataFavouritesList: LiveData<List<MainDBItem>> get() = mutableFavouritesList

    init {
        getFavourite()
    }

    fun setCurrentItems (phase: String, id: Int) {
        Timber.d("$TAG setCurrentItems start")
        _currentItems = MutableLiveData()
        viewModelScope.launch (Dispatchers.IO)  {
            //runBlocking (Dispatchers.IO) {
            currentItems = repository.getDetailsItems(phase).toMutableList() //}
            _currentItems.postValue(currentItems)
            currentId = getNumByID(id)
            Timber.d("$TAG curItem Initiated with Id=$currentId count=${currentItems.size} items=$currentItems")
        }
    }

    fun getNumByID(id: Int): Int {
        return currentItems.indexOf(currentItems.first { it.id == id })
    }

    fun updateComment(item: MainDBItem) {
        viewModelScope.launch (Dispatchers.IO)  {
            repository.updateMainItem(item)
        }
    }

    fun changeItemFavouriteStatus(item: MainDBItem) {
        Timber.d( "$TAG favouriteChange for - $item")
        viewModelScope.launch (Dispatchers.IO)  {
            item.isFavourite = !item.isFavourite
            val list = repository.getFavourites().toMutableList()
            if (item.isFavourite) { //если  надо добавит в избранное
                item.subId = list.size
            } else {
                list.removeAt(item.subId)
                list.indices.map { i -> list[i].subId = i }
                item.subId = 0
            }
            list.add(item)                      //добавляем запись в конец списка
            repository.updateMainItem(list)     //и обновляем избранное в базе
            currentItems[item.id] = item        //обновляем запись в текущем списке items
            _currentItems.postValue(currentItems)   //и постим в лайвдату, чтобы обновилось в презентере
            getFavourite()
        }
    }

    fun getFavourite() {
        viewModelScope.launch (Dispatchers.IO)  {
            phasesToTypes = getPhasesToTypesMap(ctx).toMutableMap()
            favouritesList = repository.getFavourites().toMutableList()
            mutableFavouritesList.postValue(favouritesList)
        }
    }

    fun setMoveTypeItems(phase: String) {
        val type = phasesToTypes.getOrElse(phase) {"BASIC3X3"}
        viewModelScope.launch (Dispatchers.IO){
            cubeTypes = repository.getTypeItems(type)
            mutableCubeTypes.postValue(cubeTypes)
        }
    }

    fun onFavouriteSwapPosition(fromPosition: Int, toPosition: Int) {
        Timber.d( "$TAG swap from $fromPosition. to $toPosition")
        favouritesList[fromPosition].subId = toPosition
        favouritesList[toPosition].subId = fromPosition
        //mutableFavouritesList.postValue(favouritesList)
    }


}