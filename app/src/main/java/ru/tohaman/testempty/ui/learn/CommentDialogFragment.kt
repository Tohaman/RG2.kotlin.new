package ru.tohaman.testempty.ui.learn

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.tohaman.testempty.R

class CommentDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Важное сообщение!")
                .setMessage("Покормите кота!")
                .setIcon(R.drawable.ic_axis)
                .setPositiveButton("ОК, иду на кухню") {
                        dialog, id ->  dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}