package ru.tohaman.testempty.ui.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ru.tohaman.testempty.R
import ru.tohaman.testempty.databinding.FragmentInfoMiniHelpBinding

/**
 * A simple [Fragment] subclass.
 */
class InfoMiniHelpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentInfoMiniHelpBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root    }

}
