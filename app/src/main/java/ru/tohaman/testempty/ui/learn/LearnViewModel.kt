package ru.tohaman.testempty.ui.learn

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dbase.entitys.CubeType
import ru.tohaman.testempty.utils.add
import ru.tohaman.testempty.utils.toMutableLiveData
import timber.log.Timber

//Наследуемся и от KoinComponent чтобы был доступ к inject (у Activity, Fragment, Service он есть и без этого)
class LearnViewModel(context: Context) : ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()
    private val ctx = context
    var curPhase = "MAIN3X3"
    var curItem = MutableLiveData<String>()

    var mutableMainMenuItems : MutableLiveData<List<MainDBItem>> = MutableLiveData()
    private var cubeTypes : List<CubeType> = listOf()
    var mutableCubeTypes : MutableLiveData<List<CubeType>> = cubeTypes.toMutableLiveData()
    private var curPhasesList : List<List<MainDBItem>> = listOf()
    var currentPhasesList : MutableLiveData<List<List<MainDBItem>>> = MutableLiveData()

    init {
        getCurrentPhase()
        updateCubeTypes()
        updateCurrentPhasesList()
    }

    fun getCurrentPhase() {
        viewModelScope.launch() {
            val list = repository.getPhaseFromMain(curPhase)
            curItem.postValue(curPhase)
            mutableMainMenuItems.postValue(list)
        }
    }

    private fun updateCubeTypes() {
        runBlocking {
            cubeTypes = repository.getCubeTypes()
            mutableCubeTypes.postValue(cubeTypes)
        }
    }

    fun updateCurrentPhasesList() {
        curPhasesList = listOf()
        cubeTypes.map {
            runBlocking {
                val list = repository.getPhaseFromMain(it.curPhase)
                curPhasesList.plus(list)
                Timber.tag(TAG).d("Add ${it.curPhase} - ${list.size}")
            }
        }
        currentPhasesList.postValue(curPhasesList)
    }

    fun onMainMenuItemClick(menuItem: MainDBItem) {
        Timber.tag(TAG).d( "ViewModel.onMainMenuItemClick - $menuItem")
        curPhase = ctx.getString(menuItem.description)
        getCurrentPhase()
    }

    fun getFragmentPhase(id: Int) : MutableLiveData<List<MainDBItem>> {
        //val phase = cubeTypes[id].curPhase
        return currentPhasesList.value?.get(id)?.toMutableLiveData() ?: MutableLiveData()
    }


    fun getCubeTypeById (id: Int) : MutableLiveData<CubeType> {
        return mutableCubeTypes.value?.get(id)?.toMutableLiveData() ?: MutableLiveData()
    }

    fun onSomeButtonClick() {
        Timber.tag(TAG).d( "onSomeButtonClick - нажали кнопку для проверки какого-то действия")
        curPhase = "MAIN3X3"
        getCurrentPhase()
    }

}