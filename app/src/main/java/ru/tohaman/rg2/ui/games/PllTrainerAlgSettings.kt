package ru.tohaman.rg2.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.databinding.FragmentAlgorithmsPropertiesBinding
import ru.tohaman.rg2.ui.shared.UiUtilViewModel

class PllTrainerAlgSettings: Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val pllViewModel by sharedViewModel<PllTrainerViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        val binding = FragmentAlgorithmsPropertiesBinding.inflate(inflater, container, false)
            .apply {
                viewModel = pllViewModel

                bottomAppbar.back.setOnClickListener { findNavController().popBackStack() }
            }

        return binding.root
    }

}