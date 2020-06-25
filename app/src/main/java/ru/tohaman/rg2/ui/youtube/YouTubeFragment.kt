package ru.tohaman.rg2.ui.youtube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.databinding.FragmentYoutubeBinding
import ru.tohaman.rg2.ui.shared.UiUtilViewModel
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class YouTubeFragment : Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val args by navArgs<YouTubeFragmentArgs>()
    private val timeArg by lazy { args.time }
    private val linkArg by lazy { args.link }
    private lateinit var youTubePlayerView: YouTubePlayerView
    private lateinit var player: YouTubePlayer
    private lateinit var binding: FragmentYoutubeBinding
    private var currentSecond = 0f
    private var startTime = 0f


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        binding = FragmentYoutubeBinding.inflate(inflater, container, false)
            .apply {
                currentSecond = stringToTimeMillis(timeArg)
                startTime = currentSecond
            }
        setYouTubePlayerButtons(binding)
        return binding.root
    }


    private fun setYouTubePlayerButtons(binding: FragmentYoutubeBinding) {
        //Используем плагин https://github.com/PierfrancescoSoffritti/android-youtube-player#api-documentation
        binding.youtubeView.enabled = true
        youTubePlayerView = binding.youtubeView.youtubePlayerView
        lifecycle.addObserver(youTubePlayerView)
        youTubePlayerView.enterFullScreen()

        val uiController = youTubePlayerView.getPlayerUiController()
        val backward10secDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_backward)!!
        val forward10secDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_forward)!!
        uiController.setCustomAction1(backward10secDrawable, backwardClickListener)
        uiController.setCustomAction2(forward10secDrawable, forwardClickListener)
        uiController.showCustomAction1(true)
        uiController.showCustomAction2(true)


        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                //cueVideo - загрзука видео с определенного момента, но без автостарта
                //loadVideo - с автостартом
                youTubePlayer.loadVideo(linkArg, currentSecond)
                player = youTubePlayer
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                currentSecond = second
                super.onCurrentSecond(youTubePlayer, second)
            }
        })
    }

    //Нажатие левой кнопки (рядом с Play)
    private val backwardClickListener = View.OnClickListener { _ ->
        //player.seekTo(currentSecond - 10)         // Перемотаем на 10 сек назад
        player.seekTo(startTime)                    // Перемотка к месту откуда начали смотреть видео
    }

    //Нажатие правой от Play кнопки
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
            Timber.e( "$TAG Ошибка при преобразовании времени. $e")
            //если не удалось преобразовать, то считаем что видео воспроизводится с начала возвращаем 0 милисек
            return 0f
        }

        calendar.time = date
        val second = calendar.get(Calendar.SECOND)
        val minute = calendar.get(Calendar.MINUTE)
        return (minute * 60 + second).toFloat()
    }

}