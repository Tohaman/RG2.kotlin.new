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
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.adapters.AzbukaGridAdapter
import ru.tohaman.rg2.databinding.DialogGetLetterBinding
import ru.tohaman.rg2.databinding.FragmentGamesAzbukaSelectBinding
import ru.tohaman.rg2.dataSource.entitys.AzbukaSimpleItem
import ru.tohaman.rg2.ui.shared.UiUtilViewModel
import ru.tohaman.rg2.utils.toast
import timber.log.Timber

class ScrambleGeneratorSettings: Fragment() {

    private val gamesViewModel by sharedViewModel<GamesViewModel>()
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        val binding = FragmentGamesAzbukaSelectBinding.inflate(inflater, container, false)
            .apply {
                content.includeGrid.viewModel = gamesViewModel
                content.viewModel = gamesViewModel

                val adapter = AzbukaGridAdapter()
                adapter.attachCallBack(selectLetterCallBack)
                adapter.isShowBuffer = true
                content.includeGrid.azbukaGridView.adapter = adapter

                gamesViewModel.currentAzbuka.observe(viewLifecycleOwner, Observer {
                    it?.let { adapter.refreshItems(it) }
                })

                gamesViewModel.message.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        //обнулим message, чтобы при повороте экрана не выводился повторно
                        gamesViewModel.message.value = null
                        toast(it, root)
                    }
                })

                bottomAppbar.back.setOnClickListener {
                    findNavController().popBackStack()
                }

            }
        return binding.root
    }


    //Колбэк в котором обрабатываем нажатие на элемент ячейки в gridView
    private val selectLetterCallBack = object: AzbukaGridAdapter.OnClickCallBack {
        override fun clickItem (azbuka: AzbukaSimpleItem, id: Int, view: View) {
            Timber.d("$TAG click ${azbuka.value}")
            val ctx = view.context
            val letter = azbuka.value
            if (!((letter == "") or (letter == "-"))) {
                val builder = MaterialAlertDialogBuilder(ctx)
                val binding = DialogGetLetterBinding.inflate(layoutInflater)

                binding.onClickListener = gamesViewModel
                builder.setView(binding.root)
                gamesViewModel.curLetter.postValue(letter)

                gamesViewModel.curLetter.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        binding.letter = it
                    }
                })

                //Добавим к диалогу кнопочки (OK и Cancel) и обработчики нажатий на эти кнопочки
                builder.setPositiveButton("OK") { _, _ ->
                    gamesViewModel.changeLetter(id)
                }
                builder.setNegativeButton("Отмена", null)

                val dialog = builder.create()
                dialog.show()

            }
        }
    }

}