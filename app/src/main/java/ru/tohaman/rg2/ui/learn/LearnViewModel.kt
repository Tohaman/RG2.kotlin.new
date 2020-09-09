package ru.tohaman.rg2.ui.learn

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.rg2.AppSettings
import ru.tohaman.rg2.Constants.FAVOURITES
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.dataSource.ItemsRepository
import ru.tohaman.rg2.dbase.entitys.CubeType
import ru.tohaman.rg2.dbase.entitys.MainDBItem
import timber.log.Timber


//Наследуемся и от KoinComponent чтобы был доступ к inject (у Activity, Fragment, Service он есть и без этого)
class LearnViewModel(context: Context) : ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()
    private val ctx = context

    private var typesCount = 10

    //номер закладки открываемой по-умолчанию
    private var _currentCubeType = AppSettings.currentCubeType
    private val currentCubeTypeMLD = MutableLiveData<Int>(_currentCubeType)
    val currentCubeType: LiveData<Int> get() = currentCubeTypeMLD

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

    //При лонгклике храним элемент по которому кликнули
    var selectedItem: MainDBItem = MainDBItem("WRONG", 0)

    //Нужно ли отображать зеленую кнопку FAB
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
            val showFab = AppSettings.showFAB
            needShowFab.set(showFab)
            cubeTypes.map {
                var list = getPhaseFromRepository(it.curPhase)
                if (it.curPhase != it.initPhase) {
                    list = addBackItem(list, it.initPhase)
                }
                //Заменяем пустой MediatorLiveData() на значение из базы
                mainDBItemsMediatorArray[it.id].postValue(list)
            }
        }
    }

    private fun addBackItem(list: List<MainDBItem>, initPhase: String): List<MainDBItem> {
        Timber.d("$TAG .addBackItem list = [${list}], initPhase = [${initPhase}]")
        val res = R.drawable.ic_arrow_up
        val backPhase = if (initPhase == FAVOURITES) initPhase else backFrom[list[0].phase] ?: initPhase
        val backItem = MainDBItem(backPhase,0, "...", res, 0, "submenu")
        val newList = list.toMutableList()
        newList.add(0, backItem)
        return newList
    }

    fun getCurrentType(): Int {
        Timber.d("$TAG .getCurrentType $_currentCubeType")
        return _currentCubeType
    }

    private fun initCubeTypes() {
            runBlocking (Dispatchers.IO) { cubeTypes = repository.getCubeTypes() }

            typesCount = cubeTypes.size
            mutableCubeTypes.postValue(cubeTypes)
    }

    fun setCurrentCubeType(id: Int) {
        _currentCubeType = id
        AppSettings.currentCubeType = id
    }

    fun getPhaseNameById(id: Int): String {
        return if (id <= cubeTypes.size) cubeTypes[id].curPhase else ""
    }

    //возвращаем true если вернулись на одну фазу назад или false если и так в главной фазе
    fun canReturnToOnePhaseBack() : Boolean {
        val fromPhase = cubeTypes[_currentCubeType].curPhase
        val defaultPhase = cubeTypes[_currentCubeType].initPhase
        var toPhase = backFrom.getOrElse(fromPhase, {defaultPhase})
        if (defaultPhase == FAVOURITES) toPhase = FAVOURITES
        Timber.d( "$TAG backOnePhase Page - $_currentCubeType, fromPhase - $fromPhase, toPhase - $toPhase, $cubeTypes")
        return if (fromPhase != toPhase) {
                changePhaseTo(toPhase)
            true
        } else false
    }

    //Получаем номер закладки из cubeTypes на которой находится головоломка (phase)
    fun changeTypeAndPhase(phase: String) {
        //Сначала по фазу определяем ее основную, идем вверх по backFrom, пока не получим в ответ null. Для основных в backFrom нет записи
        var mainPhase = phase
        while ( backFrom[mainPhase] != null) {
            mainPhase = backFrom[mainPhase]!!
        }

        //по названию основной фазы определяем номер, если такой основной нет, то считаем, что это "BEGIN3X3" и соответственно номер 2
        var type = 2
        cubeTypes.forEachIndexed { index, cubeType ->
            if (mainPhase == cubeType.initPhase) type = index
        }

        Timber.d("$TAG .getTypeFromPhase phase = [${phase}] -> mainPhase = $mainPhase, Type = $type")
        _currentCubeType = type
        currentCubeTypeMLD.postValue(_currentCubeType)
        changePhaseTo(phase)
    }

    private fun changePhaseTo(phase: String) {
        cubeTypes[_currentCubeType].curPhase = phase
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
        Timber.d("$TAG updateCurrentPhasesToArray curTypes = ${cubeTypes[_currentCubeType]}")
        viewModelScope.launch (Dispatchers.IO){
            cubeTypes.map {
                var list = getPhaseFromRepository(it.curPhase)
                if (it.curPhase != it.initPhase) {
                    list = addBackItem(list, it.initPhase)
                }
                mainDBItemsMediatorArray[it.id].postValue(list)
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
        val phase = if (menuItem.description != 0) ctx.getString(menuItem.description) else menuItem.phase
        Timber.d( "$TAG Selected_Page $_currentCubeType - setToPhase - $phase")
        changePhaseTo(phase)
    }

    //Добавляем или убираем из избранного в зависимости от menuItem.isFavourite
    //
    fun onFavouriteChangeClick(menuItem: MainDBItem, position: Int = menuItem.subId) {
        viewModelScope.launch (Dispatchers.IO) {
            Timber.d( "$TAG favouriteChange for - $menuItem")
            val list = repository.getFavourites().toMutableList()
            menuItem.isFavourite = !menuItem.isFavourite
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
        viewModelScope.launch (Dispatchers.IO) {
            val subMenusList = repository.getSubMenuList()
            subMenusList.map {it ->
                backFrom.put(ctx.getString(it.description), it.phase)
            }
        }
    }

}