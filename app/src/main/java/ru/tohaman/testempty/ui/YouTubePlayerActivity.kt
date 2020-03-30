package ru.tohaman.testempty.ui

import android.os.Bundle
import ru.tohaman.testempty.ui.shared.MyDefaultActivity
import timber.log.Timber
import ru.tohaman.testempty.DebugTag.TAG
import java.util.*

class YouTubePlayerActivity: MyDefaultActivity() {
    private var time: Date? = null
    private var videoId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var timeFromArgs: String
        intent.data!!.getQueryParameter("time").let { timeFromArgs = it!! }
        intent.data!!.getQueryParameter("link").let { videoId = it!! }

        Timber.d ("$TAG startYouTube with - $timeFromArgs , $videoId")

    }
}