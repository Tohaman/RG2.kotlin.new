package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.databinding.FragmentGamesTimerBinding
import ru.tohaman.testempty.ui.shared.UiUtilViewModel

class Timer: Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val timerViewModel by sharedViewModel<GamesTimerViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        val binding = FragmentGamesTimerBinding.inflate(inflater, container, false)
            .apply {
                viewModel = timerViewModel
            }

        return binding.root
    }

    override fun onResume() {
        timerViewModel.reloadScrambleParameters()
        super.onResume()
    }
}