package ru.tohaman.testempty.ui.games

import android.content.SharedPreferences
import android.database.Observable
import android.graphics.drawable.LayerDrawable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tohaman.testempty.Constants.ALL_PLL_COUNT
import ru.tohaman.testempty.Constants.IS_2SIDE_RECOGNITION
import ru.tohaman.testempty.Constants.PLL_TRAINING_TIMER
import ru.tohaman.testempty.Constants.PLL_TRAINING_TIMER_TIME
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.interfaces.WrongAnswerInt
import timber.log.Timber

class PllTrainerViewModel: ViewModel(), KoinComponent, WrongAnswerInt {
    private val sp = get<SharedPreferences>()

    //Settings

    private var _is2SideRecognition = sp.getBoolean(IS_2SIDE_RECOGNITION, true)
    var is2SideRecognition = ObservableBoolean(_is2SideRecognition)

    fun twoSideRecognitionSelect() {
        Timber.d("$TAG .twoSideRecognitionSelect ")
        _is2SideRecognition = true
        is2SideRecognition.set(_is2SideRecognition)
        sp.edit().putBoolean(IS_2SIDE_RECOGNITION, _is2SideRecognition).apply()
    }

    fun threeSideRecognitionSelect() {
        Timber.d("$TAG .threeSideRecognitionSelect ")
        _is2SideRecognition = false
        is2SideRecognition.set(_is2SideRecognition)
        sp.edit().putBoolean(IS_2SIDE_RECOGNITION, _is2SideRecognition).apply()
    }

    private val _pllTrainingTimer = sp.getBoolean(PLL_TRAINING_TIMER, true)
    val pllTrainingTimer = ObservableBoolean(_pllTrainingTimer)

    private var _pllTrainingTimerTime = sp.getInt(PLL_TRAINING_TIMER_TIME, 6)
    val pllTrainingTimerTime = ObservableInt(_pllTrainingTimerTime)

    fun pllTrainingTimerChange(value: Boolean) {
        pllTrainingTimer.set(value)
        sp.edit().putBoolean(PLL_TRAINING_TIMER, value).apply()
    }

    fun minusTimerTime() {
        _pllTrainingTimerTime -= 1
        if (_pllTrainingTimerTime < 2) _pllTrainingTimerTime = 2
        pllTrainingTimerTime.set(_pllTrainingTimerTime)
        sp.edit().putInt(PLL_TRAINING_TIMER_TIME, _pllTrainingTimerTime).apply()
    }

    fun plusTimerTime() {
        _pllTrainingTimerTime += 1
        if (_pllTrainingTimerTime > 15) _pllTrainingTimerTime = 15
        pllTrainingTimerTime.set(_pllTrainingTimerTime)
        sp.edit().putInt(PLL_TRAINING_TIMER_TIME, _pllTrainingTimerTime).apply()
    }

    private var _allPllCount = sp.getBoolean(ALL_PLL_COUNT, true)
    var allPllCount = ObservableBoolean(_allPllCount)

    fun allVariantsSelect() {
        _allPllCount = true
        allPllCount.set(_allPllCount)
        sp.edit().putBoolean(ALL_PLL_COUNT, _allPllCount).apply()
    }

    fun someVariantsSelect() {
        _allPllCount = false
        allPllCount.set(_allPllCount)
        sp.edit().putBoolean(ALL_PLL_COUNT, _allPllCount).apply()
    }

    // Game
    var rightAnswerLetter = ObservableField<String>("A")
    var timerProgress = ObservableInt(100)

    var rightAnswerCount = ObservableInt(0)
    var wrongAnswerCount = ObservableInt(0)

    var showStartButton = ObservableBoolean(false)

    private var layeredImageDrawable: LayerDrawable? = null
    var imageDrawable = ObservableField<LayerDrawable>(layeredImageDrawable)

    val showRightAnswer = ObservableBoolean(false)
    val showWrongAnswer = ObservableBoolean(false)

    fun startGame() {

    }

    override val wrongAnswerText: ObservableField<String>
        get() = TODO("Not yet implemented")

    override fun stopGame() {
        TODO("Not yet implemented")
    }

    override fun continueGame() {
        TODO("Not yet implemented")
    }


}