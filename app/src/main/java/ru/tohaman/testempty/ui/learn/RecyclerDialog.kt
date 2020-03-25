package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.include_recycle_view.*
import ru.tohaman.testempty.adapters.MovesAdapter
import ru.tohaman.testempty.databinding.IncludeRecycleViewBinding

class RecyclerDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = IncludeRecycleViewBinding.inflate(inflater, container, false)
            .apply {
                val adapter = MovesAdapter()

                recyclerView.adapter

                closeText.setOnClickListener {
                    dismiss()
                }
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnShowListener {
            dialog?.setTitle("JKHjkshfkjad")
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

}