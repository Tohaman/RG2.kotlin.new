package ru.tohaman.testempty.viewModel

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Config
import androidx.paging.toLiveData
import ru.tohaman.testempty.dbase.ItemsRepository
import ru.tohaman.testempty.dbase.ListPagerDBItem
import ru.tohaman.testempty.dbase.MainDb
import ru.tohaman.testempty.utils.ioThread


class MainViewModel(app: Application) : AndroidViewModel (app) {
    private val repository = ItemsRepository(app)
    var curPhase = "BEGIN"
    var curItem = MutableLiveData<String>()

    init {
        curItem.value = "000000"
    }

    fun getCurItem() : LiveData<String> {return curItem}

    val mainMenuItems = repository.getPhaseItems(curPhase)

    val allItems = repository.getAllItems()

    fun onMainMenuItemClick(index: Int) {
        //curItem.value = index.toString()
        Log.d("DEB", "Index - $index")
    }

    fun someClick(index: View) {
        Log.d("DEB", "Index")
    }


}