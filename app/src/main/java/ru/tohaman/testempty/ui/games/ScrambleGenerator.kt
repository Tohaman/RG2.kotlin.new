package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.include_scramble_gen.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.AzbukaGridAdapter
import ru.tohaman.testempty.databinding.DialogGetLetterBinding
import ru.tohaman.testempty.databinding.DialogGetScrambleBinding
import ru.tohaman.testempty.databinding.FragmentGamesScrambleGeneratorBinding
import timber.log.Timber

class ScrambleGenerator: Fragment() {
    private val scrambleGeneratorViewModel by sharedViewModel<ScrambleGeneratorViewModel>()
    private lateinit var binding: FragmentGamesScrambleGeneratorBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGamesScrambleGeneratorBinding.inflate(inflater, container, false)
            .apply {
                content.viewModel = scrambleGeneratorViewModel
                val adapter = AzbukaGridAdapter()
                content.scramGridView.adapter = adapter

                scrambleGeneratorViewModel.currentAzbuka.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        adapter.refreshItems(it)
                    }
                })

                //Поскольку viewModel ничего е должна знать о различных View, то все переходы между разными окнами делаем в презентере, т.е. во View
                content.scramble.setOnClickListener {
                    val scramble = scrambleGeneratorViewModel.currentScramble.get() ?: ""
                    showGetScrambleDialog(scramble, it)
                }

                content.buttonAzbuka.setOnClickListener {
                    findNavController().navigate(R.id.gamesAzbukaSettings)
                }

                content.timerButton.setOnClickListener {
                    Timber.d("$TAG Нажали кнопку Таймер")
                }

            }
        return binding.root
    }

    override fun onResume() {
        scrambleGeneratorViewModel.reloadAzbuka()
        super.onResume()

    }

    private fun showGetScrambleDialog(scramble: String, view: View) {
        Timber.d("$TAG .showGetScrambleDialog scramble = [${scramble}]")
        val ctx = view.context
        val builder = MaterialAlertDialogBuilder(ctx)
        val binding = DialogGetScrambleBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        binding.viewModel = scrambleGeneratorViewModel
        scrambleGeneratorViewModel.dialogScrambleText.set(scramble)
        //Добавим к диалогу кнопочки (OK и Cancel) и обработчики нажатий на эти кнопочки
        builder.setPositiveButton("OK") { _, _ ->
            scrambleGeneratorViewModel.updateScramble(scrambleGeneratorViewModel.dialogScrambleText.get() ?: "R R R R")
        }
        builder.setNegativeButton("Отмена", null)

        val dialog = builder.create()
        dialog.show()


    }
}