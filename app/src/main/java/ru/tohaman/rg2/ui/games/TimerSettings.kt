package ru.tohaman.rg2.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.databinding.FragmentGamesTimerSettingsBinding
import ru.tohaman.rg2.ui.shared.UiUtilViewModel

class TimerSettings: Fragment() {
    private val timerViewModel by sharedViewModel<TimerViewModel>()
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        val binding = FragmentGamesTimerSettingsBinding.inflate(inflater, container,false)
            .apply {
                viewModel = timerViewModel

                bottomAppbar.back.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
        return binding.root
    }
}