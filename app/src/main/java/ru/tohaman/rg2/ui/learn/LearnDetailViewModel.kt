package ru.tohaman.rg2.ui.learn

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.rg2.Constants
import ru.tohaman.rg2.Constants.ALL_INTERNET
import ru.tohaman.rg2.Constants.NOT_USE_INTERNET
import ru.tohaman.rg2.Constants.ONLY_WIFI
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.dataSource.ItemsRepository
import ru.tohaman.rg2.dataSource.entitys.RecyclerItem
import ru.tohaman.rg2.dbase.entitys.BasicMove
import ru.tohaman.rg2.dbase.entitys.MainDBItem
import ru.tohaman.rg2.dbase.entitys.PllGameItem
import ru.tohaman.rg2.ui.games.PllTrainerItemViewModel
import ru.tohaman.rg2.ui.learn.list_items.LeftMenuItemViewModel
import ru.tohaman.rg2.ui.learn.list_items.OnClickByLeftMenuItem
import ru.tohaman.rg2.utils.SingleLiveEvent
import ru.tohaman.rg2.utils.getConnectionType
import ru.tohaman.rg2.utils.getPhasesToTypesMap
import timber.log.Timber

class LearnDetailViewModel(context: Context) : ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()
    private val sp: SharedPreferences by inject()
    private val ctx = context
    var currentId = 0

    private var currentItems: MutableList<MainDBItem> = mutableListOf()
    private var _currentItems: MutableLiveData<List<MainDBItem>> = MutableLiveData()
    val liveCurrentItems: LiveData<List<MainDBItem>>
        get() = _currentItems

    private var currentLeftMenuItems = mutableListOf<RecyclerItem>()
    private val _currentLeftMenuItems = MutableLiveData<List<RecyclerItem>>()
    val liveCurrentLeftMenuItems : LiveData<List<RecyclerItem>>
        get() = _currentLeftMenuItems

    private var phasesToTypes: MutableMap<String, String> = mutableMapOf()
    private var cubeTypes: List<BasicMove> = listOf()
    private var mutableCubeTypes: MutableLiveData<List<BasicMove>> = MutableLiveData()
    val liveDataCubeTypes: LiveData<List<BasicMove>> get() = mutableCubeTypes

    private var favouritesList: MutableList<MainDBItem> = mutableListOf()
    private val mutableFavouritesList: MutableLiveData<List<MainDBItem>> = MutableLiveData()
    val liveDataFavouritesList: LiveData<List<MainDBItem>> get() = mutableFavouritesList

    var isTextSelectable = sp.getBoolean(Constants.IS_TEXT_SELECTABLE, false)
    private var internetLimits = 0

    private val _isLeftMenuOpen = MutableLiveData<Boolean>()
    val isLeftMenuOpen: LiveData<Boolean> get() = _isLeftMenuOpen

    init {
        Timber.d("$TAG Инициализируем LearnDetailViewModel")
        getFavourite()
    }

    fun setCurrentItems (phase: String, id: Int) {
        Timber.d("$TAG setCurrentItems start")
        internetLimits = getInternetLimits()
        _currentItems = MutableLiveData()
        viewModelScope.launch (Dispatchers.IO)  {
            currentItems = repository.getDetailsItems(phase).toMutableList() //}
            _currentItems.postValue(currentItems)
            currentId = getNumByID(id)
            updateLeftMenuAdapter(currentItems)
            isTextSelectable = sp.getBoolean(Constants.IS_TEXT_SELECTABLE, false)
            //Timber.d("$TAG curItem Initiated with Id=$currentId count=${currentItems.size} items=$currentItems")
        }
    }

    //Возвращает номер фрагмента по id этапа
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

    val onLeftMenuItemPressing  = SingleLiveEvent<Nothing>()

    private fun onClickByLeftMenuItem(): OnClickByLeftMenuItem {
        return object : OnClickByLeftMenuItem {
            override fun onItemClick(mainDBItem: MainDBItem) {
                Timber.d("$TAG .onItemClick mainDBItem = [${mainDBItem}]")
                closeLeftMenu()
                currentId = getNumByID(mainDBItem.id)
                onLeftMenuItemPressing.call()
            }
        }
    }

    private fun updateLeftMenuAdapter (items: List<MainDBItem>) {
        currentLeftMenuItems = items
            .map { LeftMenuItemViewModel(it) }
            .onEach { it.clickHandler = onClickByLeftMenuItem() }
            .map { it.toRecyclerItem() }
            .toMutableList()
        _currentLeftMenuItems.postValue(currentLeftMenuItems)
    }

    fun isYouTubePlayerEnabled(fragmentNum: Int): Boolean {
        val url = currentItems[fragmentNum].url
        val enabled = (getConnectionType(ctx) > internetLimits) and (url != "")
        Timber.d("$TAG .isYouTubePlayerEnabled enabled = [${enabled}]")
        return enabled
    }

    //Получаем текущий лимит на использование интернета установленный в настройках, 0 - любой, 1 - WiFi, интернет недоступен - 4
    //потом будем сравнивать с текущим доступным в системе 0 - недоступен, 1 - 3G/4G, 2 - WiFi, 3 - VPN
    private fun getInternetLimits(): Int {
        val allInternet = sp.getBoolean(ALL_INTERNET, true)
        val wiFi = sp.getBoolean(ONLY_WIFI, false)
        return when {
            allInternet -> 0
            wiFi -> 1
            else -> 4
        }
    }

    fun youTubePlayerListener(fragmentNum: Int): AbstractYouTubePlayerListener =
        object : AbstractYouTubePlayerListener() {
        override fun onReady(youTubePlayer: YouTubePlayer) {
            val videoId = currentItems[fragmentNum].url
            youTubePlayer.cueVideo(videoId, 0f)
        }
    }

    fun openLeftMenu() {
        _isLeftMenuOpen.postValue(true)
    }

    fun closeLeftMenu() {
        _isLeftMenuOpen.postValue(false)
    }

}