package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag

import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.MenuAdapter
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import timber.log.Timber

class LearnMenuFragment : Fragment() {
    private val learnViewModel by sharedViewModel<LearnViewModel>()

    companion object {
        private const val ARG_CUBE = "cubeType"
        fun newInstance(phase: String) = LearnMenuFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_CUBE, phase)
            }
        }
    }

    private var cubeType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cubeType = it.getString(ARG_CUBE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        val view = inflater.inflate(R.layout.fragment_learn_menu, container, false)

        val button = view.findViewById<Button>(R.id.next_button)
        button.text = cubeType
/*
        val rcv = view.findViewById<RecyclerView>(R.id.menuList)
        rcv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        //val adapter = MainAdapter(MainAdapter.OnClickListener { onMenuItemClick(it)})
        //rcv.adapter = adapter
        val menuAdapter = MenuAdapter(MenuAdapter.OnClickListener { onMenuItemClick(it)})
        rcv.adapter = menuAdapter


        //Подписываемся на LiveData из viewModel, для этого передаем два параметра
        //Первый - LifeCycleOwner, по которому LiveData определяет, надо отправлять в него данные или нет
        //Второй - callback, в который LiveData будет отправлять данные
        //val callback = Observer<PagedList<PhaseItem>> { it -> adapter.submitList(it)}
        //viewModel.mainMenuItems.observe(viewLifecycleOwner, callback)

        learnViewModel.mutableMainMenuItems.observe(viewLifecycleOwner) { value ->
            Timber.tag(DebugTag.TAG).d("$value")
            menuAdapter.refreshItems(value)
        }


        val nameObserver = Observer<String> { button.text = it }
        learnViewModel.curItem.observe(viewLifecycleOwner, nameObserver)

        button.setOnClickListener {
            learnViewModel.onSomeButtonClick()
            //Navigation.findNavController(view).navigate(R.id.action_title_screen_to_register)
        }
*/

        return view
    }

    private fun onMenuItemClick(item: MainDBItem) {
        Timber.tag(DebugTag.TAG).d("onItemClick - $item")
        learnViewModel.onMainMenuItemClick(item)
    }
}
