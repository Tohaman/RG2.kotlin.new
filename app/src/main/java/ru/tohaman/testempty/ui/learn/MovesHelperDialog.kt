package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.MovesAdapter
import ru.tohaman.testempty.databinding.DialogRecyclerViewBinding
import ru.tohaman.testempty.dbase.entitys.BasicMove
import ru.tohaman.testempty.utils.toast

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
                titleText.text = "Азбука вращений"

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