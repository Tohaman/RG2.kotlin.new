package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.adapters.AzbukaGridAdapter
import ru.tohaman.testempty.databinding.FragmentGamesAzbukaSelectBinding
import ru.tohaman.testempty.dbase.entitys.AzbukaItem

class GamesAzbukaSettings: Fragment() {

    private val gamesViewModel by sharedViewModel<GamesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentGamesAzbukaSelectBinding.inflate(inflater, container, false)
            .apply {
                val adapter = AzbukaGridAdapter()
                val list = List<AzbukaItem> (5
                ) { AzbukaItem(0, "Ant", "A", 1) }
                adapter.refreshItems(list)
                content.azbukaGridView.adapter = adapter
            }
        return binding.root
    }

}