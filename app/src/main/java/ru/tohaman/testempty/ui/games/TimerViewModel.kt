package ru.tohaman.testempty.ui.games

import android.app.Application
import android.content.SharedPreferences
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
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
import ru.tohaman.testempty.Constants.TIMER_NEED_BACK
import ru.tohaman.testempty.Constants.TIMER_NEED_SCRAMBLE
import ru.tohaman.testempty.Constants.TIMER_ONE_HANDED
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dataSource.generateScrambleWithParam
import ru.tohaman.testempty.dataSource.getLettersFromCurrentAzbuka
import ru.tohaman.testempty.dataSource.prepareAzbukaToShowInGridView
import ru.tohaman.testempty.interfaces.ShowPreloaderInt
import timber.log.Timber
import kotlin.concurrent.timerTask

class TimerViewModel(app : Application): AndroidViewModel(app), KoinComponent, ShowPreloaderInt {
    // ------- Общие переменные ---------
    private val repository : ItemsRepository by inject()
    private val sp = get<SharedPreferences>()
    private val ctx = app.baseContext

    //-------- Настройки таймера (TimerSettings) ----------------

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

    private var _needShowScramble = sp.getBoolean(TIMER_NEED_SCRAMBLE, true)
    val needShowScramble = ObservableBoolean(_needShowScramble)
    val showTopLayout = ObservableBoolean(true)

    fun needScrambleChange(value: Boolean) {
        _needShowScramble = value
        sp.edit().putBoolean(TIMER_NEED_SCRAMBLE, value).apply()
    }

    private val _needBackButton = sp.getBoolean(TIMER_NEED_BACK, true)
    val needBackButton = ObservableBoolean(_needBackButton)

    fun needBackButtonChange(value: Boolean) {
        sp.edit().putBoolean(TIMER_NEED_BACK, value).apply()
    }


    //------------------ Управление самим Таймером -----------------------

    private val nullTime = "0:00.00"
    var curTime = ObservableField<String>(nullTime)

    private var cornerBuffer =true
    private var edgeBuffer = true
    private var scrambleLength = 14
    private var currentLetters: Array<String>? = null


    private var _currentScramble = sp.getString(CURRENT_SCRAMBLE, "R F L B U2 L B' R F' D B R L F D R' D L") ?: ""
    var currentScramble = ObservableField<String>(_currentScramble)

    fun reloadScrambleParameters(){
        viewModelScope.launch (Dispatchers.IO) {
            cornerBuffer = sp.getBoolean(Constants.BUFFER_CORNER, true)
            edgeBuffer = sp.getBoolean(Constants.BUFFER_EDGE, true)
            scrambleLength = sp.getInt(Constants.SCRAMBLE_LENGTH, 14)
            _currentScramble = sp.getString(CURRENT_SCRAMBLE, "R F L B U2 L B' R F' D B R L F D R' D L") ?: ""
            Timber.d("$TAG .reloadScrambleParameters $_currentScramble")
            currentScramble.set(_currentScramble)
            val listDBAzbuka = repository.getAzbukaItems(Constants.CURRENT_AZBUKA)
            currentLetters = getLettersFromCurrentAzbuka(prepareAzbukaToShowInGridView(listDBAzbuka))
        }

    }

    //-------------- Генерация нового скрамбла --------------------

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
                //сохраняем скрамбл
                sp.edit().putString(CURRENT_SCRAMBLE, scramble).apply()
            }
            //delay(2000)
            showPreloader.set(false)                                        //убираем прелоадер
        }
    }

    //------- Обработка касаний --------------------

    private var startTime: Long = 0
    private var stopTime: Long = 0
    private var resetPressedTime: Long = 0
    private var timerState = TimerStates.STOPPED
    private var delayMills = 500L

    private val redColor = ContextCompat.getColor(ctx, R.color.red)
    private val yellowColor = ContextCompat.getColor(ctx, R.color.yellow)
    private val greenColor = ContextCompat.getColor(ctx, R.color.green)
    var leftCircleColor = ObservableInt(redColor)
    var rightCircleColor = ObservableInt(redColor)


    fun onTouchOneHandPanel(): View.OnTouchListener? {
        return View.OnTouchListener { v, event ->
            val action = event.action
            //Timber.d("$TAG action = $action, $v")
            when (action) {
                MotionEvent.ACTION_DOWN ->  onOneHandActionDown()
                MotionEvent.ACTION_UP -> onOneHandActionUp()
            }
            true
        }
    }

    fun onTouchLeftPanel(): View.OnTouchListener? {
        return View.OnTouchListener { v, event ->
            val action = event.action
            //Timber.d("$TAG action = $action, $v")
            when (action) {
                MotionEvent.ACTION_DOWN ->  onTwoHandActionDown(leftCircleColor, rightCircleColor)
                MotionEvent.ACTION_UP -> onTwoHandActionUp(leftCircleColor)
            }
            true
        }
    }

    fun onTouchRightPanel(): View.OnTouchListener? {
        return View.OnTouchListener { v, event ->
            val action = event.action
            //Timber.d("$TAG action = $action, $v")
            when (action) {
                MotionEvent.ACTION_DOWN ->  onTwoHandActionDown(rightCircleColor, leftCircleColor)
                MotionEvent.ACTION_UP -> onTwoHandActionUp(rightCircleColor)
            }
            true
        }
    }

    fun onTouchTopInsidePanel(): View.OnTouchListener? {
        return View.OnTouchListener { v, event ->
            val action = event.action
            //Timber.d("$TAG action = $action, $v")
            if (action == MotionEvent.ACTION_DOWN) {
                tryToPauseTimer()
                true
            } else false
        }
    }

    //Нажали на однорукую плашку
    private fun onOneHandActionDown() {
        when (timerState) {
            TimerStates.STOPPED -> {    //Текущий статус "Остановлен", пытаемся перевести в READY
                //если таймер в статусе остановлен, запоминаем текущее время и красим кружки в желтый
                leftCircleColor.set(yellowColor)
                rightCircleColor.set(yellowColor)
                resetPressedTime = System.currentTimeMillis()
                tryChangeStateToReady()
            }
            TimerStates.READY -> { Timber.d ("$TAG ACHTUNG!!! Что-то пошло не так, нельзя нажать однорукую плашку в статусе READY") }
            TimerStates.STARTED -> {
                if ((startTime + 1000) < System.currentTimeMillis()) {
                    stopTimer()
                }
            }
            TimerStates.PAUSED -> { Timber.d("$TAG ничего не делаем, ждем когда отпустим")}
        }
    }

    private fun tryChangeStateToReady() {
        viewModelScope.launch(Dispatchers.Main) {
            //Если старт с задержкой, то ждем иначе уменьшаем resetPressedTime, чтобы сразу перевести в READY
            if (isTimerDelayed.get()) delay(delayMills) else resetPressedTime -= delayMills
            //Проверяем не изменился ли resetPressedTime за время ожидания, и переводим в статус READY, а кружки в зеленый
            if (resetPressedTime + delayMills - 1 < System.currentTimeMillis()) {
                timerState = TimerStates.READY
                leftCircleColor.set(greenColor)
                rightCircleColor.set(greenColor)
                curTime.set(nullTime)       //Сбрасываем время на таймере
            }
        }
    }

    //Отпустили "однорукую" плашку
    private fun onOneHandActionUp() {
        Timber.d("$TAG Отпустили плашку")
        resetPressedTime = System.currentTimeMillis()   //сбросим resetPressedTime, чтобы корутина не перевела в READY, если еще не в нем
        when (timerState) {
            TimerStates.STOPPED -> {    //Если еще не READY, значит покрасим кружки в красный
                leftCircleColor.set(redColor)
                rightCircleColor.set(redColor)
            }
            TimerStates.READY -> startTimer()
            TimerStates.STARTED -> Timber.d("$TAG Ничего не делаем, значит было ложное нажатие в первую секунду")
            TimerStates.PAUSED -> resumeTimer()
        }
    }

    //Нажали на одну из двух "двуруких" плашек
    private fun onTwoHandActionDown(masterCircle: ObservableInt, slaveCircle: ObservableInt) {
        masterCircle.set(yellowColor)
        when (timerState) {
            TimerStates.STOPPED -> {
                resetPressedTime = System.currentTimeMillis()
                if (slaveCircle.get() == yellowColor) {
                    tryChangeStateToReady()
                }
            }
            TimerStates.READY -> { Timber.d ("$TAG ACHTUNG!!! Что-то пошло не так, нельзя нажать плашку в статусе READY, только еще STOPPED или уже STARTED") }
            TimerStates.STARTED -> {
                if (slaveCircle.get() == yellowColor) {
                    stopTimer()
                }
            }
            TimerStates.PAUSED -> { }
        }
    }

    //Отпустили "двурукую" плашку
    private fun onTwoHandActionUp(masterCircle: ObservableInt) {
        Timber.d("$TAG Отпустили двурукую плашку")
        resetPressedTime = System.currentTimeMillis()   //сбросим resetPressedTime, чтобы корутина не перевела в READY, если еще не в нем
        when (timerState) {
            TimerStates.STOPPED -> { masterCircle.set(redColor) }
            TimerStates.READY -> startTimer()
            TimerStates.STARTED -> { masterCircle.set(greenColor) }
            TimerStates.PAUSED -> {
                masterCircle.set(greenColor)
                resumeTimer()
            }
        }
    }

    private fun tryToPauseTimer() {
        if (timerState == TimerStates.STARTED) {
            timerState = TimerStates.PAUSED
            stopTime = System.currentTimeMillis() - startTime
        }
    }

    private fun resumeTimer() {
        startTime = System.currentTimeMillis() - stopTime
        timerState = TimerStates.STARTED
        startShowTime()
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        timerState = TimerStates.STARTED
        needShowScramble.set(false)
        showTopLayout.set(false)
        startShowTime()
    }

    //Возвращаем true если статус был не STOPPED, т.е. что-то останавливали
    fun stopTimer() : Boolean {
        return if (timerState != TimerStates.STOPPED) {
            needShowScramble.set(_needShowScramble)
            showTopLayout.set(true)
            timerState = TimerStates.STOPPED
            true
        } else false
    }

    private fun startShowTime () {
        //Используя корутины Котлина, отображаем время таймера, пока isTimerStart не станет false
        viewModelScope.launch {
            do {
                showTimerTime()
                delay(30)
            } while (timerState == TimerStates.STARTED)
        }
    }

    private fun showTimerTime() {
        val currentTime = System.currentTimeMillis() - startTime
        val millis = ((currentTime % 1000) / 10).toInt()             // сотые доли секунды
        var seconds = (currentTime / 1000).toInt()
        var minutes = seconds / 60
        seconds %= 60
        if (minutes > 59) {  //если получилось больше 60 минут, то добавляем к начальному времени 60 мин.(обнуляем таймер)
            startTime += 3600000; minutes = 0
        }
        val showTime = String.format("%d:%02d.%02d", minutes, seconds, millis)
        Timber.d("$TAG showTime $showTime")
        curTime.set(showTime)
    }

}

enum class TimerStates {STOPPED, READY, STARTED, PAUSED}