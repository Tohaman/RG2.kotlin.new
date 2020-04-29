package ru.tohaman.testempty.ui.games

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tohaman.testempty.Constants.BUFFER_CORNER
import ru.tohaman.testempty.Constants.BUFFER_EDGE
import ru.tohaman.testempty.Constants.CURRENT_SCRAMBLE
import ru.tohaman.testempty.Constants.SCRAMBLE_LENGTH
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.utils.ObservableViewModel
import timber.log.Timber

class ScrambleGeneratorViewModel: ObservableViewModel(), KoinComponent {


    private var _showPreloader = MutableLiveData<Boolean>()
    val showPreloader: LiveData<Boolean> get() = _showPreloader

    private var curScramble = get<SharedPreferences>().getString(CURRENT_SCRAMBLE, "R F L B U2 L B' R F' D B R L F D R' D L")
    private var _currentScramble = MutableLiveData<String>()
    val currentScramble: LiveData<String> get() = _currentScramble

    private var _cornerBuffer = MutableLiveData(get<SharedPreferences>().getBoolean(BUFFER_CORNER, true))
    val cornerBuffer: LiveData<Boolean> get() = _cornerBuffer

    private var _edgeBuffer = MutableLiveData(get<SharedPreferences>().getBoolean(BUFFER_EDGE, false))
    val edgeBuffer: LiveData<Boolean> get() = _edgeBuffer

    private var _scrambleLength = MutableLiveData(get<SharedPreferences>().getInt(SCRAMBLE_LENGTH, 14))
    val scrambleLength: LiveData<Int> get() = _scrambleLength

    init {
        _showPreloader.postValue(false)
        _currentScramble.postValue(curScramble)
    }

    fun azbukaSelect() {
        Timber.d("$TAG AzbukaSelect Pressed")
        _edgeBuffer.postValue(true)
        _cornerBuffer.postValue(true)
    }


    fun generateScramble() {
        Timber.d("$TAG generateScramble Pressed")
        _edgeBuffer.postValue(false)
        _cornerBuffer.postValue(false)
    }

    fun cornerCheck(value: Boolean) {
        Timber.d("$TAG cornerCheck $value")
        _cornerBuffer.postValue(value)
    }

    fun edgeCheck(value: Boolean) {
        Timber.d("$TAG edgeCheck $value")
        _edgeBuffer.postValue(value)
    }


}