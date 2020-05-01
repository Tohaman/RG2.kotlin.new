package ru.tohaman.testempty.ui.games

import android.content.SharedPreferences
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import ru.tohaman.testempty.Constants
import ru.tohaman.testempty.Constants.BUFFER_CORNER
import ru.tohaman.testempty.Constants.BUFFER_EDGE
import ru.tohaman.testempty.Constants.CURRENT_SCRAMBLE
import ru.tohaman.testempty.Constants.SCRAMBLE_LENGTH
import ru.tohaman.testempty.Constants.SHOW_SOLVING
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dataSource.*
import ru.tohaman.testempty.dataSource.entitys.AzbukaSimpleItem
import ru.tohaman.testempty.interfaces.ScrambleDialogInt
import ru.tohaman.testempty.utils.toMutableLiveData
import timber.log.Timber

class ScrambleGeneratorViewModel: ViewModel(), KoinComponent, ScrambleDialogInt {
    private val repository : ItemsRepository by inject()

    private var _showPreloader = false
    val showPreloader = ObservableBoolean(_showPreloader)

    private var _cornerBuffer = get<SharedPreferences>().getBoolean(BUFFER_CORNER, true)
    val cornerBuffer = ObservableBoolean(_cornerBuffer)

    private var _edgeBuffer = get<SharedPreferences>().getBoolean(BUFFER_EDGE, true)
    val edgeBuffer = ObservableBoolean(_edgeBuffer)

    private var _scrambleLength = get<SharedPreferences>().getInt(SCRAMBLE_LENGTH, 14)
    var scrambleLength = ObservableField<String>(_scrambleLength.toString())

    private var _currentScramble = get<SharedPreferences>().getString(CURRENT_SCRAMBLE, "R F L B U2 L B' R F' D B R L F D R' D L") ?: ""
    var currentScramble = ObservableField<String>(_currentScramble)

    private var gridViewAzbukaList = listOf<AzbukaSimpleItem>()
    private var _currentAzbuka = gridViewAzbukaList.toMutableLiveData()
    val currentAzbuka: LiveData<List<AzbukaSimpleItem>> get() = _currentAzbuka

    private var _showSolving = get<SharedPreferences>().getBoolean(SHOW_SOLVING, true)
    val showSolving = ObservableBoolean(_showSolving)

    private var _solvingText = ""
    val solvingText = ObservableField<String>(_solvingText)

    private var clearCube = IntArray(54) { 0 }
    private var currentCube = IntArray(54) { 0 }
    private var currentLetters = Array<String> (54) { "" }

    init {
        reloadAzbuka()
    }

    fun reloadAzbuka(){
        viewModelScope.launch (Dispatchers.IO) {
            val listDBAzbuka = repository.getAzbukaItems(Constants.CURRENT_AZBUKA)
            currentLetters = getLettersFromCurrentAzbuka(prepareAzbukaToShowInGridView(listDBAzbuka))
            clearCube = getCubeFromCurrentAzbuka(prepareAzbukaToShowInGridView(listDBAzbuka))
            currentCube = runScramble(clearCube, _currentScramble)
            gridViewAzbukaList = prepareCubeToShowInGridView(currentCube)
            _currentAzbuka.postValue(gridViewAzbukaList)
            showSolving()
        }
    }

    fun generateScramble() {
        Timber.d("$TAG generateScramble Pressed")
        viewModelScope.launch {
            showPreloader.set(true)                                         //выводим прелоадер
            //Подбираем скрамбл
            val scramble = generateScrambleWithParam(edgeBuffer.get(), cornerBuffer.get(), _scrambleLength, currentLetters)
            updateScramble(scramble)
            showPreloader.set(false)                                        //убираем прелоадер
        }
    }

    fun updateScramble(scramble: String) {
        _currentScramble = scramble
        currentScramble.set(scramble)
        get<SharedPreferences>().edit().putString(CURRENT_SCRAMBLE, scramble).apply()
        //Выполняем скрамбл и отображаем его в grid
        currentCube = runScramble(clearCube, _currentScramble)          //мешаем кубик по скрамблу
        gridViewAzbukaList = prepareCubeToShowInGridView(currentCube)   //задаем List для gridView
        _currentAzbuka.postValue(gridViewAzbukaList)                    //публикуем в gridView
        //выводим решение или его длину
        showSolving()
    }


    private fun showSolving() {
        val solving = if (showSolving.get()) {
            getSolve(currentCube, currentLetters).solve
        } else {
            getSolve(currentCube, currentLetters).solveLength
        }
        solvingText.set(solving)
    }

    fun cornerCheck(value: Boolean) {
        Timber.d("$TAG cornerCheck $value")
        get<SharedPreferences>().edit().putBoolean(BUFFER_CORNER, value).apply()
    }

    fun edgeCheck(value: Boolean) {
        Timber.d("$TAG edgeCheck $value")
        get<SharedPreferences>().edit().putBoolean(BUFFER_EDGE, value).apply()
    }

    fun lengthPlus() {
        Timber.d("$TAG lengthPlus $_scrambleLength")
        _scrambleLength += 1
        if (_scrambleLength > 40) {_scrambleLength = 40}
        else {
            scrambleLength.set(_scrambleLength.toString())
            get<SharedPreferences>().edit().putInt(SCRAMBLE_LENGTH, _scrambleLength).apply()
        }
    }

    fun lengthMinus() {
        Timber.d("$TAG lengthMinus $_scrambleLength")
        _scrambleLength -= 1
        if (_scrambleLength < 3) { _scrambleLength = 3}
        else {
            scrambleLength.set(_scrambleLength.toString())
            get<SharedPreferences>().edit().putInt(SCRAMBLE_LENGTH, _scrambleLength).apply()
        }
    }

    fun solveCheck(value: Boolean) {
        Timber.d("$TAG solveCheck $value")
        showSolving()
        get<SharedPreferences>().edit().putBoolean(SHOW_SOLVING, value).apply()
    }

    //Магия obsrvable меняем tmpScramble, а dialogScrambleText меняется сам
    private var tmpScramble = ObservableField<String>("")

    override var dialogScrambleText: ObservableField<String>
        get() = tmpScramble
        set(value) {
            Timber.d("$TAG set $value ")
        }

    override fun pressMoveButton(letter: String) {
        var scramble = tmpScramble.get() ?: ""
        scramble += " $letter"
        tmpScramble.set(scramble.trim())
    }

    override fun pressBackSpace() {
        var scramble = tmpScramble.get() ?: ""
        val lastSymbol = scramble.takeLast(1)
        val n = if (lastSymbol == " ") 2 else 1
        scramble = scramble.dropLast(n)
        tmpScramble.set(scramble.trim())
    }

    override fun pressModifier(modifier: String) {
        var scramble = tmpScramble.get() ?: ""
        val lastSymbol = scramble.takeLast(1)
        val mod = if (modifier == "1") {"'"} else modifier
        scramble = when (lastSymbol) {
            "", "'", "2" -> scramble
            else -> "$scramble$mod"
        }

        tmpScramble.set(scramble)
    }


}