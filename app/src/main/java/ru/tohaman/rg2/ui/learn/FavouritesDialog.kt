package ru.tohaman.rg2.ui.learn

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.adapters.FavouriteListAdapter
import ru.tohaman.rg2.databinding.DialogRecyclerViewBinding
import ru.tohaman.rg2.dbase.entitys.MainDBItem
import timber.log.Timber


class FavouritesDialog : DialogFragment() {
    private val dialogViewModel by sharedViewModel<LearnDetailViewModel>()
    private val learnViewModel by sharedViewModel<LearnViewModel>()
    private lateinit var adapter: FavouriteListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Используем лэйаут диалога со списком и кнопкой назад (как и для отображения азбуки вращений), но прицепляем длругой адаптер в ресайклвью
        val binding = DialogRecyclerViewBinding.inflate(inflater, container, false)
            .apply {
                Timber.d("$TAG onCreateViewFavouritesDialog")

                titleText.text = requireContext().getText(R.string.favorites_dialog_title)

                adapter = FavouriteListAdapter()
                adapter.attachCallBack(clickCallBack, touchHelper)

                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager (context)
                touchHelper.attachToRecyclerView(recyclerView)


                dialogViewModel.liveDataFavouritesList.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        Timber.d("$TAG favList refresh by ${it.size}")
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

    private fun clickAndClose(menuItem: MainDBItem) {
        Timber.d("$TAG onFavItemClick - $menuItem")
        while (findNavController().currentDestination?.id != R.id.destLearn) {
            Timber.d("$TAG popBackStack")
            findNavController().popBackStack()
        }
        if (menuItem.url == "submenu") {
            learnViewModel.onMainMenuItemClick(menuItem)
        } else {
            Timber.d("$TAG navigate to ${menuItem.id}, ${menuItem.phase}")
            //Чтобы работал этот генерируемый класс безопасной передачи аргументов, надо добавить в зависимости classpath
            //https://developer.android.com/jetpack/androidx/releases/navigation#safe_args или https://habr.com/ru/post/416025/
            findNavController().navigate(
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

    // Реализуем перетаскивание элементов в списке https://habr.com/ru/post/427681/ https://www.youtube.com/watch?v=dldrLPNoFnk
    // Используем SimpleCallBack, в этом случае флаги движений задаем сразу в параметрах коллбэка, а не через переопределение getMovementFlags
    private val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.START or ItemTouchHelper.END) {

        //Обработка перемещений вверх/вниз элементов
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition

            learnViewModel.onFavouriteSwapPosition(fromPosition, toPosition)            //меняем местами в базе (индексы)
            recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)             //и в адаптере (визуаально)
            return true
        }

        //смахивание элемента в сторону, данные обновляем в базе [learnViewModel] и в адаптере,
        //а не в dialogVIewModel, чтобы не прерывать анимацию
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            Timber.d("$TAG onSwiped position - $position")
            (viewHolder as FavouriteListAdapter.MenuHolder).binding.viewMenuItem?.let { //Получаем в it значение свайпнутого элемента
                val item = it
                learnViewModel.onFavouriteItemSwipe(item, position)
                adapter.removeItem(it.subId)
                val snackBar = Snackbar.make(view!!, "Отменить удаление", Snackbar.LENGTH_LONG)
                snackBar.setAction("Undo") {
                    adapter.restoreItem(item, position)
                    learnViewModel.onFavouriteItemUndoSwipe(item, position)
                }
                snackBar.setActionTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
                snackBar.show()
            }
        }

        //Вызывается после завершения onMoved или onSwiped, обновляем данные во viewModel и автоматом в
        //адаптере, чтобы установить корректные индексы subID элементов избанного
        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            dialogViewModel.getFavourite()
            super.clearView(recyclerView, viewHolder)
        }

        //Используем библиотеку с декоратором https://github.com/xabaras/RecyclerViewSwipeDecorator
        //подробнее в видео https://youtu.be/rcSNkSJ624U чтобы красиво работал onSwiped (с иконкой на заднем фоне)
        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addBackgroundColor(ContextCompat.getColor(context!!, R.color.bgRowBackground))
                .addActionIcon(R.drawable.ic_delete)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    })

    private val clickCallBack = object: FavouriteListAdapter.OnClickCallBack {
        override fun clickItem(menuItem: MainDBItem) {
            clickAndClose(menuItem)
        }
    }

}