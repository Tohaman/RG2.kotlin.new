package ru.tohaman.testempty.ui.learn

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.tohaman.testempty.databinding.IncludeRecycleViewBinding
import ru.tohaman.testempty.generated.callback.OnClickListener

class RecyclerDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setTitle("Dialog Title!!!")
        val binding = IncludeRecycleViewBinding.inflate(inflater, container, false)
            binding.closeText.setOnClickListener {
                dismiss()
            }
        return binding.root
    }

}