package ru.tohaman.testempty.ui.games

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.ButtonsGridAdapter
import ru.tohaman.testempty.databinding.DialogEditCommentBinding
import ru.tohaman.testempty.databinding.FragmentGamesTimerBinding
import ru.tohaman.testempty.databinding.FragmentPllTrainerBinding
import ru.tohaman.testempty.ui.shared.UiUtilViewModel
import timber.log.Timber

class PllTrainerFragment: Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val pllViewModel by sharedViewModel<PllTrainerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Вызываем этот колбэк при нажатии кнопки back (останавливаем игру)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                if (pllViewModel.state.value != GameStates.STOPPED) pllViewModel.stopGame() else findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        val binding = FragmentPllTrainerBinding.inflate(inflater, container, false)
            .apply {
                viewModel = pllViewModel
                bottomAppbar.back.setOnClickListener {
                    if (pllViewModel.state.value != GameStates.STOPPED) pllViewModel.stopGame() else findNavController().popBackStack()
                }

                val adapter = ButtonsGridAdapter()
                adapter.attachCallBack(selectLetterCallBack)
                buttonPanel.adapter = adapter
                pllViewModel.buttonsList.observe(viewLifecycleOwner, Observer {
                    Timber.d("$TAG обновляем кнопки $it")
                    it?.let {
                        adapter.refreshItems(it)
                    }
                })

                pllViewModel.state.observe(viewLifecycleOwner, Observer {
                    Timber.d("$TAG state = $it")
                    it?.let {
                        when (it) {
                            GameStates.WAITING_4_ANSWER -> {

                            }
                            GameStates.SHOW_ANSWER -> {

                            }
                            GameStates.STOPPED -> {

                            }
                            GameStates.TIME_IS_OVER -> {

                            }
                        }
                    }
                })
            }

        return binding.root
    }

    //Колбэк в котором обрабатываем нажатие на элемент ячейки в gridView
    private val selectLetterCallBack = object: ButtonsGridAdapter.OnClickCallBack {
        override fun clickItem(letter: String, id: Int, view: View) {
            Timber.d("$TAG .clickItem letter = [${letter}], id = [${id}], view = [${view}]")
            pllViewModel.selectAnswer(letter)
        }
    }

}