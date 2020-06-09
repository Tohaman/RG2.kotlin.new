package ru.tohaman.rg2.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_pll_trainer_settings.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.R
import ru.tohaman.rg2.databinding.FragmentAlgorithmsPropertiesBinding
import ru.tohaman.rg2.databinding.FragmentPllTrainerSettingsBinding
import ru.tohaman.rg2.ui.shared.UiUtilViewModel

class PllTrainerSettings: Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val pllViewModel by sharedViewModel<PllTrainerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        val binding = FragmentPllTrainerSettingsBinding.inflate(inflater, container, false)
            .apply {
                viewModel = pllViewModel

                bottomAppbar.back.setOnClickListener { findNavController().popBackStack() }
                algorithmsPropertiesButton.setOnClickListener {
                    findNavController().navigate(R.id.pllTrainerAlgSettings)
                }
            }

        return binding.root
    }


}