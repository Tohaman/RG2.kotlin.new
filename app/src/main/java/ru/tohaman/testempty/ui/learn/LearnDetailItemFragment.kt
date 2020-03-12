package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_learn.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag
import ru.tohaman.testempty.DebugTag.TAG

import ru.tohaman.testempty.R
import ru.tohaman.testempty.databinding.FragmentLearnDetailBinding
import ru.tohaman.testempty.databinding.FragmentLearnDetailItemBinding
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import timber.log.Timber

class LearnDetailItemFragment : Fragment() {
    private val detailViewModel by sharedViewModel<LearnDetailViewModel>()
    private lateinit var binding: FragmentLearnDetailItemBinding
    private var fragmentNum = 0

    //Поскольку для вызова этого фрагмента НЕ используется Navigation component, то
    //передача/прием данных осуществляются классически через Bundle putInt/getInt
    companion object {
        private const val ARG_CUBE1 = "itemId"
        fun newInstance(mainDBItem: MainDBItem) = LearnDetailItemFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_CUBE1, mainDBItem.id)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fragmentNum = it.getInt(ARG_CUBE1)
            Timber.d("$TAG Фрагмент DetailItem = $fragmentNum")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = FragmentLearnDetailItemBinding.inflate(inflater, container, false)
            .apply {
                val item = detailViewModel.getCurrentItems()[fragmentNum]
                Timber.d("$TAG mainDBItem = $item")
                //Timber.d("$TAG ${detailViewModel.getCurrentItems()}")
                mainDBItem = item
            }

        return binding.root
    }

}
