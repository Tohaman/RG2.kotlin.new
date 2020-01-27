package ru.tohaman.testempty.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tohaman.testempty.R
import ru.tohaman.testempty.dbase.ListPagerDBItem
import ru.tohaman.testempty.recyclerView.MainAdapter
import ru.tohaman.testempty.recyclerView.MenuAdapter
import ru.tohaman.testempty.viewModel.MainViewModel


class ListViewFragment : Fragment() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_list_view, container, false)
        val button = view.findViewById<Button>(R.id.next_button)

        val rcv = view.findViewById<RecyclerView>(R.id.menuList)
        //rcv.layoutManager = LinearLayoutManager(context)
        val adapter = MainAdapter()
        //val menuAdapter = MenuAdapter()
        //rcv.adapter = adapter
        //rcv.adapter = menuAdapter

        //Подписываемся на LiveData из viewModel, для этого передаем два параметра
        //Первый - LifeCycleOwner, по которому LiveData определяет, надо отправлять в него данные или нет
        //Второй - callback, в который LiveData будет отправлять данные
        val callback = Observer<PagedList<ListPagerDBItem>> {it -> MainAdapter().submitList(it)}
        viewModel.mainMenuItems.observe(viewLifecycleOwner, callback)
        //val callback = Observer<List<ListPagerDBItem>> { it -> rcv.adapter = MenuAdapter(it) }
        //viewModel.allItems.observe(viewLifecycleOwner, callback)


        val nameObserver = Observer<String> { button.text = it }
        //viewModel.curItem.observe(this, nameObserver)

        view.findViewById<Button>(R.id.next_button).setOnClickListener {
            //Navigation.findNavController(view).navigate(R.id.action_title_screen_to_register)

        }

        //initTouches(rcv)

        return view
    }

    private fun initTouches(rcv:RecyclerView) {
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int = makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }).attachToRecyclerView(rcv)
    }

}
