package ru.tohaman.testempty.ui.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ru.tohaman.testempty.R
import ru.tohaman.testempty.databinding.FragmentInfoAboutBinding
import ru.tohaman.testempty.databinding.FragmentInfoThanksBinding

/**
 * A simple [Fragment] subclass.
 */
class InfoThanksFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentInfoThanksBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root    }

}
