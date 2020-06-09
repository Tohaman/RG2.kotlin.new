package ru.tohaman.rg2.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.DebugTag
import ru.tohaman.rg2.adapters.AzbukaGridAdapter
import ru.tohaman.rg2.dataSource.entitys.AzbukaSimpleItem
import ru.tohaman.rg2.databinding.DialogGetLetterBinding
import ru.tohaman.rg2.databinding.FragmentAzbukaTrainerSettingsBinding
import ru.tohaman.rg2.ui.shared.UiUtilViewModel
import timber.log.Timber

class AzbukaTrainerSettings: Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val settingsViewModel by sharedViewModel<GamesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        val binding = FragmentAzbukaTrainerSettingsBinding.inflate(inflater, container, false)
            .apply {
                viewModel = settingsViewModel
                bottomAppbar.back.setOnClickListener { findNavController().popBackStack() }

                azbukaSelect.includeGrid.viewModel = settingsViewModel
                azbukaSelect.viewModel = settingsViewModel

                val adapter = AzbukaGridAdapter()
                adapter.attachCallBack(selectLetterCallBack)
                adapter.isShowBuffer = true
                azbukaSelect.includeGrid.azbukaGridView.adapter = adapter

                settingsViewModel.currentAzbuka.observe(viewLifecycleOwner, Observer {
                    it?.let { adapter.refreshItems(it) }
                })
            }
        return binding.root
    }

    //Колбэк в котором обрабатываем нажатие на элемент ячейки в gridView
    private val selectLetterCallBack = object: AzbukaGridAdapter.OnClickCallBack {
        override fun clickItem (azbuka: AzbukaSimpleItem, id: Int, view: View) {
            Timber.d("${DebugTag.TAG} click ${azbuka.value}")
            val ctx = view.context
            val letter = azbuka.value
            if (!((letter == "") or (letter == "-"))) {
                val builder = MaterialAlertDialogBuilder(ctx)
                val binding = DialogGetLetterBinding.inflate(layoutInflater)

                binding.onClickListener = settingsViewModel
                builder.setView(binding.root)
                settingsViewModel.curLetter.postValue(letter)

                settingsViewModel.curLetter.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        binding.letter = it
                    }
                })

                //Добавим к диалогу кнопочки (OK и Cancel) и обработчики нажатий на эти кнопочки
                builder.setPositiveButton("OK") { _, _ ->
                    settingsViewModel.changeLetter(id)
                }
                builder.setNegativeButton("Отмена", null)

                val dialog = builder.create()
                dialog.show()

            }
        }
    }


}