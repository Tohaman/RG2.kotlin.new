package ru.tohaman.testempty.ui.learn

import android.content.Context
import androidx.lifecycle.*
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
    private val repository : ItemsRepository by inject()
    private val ctx = context
    private var typesCount = 0
    //номер закладки открываемой по-умолчанию
    private var currentCubeType = 1
    private var backFrom : HashMap<String, String> = hashMapOf()    //map для получения предыдущей фазы, по ее названию через map.getOrDefault()

    //Массив из MwdiatorLiveData, содержащих списки записей определенной фазы
    private var mainDBItemsMediatorArray = Array<MediatorLiveData<List<MainDBItem>>>(typesCount) { MediatorLiveData() }
    val mainDBItemLiveArray
        get() = Array<LiveData<List<MainDBItem>>>(typesCount) { mainDBItemsMediatorArray[it]}

    private var cubeTypes : List<CubeType> = listOf()
    private var mutableCubeTypes : MutableLiveData<List<CubeType>> = cubeTypes.toMutableLiveData()
    //Подписываться будем на liveData, чтобы из view не было возможности поменять содержимое переменной, только чтение
    var liveDataCubeTypes : LiveData<List<CubeType>> = mutableCubeTypes

    init {
        //Получаем список MainDBItem в котором в getString(description) имя вызываемой фазы, а в name - из какой фазы она вызвается
        getSubMenuList()
        //Получаем список основных типов головоломок из базы
        updateCubeTypes()
        //Обновляем размер списка в зависимости от числа типов головоломок
        mainDBItemsMediatorArray = Array<MediatorLiveData<List<MainDBItem>>>(typesCount) { MediatorLiveData() }
        //Для каждого типа подгружаем текущую фазу
        updateCurrentPhasesToArray()
    }

    fun getCurrentType(): Int = currentCubeType

    fun getCubeTypeById (id: Int) : LiveData<CubeType> {
        return mutableCubeTypes.value!![id].toMutableLiveData()
    }

    private fun updateCubeTypes() {
        runBlocking {
            cubeTypes = repository.getCubeTypes()
            typesCount = cubeTypes.size
            mutableCubeTypes.postValue(cubeTypes)
        }
    }

    private fun saveCubeTypes() {
        viewModelScope.launch {
            repository.update(cubeTypes)
            mutableCubeTypes.postValue(cubeTypes)
        }
    }

    fun setCurrentCubeType(id : Int) {
        currentCubeType = id
    }

    fun updateCurrentPhasesToArray() {
        viewModelScope.launch {
            cubeTypes.map {
                val list = repository.getLivePhaseFromMain(it.curPhase)
                //Заменяем пустой MediatorLiveData() на значение из базы
                mainDBItemsMediatorArray[it.id].addSource(list, mainDBItemsMediatorArray[it.id]::setValue)
            }
        }
    }

    fun backOnePhase() {
        val fromPhase = cubeTypes[currentCubeType].curPhase
        val defaultPhase = cubeTypes[currentCubeType].initPhase
        val toPhase = backFrom.getOrElse(fromPhase, {defaultPhase})
        Timber.d( "$TAG backOnePhase Page - $currentCubeType, fromPhase - $fromPhase, toPhase - $toPhase")
        if (fromPhase != toPhase) {
            cubeTypes[currentCubeType].curPhase = toPhase
            saveCubeTypes()
            updateCurrentPhasesToArray()
        }
    }

    fun onMainMenuItemClick(menuItem: MainDBItem) {
        Timber.d( "$TAG Selected_Page $currentCubeType - ViewModel.onMainMenuItemClick - $menuItem")
        cubeTypes[currentCubeType].curPhase = ctx.getString(menuItem.description)
        saveCubeTypes()
        updateCurrentPhasesToArray()
    }

    fun onSomeButtonClick() {
        Timber.d( "$TAG onSomeButtonClick - нажали кнопку для проверки какого-то действия")
        cubeTypes[currentCubeType].curPhase = cubeTypes[currentCubeType].initPhase
        saveCubeTypes()
        updateCurrentPhasesToArray()
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