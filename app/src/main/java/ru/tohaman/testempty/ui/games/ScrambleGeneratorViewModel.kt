package ru.tohaman.testempty.ui.games

import android.content.SharedPreferences
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tohaman.testempty.Constants.BUFFER_CORNER
import ru.tohaman.testempty.Constants.BUFFER_EDGE
import ru.tohaman.testempty.Constants.CURRENT_SCRAMBLE
import ru.tohaman.testempty.Constants.SCRAMBLE_LENGTH
import ru.tohaman.testempty.Constants.SHOW_SOLVING
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dataSource.generateScramble
import ru.tohaman.testempty.utils.ObservableViewModel
import timber.log.Timber

class ScrambleGeneratorViewModel: ObservableViewModel(), KoinComponent {

    private var _showPreloader = false
    val showPreloader = ObservableBoolean(_showPreloader)

    private var _cornerBuffer = MutableLiveData(get<SharedPreferences>().getBoolean(BUFFER_CORNER, true))
    val cornerBuffer: LiveData<Boolean> get() = _cornerBuffer

    private var _edgeBuffer = MutableLiveData(get<SharedPreferences>().getBoolean(BUFFER_EDGE, false))
    val edgeBuffer: LiveData<Boolean> get() = _edgeBuffer

    private var _scrambleLength = get<SharedPreferences>().getInt(SCRAMBLE_LENGTH, 14)
    var scrambleLength = ObservableField<String>(_scrambleLength.toString())

    private var _currentScramble = get<SharedPreferences>().getString(CURRENT_SCRAMBLE, "R F L B U2 L B' R F' D B R L F D R' D L") ?: ""
    var currentScramble = ObservableField<String>(_currentScramble)

    private var _showSolving = MutableLiveData(get<SharedPreferences>().getBoolean(SHOW_SOLVING, true))
    val showSolving: LiveData<Boolean> get() = _showSolving

    private var _solvingText = MutableLiveData<String>()
    val solvingText: LiveData<String> get() = _solvingText


    fun azbukaSelect() {
        Timber.d("$TAG AzbukaSelect Pressed")
        showPreloader.set(false)
    }


    fun generateScramble() {
        Timber.d("$TAG generateScramble Pressed")
        showPreloader.set(true)
        currentScramble.set(generateScramble(_scrambleLength))
    }

    fun cornerCheck(value: Boolean) {
        Timber.d("$TAG cornerCheck $value")
        _cornerBuffer.postValue(value)
    }

    fun edgeCheck(value: Boolean) {
        Timber.d("$TAG edgeCheck $value")
        _edgeBuffer.postValue(value)
    }

    fun lengthPlus() {
        Timber.d("$TAG lengthPlus $_scrambleLength")
        _scrambleLength += 1
        if (_scrambleLength > 40) {_scrambleLength = 40}
        scrambleLength.set(_scrambleLength.toString())
    }

    fun lengthMinus() {
        Timber.d("$TAG lengthMinus $_scrambleLength")
        _scrambleLength -= 1
        if (_scrambleLength < 3) { _scrambleLength = 3}
        scrambleLength.set(_scrambleLength.toString())
    }

}