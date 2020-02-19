package ru.tohaman.testempty.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.PhaseItem
import ru.tohaman.testempty.utils.DebugTag.TAG
import timber.log.Timber


class MainViewModel(app: Application) : AndroidViewModel (app) {
    private val repository = ItemsRepository()
    var curPhase = "BEGIN"
    var curItem = MutableLiveData<String>()
    var mainMenuItems = repository.getCurrentPhase()

    init {
        curItem.value = "000000"

    }

    fun getCurItem() : LiveData<String> {return curItem}

    internal val allItems = repository.getAllItems()

    fun onMainMenuItemClick(menuItem: PhaseItem) {
        Timber.tag(TAG).d( "ViewModel.onMainMenuItemClick - $menuItem")
        curPhase = "MAIN3X3"
        repository.changePhase(curPhase)
        //repository.insert(MainDBItem("BEGIN",13,"37218368"))
        //mainMenuItems.value = repository.updateMenu(curPhase)

    }

}