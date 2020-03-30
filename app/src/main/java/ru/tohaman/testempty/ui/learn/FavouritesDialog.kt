package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.MenuAdapter
import ru.tohaman.testempty.databinding.DialogRecyclerViewBinding
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import timber.log.Timber

class FavouritesDialog : DialogFragment() {
    private val dialogViewModel by sharedViewModel<LearnDetailViewModel>()
    private val learnViewModel by sharedViewModel<LearnViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DialogRecyclerViewBinding.inflate(inflater, container, false)
            .apply {
                Timber.d("$TAG onCreateViewFavouritesDialog")
                val adapter = MenuAdapter()
                adapter.attachCallBack(object: MenuAdapter.OnClickCallBack {
                    override fun openItem(menuItem: MainDBItem) {
                        clickAndCloase(menuItem)
                    }
                    override fun favouriteChange(menuItem: MainDBItem) {
                    }
                    override fun longClick(menuItem: MainDBItem, view: View) {
                    }

                })

                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager (context)


                dialogViewModel.liveDataFavouritesList.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        Timber.d("$TAG o Refresh by ${it.size}")
                        adapter.refreshItems(it)
                    }
                })

                dialogViewModel.getFavourite()

                closeText.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
        return binding.root
    }

    private fun clickAndCloase(menuItem: MainDBItem) {
        Timber.d("$TAG onFavItemClick - $menuItem")
        while (findNavController().currentDestination!!.id != R.id.destLearn) {
            Timber.d("$TAG popBackStack")
            findNavController().popBackStack()
        }
        if (menuItem.url == "submenu") {
            learnViewModel.onMainMenuItemClick(menuItem)
        } else {
            Timber.d("$TAG navigate to $menuItem")
            findNavController().navigate(
                //Чтобы работал этот генерируемый класс безопасной передачи аргументов, надо добавить в зависимости classpath
                //https://developer.android.com/jetpack/androidx/releases/navigation#safe_args или https://habr.com/ru/post/416025/
                LearnFragmentDirections.actionToLearnDetails(menuItem.id, menuItem.phase)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnShowListener {
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

}