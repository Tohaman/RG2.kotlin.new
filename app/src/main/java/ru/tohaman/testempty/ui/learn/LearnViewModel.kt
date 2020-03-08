package ru.tohaman.testempty.ui.learn

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dbase.entitys.CubeType
import ru.tohaman.testempty.utils.toMutableLiveData
import timber.log.Timber

//Наследуемся и от KoinComponent чтобы был доступ к inject (у Activity, Fragment, Service он есть и без этого)
class LearnViewModel(context: Context) : ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()
    private val ctx = context
    private var typesCount = 0
    private var currentCubeType = 0
    private var backFrom : HashMap<String, String> = hashMapOf()    //map для получения предыдущей фазы, по ее названию через map.getOrDefault()

    //Массив из MwdiatorLiveData, содержащих списки записей определенной фазы
    private var mainDBItemsMediatorArray = Array<MediatorLiveData<List<MainDBItem>>>(typesCount) { MediatorLiveData() }
    val mainDBItemLiveArray
        get() = Array<LiveData<List<MainDBItem>>>(typesCount) { mainDBItemsMediatorArray[it]}

    private var cubeTypes : List<CubeType> = listOf()
    var mutableCubeTypes : MutableLiveData<List<CubeType>> = cubeTypes.toMutableLiveData()

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

    fun getCubeTypeById (id: Int) : MutableLiveData<CubeType> {
        return mutableCubeTypes.value?.get(id)?.toMutableLiveData() ?: MutableLiveData()
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


    fun onMainMenuItemClick(menuItem: MainDBItem) {
        Timber.tag(TAG).d( "Selected_Page ${currentCubeType} - ViewModel.onMainMenuItemClick - $menuItem")
        cubeTypes[currentCubeType].curPhase = ctx.getString(menuItem.description)
        saveCubeTypes()
        updateCurrentPhasesToArray()
    }

    fun onSomeButtonClick() {
        Timber.tag(TAG).d( "onSomeButtonClick - нажали кнопку для проверки какого-то действия")
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

    fun getBackPhase(phase: String) {


//
//        val submenuLP = listPagers
//            .filter {("submenu" == it.url) or ("ollPager" == it.url)
//            }
//        submenuLP
//            .filter { (ctx.getString(it.description) == phase) }
//            .forEach { listPager = it }
//
//        return listPager.phase
    }

}