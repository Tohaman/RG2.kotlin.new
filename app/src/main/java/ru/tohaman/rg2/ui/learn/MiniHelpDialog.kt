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
import ru.tohaman.rg2.Constants.galleryDrawables
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.adapters.FavouriteListAdapter
import ru.tohaman.rg2.dataSource.entitys.TipsItem
import ru.tohaman.rg2.databinding.DialogHelpBinding
import ru.tohaman.rg2.databinding.DialogRecyclerViewBinding
import ru.tohaman.rg2.dbase.entitys.MainDBItem
import timber.log.Timber


class MiniHelpDialog : DialogFragment() {
    private val miniHelpViewModel by sharedViewModel<MiniHelpViewModel>()
    lateinit var tItem: TipsItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = MiniHelpDialogArgs.fromBundle(requireArguments())
        val id = args.id
        tItem = galleryDrawables[id]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Используем лэйаут диалога со списком и кнопкой назад (как и для отображения азбуки вращений), но прицепляем длругой адаптер в ресайклвью
        val binding = DialogHelpBinding.inflate(inflater, container, false)
            .apply {
                viewModel = miniHelpViewModel
                okButton.setOnClickListener { findNavController().popBackStack() }
            }
        return binding.root
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