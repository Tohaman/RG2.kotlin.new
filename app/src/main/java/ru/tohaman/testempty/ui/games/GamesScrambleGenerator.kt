package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.include_scramble_gen.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.databinding.FragmentGamesScrambleGeneratorBinding

class GamesScrambleGenerator: Fragment() {
    private val gamesViewModel by sharedViewModel<GamesViewModel>()
    private val scrambleGeneratorViewModel by sharedViewModel<ScrambleGeneratorViewModel>()
    private lateinit var binding: FragmentGamesScrambleGeneratorBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGamesScrambleGeneratorBinding.inflate(inflater, container, false)
            .apply {
                content.viewModel = scrambleGeneratorViewModel

            }

        return binding.root
    }

}