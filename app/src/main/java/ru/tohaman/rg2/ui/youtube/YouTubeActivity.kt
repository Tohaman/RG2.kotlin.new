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
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.tohaman.rg2.Constants.IS_VIDEO_SCREEN_ON
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.databinding.ActivityYoutubeBinding
import ru.tohaman.rg2.ui.shared.MyDefaultActivity
import ru.tohaman.rg2.ui.shared.UiUtilViewModel
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class YouTubeActivity: MyDefaultActivity() {
    private val sp: SharedPreferences by inject()
    private val youTubeViewModel by viewModel<YouTubeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDisplayOnProperties()

        val data = intent.data
//        var timeFromArgs: String = "0:00"
//        data?.getQueryParameter("time")?.let { timeFromArgs = it }
//        data?.getQueryParameter("link")?.let { videoId = it }
        val timeFromArgs = intent.getStringExtra("time") ?: "0:00"
        val videoId = intent.getStringExtra("link") ?: "Kul7jGhYiPQ"
        Timber.d ("$TAG startYouTube with - $data, $timeFromArgs, $videoId")

        youTubeViewModel.setStartTime(timeFromArgs)
        youTubeViewModel.videoId = videoId

        DataBindingUtil.setContentView<ActivityYoutubeBinding>(this, R.layout.activity_youtube)
            .apply {
                youtubeView.enabled = true
                viewModel = youTubeViewModel
                val youTubePlayerView = youtubeView.youtubePlayerView
                youTubePlayerView.addYouTubePlayerListener(youTubeViewModel.youTubePlayerListener)
                closeButton.setOnClickListener {
                    finish()
                }
            }
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