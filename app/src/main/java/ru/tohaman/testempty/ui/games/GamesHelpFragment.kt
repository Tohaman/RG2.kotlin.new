package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.databinding.FragmentGamesHelpBinding
import ru.tohaman.testempty.ui.shared.UiUtilViewModel

class GamesHelpFragment: Fragment()  {
    private val gamesViewModel by sharedViewModel<GamesViewModel>()
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        val binding = FragmentGamesHelpBinding.inflate(inflater, container, false)
            .apply {
                val itemNumber = gamesViewModel.selectedItem
                gamesViewModel.gamesList.observe(viewLifecycleOwner, Observer {
                    item = it[itemNumber]
                })

                bottomAppbar.back.setOnClickListener {
                    findNavController().popBackStack()
                }

            }
        return binding.root
    }
}