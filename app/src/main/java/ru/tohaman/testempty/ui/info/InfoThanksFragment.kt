package ru.tohaman.testempty.ui.info

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_info_mini_help.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

import ru.tohaman.testempty.R
import ru.tohaman.testempty.databinding.FragmentInfoAboutBinding
import ru.tohaman.testempty.databinding.FragmentInfoThanksBinding

/**
 * A simple [Fragment] subclass.
 */
class InfoThanksFragment : Fragment() {
    private val infoViewModel by sharedViewModel<InfoViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentInfoThanksBinding.inflate(inflater, container, false)
            .apply {
                viewModel = infoViewModel


            }
        return binding.root    }

}
