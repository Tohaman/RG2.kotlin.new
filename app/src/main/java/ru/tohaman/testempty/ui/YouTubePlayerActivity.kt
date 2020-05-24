package ru.tohaman.testempty.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tohaman.testempty.Constants.IS_VIDEO_SCREEN_ON
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.databinding.ActivityYoutubeBinding
import ru.tohaman.testempty.generated.callback.OnClickListener
import ru.tohaman.testempty.ui.shared.MyDefaultActivity
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class YouTubePlayerActivity: MyDefaultActivity(), KoinComponent {
    private var videoId = ""
    private lateinit var youTubePlayerView: YouTubePlayerView
    private var currentSecond = 0f
    private var startTime = 0f
    private lateinit var player: YouTubePlayer
    private val sp = get<SharedPreferences>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDisplayOnProperties()

        var timeFromArgs: String
        intent.data!!.getQueryParameter("time").let { timeFromArgs = it!! }
        intent.data!!.getQueryParameter("link").let { videoId = it!! }
        currentSecond = stringToTimeMillis(timeFromArgs)
        startTime = currentSecond

        Timber.d ("$TAG startYouTube with - $timeFromArgs , $videoId")

        val binding = DataBindingUtil.setContentView<ActivityYoutubeBinding>(this, R.layout.activity_youtube)
        setYouTubePlayerButtons(binding)
    }

    private fun setYouTubePlayerButtons(binding: ActivityYoutubeBinding) {
        //Используем плагин https://github.com/PierfrancescoSoffritti/android-youtube-player#api-documentation
        binding.youtubeView.enabled = true
        youTubePlayerView = binding.youtubeView.youtubePlayerView
        lifecycle.addObserver(youTubePlayerView)
        youTubePlayerView.enterFullScreen()

        val uiController = youTubePlayerView.getPlayerUiController()
        val backward10secDrawable = ContextCompat.getDrawable(this, R.drawable.ic_backward)!!
        val forward10secDrawable = ContextCompat.getDrawable(this, R.drawable.ic_forward)!!
        uiController.setCustomAction1(backward10secDrawable, backwardClickListner)
        uiController.setCustomAction2(forward10secDrawable, forwardClickListner)
        uiController.showCustomAction1(true)
        uiController.showCustomAction2(true)


        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                //cueVideo - загрзука видео с определенного момента, но без автостарта
                //loadVideo - с автостартом
                youTubePlayer.loadVideo(videoId, currentSecond)
                player = youTubePlayer
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                currentSecond = second
                super.onCurrentSecond(youTubePlayer, second)
            }
        })
    }

    private fun setDisplayOnProperties() {
        // Проверяем значения из настроек, выключать экран или нет при прсмотре видео
        val sleepOnYouTube = sp.getBoolean(IS_VIDEO_SCREEN_ON, false)
        if (sleepOnYouTube) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    //Нажатие левой кнопки (рядом с Play)
    private val backwardClickListner = View.OnClickListener { _ ->
        //player.seekTo(currentSecond - 10)         // Перемотаем на 10 сек назад
        player.seekTo(startTime)                    // Перемотка к месту откуда начали смотреть видео
    }

    //Нажатие правой от Play кнопки
    private val forwardClickListner = View.OnClickListener { _ ->
        player.seekTo(currentSecond + 10)       //Перемотка на 10 сек вперед
    }

    //Преобразует строку вида 0:25 в милисекунды (25000)
    private fun stringToTimeMillis(text: String): Float {
        var date: Date
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("m:s", Locale.ENGLISH)
        try {
            date = format.parse(text)!!
        } catch (e: ParseException) {
            Timber.w( "$TAG Это не должно произойти. Ошибка при преобразовании даты. $e")
            //но если произошло, то считаем что видео воспроизводится с начала возвращаем 0 милисек
            return 0f
        }

        calendar.time = date
        val second = calendar.get(Calendar.SECOND)
        val minute = calendar.get(Calendar.MINUTE)
        return (minute * 60 + second).toFloat()
    }
}