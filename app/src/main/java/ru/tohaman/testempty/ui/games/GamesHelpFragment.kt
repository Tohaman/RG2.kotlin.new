package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.databinding.FragmentGamesHelpBinding

class GamesHelpFragment: Fragment()  {
    private val gamesViewModel by sharedViewModel<GamesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentGamesHelpBinding.inflate(inflater, container, false)
            .apply {
                val itemNumber = gamesViewModel.selectedItem
                gamesViewModel.gamesList.observe(viewLifecycleOwner, Observer {
                    item = it[itemNumber]
                })
            }
        return binding.root
    }
}