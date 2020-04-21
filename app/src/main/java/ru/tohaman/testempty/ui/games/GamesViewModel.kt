package ru.tohaman.testempty.ui.games

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.testempty.Constants.ANTONS_AZBUKA
import ru.tohaman.testempty.Constants.CURRENT_AZBUKA
import ru.tohaman.testempty.Constants.MAKSIMS_AZBUKA
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dataSource.*
import ru.tohaman.testempty.dbase.entitys.AzbukaSimpleItem
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.utils.toMutableLiveData
import timber.log.Timber

class GamesViewModel: ViewModel(), KoinComponent, GamesAzbukaButtons {
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

            val listDBAzbuka = repository.getAzbukaItems(CURRENT_AZBUKA)

            gridViewAzbukaList = prepareAzbukaToShowInGridView(listDBAzbuka)
            _currentAzbuka.postValue(gridViewAzbukaList)
        }
    }

    override fun leftArrowButtonPressed() {
        Timber.d("$TAG вращаем кубик влево")
        var coloredCube = getCubeFromCurrentAzbuka(gridViewAzbukaList)
        coloredCube = moveZb(coloredCube)
        gridViewAzbukaList = setAzbukaColors(coloredCube, gridViewAzbukaList)
        _currentAzbuka.postValue(gridViewAzbukaList)
    }

    override fun rightArrowButtonPressed() {
        Timber.d("$TAG вращаем кубик вправо")
        var coloredCube = getCubeFromCurrentAzbuka(gridViewAzbukaList)
        coloredCube = moveZ(coloredCube)
        gridViewAzbukaList = setAzbukaColors(coloredCube, gridViewAzbukaList)
        _currentAzbuka.postValue(gridViewAzbukaList)
    }

    override fun clockWiseArrowButtonPressed() {
        Timber.d("$TAG вращаем кубик по часовой стрелке")
        var coloredCube = getCubeFromCurrentAzbuka(gridViewAzbukaList)
        coloredCube = moveY(coloredCube)
        gridViewAzbukaList = setAzbukaColors(coloredCube, gridViewAzbukaList)
        _currentAzbuka.postValue(gridViewAzbukaList)
    }

    override fun antiClockWiseArrowButtonPressed() {
        Timber.d("$TAG вращаем кубик против часовой стрелке")
        var coloredCube = getCubeFromCurrentAzbuka(gridViewAzbukaList)
        coloredCube = moveYb(coloredCube)
        gridViewAzbukaList = setAzbukaColors(coloredCube, gridViewAzbukaList)
        _currentAzbuka.postValue(gridViewAzbukaList)
    }

    override fun loadAntonsAzbuka() {
        Timber.d("$TAG загружаем мою азбуку")
        viewModelScope.launch (Dispatchers.IO) {
            val listDBAzbuka = repository.getAzbukaItems(ANTONS_AZBUKA)
            gridViewAzbukaList = prepareAzbukaToShowInGridView(listDBAzbuka)
            _currentAzbuka.postValue(gridViewAzbukaList)
        }
    }

    override fun loadMaksimsAzbuka() {
        Timber.d("$TAG загружаем азбуку Максима")
        viewModelScope.launch (Dispatchers.IO) {
            val listDBAzbuka = repository.getAzbukaItems(MAKSIMS_AZBUKA)
            gridViewAzbukaList = prepareAzbukaToShowInGridView(listDBAzbuka)
            _currentAzbuka.postValue(gridViewAzbukaList)
        }

    }

    override fun saveCurrentAzbuka() {
        viewModelScope.launch (Dispatchers.IO) {
//            val dbAzbuka = setAzbukaDBItemFromSimple(gridViewAzbukaList, )
//            repository.updateAzbuka(dbAzbuka)
        }
    }

    override fun loadCurrentAzbuka() {
        viewModelScope.launch (Dispatchers.IO) {
            val listDBAzbuka = repository.getAzbukaItems(CURRENT_AZBUKA)
            gridViewAzbukaList = prepareAzbukaToShowInGridView(listDBAzbuka)
            _currentAzbuka.postValue(gridViewAzbukaList)
        }
    }


}