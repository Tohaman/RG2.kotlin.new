package ru.tohaman.rg2.ui.games

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
import ru.tohaman.rg2.Constants
import ru.tohaman.rg2.Constants.BUFFER_CORNER
import ru.tohaman.rg2.Constants.BUFFER_EDGE
import ru.tohaman.rg2.Constants.CURRENT_SCRAMBLE
import ru.tohaman.rg2.Constants.SCRAMBLE_LENGTH
import ru.tohaman.rg2.Constants.SHOW_SOLVING
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.dataSource.*
import ru.tohaman.rg2.dataSource.entitys.AzbukaSimpleItem
import ru.tohaman.rg2.interfaces.ScrambleDialogInt
import ru.tohaman.rg2.interfaces.ShowPreloaderInt
import ru.tohaman.rg2.utils.toMutableLiveData
import timber.log.Timber

class ScrambleGeneratorViewModel: ViewModel(), KoinComponent, ScrambleDialogInt, ShowPreloaderInt {
    private val repository : ItemsRepository by inject()
    private val sp = get<SharedPreferences>()

    private var _showPreloader = false
    override val showPreloader = ObservableBoolean(_showPreloader)

    private val _cornerBuffer = sp.getBoolean(BUFFER_CORNER, true)
    val cornerBuffer = ObservableBoolean(_cornerBuffer)

    private val _edgeBuffer = sp.getBoolean(BUFFER_EDGE, true)
    val edgeBuffer = ObservableBoolean(_edgeBuffer)

    private var _scrambleLength = sp.getInt(SCRAMBLE_LENGTH, 14)
    var scrambleLength = ObservableField<String>(_scrambleLength.toString())

    private var _currentScramble = sp.getString(CURRENT_SCRAMBLE, "R F L B U2 L B' R F' D B R L F D R' D L") ?: ""
    var currentScramble = ObservableField<String>(_currentScramble)

    private var gridViewAzbukaList = listOf<AzbukaSimpleItem>()
    private var _currentAzbuka = gridViewAzbukaList.toMutableLiveData()
    val currentAzbuka: LiveData<List<AzbukaSimpleItem>> get() = _currentAzbuka

    private var _showSolving = sp.getBoolean(SHOW_SOLVING, true)
    val showSolving = ObservableBoolean(_showSolving)

    private var _solvingText = ""
    val solvingText = ObservableField<String>(_solvingText)

    private var clearCube = resetCube()
    private var currentCube = resetCube()
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
            //delay(2000)
            updateScramble(scramble)
            showPreloader.set(false)                                        //убираем прелоадер
        }
    }

    fun updateScramble(scramble: String) {
        _currentScramble = scramble
        currentScramble.set(scramble)
        sp.edit().putString(CURRENT_SCRAMBLE, scramble).apply()
        //Выполняем скрамбл и отображаем его в grid
        currentCube = runScramble(clearCube, _currentScramble)          //мешаем кубик по скрамблу
        gridViewAzbukaList = prepareCubeToShowInGridView(currentCube)   //задаем List для gridView
        _currentAzbuka.postValue(gridViewAzbukaList)                    //публикуем в gridView
        //выводим решение или его длину
        showSolving()
    }


    private fun showSolving() {
        val scramble = currentScramble.get() ?: ""
        val scrambledCube = runScramble(resetCube(), scramble)
        val solving = if (showSolving.get()) {
            getSolve(scrambledCube, currentLetters).solve
        } else {
            getSolve(scrambledCube, currentLetters).solveLength
        }
        solvingText.set(solving)
    }

    fun cornerCheck(value: Boolean) {
        Timber.d("$TAG cornerCheck $value")
        sp.edit().putBoolean(BUFFER_CORNER, value).apply()
    }

    fun edgeCheck(value: Boolean) {
        Timber.d("$TAG edgeCheck $value")
        sp.edit().putBoolean(BUFFER_EDGE, value).apply()
    }

    fun lengthPlus() {
        Timber.d("$TAG lengthPlus $_scrambleLength")
        _scrambleLength += 1
        if (_scrambleLength > 40) {_scrambleLength = 40}
        else {
            scrambleLength.set(_scrambleLength.toString())
            sp.edit().putInt(SCRAMBLE_LENGTH, _scrambleLength).apply()
        }
    }

    fun lengthMinus() {
        Timber.d("$TAG lengthMinus $_scrambleLength")
        _scrambleLength -= 1
        if (_scrambleLength < 3) { _scrambleLength = 3}
        else {
            scrambleLength.set(_scrambleLength.toString())
            sp.edit().putInt(SCRAMBLE_LENGTH, _scrambleLength).apply()
        }
    }

    fun solveCheck(value: Boolean) {
        Timber.d("$TAG solveCheck $value")
        showSolving()
        sp.edit().putBoolean(SHOW_SOLVING, value).apply()
    }

    //Магия obsrvable меняем tmpScramble, а dialogScrambleText меняется сам, т.к. dialogScrambleText.get()=tmpScramble
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