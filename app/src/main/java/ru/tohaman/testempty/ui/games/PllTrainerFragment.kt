package ru.tohaman.testempty.ui.games

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.R
import ru.tohaman.testempty.databinding.DialogEditCommentBinding
import ru.tohaman.testempty.databinding.FragmentGamesTimerBinding
import ru.tohaman.testempty.databinding.FragmentPllTrainerBinding
import ru.tohaman.testempty.ui.shared.UiUtilViewModel

class PllTrainerFragment: Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val pllViewModel by sharedViewModel<PllTrainerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        val binding = FragmentPllTrainerBinding.inflate(inflater, container, false)
            .apply {
                viewModel = pllViewModel
            }

        return binding.root
    }

}