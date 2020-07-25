package ru.tohaman.rg2.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.adapters.AzbukaGridAdapter
import ru.tohaman.rg2.databinding.DialogGetScrambleBinding
import ru.tohaman.rg2.databinding.FragmentGamesScrambleGeneratorBinding
import ru.tohaman.rg2.ui.shared.UiUtilViewModel
import timber.log.Timber

class ScrambleGeneratorFragment: Fragment() {
    private val args by navArgs<ScrambleGeneratorFragmentArgs>()
    private val scrambleArg by lazy { args.scramble }
    private val scrambleGeneratorViewModel by sharedViewModel<ScrambleGeneratorViewModel>()
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private lateinit var binding: FragmentGamesScrambleGeneratorBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.hideBottomNav()
        Timber.d("$TAG .onCreateScrambleGenerator with $scrambleArg scramble")
        binding = FragmentGamesScrambleGeneratorBinding.inflate(inflater, container, false)
            .apply {
                content.viewModel = scrambleGeneratorViewModel
                scrambleGeneratorViewModel.setCurrentScramble(scrambleArg)

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
                    findNavController().navigate(R.id.timerFragment)
                }

                bottomAppbar.back.setOnClickListener {
                    findNavController().popBackStack()
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