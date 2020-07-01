package ru.tohaman.rg2.ui.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ru.tohaman.rg2.Constants.galleryDrawables

import ru.tohaman.rg2.R
import ru.tohaman.rg2.databinding.DialogInfoTipsBinding
import ru.tohaman.rg2.databinding.FragmentInfoMiniHelpBinding

/**
 * A simple [Fragment] subclass.
 */
class InfoMiniHelpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentInfoMiniHelpBinding.inflate(inflater, container, false)
            .apply {
                val randomItem = galleryDrawables.shuffled()[0]
                images.setImageResource(randomItem.imageRes)
                imageComment.text = randomItem.imageComment
                images.setOnClickListener {
                    findNavController().navigate(R.id.action_destInfo_to_showTipsDialog)
                }
            }

        return binding.root
    }

}
