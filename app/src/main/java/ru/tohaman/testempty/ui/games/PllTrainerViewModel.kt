package ru.tohaman.testempty.ui.games

import android.app.Application
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import ru.tohaman.testempty.Constants.ALL_PLL_COUNT
import ru.tohaman.testempty.Constants.IS_2SIDE_RECOGNITION
import ru.tohaman.testempty.Constants.PLLS_NAME
import ru.tohaman.testempty.Constants.PLL_TRAINING_TIMER
import ru.tohaman.testempty.Constants.PLL_TRAINING_TIMER_TIME
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dataSource.cubeColor
import ru.tohaman.testempty.dataSource.resetCube
import ru.tohaman.testempty.dataSource.runScramble
import ru.tohaman.testempty.dbase.entitys.PllGameItem
import ru.tohaman.testempty.interfaces.WrongAnswerInt
import ru.tohaman.testempty.utils.toMutableLiveData
import timber.log.Timber
import java.util.*

class PllTrainerViewModel(app : Application): AndroidViewModel(app), KoinComponent, WrongAnswerInt {
    private val repository : ItemsRepository by inject()
    private val sp = get<SharedPreferences>()
    private val ctx = app.baseContext

    //----------------------- Settings -------------------------------

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

    private var _pllTrainingTimer = sp.getBoolean(PLL_TRAINING_TIMER, true)
    val pllTrainingTimer = ObservableBoolean(_pllTrainingTimer)

    private var _pllTrainingTimerTime = sp.getInt(PLL_TRAINING_TIMER_TIME, 6)
    val pllTrainingTimerTime = ObservableInt(_pllTrainingTimerTime)

    fun pllTrainingTimerChange(value: Boolean) {
        _pllTrainingTimer = value
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


    // -------------------- Game ------------------------------

    private var correctAnswer = 0 //Random().nextInt(21)
    var rightAnswer = ObservableField<String>("A")
    var timerProgress = ObservableInt(100)

    private var _rightAnswerCount = 0
    var rightAnswerCount = ObservableField<String>("0")
    private var _wrongAnswerCount = 0
    var wrongAnswerCount = ObservableField<String>("0")

    var showStartButton = ObservableBoolean(false)

    private var layeredImageDrawable: LayerDrawable = getScrambledDrawable(resetCube())
    var imageDrawable = ObservableField<LayerDrawable>(layeredImageDrawable)

    val showRightAnswer = ObservableBoolean(false)
    val showWrongAnswer = ObservableBoolean(false)

    private var curState = GameStates.STOPPED
    private var _state = curState.toMutableLiveData()
    val state: LiveData<GameStates> get() = _state

    var pllGameItems = listOf<PllGameItem>()
    init {
        viewModelScope.launch (Dispatchers.IO){
            pllGameItems = repository.getAllPllGameItems()
        }
    }


    //На входе разобранный по скрамблу куб (IntArray), на выходе 28-ми слойный Drawable
    private fun getScrambledDrawable(scrambledCube: IntArray): LayerDrawable {
        //разворачиваем кубик т.к. с сине-оранжево-белой стороны (для моей азбуки) его проще отобразить, т.к. это первые три стороны в Array
        val scramble = "y y"
        val rotatedCube = runScramble(scrambledCube, scramble)

        rotatedCube[27] = 6
        return LayerDrawable( Array(28) { i ->
            val resID = ctx.resources.getIdentifier("z_2s_0$i", "drawable", ctx.packageName)
            val layer = AppCompatResources.getDrawable(ctx, resID)
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

    private var btnList = PLLS_NAME
    private var _buttonsList = btnList.toMutableLiveData()
    val buttonsList: LiveData<List<String>> get() = _buttonsList

    fun startGame() {
        _state.postValue(GameStates.WAITING_4_ANSWER)
        //Выводим кнопку "Начать"
        showStartButton.set(false)

        nextPll()
    }

    fun selectAnswer(letter: String) {
        Timber.d("$TAG .selectAnswer letter = [${letter}]")
        if (_state.value == GameStates.WAITING_4_ANSWER) {
            _state.postValue(GameStates.SHOW_ANSWER)
            viewModelScope.launch {
                if (letter == rightAnswer.get()) {
                    showRightAnswerAlert()
                } else {
                    val wrongText =
                        ctx.getText(R.string.wrong_answer_text) as String + " ${rightAnswer.get()}"
                    showWrongAnswerAlert(wrongText)
                }
            }
        }
    }

    private suspend fun showRightAnswerAlert() {
        _rightAnswerCount += 1
        rightAnswerCount.set(_rightAnswerCount.toString())
        showRightAnswer.set(true)
        delay(1500)
        showRightAnswer.set(false)
        nextPll()
        _state.postValue(GameStates.WAITING_4_ANSWER)
        startTimer()
    }

    private fun showWrongAnswerAlert(alert: String) {
        _wrongAnswerCount += 1
        wrongAnswerCount.set(_wrongAnswerCount.toString())
        showWrongAnswer.set(true)
        wrongAnswerText.set(alert)
    }

    private var _wrongAnswerText = ObservableField("Неверно")
    override val wrongAnswerText: ObservableField<String>
        get() = _wrongAnswerText

    override fun stopGame() {
        Timber.d("$TAG .stopGame ")
        //Меняем статус игры на STOPPED
        _state.postValue(GameStates.STOPPED)
        //Сбрасываем счетчики
        _wrongAnswerCount = 0
        wrongAnswerCount.set(_wrongAnswerCount.toString())
        _rightAnswerCount = 0
        rightAnswerCount.set(_rightAnswerCount.toString())
        //Убираем сообщения об ответе
        showWrongAnswer.set(false)
        showRightAnswer.set(false)
        //Выводим кнопку "Начать"
        showStartButton.set(true)
        //Выводим полный прогрессбар
        timerProgress.set(100)
    }

    override fun continueGame() {
        showWrongAnswer.set(false)
        nextPll()
        _state.postValue(GameStates.WAITING_4_ANSWER)
        startTimer()
    }

    private fun nextPll() {
        val scramble = "x x ${getRandomPll()}"
        rightAnswer.set(pllGameItems[correctAnswer].InternationalName)
        val cube = runScramble(resetCube(), scramble)
        layeredImageDrawable = getScrambledDrawable(cube)
        imageDrawable.set(layeredImageDrawable)
    }

    private fun getRandomPll() : String {
        // выбираем случайный алгоритм
        var rnd = Random().nextInt(21)
        while (rnd == correctAnswer) { rnd = Random().nextInt(21) }
        correctAnswer = rnd
        return  pllGameItems[correctAnswer].scramble
    }

    private fun startTimer() {
        timerProgress.set(100)
        if (_pllTrainingTimer) { //Запускаем таймер, только если он включен в настройках
            val maxTime = System.currentTimeMillis() + _pllTrainingTimerTime * 1000
            viewModelScope.launch(Dispatchers.Main) {
                do {
                    delay(16)   //примерно 1/60 секунды
                    val ostTime = (maxTime - System.currentTimeMillis()) / (_pllTrainingTimerTime * 10)
                    timerProgress.set(ostTime.toInt())
                } while ((_state.value == GameStates.WAITING_4_ANSWER) and (ostTime > 0))

                //если цикл прекратился из-за того, что кончилось время
                if (_state.value == GameStates.WAITING_4_ANSWER) {
                    _state.postValue(GameStates.TIME_IS_OVER)
                    val wrongText = ctx.getText(R.string.time_is_over) as String + " ${rightAnswer.get()}"
                    showWrongAnswerAlert(wrongText)
                }
            }
        }
    }

}