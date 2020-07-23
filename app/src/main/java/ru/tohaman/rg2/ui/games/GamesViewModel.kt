package ru.tohaman.rg2.ui.games

import android.content.SharedPreferences
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import ru.tohaman.rg2.Constants.ANTONS_AZBUKA
import ru.tohaman.rg2.Constants.CURRENT_AZBUKA
import ru.tohaman.rg2.Constants.CUSTOM_AZBUKA
import ru.tohaman.rg2.Constants.MAKSIMS_AZBUKA
import ru.tohaman.rg2.Constants.TRAINING_CORNERS
import ru.tohaman.rg2.Constants.TRAINING_EDGES
import ru.tohaman.rg2.Constants.TRAINING_TIMER
import ru.tohaman.rg2.Constants.TRAINING_TIMER_TIME
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.dataSource.*
import ru.tohaman.rg2.dbase.entitys.AzbukaDBItem
import ru.tohaman.rg2.dataSource.entitys.AzbukaSimpleItem
import ru.tohaman.rg2.dbase.entitys.MainDBItem
import ru.tohaman.rg2.interfaces.GamesAzbukaButtons
import ru.tohaman.rg2.interfaces.SetLetterButtonsInt
import ru.tohaman.rg2.utils.toMutableLiveData
import timber.log.Timber

//Эта viewModel используется для общих настроек Тренажеров + настройки Генератора скрамблов (Азбуки) и настройки теренировки Азбуки
class GamesViewModel: ViewModel(), KoinComponent, GamesAzbukaButtons, SetLetterButtonsInt {
    private val repository : ItemsRepository by inject()
    private val sp = get<SharedPreferences>()

    private var simpleGamesList = listOf<MainDBItem>()
    private var _gamesList: MutableLiveData<List<MainDBItem>> = simpleGamesList.toMutableLiveData()
    val gamesList: LiveData<List<MainDBItem>> get() = _gamesList

    private var gridViewAzbukaList = listOf<AzbukaSimpleItem>()
    private var _currentAzbuka = gridViewAzbukaList.toMutableLiveData()
    val currentAzbuka: LiveData<List<AzbukaSimpleItem>> get() = _currentAzbuka

    var selectedItem = 0

    var curLetter = MutableLiveData<String>()
    var message = MutableLiveData<String>()

    private var _trainingCorners = sp.getBoolean(TRAINING_CORNERS, true)
    val trainingCorners = ObservableBoolean(_trainingCorners)

    private val _trainingEdges = sp.getBoolean(TRAINING_EDGES, true)
    val trainingEdges = ObservableBoolean(_trainingEdges)

    private val _trainingTimer = sp.getBoolean(TRAINING_TIMER, true)
    val trainingTimer = ObservableBoolean(_trainingTimer)

    private var _trainingTimerTime = sp.getInt(TRAINING_TIMER_TIME, 6)
    val trainingTimerTime = ObservableInt(_trainingTimerTime)


    init {
        viewModelScope.launch (Dispatchers.IO) {
            simpleGamesList = repository.getPhaseFromMain("GAMES")
            _gamesList.postValue(simpleGamesList)

            val listDBAzbuka = repository.getAzbukaItems(CURRENT_AZBUKA)

            gridViewAzbukaList = prepareAzbukaToShowInGridView(listDBAzbuka)
            _currentAzbuka.postValue(gridViewAzbukaList)
        }
    }

    fun trainingCornersChange(value: Boolean) {
        if (!(value or trainingEdges.get())) trainingEdgesChange(true)
        trainingCorners.set(value)
        sp.edit().putBoolean(TRAINING_CORNERS, value).apply()
    }

    fun trainingEdgesChange(value: Boolean) {
        if (!(value or trainingCorners.get())) trainingCornersChange(true)
        trainingEdges.set(value)
        sp.edit().putBoolean(TRAINING_EDGES, value).apply()
    }

    fun trainingTimerChange(value: Boolean) {
        trainingTimer.set(value)
        sp.edit().putBoolean(TRAINING_TIMER, value).apply()
    }

    fun minusTimerTime() {
        _trainingTimerTime -= 1
        if (_trainingTimerTime < 2) _trainingTimerTime = 2
        trainingTimerTime.set(_trainingTimerTime)
        sp.edit().putInt(TRAINING_TIMER_TIME, _trainingTimerTime).apply()
    }

    fun plusTimerTime() {
        _trainingTimerTime += 1
        if (_trainingTimerTime > 15) _trainingTimerTime = 15
        trainingTimerTime.set(_trainingTimerTime)
        sp.edit().putInt(TRAINING_TIMER_TIME, _trainingTimerTime).apply()
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