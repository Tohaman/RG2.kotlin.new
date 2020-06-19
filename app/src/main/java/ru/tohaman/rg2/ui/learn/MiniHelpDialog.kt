package ru.tohaman.rg2.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.Constants.galleryDrawables
import ru.tohaman.rg2.dataSource.entitys.TipsItem
import ru.tohaman.rg2.databinding.DialogHelpBinding


class MiniHelpDialog : DialogFragment() {
    private val miniHelpViewModel by sharedViewModel<MiniHelpViewModel>()
    private lateinit var tItem: TipsItem

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
                okButton.setOnClickListener {
                    miniHelpViewModel.closeAndDoNotShowInSession()
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