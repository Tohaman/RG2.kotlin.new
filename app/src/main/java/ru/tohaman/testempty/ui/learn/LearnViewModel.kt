package ru.tohaman.testempty.ui.learn

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.MainDBItem
import ru.tohaman.testempty.DebugTag.TAG
import timber.log.Timber


class LearnViewModel(context: Context) : ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()
    private val ctx = context
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