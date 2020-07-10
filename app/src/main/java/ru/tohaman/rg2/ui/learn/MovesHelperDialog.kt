package ru.tohaman.rg2.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.R
import ru.tohaman.rg2.adapters.MovesAdapter
import ru.tohaman.rg2.databinding.DialogRecyclerViewBinding
import ru.tohaman.rg2.dbase.entitys.BasicMove
import ru.tohaman.rg2.utils.toast

class MovesHelperDialog : DialogFragment() {
    private val args by navArgs<MovesHelperDialogArgs>()
    private val type by lazy { args.type}
    private val detailViewModel by sharedViewModel<LearnDetailViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DialogRecyclerViewBinding.inflate(inflater, container, false)
            .apply {
                val adapter = MovesAdapter()
                adapter.attachCallBack(object: MovesAdapter.OnClickCallBack {
                    override fun toastItem(menuItem: BasicMove) {
                        toast(menuItem.toast, root)
                    }
                })
                titleText.text = getString(R.string.azbuka_dialog_title)
                hint.text = getString(R.string.tap_to_see_hint)

                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager (context)

                detailViewModel.setMoveTypeItems(type)
                detailViewModel.liveDataCubeTypes.observe(viewLifecycleOwner, Observer {
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

}