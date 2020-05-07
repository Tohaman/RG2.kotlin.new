package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.R
import ru.tohaman.testempty.databinding.FragmentAzbukaTrainerBinding
import ru.tohaman.testempty.databinding.FragmentAzbukaTrainerSettingsBinding
import ru.tohaman.testempty.ui.shared.UiUtilViewModel

class AzbukaTrainerSettings: Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        val binding = FragmentAzbukaTrainerSettingsBinding.inflate(inflater, container, false)
            .apply {
                appBar.title = getString(R.string.azbuka_training_settings)
                bottomAppbar.back.setOnClickListener { findNavController().popBackStack() }
            }
        return binding.root
    }

}