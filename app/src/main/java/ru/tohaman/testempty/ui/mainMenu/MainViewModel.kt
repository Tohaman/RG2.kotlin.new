package ru.tohaman.testempty.ui.mainMenu

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.MainDBItem
import ru.tohaman.testempty.utils.DebugTag.TAG
import timber.log.Timber


class MainViewModel(app: Application) : AndroidViewModel (app) {
    private val repository = ItemsRepository()
    private val ctx = app.applicationContext
    var curPhase = "MAIN3X3"
    var curItem = MutableLiveData<String>()
    var mainMenuItems = repository.getCurrentPhase()

    var mutableMainMenuItems : MutableLiveData<List<MainDBItem>> = MutableLiveData()
    var allItems = repository.getAllLiveDataItems()

    init {
        getCurrentPhase()
    }

    fun getCurrentPhase() {
        viewModelScope.launch() {
            val list = repository.getPhaseFromMain(curPhase)
            curItem.postValue(curPhase)
            mutableMainMenuItems.postValue(list)
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