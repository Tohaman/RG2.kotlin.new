package ru.tohaman.rg2.ui.youtube

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.koin.android.ext.android.inject
import ru.tohaman.rg2.Constants.IS_VIDEO_SCREEN_ON
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.databinding.ActivityYoutubeBinding
import ru.tohaman.rg2.ui.shared.MyDefaultActivity
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class YouTubeActivity: MyDefaultActivity() {
    private var videoId = ""
    private lateinit var youTubePlayerView: YouTubePlayerView
    private var currentSecond = 0f
    private var startTime = 0f
    private lateinit var player: YouTubePlayer
    private var currentState: PlayerConstants.PlayerState = PlayerConstants.PlayerState.UNKNOWN
    private val sp: SharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDisplayOnProperties()

        var timeFromArgs: String = "0:00"
//        intent.data?.getQueryParameter("time")?.let { timeFromArgs = it }
//        intent.data?.getQueryParameter("link")?.let { videoId = it }
        timeFromArgs = intent.getStringExtra("time") ?: "0:00"
        videoId = intent.getStringExtra("link") ?: "Kul7jGhYiPQ"
        currentSecond = stringToTimeMillis(timeFromArgs)
        startTime = currentSecond

        val data = intent.data
        Timber.d ("$TAG startYouTube with - $data, $timeFromArgs, $videoId")

        val binding = DataBindingUtil.setContentView<ActivityYoutubeBinding>(this, R.layout.activity_youtube)
        setYouTubePlayerButtons(binding)
    }


    private fun setYouTubePlayerButtons(binding: ActivityYoutubeBinding) {
        //Используем плагин https://github.com/PierfrancescoSoffritti/android-youtube-player#api-documentation
        binding.youtubeView.enabled = true
        youTubePlayerView = binding.youtubeView.youtubePlayerView
        lifecycle.addObserver(youTubePlayerView)
        //youTubePlayerView.enterFullScreen()

//        val uiController = youTubePlayerView.getPlayerUiController()
//        val backwardDrawable = ContextCompat.getDrawable(this, R.drawable.ic_backward)!!
//        val forward10secDrawable = ContextCompat.getDrawable(this, R.drawable.ic_forward)!!
//        uiController.setCustomAction1(backwardDrawable, backwardClickListener)
//        uiController.setCustomAction2(forward10secDrawable, forwardClickListener)
//        uiController.showCustomAction1(true)
//        uiController.showCustomAction2(true)

        binding.toMarkButton.setOnClickListener(toMarkClickListener)
        binding.backButton.setOnClickListener(backwardClickListener)
        binding.forwardButton.setOnClickListener(forwardClickListener)
        binding.playPauseButton.setOnClickListener(playClickListener)


        val playDrawable = ContextCompat.getDrawable(this, R.drawable.player_play)!!
        val pauseDrawable = ContextCompat.getDrawable(this, R.drawable.player_pause)!!

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

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                currentState = state
                if (state == PlayerConstants.PlayerState.PLAYING)
                    binding.playPauseButton.background = pauseDrawable
                else
                    binding.playPauseButton.background = playDrawable
                super.onStateChange(youTubePlayer, state)
            }
        })
    }

    private val playClickListener = View.OnClickListener { _ ->
        if (currentState == PlayerConstants.PlayerState.PLAYING)
            player.pause()
        else
            player.play()
    }

    private val toMarkClickListener = View.OnClickListener { _ ->
        player.seekTo(startTime)                        // Перемотка к месту откуда начали смотреть видео
    }

    private val backwardClickListener = View.OnClickListener { _ ->
        player.seekTo(currentSecond - 10)         // Перемотаем на 10 сек назад
    }

    private val forwardClickListener = View.OnClickListener { _ ->
        player.seekTo(currentSecond + 10)       //Перемотка на 10 сек вперед
    }

    //Преобразует строку вида 0:25 в милисекунды (25000)
    private fun stringToTimeMillis(text: String): Float {
        val date: Date
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("m:s", Locale.ENGLISH)
        try {
            date = format.parse(text)!!
        } catch (e: ParseException) {
            Timber.e( "$TAG Это не должно произойти. Ошибка при преобразовании даты. $e")
            //но если произошло, то считаем что видео воспроизводится с начала возвращаем 0 милисек
            return 0f
        }

        calendar.time = date
        val second = calendar.get(Calendar.SECOND)
        val minute = calendar.get(Calendar.MINUTE)
        return (minute * 60 + second).toFloat()
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
}