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
import ru.tohaman.testempty.ui.shared.UiUtilViewModel

class TimerFragment: Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val timerViewModel by sharedViewModel<TimerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Вызываем этот колбэк при нажатии кнопки back (останавливаем таймер)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                if (!timerViewModel.stopTimer()) findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        val binding = FragmentGamesTimerBinding.inflate(inflater, container, false)
            .apply {
                viewModel = timerViewModel

                saveWithCommentButton.setOnClickListener {
                    val ctx = saveWithCommentButton.context
                    val builder = MaterialAlertDialogBuilder(ctx)
                    val binding = DialogEditCommentBinding.inflate(layoutInflater)

                    //Инициализируем imm чтобы показать/скрыть клавиатуру
                    val imm = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    val eText = binding.editText
                    eText.requestFocus()
                    imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN,0)

                    builder.setPositiveButton(ctx.getText(R.string.ok)) { _, _ ->
                        imm.hideSoftInputFromWindow(eText.windowToken, 0)
                        val comment = binding.editText.text.toString()
                        timerViewModel.saveCurrentResultWithComment(comment)
                    }
                    builder.setNegativeButton(ctx.getText(R.string.cancel)) { _, _ ->
                        imm.hideSoftInputFromWindow(eText.windowToken, 0)
                    }
                    builder.setView(binding.root).create().show()
                }

                settings.setOnClickListener {
                    findNavController().navigate(R.id.gamesTimerSettings)
                }

                back.setOnClickListener {
                    findNavController().popBackStack()
                }

                topLayout.setOnClickListener {
                    findNavController().navigate(R.id.timerResultDialog)
                }
            }

        return binding.root
    }

    override fun onResume() {
        timerViewModel.reloadScrambleParameters()
        super.onResume()
    }


}