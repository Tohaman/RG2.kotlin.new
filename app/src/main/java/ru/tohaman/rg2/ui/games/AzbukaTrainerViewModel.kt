package ru.tohaman.rg2.ui.games

import android.app.Application
import android.content.SharedPreferences
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import ru.tohaman.rg2.Constants
import ru.tohaman.rg2.Constants.CURRENT_AZBUKA
import ru.tohaman.rg2.Constants.TRAINING_TIMER
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.dataSource.*
import ru.tohaman.rg2.interfaces.WrongAnswerInt
import ru.tohaman.rg2.utils.toMutableLiveData
import timber.log.Timber
import java.util.*


class AzbukaTrainerViewModel(app : Application): AndroidViewModel(app), KoinComponent, WrongAnswerInt {
    private val repository : ItemsRepository by inject()
    private val sp = get<SharedPreferences>()
    private val ctx = app.baseContext

    private var trainingCorners = sp.getBoolean(Constants.TRAINING_CORNERS, true)
    private var trainingEdges = sp.getBoolean(Constants.TRAINING_EDGES, true)
    private var trainingTimer = sp.getBoolean(TRAINING_TIMER, true)
    private var trainingTimerTime = sp.getInt(Constants.TRAINING_TIMER_TIME, 6)
    private var edgesArray = listOf<String>()
    private var cornersArray = listOf<String>()

    val rightAnswerLetter = ObservableField<String>("")
    val timerProgress = ObservableInt(100)

    private var _rightAnswerCount = 0
    val rightAnswerCount = ObservableField<String>(_rightAnswerCount.toString())
    private var _wrongAnswerCount = 0
    val wrongAnswerCount = ObservableField<String>(_wrongAnswerCount.toString())
    val showRightAnswer = ObservableBoolean(false)
    val showWrongAnswer = ObservableBoolean(false)
    val showStartButton = ObservableBoolean(true)
    private var _wrongAnswerText = ObservableField("Неверно")
    override val wrongAnswerText: ObservableField<String>
        get() = _wrongAnswerText

    private var currentLetters = getMyAzbuka()    //лист из 54 элементов, например как getMyAzbuka()
    private var btnList = listOf<String>()
    private var _buttonsList = btnList.toMutableLiveData()
    val buttonsList: LiveData<List<String>> get() = _buttonsList

    private var layeredImageDrawable: LayerDrawable? = null
    var imageDrawable = ObservableField<LayerDrawable>(layeredImageDrawable)
    private val width = 200

    private var curState = GameStates.STOPPED
    private var _state = curState.toMutableLiveData()
    val state: LiveData<GameStates> get() = _state

    init {
        reloadSettings()
    }

    fun reloadSettings() {
        viewModelScope.launch (Dispatchers.IO) {
            trainingCorners = sp.getBoolean(Constants.TRAINING_CORNERS, true)
            trainingEdges = sp.getBoolean(Constants.TRAINING_EDGES, true)
            trainingTimer = sp.getBoolean(TRAINING_TIMER, true)
            trainingTimerTime = sp.getInt(Constants.TRAINING_TIMER_TIME, 6)

            val listDBAzbuka = repository.getAzbukaItems(CURRENT_AZBUKA)
            currentLetters = getLettersFromCurrentAzbuka(prepareAzbukaToShowInGridView(listDBAzbuka))
            edgesArray = covertAzbukaToSortedEdgeList(listDBAzbuka)
            cornersArray = covertAzbukaToSortedCornersList(listDBAzbuka)
        }
    }

    fun startGame() {
        _state.postValue(GameStates.WAITING_4_ANSWER)
        showStartButton.set(false)
        startTimer()
    }

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
        Timber.d("$TAG .continueGame ")
        showWrongAnswer.set(false)
        loadNextBlind()
        _state.postValue(GameStates.WAITING_4_ANSWER)
        startTimer()
    }

    fun loadNextBlind() {
        //сгенерируем скрамбл и разберем по нему кубик
        val scramble = generateScramble(19)
        val scrambledCube = runScramble(resetCube(), scramble)

        val layerImageDrawable = getScrambledDrawable(scrambledCube)
        val slot = selectRandomSlot()
        //поскольку slot в диапазоне от [0 до 7), то можем использовать slotElementNumbers[slot]!!
        val colorOfElement = getColorOfElement(scrambledCube, slotElementNumbers[slot]!!.first, slotElementNumbers[slot]!!.second)

        //и по цвету элемента и его типу (угол/ребро), определяем его номер в кубике через mainCorner/mainEdge, а по номеру достаем выбранную букву из азбуки
        val letter = if (slot < 3) {
            currentLetters[mainCorner[colorOfElement]!!]
        } else {
            currentLetters[mainEdge[colorOfElement]!!]
        }
        rightAnswerLetter.set(letter)      //если включена подсказка, то выводим туда загаданную букву

        layeredImageDrawable = getMaskedDrawable(layerImageDrawable, slot)
        imageDrawable.set(layeredImageDrawable)
        //_state.postValue(GameStates.WAITING_4_ANSWER)
    }

    private fun selectRandomSlot(): Int {
        //выбираем случайный слот из диапазона и смотрим, какой там элемент (буква) 0..2 - слоты углов, 3..6 - ребер
        val fromSlot = if (trainingCorners) 0 else 3
        val toSlot = if (trainingEdges) 7 else 3
        val slot = Random().nextInt(fromSlot..toSlot)
        //Обновим буквы на кнопкках ответов
        if (slot in 0..2) _buttonsList.postValue(cornersArray) else _buttonsList.postValue(edgesArray)
        return slot
    }

    //На входе разобранный по скрамблу куб, на выходе 28-ми слойный Drawable
    private fun getScrambledDrawable(scrambledCube: IntArray): LayerDrawable {
        //разворачиваем кубик т.к. с сине-оранжево-белой стороны (для моей азбуки) его проще отобразить, т.к. это первые три стороны в Array
        val scramble = "y y"
        val rotatedCube = runScramble(scrambledCube, scramble)

        rotatedCube[27] = 6
        return LayerDrawable( Array(28) { i ->
            val resID = ctx.resources.getIdentifier("z_2s_0$i", "drawable", ctx.packageName)
            val layer = AppCompatResources.getDrawable(ctx, resID)
            val color = ContextCompat.getColor(ctx,cubeColor[rotatedCube[27-i]])
            layer?.let { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DrawableCompat.setTint(layer, color)
            } else {
                @Suppress("DEPRECATION")
                layer.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)
            }}
            layer
        })
    }

    //Накладываем маску опредленного слота на изображение кубика
    private fun getMaskedDrawable(initDrawable: LayerDrawable, slot: Int) : LayerDrawable {
        //Преобразуем LayerDrawable в Bitmap
        val originalBitmap = getBitmapFromDrawable(initDrawable, width)
        val maskBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(maskBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //заливаем канву (фон) полупрозрачным
        canvas.drawARGB(100, 0, 0, 0) //прозрачность 100 (0 - в итоге фон будет непрозрачный, 255 - полностью прозрачный)

        //получаем координаты и радиус круга подсветки для слота из массива констант ArraysForScramble.kt
        val tripleCoordinates = slotLightingCoordinate[slot]
        //рисуем на canvas круг подсветки, если получили координаты из массива
        tripleCoordinates?.let { canvas.drawCircle(
            width * tripleCoordinates.first.toFloat(),
            width * tripleCoordinates.second.toFloat(),
            width * tripleCoordinates.third.toFloat(),
            paint
        )}

        //накладываем маску из цвета фона (полупрозрачного) и непрозрачного кружка
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        val rect = Rect(0, 0, originalBitmap.width, originalBitmap.height)
        canvas.drawBitmap(originalBitmap, rect, rect, paint)

        //нарисуем исходник и преобразуем  в LayerDrawable
        val bitmapDrawable = BitmapDrawable(ctx.resources, maskBitmap)
        val drawableArray =  arrayOf (bitmapDrawable)
        return LayerDrawable (drawableArray)
    }

    private fun startTimer() {
        timerProgress.set(100)
        if (trainingTimer) {        //Запускаем таймер, только если он включен в настройках
            val maxTime = System.currentTimeMillis() + trainingTimerTime * 1000
            viewModelScope.launch(Dispatchers.Main) {
                do {
                    delay(16)   //примерно 60 раз в секунду
                    val ostTime = (maxTime - System.currentTimeMillis()) / (trainingTimerTime * 10)
                    timerProgress.set(ostTime.toInt())
                } while ((_state.value == GameStates.WAITING_4_ANSWER) and (ostTime > 0))

                if (_state.value == GameStates.WAITING_4_ANSWER) {
                    _state.postValue(GameStates.TIME_IS_OVER)
                    val wrongText = ctx.getText(R.string.time_is_over) as String + " ${rightAnswerLetter.get()}"
                    showWrongAnswerAlert(wrongText)
                }
            }
        }
    }


    fun selectAnswer(letter: String) {
        if (_state.value == GameStates.WAITING_4_ANSWER) {
            _state.postValue(GameStates.SHOW_ANSWER)
            viewModelScope.launch {
                if (letter == rightAnswerLetter.get()) {
                    showRightAnswerAlert()
                } else {
                    val wrongText =
                        ctx.getText(R.string.wrong_answer_text) as String + " ${rightAnswerLetter.get()}"
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
        loadNextBlind()
        _state.postValue(GameStates.WAITING_4_ANSWER)
        startTimer()
    }

    private fun showWrongAnswerAlert(alert: String) {
        _wrongAnswerCount += 1
        wrongAnswerCount.set(_wrongAnswerCount.toString())
        showWrongAnswer.set(true)
        wrongAnswerText.set(alert)
    }


    companion object {

        /**
         * Возвращает Bitmap из Drawable и масштабирует до expectedSize
         * Extract the Bitmap from a Drawable and resize it to the expectedSize conserving the ratio.
         *
         * @param drawable   Drawable used to extract the Bitmap. Can't be null.
         * @param expectSize Expected size for the Bitmap. Use @link #DEFAULT_DRAWABLE_SIZE to
         * keep the original [Drawable] size.
         * @return The Bitmap associated to the Drawable or null if the drawable was null.
         * @see <html>[StackOverflow answer](https://stackoverflow.com/a/10600736/1827254)</html>
         */

        private fun getBitmapFromDrawable(drawable: Drawable, expectSize: Int): Bitmap {
            val bitmap: Bitmap

            if (drawable is BitmapDrawable) {
                val bitmapDrawable = drawable as BitmapDrawable?
                if (bitmapDrawable!!.bitmap != null) {
                    return bitmapDrawable.bitmap
                }
            }

            bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
            } else {
                val ratio = if (expectSize != 1)
                    calculateRatio(drawable.intrinsicWidth, drawable.intrinsicHeight, expectSize)
                else
                    1.0f

                val width = (drawable.intrinsicWidth * ratio).toInt()
                val height = (drawable.intrinsicHeight * ratio).toInt()

                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            }

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }

        /**
         * Calculate the ratio to multiply the Bitmap size with, for it to be the maximum size of
         * "expected".
         *
         * @param height   Original Bitmap height
         * @param width    Original Bitmap width
         * @param expected Expected maximum size.
         * @return If height and with equals 0, 1 is return. Otherwise the ratio is returned.
         * The ration is base on the greatest side so the image will always be the maximum size.
         */
        private fun calculateRatio(height: Int, width: Int, expected: Int): Float {
            if (height == 0 && width == 0) {
                return 1f
            }
            return if (height > width)
                expected / width.toFloat()
            else
                expected / height.toFloat()
        }
    }

}

enum class GameStates {STOPPED, SHOW_ANSWER, WAITING_4_ANSWER, TIME_IS_OVER}