package ru.tohaman.testempty.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.tohaman.testempty.dbase.ItemsRepository
import ru.tohaman.testempty.dbase.MainDBItem
import ru.tohaman.testempty.dbase.PhaseItem


class MainViewModel(app: Application) : AndroidViewModel (app) {
    private val repository = ItemsRepository(app)
    var curPhase = "BEGIN"
    var curItem = MutableLiveData<String>()
    var mainMenuItems = repository.getCurrentPhase()

    init {
        curItem.value = "000000"

    }

    fun getCurItem() : LiveData<String> {return curItem}

    internal val allItems = repository.getAllItems()

    fun onMainMenuItemClick(menuItem: PhaseItem) {
        Log.d("DEB", "ViewModel.onMainMenuItemClick - $menuItem")
        curPhase = "MAIN3X3"
        repository.changePhase(curPhase)
        //repository.insert(MainDBItem("BEGIN",13,"37218368"))
        //mainMenuItems.value = repository.updateMenu(curPhase)

    }

}