package ru.tohaman.testempty.ui.games

import android.content.SharedPreferences
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import ru.tohaman.testempty.Constants.TIMER_DELAYED
import ru.tohaman.testempty.Constants.TIMER_METRONOM
import ru.tohaman.testempty.Constants.TIMER_METRONOM_FREQ
import ru.tohaman.testempty.Constants.TIMER_NEED_SCRAMBLE
import ru.tohaman.testempty.Constants.TIMER_ONE_HANDED
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dataSource.ItemsRepository
import timber.log.Timber

class GamesTimerViewModel: ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()
    private val sp = get<SharedPreferences>()

    private var _isTimerDelayed = sp.getBoolean(TIMER_DELAYED, true)
    val isTimerDelayed = ObservableBoolean(_isTimerDelayed)

    fun isTimerDelayedChange(value: Boolean) {
        Timber.d("$TAG .isTimerDelayedChange $value")
        sp.edit().putBoolean(TIMER_DELAYED, value).apply()
    }

    private var _isOneHanded = sp.getBoolean(TIMER_ONE_HANDED, false)
    val isOneHanded = ObservableBoolean(_isOneHanded)

    fun isOneHandedChange(value: Boolean) {
        sp.edit().putBoolean(TIMER_ONE_HANDED, value).apply()
    }

    private var _metronom = sp.getBoolean(TIMER_METRONOM, false)
    val metronom = ObservableBoolean(_metronom)

    fun isMetronomChange(value: Boolean) {
        sp.edit().putBoolean(TIMER_METRONOM, value).apply()
    }

    private var _metronomFrequency = sp.getInt(TIMER_METRONOM_FREQ, 60)
    val metronomFrequency = ObservableInt(_metronomFrequency)

    fun onSeek(): OnSeekBarChangeListener? {
        return object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                //Timber.d("$TAG .onProgressChanged progress = [${progress}], fromUser = [${fromUser}]")
                _metronomFrequency = if (progress !=0) progress else 1
                metronomFrequency.set(_metronomFrequency)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                //Timber.d("$TAG .onStartTrackingTouch seekBar = [${seekBar}]")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Timber.d("$TAG .onStopTrackingTouch ${metronomFrequency.get()}")
                sp.edit().putInt(TIMER_METRONOM_FREQ, metronomFrequency.get()).apply()
            }
        }
    }

    fun minusFreq() {
        _metronomFrequency -= 1
        metronomFrequency.set(_metronomFrequency)
        sp.edit().putInt(TIMER_METRONOM_FREQ, _metronomFrequency).apply()
    }

    fun plusFreq() {
        _metronomFrequency += 1
        metronomFrequency.set(_metronomFrequency)
        sp.edit().putInt(TIMER_METRONOM_FREQ, _metronomFrequency).apply()
    }

    private var _needScramble = sp.getBoolean(TIMER_NEED_SCRAMBLE, true)
    val needScramble = ObservableBoolean(_needScramble)

    fun needScrambleChange(value: Boolean) {
        sp.edit().putBoolean(TIMER_NEED_SCRAMBLE, value).apply()
    }

}