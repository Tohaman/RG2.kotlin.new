package ru.tohaman.testempty.ui.games

import android.app.Application
import android.content.SharedPreferences
import android.database.Observable
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tohaman.testempty.Constants.ALL_PLL_COUNT
import ru.tohaman.testempty.Constants.IS_2SIDE_RECOGNITION
import ru.tohaman.testempty.Constants.PLL_TRAINING_TIMER
import ru.tohaman.testempty.Constants.PLL_TRAINING_TIMER_TIME
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dataSource.cubeColor
import ru.tohaman.testempty.dataSource.resetCube
import ru.tohaman.testempty.dataSource.runScramble
import ru.tohaman.testempty.interfaces.WrongAnswerInt
import ru.tohaman.testempty.utils.toMutableLiveData
import timber.log.Timber

class PllTrainerViewModel(app : Application): AndroidViewModel(app), KoinComponent, WrongAnswerInt {
    private val sp = get<SharedPreferences>()
    private val ctx = app.baseContext

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

    var rightAnswerCount = ObservableField<String>("0")
    var wrongAnswerCount = ObservableField<String>("0")

    var showStartButton = ObservableBoolean(false)

    private var layeredImageDrawable: LayerDrawable = getScrambledDrawable(resetCube())
    var imageDrawable = ObservableField<LayerDrawable>(layeredImageDrawable)

    val showRightAnswer = ObservableBoolean(false)
    val showWrongAnswer = ObservableBoolean(false)

    private var curState = GameStates.STOPPED
    private var _state = curState.toMutableLiveData()
    val state: LiveData<GameStates> get() = _state

    //На входе разобранный по скрамблу куб, на выходе 28-ми слойный Drawable
    private fun getScrambledDrawable(scrambledCube: IntArray): LayerDrawable {
        //разворачиваем кубик т.к. с сине-оранжево-белой стороны (для моей азбуки) его проще отобразить, т.к. это первые три стороны в Array
        val scramble = "y y"
        val rotatedCube = runScramble(scrambledCube, scramble)

        rotatedCube[27] = 6
        return LayerDrawable( Array(28) { i ->
            val layer = ContextCompat.getDrawable(ctx, ctx.resources.getIdentifier("z_2s_0$i", "drawable", ctx.packageName))
            val color = ContextCompat.getColor(ctx, cubeColor[rotatedCube[27-i]])
            layer?.let { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DrawableCompat.setTint(layer, color)
            } else {
                @Suppress("DEPRECATION")
                layer.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)
            }}
            layer
        })
    }

    private var btnList = listOf<String>("Ua","Ub","Ra","Rb")
    private var _buttonsList = btnList.toMutableLiveData()
    val buttonsList: LiveData<List<String>> get() = _buttonsList

    fun startGame() {

    }

    fun selectAnswer(letter: String) {
        Timber.d("$TAG .selectAnswer letter = [${letter}]")
    }

    override val wrongAnswerText: ObservableField<String>
        get() = wrongAnswerCount

    override fun stopGame() {
        Timber.d("$TAG .stopGame ")
        TODO("Not yet implemented")
    }

    override fun continueGame() {
        TODO("Not yet implemented")
    }



}