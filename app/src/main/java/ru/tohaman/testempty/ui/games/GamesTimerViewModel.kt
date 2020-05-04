package ru.tohaman.testempty.ui.games

import android.content.SharedPreferences
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import ru.tohaman.testempty.Constants
import ru.tohaman.testempty.Constants.CURRENT_SCRAMBLE
import ru.tohaman.testempty.Constants.TIMER_DELAYED
import ru.tohaman.testempty.Constants.TIMER_METRONOM
import ru.tohaman.testempty.Constants.TIMER_METRONOM_FREQ
import ru.tohaman.testempty.Constants.TIMER_NEED_SCRAMBLE
import ru.tohaman.testempty.Constants.TIMER_ONE_HANDED
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dataSource.generateScrambleWithParam
import ru.tohaman.testempty.dataSource.getLettersFromCurrentAzbuka
import ru.tohaman.testempty.dataSource.prepareAzbukaToShowInGridView
import ru.tohaman.testempty.interfaces.ShowPreloaderInt
import timber.log.Timber

class GamesTimerViewModel: ViewModel(), KoinComponent, ShowPreloaderInt {
    private val repository : ItemsRepository by inject()
    private val sp = get<SharedPreferences>()

    private var cornerBuffer =true
    private var edgeBuffer = true
    private var scrambleLength = 14
    private var currentLetters: Array<String>? = null

    init {
        reloadScrambleParameters()
    }

    private val _showPreloader = false
    override val showPreloader = ObservableBoolean(_showPreloader)

    private val _isTimerDelayed = sp.getBoolean(TIMER_DELAYED, true)
    val isTimerDelayed = ObservableBoolean(_isTimerDelayed)

    fun isTimerDelayedChange(value: Boolean) {
        Timber.d("$TAG .isTimerDelayedChange $value")
        sp.edit().putBoolean(TIMER_DELAYED, value).apply()
    }

    private val _isOneHanded = sp.getBoolean(TIMER_ONE_HANDED, false)
    val isOneHanded = ObservableBoolean(_isOneHanded)

    fun isOneHandedChange(value: Boolean) {
        sp.edit().putBoolean(TIMER_ONE_HANDED, value).apply()
    }

    private val _metronom = sp.getBoolean(TIMER_METRONOM, false)
    val metronom = ObservableBoolean(_metronom)

    fun isMetronomChange(value: Boolean) {
        sp.edit().putBoolean(TIMER_METRONOM, value).apply()
    }

    private var _metronomFrequency = sp.getInt(TIMER_METRONOM_FREQ, 60)
    val metronomFrequency = ObservableInt(_metronomFrequency)

    fun onSeek(): OnSeekBarChangeListener? {
        return object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                _metronomFrequency = if (progress !=0) progress else 1
                metronomFrequency.set(_metronomFrequency)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Timber.d("$TAG .onStopTrackingTouch ${metronomFrequency.get()}")
                sp.edit().putInt(TIMER_METRONOM_FREQ, metronomFrequency.get()).apply()
            }
        }
    }

    fun minusFreq() {
        _metronomFrequency -= 1
        if (_metronomFrequency == 0) {_metronomFrequency = 1}
        metronomFrequency.set(_metronomFrequency)
        sp.edit().putInt(TIMER_METRONOM_FREQ, _metronomFrequency).apply()
    }

    fun plusFreq() {
        _metronomFrequency += 1
        if (_metronomFrequency == 241) {_metronomFrequency = 240}
        metronomFrequency.set(_metronomFrequency)
        sp.edit().putInt(TIMER_METRONOM_FREQ, _metronomFrequency).apply()
    }

    private val _needScramble = sp.getBoolean(TIMER_NEED_SCRAMBLE, true)
    val needScramble = ObservableBoolean(_needScramble)

    fun needScrambleChange(value: Boolean) {
        sp.edit().putBoolean(TIMER_NEED_SCRAMBLE, value).apply()
    }

    //---------------------------

    fun reloadScrambleParameters(){
        viewModelScope.launch (Dispatchers.IO) {
            cornerBuffer = sp.getBoolean(Constants.BUFFER_CORNER, true)
            edgeBuffer = sp.getBoolean(Constants.BUFFER_EDGE, true)
            scrambleLength = sp.getInt(Constants.SCRAMBLE_LENGTH, 14)
            val listDBAzbuka = repository.getAzbukaItems(Constants.CURRENT_AZBUKA)
            currentLetters = getLettersFromCurrentAzbuka(prepareAzbukaToShowInGridView(listDBAzbuka))
        }

    }

    //private var _currentScramble = sp.getString(CURRENT_SCRAMBLE, "R F L B U2 L B' R F' D B R L F D R' D L") ?: ""
    private val _currentScramble = "R F L B U2 L B' R F' D B R L F D R' D L"
    var currentScramble = ObservableField<String>(_currentScramble)

    fun generateNewScramble() {
        Timber.d("$TAG .generateNewScramble ")

        viewModelScope.launch {
            showPreloader.set(true)                                         //выводим прелоадер
            //Подбираем скрамбл
            if (currentLetters != null)  {
                val scramble = generateScrambleWithParam(edgeBuffer, cornerBuffer, scrambleLength,
                    currentLetters!!
                )
                currentScramble.set(scramble)
            }
            //delay(2000)
            showPreloader.set(false)                                        //убираем прелоадер
        }
    }


}