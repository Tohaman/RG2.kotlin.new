package ru.tohaman.testempty.ui.mainMenu

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.MainDBItem
import ru.tohaman.testempty.dbase.PhaseItem
import timber.log.Timber


class MainViewModel(app: Application) : AndroidViewModel (app) {
    private val repository = ItemsRepository()
    var curPhase = "BEGIN"
    var curItem = MutableLiveData<String>()
    var mainMenuItems = repository.getCurrentPhase()

    var mutableMainMenuItems : MutableLiveData<List<MainDBItem>> = MutableLiveData()
    var allItems = repository.getAllLiveDataItems()

    init {
        curItem.value = "000000"
        //val tmp = repository.getAllItems()
        //val tmp2 = allItems.value
        //mutableMainMenuItems.value = tmp2
        getCurrentPhase()
    }

    fun getCurItem() : LiveData<String> {return curItem}

    fun getCurrentPhase() {
        viewModelScope.launch() {
            val list = repository.getPhaseFromMain(curPhase)
            mutableMainMenuItems.postValue(list)
        }
    }

    fun onMainMenuItemClick(menuItem: MainDBItem) {
        Timber.d( "ViewModel.onMainMenuItemClick - $menuItem")
        curPhase = "MAIN3X3"
        getCurrentPhase()
        //repository.changePhase(curPhase)
        //ldMainMenuItems =
        //allItems = repository.observePhase(curPhase)
        //repository.insert(MainDBItem("BEGIN",13,"37218368"))
        //mainMenuItems.value = repository.updateMenu(curPhase)
    }

    fun onSomeButtonClick() {
        Timber.d( "onSomeButtonClick")
        curPhase = "AXIS"
        getCurrentPhase()
        //allItems = repository.observePhase("MAIN3X3")
    }

}