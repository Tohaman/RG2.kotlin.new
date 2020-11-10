package ru.tohaman.rg2.ui.youtube

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import org.koin.core.KoinComponent
import ru.tohaman.rg2.DebugTag
import ru.tohaman.rg2.DebugTag.TAG
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

//Используем плагин https://github.com/PierfrancescoSoffritti/android-youtube-player#api-documentation
class YouTubeViewModel: ViewModel(), KoinComponent {
    private var player: YouTubePlayer? = null
    var currentState = ObservableField<PlayerConstants.PlayerState>(PlayerConstants.PlayerState.UNKNOWN)
    private var startTime = 0f
    private var currentSecond = 0f
    var videoId = ""
    var algorithm = ObservableField<String>("")

    var youTubePlayerListener = object : AbstractYouTubePlayerListener() {
        override fun onReady(youTubePlayer: YouTubePlayer) {
            //cueVideo - загрзука видео с определенного момента, но без автостарта
            //loadVideo - с автостартом
            youTubePlayer.loadVideo(videoId, currentSecond)
            player = youTubePlayer

        }

        override fun onPlaybackRateChange(youTubePlayer: YouTubePlayer, playbackRate: PlayerConstants.PlaybackRate) {
            super.onPlaybackRateChange(youTubePlayer, playbackRate)
            Timber.d("$TAG .onPlaybackRateChange playbackRate = [${playbackRate}]")
        }

        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            currentSecond = second
            super.onCurrentSecond(youTubePlayer, second)
        }

        override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
            currentState.set(state)
        }
    }


    fun onMarkClick() {
        player?.seekTo(startTime)
    }

    fun onForwardClick() {
        currentSecond += 10
        player?.seekTo(currentSecond)
    }

    fun onBackwardClick() {
        currentSecond -= 10
        if (currentSecond < 0) currentSecond = 0f
        player?.seekTo(currentSecond)
    }

    fun onPlayPauseClick() {
        if (currentState.get() == PlayerConstants.PlayerState.PLAYING)
            player?.pause()
        else
            player?.play()
    }


    fun setStartTime(stringTime: String) {
        startTime = stringToTimeInSeconds(stringTime)
        currentSecond = startTime
    }


    //Преобразует строку вида 1:25 в секунды (85)
    private fun stringToTimeInSeconds(text: String): Float {
        val date: Date
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("m:s", Locale.ENGLISH)
        try {
            date = format.parse(text)!!
        } catch (e: ParseException) {
            Timber.e( "${DebugTag.TAG} Это не должно произойти. Ошибка при преобразовании даты. $e")
            //но если произошло, то считаем что видео воспроизводится с начала возвращаем 0 сек
            return 0f
        }

        calendar.time = date
        val second = calendar.get(Calendar.SECOND)
        val minute = calendar.get(Calendar.MINUTE)
        return (minute * 60 + second).toFloat()
    }

}