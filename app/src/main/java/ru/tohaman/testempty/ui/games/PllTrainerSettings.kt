package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_pll_trainer_settings.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.R
import ru.tohaman.testempty.databinding.FragmentAlgorithmsPropertiesBinding
import ru.tohaman.testempty.databinding.FragmentPllTrainerSettingsBinding
import ru.tohaman.testempty.ui.shared.UiUtilViewModel

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
                    showAlgorithmPropertiesDialog(it)
                }
            }

        return binding.root
    }

    private fun showAlgorithmPropertiesDialog (view: View) {
        val ctx = view.context
        val alertBuilder = MaterialAlertDialogBuilder(ctx)
        val alertBinding =
            FragmentAlgorithmsPropertiesBinding.inflate(layoutInflater).also {
                it.viewModel = pllViewModel
                it.lifecycleOwner = viewLifecycleOwner
            }

//        alertBuilder.setPositiveButton(ctx.getText(R.string.ok)) { _, _ ->
//        }
//        alertBuilder.setNegativeButton(ctx.getText(R.string.cancel)) { _, _ ->
//
//        }
        alertBuilder.setView(alertBinding.root).create().show()
    }

}