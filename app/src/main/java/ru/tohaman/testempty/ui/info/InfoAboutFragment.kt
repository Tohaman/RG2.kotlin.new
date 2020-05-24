package ru.tohaman.testempty.ui.info

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

import ru.tohaman.testempty.R
import ru.tohaman.testempty.databinding.FragmentInfoAboutBinding
import ru.tohaman.testempty.databinding.FragmentInfoBinding
import ru.tohaman.testempty.ui.games.GamesViewModel
import ru.tohaman.testempty.ui.shared.UiUtilViewModel

/**
 * A simple [Fragment] subclass.
 */
class InfoAboutFragment : Fragment() {
    private val infoViewModel by sharedViewModel<InfoViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentInfoAboutBinding.inflate(inflater, container, false)
            .apply {
                viewModel = infoViewModel
                aboutText.movementMethod = LinkMovementMethod.getInstance()
            }
        return binding.root
    }

}
