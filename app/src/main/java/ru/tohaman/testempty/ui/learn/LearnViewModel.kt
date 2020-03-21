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
    private var mainDBItemsMediatorArray = Array<MutableLiveData<List<MainDBItem>>>(typesCount) { MutableLiveData() }
    val mainDBItemLiveArray
        get() = Array<LiveData<List<MainDBItem>>>(typesCount) { mainDBItemsMediatorArray[it]}

    private var cubeTypes : List<CubeType> = listOf()
    private var mutableCubeTypes : MutableLiveData<List<CubeType>> = MutableLiveData()
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
        mainDBItemsMediatorArray = Array<MutableLiveData<List<MainDBItem>>>(typesCount) { MutableLiveData() }
        //Для каждого типа подгружаем текущую фазу
        initPhasesToArray()
    }

    private fun initPhasesToArray() {
        Timber.d("$TAG initPhasesToArray")
        viewModelScope.launch (Dispatchers.IO) {
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
    }

    fun getPhaseNameById(id: Int): String {
        return if (id <= cubeTypes.size) cubeTypes[id].curPhase else ""
    }

    private fun getLivePhaseFromRepository(phase: String): LiveData<List<MainDBItem>> {
        return if (phase != FAVOURITES) {
            repository.getLivePhaseFromMain(phase)
        } else {
            repository.getLiveFavourites()
        }
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
            repository.getFavourites()
        }
    }

    fun onMainMenuItemClick(menuItem: MainDBItem) {
        val phase = ctx.getString(menuItem.description)
        Timber.d( "$TAG Selected_Page $currentCubeType - setToPhase - $phase")
        changePhaseTo(phase)
    }

    fun onFavouriteChangeClick(menuItem: MainDBItem) {
        Timber.d( "$TAG favouriteChange for - $menuItem")
        Timber.d( "$TAG favouriteChange curPhase - ${cubeTypes[currentCubeType].curPhase}")
        viewModelScope.launch (Dispatchers.IO) {
            menuItem.isFavourite = !menuItem.isFavourite
            repository.updateMainItem(menuItem)
            updateCurrentPhasesToArray()
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