package ru.tohaman.testempty.ui.games

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.utils.toMutableLiveData

class GamesViewModel: ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()

    private var simpleGamesList: List<MainDBItem> = listOf()
    private var _gamesList: MutableLiveData<List<MainDBItem>> = simpleGamesList.toMutableLiveData()
    val gamesList: LiveData<List<MainDBItem>> get() = _gamesList

    init {
        viewModelScope.launch (Dispatchers.IO) {
            simpleGamesList = repository.getPhaseFromMain("GAMES")
            _gamesList.postValue(simpleGamesList)
        }
    }
}