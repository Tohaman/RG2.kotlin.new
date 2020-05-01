package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.include_scramble_gen.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.AzbukaGridAdapter
import ru.tohaman.testempty.databinding.FragmentGamesScrambleGeneratorBinding
import timber.log.Timber

class GamesScrambleGenerator: Fragment() {
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

}