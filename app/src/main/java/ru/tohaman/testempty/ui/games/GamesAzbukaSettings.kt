package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.include_azbuka_grid.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.adapters.AzbukaGridAdapter
import ru.tohaman.testempty.dataSource.moveZ
import ru.tohaman.testempty.dataSource.prepareCubeToShowInGridView
import ru.tohaman.testempty.dataSource.resetCube
import ru.tohaman.testempty.databinding.FragmentGamesAzbukaSelectBinding
import ru.tohaman.testempty.dbase.entitys.AzbukaDBItem
import timber.log.Timber

class GamesAzbukaSettings: Fragment() {

    private val gamesViewModel by sharedViewModel<GamesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentGamesAzbukaSelectBinding.inflate(inflater, container, false)
            .apply {
                content.includeGrid.viewModel = gamesViewModel

                val adapter = AzbukaGridAdapter()
                content.includeGrid.azbukaGridView.adapter = adapter

                gamesViewModel.currentAzbuka.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        adapter.refreshItems(it)
                    }
                })

                content.buttonMyAzbuka.setOnClickListener {
                    gamesViewModel.loadAntonsAzbuka()
                }

                content.buttonMaxAzbuka.setOnClickListener {
                    gamesViewModel.loadMaksimsAzbuka()
                }


            }
        return binding.root
    }

}