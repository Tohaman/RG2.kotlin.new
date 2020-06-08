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
import kotlinx.android.synthetic.main.fragment_pll_trainer_settings.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.ButtonsGridAdapter
import ru.tohaman.testempty.databinding.DialogEditCommentBinding
import ru.tohaman.testempty.databinding.FragmentPllTrainerBinding
import ru.tohaman.testempty.ui.shared.UiUtilViewModel
import ru.tohaman.testempty.utils.toEditable
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
                //Если не игра еще не запускалась (null) или остановлена, то выводим кнопку "Начать игру"
                if ((pllViewModel.state.value == null) or (pllViewModel.state.value==GameStates.STOPPED)) {
                    pllViewModel.stopGame()
                }

                bottomAppbar.showSettings = true
                bottomAppbar.back.setOnClickListener {
                    if (pllViewModel.state.value != GameStates.STOPPED) pllViewModel.stopGame() else findNavController().popBackStack()
                }
                bottomAppbar.settings.setOnClickListener {
                    if (pllViewModel.state.value != GameStates.STOPPED) pllViewModel.stopGame()
                    findNavController().navigate(R.id.pllTrainerSettings)
                }

                val adapter = ButtonsGridAdapter()
                adapter.attachCallBack(selectLetterCallBack)
                fullButtonPanel.adapter = adapter
                pllViewModel.buttonsList.observe(viewLifecycleOwner, Observer {
                    Timber.d("$TAG PllTrainerFragment.onCreateView.observer обновляем кнопки $it")
                    it?.let {
                        adapter.refreshItems(it)
                    }
                })

                algorithmsPropertiesButton.setOnClickListener {
                    showAlgorithmPropertiesDialog(it)
                }

                //TODO удалить обзервер, за ненадобностью. Пока нужен для отладки.
                pllViewModel.state.observe(viewLifecycleOwner, Observer {
                    Timber.d("$TAG state = $it")
                })
            }

        return binding.root
    }

    //Колбэк в котором обрабатываем нажатие на элемент ячейки в gridView
    private val selectLetterCallBack = object: ButtonsGridAdapter.OnClickCallBack {
        override fun clickItem(buttonText: String, id: Int, view: View) {
            pllViewModel.selectAnswer(buttonText)
        }
    }

    private fun showAlgorithmPropertiesDialog (view: View) {
        val ctx = view.context
        val alertBuilder = MaterialAlertDialogBuilder(ctx)
        val alertBinding = DialogEditCommentBinding.inflate(layoutInflater)

        alertBuilder.setPositiveButton(ctx.getText(R.string.ok)) { _, _ ->
        }
        alertBuilder.setNegativeButton(ctx.getText(R.string.cancel)) { _, _ ->

        }
        alertBuilder.setView(alertBinding.root).create().show()
    }

}