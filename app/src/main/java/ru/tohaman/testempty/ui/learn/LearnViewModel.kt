package ru.tohaman.testempty.ui.learn

import android.content.Context
import android.content.SharedPreferences
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import ru.tohaman.testempty.Constants
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.entitys.CubeType
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.utils.toMutableLiveData
import timber.log.Timber
import java.text.ParsePosition


//Наследуемся и от KoinComponent чтобы был доступ к inject (у Activity, Fragment, Service он есть и без этого)
class LearnViewModel(context: Context) : ViewModel(), KoinComponent {
    private val sp = get<SharedPreferences>()

    private val FAVOURITES = "FAVOURITES"
    private val CUR_CUBE_TYPE = "CUR_CUBE_TYPE"
    private val repository : ItemsRepository by inject()
    private val ctx = context
    private var typesCount = 10
    //номер закладки открываемой по-умолчанию
    private var currentCubeType = get<SharedPreferences>().getInt(CUR_CUBE_TYPE, 2)
    private var backFrom : HashMap<String, String> = hashMapOf()    //map для получения предыдущей фазы, по ее названию через map.getOrDefault()

    //Массив из MediatorLiveData, содержащих списки записей определенной фазы
    private var mainDBItemsMediatorArray = Array<MutableLiveData<List<MainDBItem>>>(typesCount) { MutableLiveData() }
    val mainDBItemLiveArray
        get() = Array<LiveData<List<MainDBItem>>>(typesCount) { mainDBItemsMediatorArray[it]}

    private var cubeTypes : List<CubeType> = listOf()
    private var mutableCubeTypes : MutableLiveData<List<CubeType>> = MutableLiveData()
    //Подписываться будем на liveData, чтобы из view не было возможности поменять содержимое переменной, только чтение
    //для этого преобразуем MutableLiveData в LiveData
    val liveDataCubeTypes : LiveData<List<CubeType>>
        get() = mutableCubeTypes


    var needShowFab = ObservableBoolean()

    init {
        //Получаем список MainDBItem в котором в getString(description) имя вызываемой фазы, а в name - из какой фазы она вызвается
        getSubMenuList()
        //Получаем список основных типов головоломок из базы
        initCubeTypes()
        //Обновляем размер списка в зависимости от числа типов головоломок
        mainDBItemsMediatorArray = Array<MutableLiveData<List<MainDBItem>>>(typesCount) { MutableLiveData() }
        //Для каждого типа подгружаем текущую фазу
        initPhasesToArray()
    }

    fun initPhasesToArray() {
        Timber.d("$TAG initPhasesToArray")
        viewModelScope.launch (Dispatchers.IO) {
            val showFab = sp.getBoolean(Constants.SHOW_FAB, true)
            needShowFab.set(showFab)
            cubeTypes.map {
                val list = getPhaseFromRepository(it.curPhase)
                //Заменяем пустой MediatorLiveData() на значение из базы
                mainDBItemsMediatorArray[it.id].postValue(list)
            }
        }
    }

    fun getCurrentType(): Int = currentCubeType

    private fun initCubeTypes() {
            runBlocking (Dispatchers.IO) { cubeTypes = repository.getCubeTypes() }

            typesCount = cubeTypes.size
            mutableCubeTypes.postValue(cubeTypes)
    }

    fun setCurrentCubeType(id: Int) {
        currentCubeType = id
        get<SharedPreferences>().edit().putInt(CUR_CUBE_TYPE, id).apply()
    }

    fun getPhaseNameById(id: Int): String {
        return if (id <= cubeTypes.size) cubeTypes[id].curPhase else ""
    }

    //возвращаем true если вернулись на одну фазу назад или false если и так в главной фазе
    fun canReturnToOnePhaseBack() : Boolean {
        val fromPhase = cubeTypes[currentCubeType].curPhase
        val defaultPhase = cubeTypes[currentCubeType].initPhase
        var toPhase = backFrom.getOrElse(fromPhase, {defaultPhase})
        if (defaultPhase == FAVOURITES) toPhase = FAVOURITES
        Timber.d( "$TAG backOnePhase Page - $currentCubeType, fromPhase - $fromPhase, toPhase - $toPhase, $cubeTypes")
        return if (fromPhase != toPhase) {
                changePhaseTo(toPhase)
            true
        } else false
    }

    private fun changePhaseTo(phase: String) {
        cubeTypes[currentCubeType].curPhase = phase
        saveCubeTypes()
        updateCurrentPhasesToArray()
    }

    private fun saveCubeTypes() {
        Timber.d("$TAG saveCubeTypes $cubeTypes")
        viewModelScope.launch {
            repository.update(cubeTypes)
        }
    }

    //обновляем данные в текущих фазах (перечитываем из репозитория) для основного меню, выполняем в фоне
    private fun updateCurrentPhasesToArray() {
        Timber.d("$TAG updateCurrentPhasesToArray curTypes = ${cubeTypes[currentCubeType]}")
        viewModelScope.launch (Dispatchers.IO){
            cubeTypes.map {
                val listItem = getPhaseFromRepository(it.curPhase)
                mainDBItemsMediatorArray[it.id].postValue(listItem)
            }
        }
    }

    private suspend fun getPhaseFromRepository (phase: String): List<MainDBItem> {
        return if (phase != FAVOURITES) {
            repository.getPhaseFromMain(phase)
        } else {
            val list = repository.getFavourites()
            list.indices.map {i -> list[i].subId = i}
            list
        }
    }

    fun onMainMenuItemClick(menuItem: MainDBItem) {
        val phase = ctx.getString(menuItem.description)
        Timber.d( "$TAG Selected_Page $currentCubeType - setToPhase - $phase")
        changePhaseTo(phase)
    }

    //Добавляем или убираем из избранного в зависимости от menuItem.isFavourite
    //
    fun onFavouriteChangeClick(menuItem: MainDBItem, position: Int = menuItem.subId) {
        Timber.d( "$TAG favouriteChange for - $menuItem")
        viewModelScope.launch (Dispatchers.IO) {
            menuItem.isFavourite = !menuItem.isFavourite
            val list = repository.getFavourites().toMutableList()
            if (menuItem.isFavourite) {             // добавляем в избранное
                list.add(position, menuItem)
                list.indices.map {i -> list[i].subId = i}
            } else {                                // убираем из избранного
                Timber.d( "$TAG removeFAV - $position ${list.size}")
                list.removeAt(position)
                list.indices.map {i -> list[i].subId = i}
                menuItem.subId = 0
                repository.updateMainItem(menuItem)
            }
            runBlocking { repository.updateMainItem(list) }
            updateCurrentPhasesToArray()    //обновляем данные в текущих фазах (перечитываем из репозитория) для основного меню
        }
    }

    //меняем местами две записи в избранном
    fun onFavouriteSwapPosition(fromPosition: Int, toPosition: Int) {
        Timber.d( "$TAG swap from $fromPosition. to $toPosition")
        viewModelScope.launch (Dispatchers.IO) {
            val list = repository.getFavourites().toMutableList()
            list[fromPosition].subId = toPosition
            list[toPosition].subId = fromPosition
            repository.updateMainItem(list)
            updateCurrentPhasesToArray()    //обновляем данные в текущих фазах (перечитываем из репозитория)
        }
    }

    fun onFavouriteItemSwipe(item: MainDBItem, position: Int) {
        Timber.d("$TAG удалить из избранного ${item.phase}, ${item.id}")
        onFavouriteChangeClick(item, position)
    }

    fun onFavouriteItemUndoSwipe(item: MainDBItem, position: Int) {
        Timber.d("$TAG вернуть в избранное ${item.phase}, ${item.id}")
        onFavouriteChangeClick(item, position)
    }

    private fun getSubMenuList () {
        viewModelScope.launch {
            val subMenusList = repository.getSubMenuList()
            subMenusList.map {it ->
                backFrom.put(ctx.getString(it.description), it.phase)
            }
        }
    }

}