package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.R
import ru.tohaman.testempty.databinding.FragmentGamesTimerBinding
import ru.tohaman.testempty.ui.shared.UiUtilViewModel

class TimerFragment: Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val timerViewModel by sharedViewModel<TimerViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        val binding = FragmentGamesTimerBinding.inflate(inflater, container, false)
            .apply {
                viewModel = timerViewModel

                settings.setOnClickListener {
                    findNavController().navigate(R.id.gamesTimerSettings)
                }
            }

        return binding.root
    }

    override fun onResume() {
        timerViewModel.reloadScrambleParameters()
        super.onResume()
    }
}