package ru.tohaman.testempty.ui.games

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.ButtonsGridAdapter
import ru.tohaman.testempty.dataSource.cubeColor
import ru.tohaman.testempty.dataSource.resetCube
import ru.tohaman.testempty.dataSource.runScramble
import ru.tohaman.testempty.dataSource.slotLightingCoordinate
import ru.tohaman.testempty.databinding.FragmentAzbukaTrainerBinding
import ru.tohaman.testempty.ui.shared.UiUtilViewModel
import timber.log.Timber

class AzbukaTrainerFragment: Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val trainerViewModel by sharedViewModel<AzbukaTrainerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Вызываем этот колбэк при нажатии кнопки back (останавливаем игру)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                if (trainerViewModel.state.value != GameStates.STOPPED) trainerViewModel.stopGame() else findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        val binding = FragmentAzbukaTrainerBinding.inflate(inflater, container, false)
            .apply {
                viewModel = trainerViewModel
                Timber.d("$TAG .onCreateView ${trainerViewModel.state.value}")
                if ((trainerViewModel.state.value == null) or (trainerViewModel.state.value==GameStates.STOPPED)) {
                    trainerViewModel.reloadSettings()
                    trainerViewModel.loadNextBlind()
                    trainerViewModel.stopGame()
                }
                bottomAppbar.back.setOnClickListener {

                    if (trainerViewModel.state.value != GameStates.STOPPED) trainerViewModel.stopGame() else findNavController().popBackStack()
                }

                val adapter = ButtonsGridAdapter()
                adapter.attachCallBack(selectLetterCallBack)
                buttonPanel.adapter = adapter
                trainerViewModel.buttonsList.observe(viewLifecycleOwner, Observer {
                    Timber.d("$TAG обновляем кнопки $it")
                    it?.let {
                        adapter.refreshItems(it)
                    }
                })

                trainerViewModel.state.observe(viewLifecycleOwner, Observer {
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
            //Timber.d("$TAG .clickItem letter = [${letter}], id = [${id}], view = [${view}]")
            trainerViewModel.selectAnswer(letter)
        }
    }



}