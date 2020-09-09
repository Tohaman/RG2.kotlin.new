package ru.tohaman.rg2.ui.youtube

import android.os.Bundle
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.tohaman.rg2.AppSettings
import ru.tohaman.rg2.Constants.ALG
import ru.tohaman.rg2.Constants.LINK
import ru.tohaman.rg2.Constants.TIME
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.databinding.ActivityYoutubeBinding
import ru.tohaman.rg2.ui.shared.MyDefaultActivity
import timber.log.Timber

class YouTubeActivity: MyDefaultActivity() {
    private val youTubeViewModel by viewModel<YouTubeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDisplayOnProperties()

        val data = intent.data
//        var timeFromArgs: String = "0:00"
//        data?.getQueryParameter("time")?.let { timeFromArgs = it }
//        data?.getQueryParameter("link")?.let { videoId = it }
        val timeFromArgs = intent.getStringExtra(TIME) ?: "0:00"
        val videoId = intent.getStringExtra(LINK) ?: "Kul7jGhYiPQ"
        val algorithm = intent.getStringExtra(ALG) ?: ""
        Timber.d ("$TAG startYouTube with - $data, $timeFromArgs, $videoId, $algorithm")

        youTubeViewModel.setStartTime(timeFromArgs)
        youTubeViewModel.videoId = videoId
        youTubeViewModel.algorithm.set(algorithm)

        DataBindingUtil.setContentView<ActivityYoutubeBinding>(this, R.layout.activity_youtube)
            .apply {
                youtubeView.enabled = true
                viewModel = youTubeViewModel
                val youTubePlayerView = youtubeView.youtubePlayerView
                youTubePlayerView.addYouTubePlayerListener(youTubeViewModel.youTubePlayerListener)
                lifecycle.addObserver(youTubePlayerView)

                closeButton.setOnClickListener {
                    finish()
                }
            }

    }


    private fun setDisplayOnProperties() {
        // Проверяем значения из настроек, выключать экран или нет при прсмотре видео
        val sleepOnYouTube = AppSettings.isYouTubeDisplayAlwaysOn
        if (sleepOnYouTube) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}