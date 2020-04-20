package ru.tohaman.testempty.ui.games

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.testempty.dataSource.*
import ru.tohaman.testempty.dbase.entitys.AzbukaSimpleItem
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.utils.toMutableLiveData

class GamesViewModel: ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()

    private var simpleGamesList = listOf<MainDBItem>()
    private var _gamesList: MutableLiveData<List<MainDBItem>> = simpleGamesList.toMutableLiveData()
    val gamesList: LiveData<List<MainDBItem>> get() = _gamesList

    private var gridViewAzbukaList = listOf<AzbukaSimpleItem>()
    private var _currentAzbuka = gridViewAzbukaList.toMutableLiveData()
    val currentAzbuka: LiveData<List<AzbukaSimpleItem>> get() = _currentAzbuka

    var selectedItem = 0

    init {
        viewModelScope.launch (Dispatchers.IO) {
            simpleGamesList = repository.getPhaseFromMain("GAMES")
            _gamesList.postValue(simpleGamesList)

            val antonAzbuka = getMyAzbuka()
            var newCube = resetCube()
            newCube = moveZ(newCube)
            val listDBAzbuka = setAzbukaDBItemFromSimple(antonAzbuka, newCube, "AntonsAzbuka")

            gridViewAzbukaList = prepareAzbukaToShowInGridView(listDBAzbuka)
            _currentAzbuka.postValue(gridViewAzbukaList)
        }
    }



}