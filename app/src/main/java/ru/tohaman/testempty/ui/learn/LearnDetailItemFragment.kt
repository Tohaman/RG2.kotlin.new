package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.fragment_learn.*
import kotlinx.android.synthetic.main.include_youtube_player.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag
import ru.tohaman.testempty.DebugTag.TAG

import ru.tohaman.testempty.R
import ru.tohaman.testempty.databinding.FragmentLearnDetailBinding
import ru.tohaman.testempty.databinding.FragmentLearnDetailItemBinding
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import timber.log.Timber

class LearnDetailItemFragment : Fragment() {
    private val detailViewModel by sharedViewModel<LearnDetailViewModel>()
    private lateinit var binding: FragmentLearnDetailItemBinding
    private var fragmentNum = 0

    //Поскольку для вызова этого фрагмента НЕ используется Navigation component, то
    //передача/прием данных осуществляются классически через Bundle putInt/getInt
    companion object {
        private const val ARG_CUBE1 = "itemId"
        fun newInstance(mainDBItem: MainDBItem) = LearnDetailItemFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_CUBE1, mainDBItem.id)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fragmentNum = it.getInt(ARG_CUBE1)
            Timber.d("$TAG Фрагмент DetailItem = $fragmentNum")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = FragmentLearnDetailItemBinding.inflate(inflater, container, false)
            .apply {
                val item = detailViewModel.getCurrentItems()[fragmentNum]
                Timber.d("$TAG mainDBItem = $item")
                mainDBItem = item

                val youTubePlayerView = content.youtubeView.youtubePlayerView
                lifecycle.addObserver(youTubePlayerView)

                youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        val videoId = item.url
                        youTubePlayer.cueVideo(videoId, 0f)
                    }
                })

                content.youtubeView.enabled = item.url != ""

                setClickListeners(this)
            }
        return binding.root
    }

    private fun setClickListeners(binding: FragmentLearnDetailItemBinding) {

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

    }

}
