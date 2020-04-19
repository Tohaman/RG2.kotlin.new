package ru.tohaman.testempty.ui.games

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
import ru.tohaman.testempty.DebugTag
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.GamesAdapter
import ru.tohaman.testempty.databinding.FragmentGamesBinding
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.ui.shared.UiUtilViewModel
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
            Timber.d("$TAG Games-clickItem")
        }

        override fun clickSettings(menuItem: MainDBItem) {
            Timber.d("$TAG Games-clickSettings")
        }

        override fun clickHelp(menuItem: MainDBItem) {
            Timber.d("$TAG Games-clickHelp")
            gamesViewModel.selectedItem = menuItem.id
            findNavController().navigate(R.id.gamesHelpFragment)
        }

    }

}