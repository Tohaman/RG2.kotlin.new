package ru.tohaman.rg2.ui.games

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.adapters.TimerResultAdapter
import ru.tohaman.rg2.databinding.DialogEditCommentBinding
import ru.tohaman.rg2.databinding.DialogRecyclerViewBinding
import ru.tohaman.rg2.databinding.DialogTimeNoteBinding
import ru.tohaman.rg2.dbase.entitys.TimeNoteItem
import ru.tohaman.rg2.utils.toEditable
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
            binding.textComment.setOnClickListener {
                showEditCommentDialog(ctx, item)
            }
            //Добавим к диалогу кнопочки (OK и Cancel) и обработчики нажатий на эти кнопочки
            builder.setPositiveButton(ctx.getText(R.string.ok), null)
            builder.setNegativeButton(ctx.getText(R.string.delete_item)) { _, _ ->
                resultViewModel.deleteItem(item)
            }

            builder.setView(binding.root)
            builder.create().show()
        }
    }

    private fun showEditCommentDialog(ctx: Context, item: TimeNoteItem) {
        val alertBuilder = MaterialAlertDialogBuilder(ctx)
        val alertBinding = DialogEditCommentBinding.inflate(layoutInflater)

        val imm = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val eText = alertBinding.editText
        eText.text = item.comment.toEditable()
        eText.requestFocus()
        imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0)

        alertBuilder.setPositiveButton(ctx.getText(R.string.ok)) { _, _ ->
            item.comment = eText.text.toString()
            resultViewModel.updateItem(item)
            imm.hideSoftInputFromWindow(eText.windowToken, 0)
        }
        alertBuilder.setNegativeButton(ctx.getText(R.string.cancel)) { _, _ ->
            resultViewModel.editedItem.set(item)
            imm.hideSoftInputFromWindow(eText.windowToken, 0)
        }
        alertBuilder.setView(alertBinding.root).create().show()
    }


}