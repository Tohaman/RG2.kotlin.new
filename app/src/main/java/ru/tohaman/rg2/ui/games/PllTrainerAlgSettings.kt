package ru.tohaman.rg2.ui.games

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.R
import ru.tohaman.rg2.databinding.DialogEditPllNameBinding
import ru.tohaman.rg2.databinding.FragmentAlgorithmsPropertiesBinding
import ru.tohaman.rg2.dbase.entitys.PllGameItem
import ru.tohaman.rg2.ui.shared.UiUtilViewModel
import ru.tohaman.rg2.utils.toEditable

class PllTrainerAlgSettings: Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val pllViewModel by sharedViewModel<PllTrainerViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        val binding = FragmentAlgorithmsPropertiesBinding.inflate(inflater, container, false)
            .apply {
                viewModel = pllViewModel
                lifecycleOwner = viewLifecycleOwner

                pllViewModel.editPllName.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        showEditDialog(it)
                    }
                })

                bottomAppbar.back.setOnClickListener { findNavController().popBackStack() }
            }
        return binding.root
    }

    private fun showEditDialog(pllGameItem: PllGameItem) {
        val ctx = requireContext()
        val alertBuilder = MaterialAlertDialogBuilder(ctx)
        val alertBinding = DialogEditPllNameBinding.inflate(layoutInflater)

        val imm = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val eText = alertBinding.editText
        eText.text = pllGameItem.currentName.toEditable()
        eText.requestFocus()
        imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0)

        alertBuilder.setPositiveButton(ctx.getText(R.string.ok)) { _, _ ->
            pllGameItem.currentName = eText.text.toString()
            pllViewModel.savePllGameItem2Base(pllGameItem)
            imm.hideSoftInputFromWindow(eText.windowToken, 0)
        }
        alertBuilder.setNegativeButton(ctx.getText(R.string.cancel)) { _, _ ->
            imm.hideSoftInputFromWindow(eText.windowToken, 0)
        }
        alertBuilder.setView(alertBinding.root).create().show()
    }


}