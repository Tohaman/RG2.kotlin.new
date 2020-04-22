package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.AzbukaGridAdapter
import ru.tohaman.testempty.databinding.DialogGetLetterBinding
import ru.tohaman.testempty.databinding.FragmentGamesAzbukaSelectBinding
import ru.tohaman.testempty.dbase.entitys.AzbukaSimpleItem
import ru.tohaman.testempty.utils.dp
import timber.log.Timber

class GamesAzbukaSettings: Fragment() {

    private val gamesViewModel by sharedViewModel<GamesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentGamesAzbukaSelectBinding.inflate(inflater, container, false)
            .apply {
                content.includeGrid.viewModel = gamesViewModel

                val adapter = AzbukaGridAdapter()
                adapter.attachCallBack(callBack)
                content.includeGrid.azbukaGridView.adapter = adapter

                gamesViewModel.currentAzbuka.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        adapter.refreshItems(it)
                    }
                })

                content.buttonMyAzbuka.setOnClickListener {
                    gamesViewModel.loadAntonsAzbuka()
                }

                content.buttonMaxAzbuka.setOnClickListener {
                    gamesViewModel.loadMaksimsAzbuka()
                }

                content.buttonLoadAzbuka.setOnClickListener {
                    gamesViewModel.loadCurrentAzbuka()
                }

                content.buttonSaveAzbuka.setOnClickListener {
                    gamesViewModel.saveCurrentAzbuka()
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
                gamesViewModel.letter.postValue(letter)

                gamesViewModel.letter.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        binding.letter = it
                    }
                })

                builder.setPositiveButton("OK") { _, _ ->
                    //TODO сохранение буквы в CURRENT_AZBUKA
                    //gamesViewModel.ChangeLetter(id)
                }
                builder.setNegativeButton("Отмена", null)

                val dialog = builder.create()
                dialog.show()

                //Добавим к диалогу кнопочки (OK и Cancel) и обработчики нажатий на эти кнопочки
            }
        }
    }

}