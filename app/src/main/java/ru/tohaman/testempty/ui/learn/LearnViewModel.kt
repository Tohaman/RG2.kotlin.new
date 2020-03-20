package ru.tohaman.testempty.ui.learn

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.entitys.CubeType
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.utils.toMutableLiveData
import timber.log.Timber


//Наследуемся и от KoinComponent чтобы был доступ к inject (у Activity, Fragment, Service он есть и без этого)
class LearnViewModel(context: Context) : ViewModel(), KoinComponent {
    private val FAVOURITES = "FAVOURITES"
    private val repository : ItemsRepository by inject()
    private val ctx = context
    private var typesCount = 10
    //номер закладки открываемой по-умолчанию
    private var currentCubeType = 2
    private var backFrom : HashMap<String, String> = hashMapOf()    //map для получения предыдущей фазы, по ее названию через map.getOrDefault()

    //Массив из MediatorLiveData, содержащих списки записей определенной фазы
    private var mainDBItemsMediatorArray = Array<MediatorLiveData<List<MainDBItem>>>(typesCount) { MediatorLiveData() }
    val mainDBItemLiveArray
        get() = Array<LiveData<List<MainDBItem>>>(typesCount) { mainDBItemsMediatorArray[it]}

    private var cubeTypes : List<CubeType> = listOf()
    private var mutableCubeTypes : MutableLiveData<List<CubeType>> = cubeTypes.toMutableLiveData()
    //Подписываться будем на liveData, чтобы из view не было возможности поменять содержимое переменной, только чтение
    //для этого преобразуем MutableLiveData в LiveData
    val liveDataCubeTypes : LiveData<List<CubeType>>
        get() = mutableCubeTypes

    init {
        //Получаем список MainDBItem в котором в getString(description) имя вызываемой фазы, а в name - из какой фазы она вызвается
        getSubMenuList()
        //Получаем список основных типов головоломок из базы
        initCubeTypes()
        //Обновляем размер списка в зависимости от числа типов головоломок
        mainDBItemsMediatorArray = Array<MediatorLiveData<List<MainDBItem>>>(typesCount) { MediatorLiveData() }
        //Для каждого типа подгружаем текущую фазу
        updateCurrentPhasesToArray()
    }

    fun getCurrentType(): Int = currentCubeType

    private fun initCubeTypes() {
            runBlocking (Dispatchers.IO) { cubeTypes = repository.getCubeTypes() }
            typesCount = cubeTypes.size
            //Вот так задаем значения, чтобы с одной стороны быстро применилось, с другой
            //уведомило подписчиков об изменении значения
            mutableCubeTypes.value = cubeTypes
            mutableCubeTypes.postValue(cubeTypes)
    }

    private fun saveCubeTypes() {
        viewModelScope.launch {
            repository.update(cubeTypes)
            mutableCubeTypes.postValue(cubeTypes)
        }
    }

    fun setCurrentCubeType(id: Int) {
        currentCubeType = id
    }

    fun getPhaseNameById(id: Int): String {
        return if (id <= cubeTypes.size) cubeTypes[id].curPhase else ""
    }

    private fun updateCurrentPhasesToArray() {
        viewModelScope.launch {
            cubeTypes.map {
                val list = getLivePhaseFromRepository(it.curPhase)
                //Заменяем пустой MediatorLiveData() на значение из базы
                mainDBItemsMediatorArray[it.id].addSource(list, mainDBItemsMediatorArray[it.id]::setValue)
            }
        }
    }

    private fun getLivePhaseFromRepository(phase: String): LiveData<List<MainDBItem>> {
        return if (phase != FAVOURITES) {
            repository.getLivePhaseFromMain(phase)
        } else {
            repository.getLiveFavourites()
        }
    }

    fun updateFavourites() {
        viewModelScope.launch(Dispatchers.IO) {
            cubeTypes.map {
                if (it.curPhase == FAVOURITES) {
                    val favouritesList = repository.getFavourites()
                    mainDBItemsMediatorArray[it.id].postValue(favouritesList)
                }
            }
        }
    }

    //возвращаем true если вернулись на одну фазу назад или false если и так в главной фазе
    fun canOnePhaseBack() : Boolean {
        val fromPhase = cubeTypes[currentCubeType].curPhase
        val defaultPhase = cubeTypes[currentCubeType].initPhase
        var toPhase = backFrom.getOrElse(fromPhase, {defaultPhase})
        if (defaultPhase == FAVOURITES) toPhase = FAVOURITES
        Timber.d( "$TAG backOnePhase Page - $currentCubeType, fromPhase - $fromPhase, toPhase - $toPhase")
        return if (fromPhase != toPhase) {
            cubeTypes[currentCubeType].curPhase = toPhase
            saveCubeTypes()
            updateCurrentPhasesToArray()
            true
        } else false
    }

    fun onMainMenuItemClick(menuItem: MainDBItem) {
        Timber.d( "$TAG Selected_Page $currentCubeType - ViewModel.onMainMenuItemClick - $menuItem")
        cubeTypes[currentCubeType].curPhase = ctx.getString(menuItem.description)
        saveCubeTypes()
        updateCurrentPhasesToArray()
    }

    fun onFavouriteChangeClick(menuItem: MainDBItem) {
        Timber.d( "$TAG favouriteChange for - $menuItem")
        viewModelScope.launch (Dispatchers.IO) {
            menuItem.isFavourite = !menuItem.isFavourite
            repository.updateMainItem(menuItem)
        }
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