package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tohaman.testempty.databinding.FragmentGamesScrambleGeneratorBinding

class GamesScrambleGenerator: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentGamesScrambleGeneratorBinding.inflate(inflater, container, false)

        return binding.root
    }
}