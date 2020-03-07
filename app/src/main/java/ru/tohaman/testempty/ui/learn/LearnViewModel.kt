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
    var curPhase = "MAIN3X3"
    var curItem = MutableLiveData<String>()

    var mutableMainMenuItems : MutableLiveData<List<MainDBItem>> = MutableLiveData()

    //Список
    private var mainDBItemsMediatorArray = Array<MediatorLiveData<List<MainDBItem>>>(typesCount) { MediatorLiveData() }
    val mainDBItemLiveArray
        get() = Array<LiveData<List<MainDBItem>>>(typesCount) { mainDBItemsMediatorArray[it]}


    private var cubeTypes : List<CubeType> = listOf()
    var mutableCubeTypes : MutableLiveData<List<CubeType>> = cubeTypes.toMutableLiveData()

    init {
        //Получаем список основных типов головоломок из базы
        updateCubeTypes()
        //Обновляем размер списка в зависимости от числа типов головоломок
        mainDBItemsMediatorArray = Array<MediatorLiveData<List<MainDBItem>>>(typesCount) { MediatorLiveData() }
        //Для каждого типа подгружаем текущую фазу
        updateCurrentPhasesToArray()
    }

    private fun updateCubeTypes() {
        runBlocking {
            cubeTypes = repository.getCubeTypes()
            typesCount = cubeTypes.size
            mutableCubeTypes.postValue(cubeTypes)
        }
    }

    fun updateCurrentPhasesToArray() {
        cubeTypes.map {
            runBlocking {
                val list = repository.getLivePhaseFromMain(it.curPhase)
                //Заменяем пустой MediatorLiveData() на значение из базы
                mainDBItemsMediatorArray[it.id].addSource(list, mainDBItemsMediatorArray[it.id]::setValue)
                //Timber.tag(TAG).d("Add ${it.curPhase} - ${list.value?.size}")
            }
        }
    }

    fun onMainMenuItemClick(menuItem: MainDBItem) {
        Timber.tag(TAG).d( "ViewModel.onMainMenuItemClick - $menuItem")
        curPhase = ctx.getString(menuItem.description)
    }

    fun getCubeTypeById (id: Int) : MutableLiveData<CubeType> {
        return mutableCubeTypes.value?.get(id)?.toMutableLiveData() ?: MutableLiveData()
    }

    fun onSomeButtonClick() {
        Timber.tag(TAG).d( "onSomeButtonClick - нажали кнопку для проверки какого-то действия")
        curPhase = "MAIN3X3"
    }

}