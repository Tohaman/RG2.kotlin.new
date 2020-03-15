package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.MenuAdapter
import ru.tohaman.testempty.databinding.FragmentLearnBinding
import ru.tohaman.testempty.databinding.FragmentLearnMenuBinding
import ru.tohaman.testempty.dbase.entitys.CubeType
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import timber.log.Timber

class LearnMenuFragment : Fragment() {
    private val learnViewModel by sharedViewModel<LearnViewModel>()
    private var ctId : Int = 0
    private lateinit var binding : FragmentLearnMenuBinding

    //Поскольку для вызова этого фрагмента НЕ используется Navigation component, то
    //передача/прием данных осуществляются классически через Bundle putInt/getInt
    companion object {
        private const val ARG_CUBE = "cubeType"
        fun newInstance(cubeType: CubeType) = LearnMenuFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_CUBE, cubeType.id)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val id = it.getInt(ARG_CUBE)
            ctId = id
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        binding = FragmentLearnMenuBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = this@LearnMenuFragment
                menuList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

                val menuAdapter = MenuAdapter(MenuAdapter.OnClickListener { mainDBItem: MainDBItem, view: View -> onMenuItemClick(mainDBItem, view) })
                learnViewModel.mainDBItemLiveArray[ctId].observe(viewLifecycleOwner, Observer {
                    menuAdapter.refreshItems(it)
                })
                menuList.adapter = menuAdapter
            }
        return binding.root
    }

    private fun onMenuItemClick(item: MainDBItem, view: View) {
        Timber.d("$TAG onItemClick - $item")
        if (item.url == "submenu") {
            learnViewModel.onMainMenuItemClick(item)
        } else {
            view.findNavController().navigate(
                //Чтобы работал этот генерируемый класс безопасной передачи аргументов, надо добавить в зависимости classpath
                //https://developer.android.com/jetpack/androidx/releases/navigation#safe_args или https://habr.com/ru/post/416025/
                LearnFragmentDirections.actionToLearnDetails(item.id, item.phase)
            )
        }
    }
}
