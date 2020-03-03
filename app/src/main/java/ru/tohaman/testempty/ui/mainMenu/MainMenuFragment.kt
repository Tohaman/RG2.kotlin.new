package ru.tohaman.testempty.ui.mainMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tohaman.testempty.R
import ru.tohaman.testempty.dbase.MainDBItem
import ru.tohaman.testempty.recyclerView.MenuAdapter
import ru.tohaman.testempty.utils.DebugTag.TAG
import timber.log.Timber


class MainMenuFragment : Fragment() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_list_view, container, false)
        val button = view.findViewById<Button>(R.id.next_button)

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

        viewModel.mutableMainMenuItems.observe(viewLifecycleOwner) { value ->
            Timber.tag(TAG).d("$value")
            menuAdapter.refreshItems(value)
        }


       val nameObserver = Observer<String> { button.text = it }
       viewModel.curItem.observe(viewLifecycleOwner, nameObserver)

        button.setOnClickListener {
            viewModel.onSomeButtonClick()
            //Navigation.findNavController(view).navigate(R.id.action_title_screen_to_register)
        }

        return view
    }

    private fun onMenuItemClick(item: MainDBItem) {
        Timber.tag(TAG).d("onItemClick - $item")
        viewModel.onMainMenuItemClick(item)
    }
}
