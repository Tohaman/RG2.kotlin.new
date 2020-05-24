package ru.tohaman.testempty.ui.info

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
import ru.tohaman.testempty.Constants.galleryDrawables
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.FavouriteListAdapter
import ru.tohaman.testempty.adapters.SliderAdapter
import ru.tohaman.testempty.databinding.DialogInfoTipsBinding
import ru.tohaman.testempty.databinding.DialogRecyclerViewBinding
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.ui.learn.LearnDetailViewModel
import ru.tohaman.testempty.ui.learn.LearnFragmentDirections
import ru.tohaman.testempty.ui.learn.LearnViewModel
import timber.log.Timber


class ShowTipsDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Используем лэйаут диалога со списком и кнопкой назад (как и для отображения азбуки вращений), но прицепляем длругой адаптер в ресайклвью
        val binding = DialogInfoTipsBinding.inflate(inflater, container, false)
            .apply {
                Timber.d("$TAG OpenUrlDialog onCreateView")
                val adapter = SliderAdapter()
                adapter.refreshItems(galleryDrawables.shuffled())
                imageSlider.sliderAdapter = adapter
                closeButton.setOnClickListener {
                    findNavController().popBackStack()
                }
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