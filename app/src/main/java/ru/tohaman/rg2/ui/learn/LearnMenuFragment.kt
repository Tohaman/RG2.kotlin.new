package ru.tohaman.rg2.ui.learn

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.adapters.MenuAdapter
import ru.tohaman.rg2.databinding.FragmentLearnMenuBinding
import ru.tohaman.rg2.dbase.entitys.CubeType
import ru.tohaman.rg2.dbase.entitys.MainDBItem
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

                val menuAdapter = MenuAdapter()
                menuAdapter.attachCallBack(onClickCallBack())

                learnViewModel.mainDBItemLiveArray[ctId].observe(viewLifecycleOwner, Observer { listMainDBItems ->
                    val phase = learnViewModel.getPhaseNameById(ctId)
                    menuAdapter.refreshItems(listMainDBItems, phase)
                })
                menuList.adapter = menuAdapter
            }
        return binding.root
    }

    private fun onClickCallBack(): MenuAdapter.OnClickCallBack {
        return object : MenuAdapter.OnClickCallBack {
            override fun openItem(menuItem: MainDBItem) {
                Timber.d("$TAG openItem $menuItem")
                onMenuItemClick(menuItem)
            }

            override fun favouriteChange(menuItem: MainDBItem) {
                learnViewModel.onFavouriteChangeClick(menuItem)
                Timber.d("$TAG favouriteLambdaChange $menuItem")
            }

            override fun longClick(menuItem: MainDBItem, view: View) {
                learnViewModel.selectedItem = menuItem
                Timber.d("$TAG onLongItemClick - $menuItem")
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerForContextMenu(binding.menuList)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        activity?.menuInflater?.inflate(R.menu.favourite_context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        Timber.d("$TAG onContextItemSelected")
        return when (item.itemId) {
            R.id.show_favourite -> {
                Timber.d("$TAG Показать список избранного вызвано из контекстного меню $item")
                findNavController().navigate(R.id.dialog_favourites)
                true
            }
            R.id.change_favourite -> {
                val selectedItem = learnViewModel.selectedItem
                Timber.d("$TAG Сменить статус $selectedItem")
                learnViewModel.onFavouriteChangeClick(selectedItem)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }


    private fun onMenuItemClick(item: MainDBItem) {
        Timber.d("$TAG onItemClick - ${item.id}, ${item.phase}")
        //learnViewModel.changeTypeAndPhase(item.phase)         //Разблокировать если нужно перейти на закладку головоломки (сменить тип)
        if (item.url == "submenu") {
            learnViewModel.onMainMenuItemClick(item)
        } else {
            //Чтобы работал этот генерируемый класс безопасной передачи аргументов, надо добавить в зависимости classpath
            //https://developer.android.com/jetpack/androidx/releases/navigation#safe_args или https://habr.com/ru/post/416025/
            findNavController().navigate(
                LearnFragmentDirections.actionToLearnDetails(item.id, item.phase)
            )
        }
    }
}
