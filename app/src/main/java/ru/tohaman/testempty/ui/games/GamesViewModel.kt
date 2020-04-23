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
import ru.tohaman.testempty.Constants.CUSTOM_AZBUKA
import ru.tohaman.testempty.Constants.MAKSIMS_AZBUKA
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dataSource.*
import ru.tohaman.testempty.dbase.entitys.AzbukaDBItem
import ru.tohaman.testempty.dbase.entitys.AzbukaSimpleItem
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.interfaces.GamesAzbukaButtons
import ru.tohaman.testempty.interfaces.SetLetterButtons
import ru.tohaman.testempty.utils.toMutableLiveData
import timber.log.Timber

class GamesViewModel: ViewModel(), KoinComponent,
    GamesAzbukaButtons, SetLetterButtons {
    private val repository : ItemsRepository by inject()

    private var simpleGamesList = listOf<MainDBItem>()
    private var _gamesList: MutableLiveData<List<MainDBItem>> = simpleGamesList.toMutableLiveData()
    val gamesList: LiveData<List<MainDBItem>> get() = _gamesList

    private var gridViewAzbukaList = listOf<AzbukaSimpleItem>()
    private var _currentAzbuka = gridViewAzbukaList.toMutableLiveData()
    val currentAzbuka: LiveData<List<AzbukaSimpleItem>> get() = _currentAzbuka

    var selectedItem = 0

    var curLetter = MutableLiveData<String>()
    var message = MutableLiveData<String>()

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
        updateDBAzbuka(CURRENT_AZBUKA)
    }

    override fun rightArrowButtonPressed() {
        Timber.d("$TAG вращаем кубик вправо")
        var coloredCube = getCubeFromCurrentAzbuka(gridViewAzbukaList)
        coloredCube = moveZ(coloredCube)
        gridViewAzbukaList = setAzbukaColors(coloredCube, gridViewAzbukaList)
        _currentAzbuka.postValue(gridViewAzbukaList)
        updateDBAzbuka(CURRENT_AZBUKA)
    }

    override fun clockWiseArrowButtonPressed() {
        Timber.d("$TAG вращаем кубик по часовой стрелке")
        var coloredCube = getCubeFromCurrentAzbuka(gridViewAzbukaList)
        coloredCube = moveY(coloredCube)
        gridViewAzbukaList = setAzbukaColors(coloredCube, gridViewAzbukaList)
        _currentAzbuka.postValue(gridViewAzbukaList)
        updateDBAzbuka(CURRENT_AZBUKA)
    }

    override fun antiClockWiseArrowButtonPressed() {
        Timber.d("$TAG вращаем кубик против часовой стрелке")
        var coloredCube = getCubeFromCurrentAzbuka(gridViewAzbukaList)
        coloredCube = moveYb(coloredCube)
        gridViewAzbukaList = setAzbukaColors(coloredCube, gridViewAzbukaList)
        _currentAzbuka.postValue(gridViewAzbukaList)
        updateDBAzbuka(CURRENT_AZBUKA)
    }

    override fun loadAntonsAzbuka() {
        Timber.d("$TAG загружаем мою азбуку")
        viewModelScope.launch (Dispatchers.IO) {
            val listDBAzbuka = repository.getAzbukaItems(ANTONS_AZBUKA)
            gridViewAzbukaList = prepareAzbukaToShowInGridView(listDBAzbuka)
            _currentAzbuka.postValue(gridViewAzbukaList)
            updateDBAzbuka(CURRENT_AZBUKA)
        }
    }

    override fun loadMaksimsAzbuka() {
        Timber.d("$TAG загружаем азбуку Максима")
        viewModelScope.launch (Dispatchers.IO) {
            val listDBAzbuka = repository.getAzbukaItems(MAKSIMS_AZBUKA)
            gridViewAzbukaList = prepareAzbukaToShowInGridView(listDBAzbuka)
            _currentAzbuka.postValue(gridViewAzbukaList)
            updateDBAzbuka(CURRENT_AZBUKA)
        }
    }

    override fun saveCustomAzbuka() {
        updateDBAzbuka(CUSTOM_AZBUKA)
        //Выведем тост (снэк). Запишем сообщение в LiveData, на которую должен быть подписан презентер
        message.postValue("Азбука сохранена")
    }

    override fun loadCustomAzbuka() {
        viewModelScope.launch (Dispatchers.IO) {
            val listDBAzbuka = repository.getAzbukaItems(CUSTOM_AZBUKA)
            gridViewAzbukaList = prepareAzbukaToShowInGridView(listDBAzbuka)
            _currentAzbuka.postValue(gridViewAzbukaList)
            updateDBAzbuka(CURRENT_AZBUKA)
        }
    }

    //Сохраняем в базе текущую азбуку из gridView под указанным именем (custom или current)
    private fun updateDBAzbuka(azbukaName: String) {
        viewModelScope.launch (Dispatchers.IO) {
            //Получим из текущего состояния списка отображаемого в GridView (108 элементов)
            //два "чистых" списка с буквами и цветами
            val coloredCube = getCubeFromCurrentAzbuka(gridViewAzbukaList)
            val lettersArray = getLettersFromCurrentAzbuka(gridViewAzbukaList)
            val dbAzbuka = mutableListOf<AzbukaDBItem>()
            for (i in lettersArray.indices) {
                dbAzbuka.add(AzbukaDBItem(azbukaName, i, lettersArray[i], coloredCube[i]))
            }
            repository.updateAzbuka(dbAzbuka)
        }
    }

    override fun clickMinus(letter: String) {
        var ch = letter[0]
        when {
            //код Ё находится не между Е и Ж
            (ch == 'Ж') -> { ch = 'Ё' }
            (ch == 'Ё') -> { ch = 'Е' }
            (ch == 'А') -> { ch = 'Z' }
            (ch == 'A') -> { ch = '9' }
            (ch == '0') -> { ch = 'Я' }
            else -> {ch--}
        }
        curLetter.postValue(ch.toString())
    }

    override fun clickPlus(letter: String) {
        var ch = letter[0]
        when {
            (ch == 'Я') -> { ch = '0' }
            (ch == '9') -> { ch = 'A'}
            (ch == 'Z') -> { ch = 'А'}
            //код Ё находится не между Е и Ж
            (ch == 'Е') -> { ch = 'Ё' }
            (ch == 'Ё') -> { ch = 'Ж' }
            else -> {ch++}
        }
        curLetter.postValue(ch.toString())
    }

    override fun changeLetter(id: Int) {
        val letter = curLetter.value ?: "A"
        gridViewAzbukaList[id].value = letter
        _currentAzbuka.postValue(gridViewAzbukaList)
        updateDBAzbuka(CURRENT_AZBUKA)
    }

}