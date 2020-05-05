package ru.tohaman.testempty.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.MovesAdapter
import ru.tohaman.testempty.adapters.TimerResultAdapter
import ru.tohaman.testempty.databinding.DialogRecyclerViewBinding
import ru.tohaman.testempty.dbase.entitys.BasicMove
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.dbase.entitys.TimeNoteItem
import ru.tohaman.testempty.ui.learn.LearnDetailViewModel
import ru.tohaman.testempty.utils.toast
import timber.log.Timber

class TimerResultDialog : DialogFragment() {
    private val detailViewModel by sharedViewModel<TimerResultViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DialogRecyclerViewBinding.inflate(inflater, container, false)
            .apply {
                val adapter = TimerResultAdapter()
                adapter.attachCallBack(object: TimerResultAdapter.OnClickCallBack {
                    override fun clickItem(item: TimeNoteItem) {
                        Timber.d("$TAG .clickItem item = [${item}]")
                    }

                })
                titleText.text = requireContext().getText(R.string.timer_resault_title)

                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager (context)

                //TODO Доделать получение данных во вьюмодел и подписаться на них + кнопка НАЗАД в хелпе по минииграм

//                detailViewModel.liveDataCubeTypes.observe(viewLifecycleOwner, Observer {
//                    it?.let {
//                        adapter.refreshItems(it)
//                    }
//                })

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

}