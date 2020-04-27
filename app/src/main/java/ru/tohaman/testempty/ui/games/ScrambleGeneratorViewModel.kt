package ru.tohaman.testempty.ui.games

import android.content.SharedPreferences
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tohaman.testempty.Constants.BUFFER_CORNER
import ru.tohaman.testempty.Constants.BUFFER_EDGE
import ru.tohaman.testempty.Constants.CURRENT_SCRAMBLE
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.interfaces.ScrambleGeneratorButtons
import ru.tohaman.testempty.utils.ObservableViewModel
import timber.log.Timber

class ScrambleGeneratorViewModel: ObservableViewModel(), KoinComponent, ScrambleGeneratorButtons {


    private var _showPreloader = MutableLiveData<Boolean>()
    val showPreloader: LiveData<Boolean> get() = _showPreloader

    private var curScramble = get<SharedPreferences>().getString(CURRENT_SCRAMBLE, "R F L B U2 L B' R F' D B R L F D R' D L")
    private var _currentScramble = MutableLiveData<String>()
    val currentScramble: LiveData<String> get() = _currentScramble

    private var cornerBufferSet = get<SharedPreferences>().getBoolean(BUFFER_CORNER, true)
    private var edgeBufferSet = MutableLiveData<Boolean>(get<SharedPreferences>().getBoolean(BUFFER_EDGE, true))

    init {
        _showPreloader.postValue(false)
        _currentScramble.postValue(curScramble)
    }

    override var isCornerBufferSet: Boolean
        @Bindable
        get() = cornerBufferSet
        set(value) {
            Timber.d("$TAG CorenerBuffer Changed - $value")
            cornerBufferSet = value
        }

    override var isEdgeBufferSet: MutableLiveData<Boolean>
        get() {
            Timber.d("$TAG EdgeBuffer get")
            return edgeBufferSet
        }
        set(value) {
            Timber.d("$TAG EdgeBuffer set")
            edgeBufferSet = value
        }


    override fun generateScramble() {
        _showPreloader.postValue(true)
    }

    override fun azbukaSelect() {
        _showPreloader.postValue(false)
    }


}