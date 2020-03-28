package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.include_recycle_view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.adapters.MovesAdapter
import ru.tohaman.testempty.databinding.IncludeRecycleViewBinding
import ru.tohaman.testempty.dbase.entitys.BasicMove
import timber.log.Timber

class RecyclerDialog : DialogFragment() {
    private val args by navArgs<RecyclerDialogArgs>()
    private val type by lazy { args.type}
    private val dialogViewModel by sharedViewModel<RecyclerDialogViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = IncludeRecycleViewBinding.inflate(inflater, container, false)
            .apply {
                val adapter = MovesAdapter()
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager (context)
                adapter.attachCallBack(object: MovesAdapter.OnClickCallBack {
                    override fun toastItem(menuItem: BasicMove) {
                        toast(menuItem.toast)
                    }
                })

                dialogViewModel.setTypeItems(type)

                dialogViewModel.liveDataCubeTypes.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        adapter.refreshItems(it)
                    }
                })

                closeText.setOnClickListener {
                    dismiss()
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

    private fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}