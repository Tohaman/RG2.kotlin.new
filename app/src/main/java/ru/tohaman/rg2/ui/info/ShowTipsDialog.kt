package ru.tohaman.rg2.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ru.tohaman.rg2.Constants.galleryDrawables
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.adapters.SliderAdapter
import ru.tohaman.rg2.databinding.DialogInfoTipsBinding
import ru.tohaman.rg2.databinding.DialogRecyclerViewBinding
import timber.log.Timber


class ShowTipsDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Используем лэйаут диалога со списком и кнопкой назад (как и для отображения азбуки вращений), но прицепляем длругой адаптер в ресайклвью
        val binding = DialogInfoTipsBinding.inflate(inflater, container, false)
            .apply {
                Timber.d("$TAG OpenUrlDialog onCreateView")
                val adapter = SliderAdapter()
                adapter.refreshItems(galleryDrawables)
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