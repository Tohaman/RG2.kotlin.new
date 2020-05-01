package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.adapters.AzbukaGridAdapter
import ru.tohaman.testempty.databinding.DialogGetLetterBinding
import ru.tohaman.testempty.databinding.FragmentGamesAzbukaSelectBinding
import ru.tohaman.testempty.dataSource.entitys.AzbukaSimpleItem
import ru.tohaman.testempty.utils.toast
import timber.log.Timber

class GamesAzbukaSettings: Fragment() {

    private val gamesViewModel by sharedViewModel<GamesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentGamesAzbukaSelectBinding.inflate(inflater, container, false)
            .apply {
                content.includeGrid.viewModel = gamesViewModel

                val adapter = AzbukaGridAdapter()
                adapter.attachCallBack(callBack)
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

                content.buttonMyAzbuka.setOnClickListener {
                    gamesViewModel.loadAntonsAzbuka()
                }

                content.buttonMaxAzbuka.setOnClickListener {
                    gamesViewModel.loadMaksimsAzbuka()
                }

                content.buttonLoadAzbuka.setOnClickListener {
                    gamesViewModel.loadCustomAzbuka()
                }

                content.buttonSaveAzbuka.setOnClickListener {
                    gamesViewModel.saveCustomAzbuka()
                }

            }
        return binding.root
    }


    //Колбэк в котором обрабатываем нажатие на элемент ячейки в gridView
    private val callBack = object: AzbukaGridAdapter.OnClickCallBack {
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