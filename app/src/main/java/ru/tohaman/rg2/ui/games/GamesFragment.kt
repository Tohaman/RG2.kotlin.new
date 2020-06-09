package ru.tohaman.rg2.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.adapters.GamesAdapter
import ru.tohaman.rg2.databinding.FragmentGamesBinding
import ru.tohaman.rg2.dbase.entitys.MainDBItem
import ru.tohaman.rg2.ui.shared.UiUtilViewModel
import timber.log.Timber

class GamesFragment : Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val gamesViewModel by sharedViewModel<GamesViewModel>()
    private lateinit var binding : FragmentGamesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("$TAG onGames bottomNavShow")
        uiUtilViewModel.showBottomNav()

        binding = FragmentGamesBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = this@GamesFragment
                gamesRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                val adapter = GamesAdapter()
                adapter.attachCallBack(callBack)

                gamesViewModel.gamesList.observe(viewLifecycleOwner, Observer {
                    adapter.refreshItems(it)
                })

                gamesRecyclerView.adapter = adapter
            }

        return binding.root
    }

    private val callBack = object: GamesAdapter.OnClickCallBack {
        override fun clickItem(menuItem: MainDBItem) {
            Timber.d("$TAG Games-clickItem ${menuItem.url}")
            when (menuItem.url) {
                "GENERATOR" -> findNavController().navigate(R.id.scrambleGeneratorFragment)
                "TIMER" -> findNavController().navigate(R.id.timerFragment)
                "PLL_TRAINING" -> findNavController().navigate(R.id.pllTrainerFragment)
                "AZBUKA_TRAINING" -> findNavController().navigate(R.id.azbukaTrainerFragment)
            }
        }

        override fun clickSettings(menuItem: MainDBItem) {
            Timber.d("$TAG Games-clickSettings ${menuItem.url}")
            when (menuItem.url) {
                "GENERATOR" -> findNavController().navigate(R.id.action_destGames_to_gamesAzbukaSettings)
                "TIMER" -> findNavController().navigate(R.id.action_destGames_to_gamesTimerSettings)
                "PLL_TRAINING" -> findNavController().navigate(R.id.action_destGames_to_pllTrainerSettings)
                "AZBUKA_TRAINING" -> findNavController().navigate(R.id.action_destGames_to_azbukaTrainerSettings)
            }
        }

        override fun clickHelp(menuItem: MainDBItem) {
            Timber.d("$TAG Games-clickHelp ${menuItem.url}")
            gamesViewModel.selectedItem = menuItem.id
            findNavController().navigate(R.id.gamesHelpFragment)
        }

    }

}