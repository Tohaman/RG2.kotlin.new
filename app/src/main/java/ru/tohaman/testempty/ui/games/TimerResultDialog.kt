package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.TimerResultAdapter
import ru.tohaman.testempty.databinding.DialogEditCommentBinding
import ru.tohaman.testempty.databinding.DialogRecyclerViewBinding
import ru.tohaman.testempty.databinding.DialogTimeNoteBinding
import ru.tohaman.testempty.dbase.entitys.TimeNoteItem
import ru.tohaman.testempty.interfaces.EditCommentInt
import ru.tohaman.testempty.utils.toEditable
import timber.log.Timber

class TimerResultDialog : DialogFragment() {
    private val resultViewModel by sharedViewModel<TimerResultViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        resultViewModel.updateList()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DialogRecyclerViewBinding.inflate(inflater, container, false)
            .apply {
                val adapter = TimerResultAdapter()
                adapter.attachCallBack(callBack)
                titleText.text = requireContext().getText(R.string.timer_resault_title)

                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager (context)

                //TODO Доделать получение данных во вьюмодел и подписаться на них + кнопка НАЗАД в хелпе по минииграм

                resultViewModel.timeNotesList.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        adapter.refreshItems(it)
                    }
                })

                closeText.setOnClickListener {
                    dismiss()   //Единственная кнопка и та для выхода
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

    private val callBack = object: TimerResultAdapter.OnClickCallBack {
        override fun clickItem(view: View, item: TimeNoteItem) {
            Timber.d("$TAG .clickItem item = [${item}]")
            val ctx = view.context
            val builder = MaterialAlertDialogBuilder(ctx)
            val binding = DialogTimeNoteBinding.inflate(layoutInflater)
            binding.viewModel = resultViewModel
            resultViewModel.editedItem.set(item)
            resultViewModel.editedComment.set(item.comment)
            binding.callBack = editCommentInt(binding)
            //Добавим к диалогу кнопочки (OK и Cancel) и обработчики нажатий на эти кнопочки
            builder.setPositiveButton(ctx.getText(R.string.ok), null)
            builder.setNegativeButton(ctx.getText(R.string.delete_item)) { _, _ ->
                resultViewModel.deleteItem(item)
            }

            builder.setView(binding.root)
            builder.create().show()
        }
    }

    private fun editCommentInt(binding1: DialogTimeNoteBinding): EditCommentInt {
        return object : EditCommentInt {
            override fun editComment(view: View, item: TimeNoteItem) {
                val ctx = view.context
                val builder = MaterialAlertDialogBuilder(ctx)
                val binding = DialogEditCommentBinding.inflate(layoutInflater)
                binding.editText.text = item.comment.toEditable()

                builder.setPositiveButton(ctx.getText(R.string.ok)) { _, _ ->
                    item.comment = binding.editText.text.toString()
                    resultViewModel.updateItem(item)
                    //resultViewModel.editedComment.set(item.comment)
                }
                builder.setNegativeButton(ctx.getText(R.string.cancel)) { _, _ ->
                    resultViewModel.editedItem.set(item)
                }
                builder.setView(binding.root).create().show()
            }
        }
    }

}