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
import timber.log.Timber

//Наследуемся и от KoinComponent чтобы был доступ к inject (у Activity, Fragment, Service он есть и без этого)
class LearnViewModel(context: Context) : ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()
    private val ctx = context
    var curPhase = "MAIN3X3"
    var curItem = MutableLiveData<String>()

    var mutableMainMenuItems : MutableLiveData<List<MainDBItem>> = MutableLiveData()
    var cubeTypes : List<CubeType> = listOf()

    init {
        getCurrentPhase()
        updateCubeTypes()
        Timber.tag(TAG).d("LearnViewModel проинициализирован, $cubeTypes")
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
        }
    }

    fun onMainMenuItemClick(menuItem: MainDBItem) {
        Timber.tag(TAG).d( "ViewModel.onMainMenuItemClick - $menuItem")
        curPhase = ctx.getString(menuItem.description)
        getCurrentPhase()
    }

    fun onSomeButtonClick() {
        Timber.tag(TAG).d( "onSomeButtonClick - нажали кнопку для проверки какого-то действия")
        curPhase = "MAIN3X3"
        getCurrentPhase()
    }

}