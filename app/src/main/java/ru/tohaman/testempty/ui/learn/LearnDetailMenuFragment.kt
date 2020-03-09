package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ru.tohaman.testempty.R
import ru.tohaman.testempty.dbase.entitys.MainDBItem

class LearnDetailMenuFragment : Fragment() {
    companion object {
        private const val ARG_CUBE1 = "phase"
        private const val ARG_CUBE2 = "id"
        fun newInstance(mainDBItem: MainDBItem) = LearnMenuFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_CUBE1, mainDBItem.phase)
                putInt(ARG_CUBE2, mainDBItem.id)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val phase = it.getString(ARG_CUBE1)
            val id = it.getInt(ARG_CUBE2)

            //cubeType = learnViewModel.getCubeTypeById(id).value
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_learn_detail_menu, container, false)
    }

}
