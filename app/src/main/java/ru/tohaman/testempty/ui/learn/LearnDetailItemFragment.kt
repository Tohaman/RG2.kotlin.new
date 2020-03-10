package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

import ru.tohaman.testempty.R
import ru.tohaman.testempty.databinding.FragmentLearnDetailBinding
import ru.tohaman.testempty.dbase.entitys.MainDBItem

class LearnDetailItemFragment : Fragment() {
    private val detailViewModel by sharedViewModel<LearnDetailViewModel>()
    private lateinit var binding: FragmentLearnDetailBinding

    //Поскольку для вызова этого фрагмента НЕ используется Navigation component, то
    //передача/прием данных осуществляются классически через Bundle putInt/getInt
    companion object {
        private const val ARG_CUBE1 = "itemId"
        private const val ARG_CUBE2 = "itemPhase"
        fun newInstance(mainDBItem: MainDBItem) = LearnMenuFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_CUBE1, mainDBItem.id)
                putString(ARG_CUBE2, mainDBItem.phase)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val id = it.getInt(ARG_CUBE1)
            val phase = it.getString(ARG_CUBE2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_learn_detail_item, container, false)
    }

}
