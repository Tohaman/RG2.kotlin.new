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
import ru.tohaman.testempty.ui.shared.UiUtilViewModel

class AzbukaTrainerFragment: Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        val binding = FragmentAzbukaTrainerBinding.inflate(inflater, container, false)
            .apply {
                appBar.title = getString(R.string.azbuka_training)
                bottomAppbar.back.setOnClickListener { findNavController().popBackStack() }

            }

        return binding.root
    }

}