package ru.tohaman.rg2.ui.info

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.R
import ru.tohaman.rg2.databinding.DialogOpenUrlBinding
import ru.tohaman.rg2.databinding.DialogShowHistoryBinding

import ru.tohaman.rg2.databinding.FragmentInfoAboutBinding

/**
 * A simple [Fragment] subclass.
 */
class InfoAboutFragment : Fragment() {
    private val infoViewModel by sharedViewModel<InfoViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentInfoAboutBinding.inflate(inflater, container, false)
            .apply {
                viewModel = infoViewModel
                aboutText.movementMethod = LinkMovementMethod.getInstance()
                versionText.setOnClickListener {
                    showVersionHistoryDialog(it)
                }
            }
        return binding.root
    }

    //Создаем простенькое диалоговое окно со статичным текстом и двумя кнопками
    private fun showVersionHistoryDialog(view: View) {
        val ctx = view.context
        val alertBuilder = MaterialAlertDialogBuilder(ctx)
        val alertBinding = DialogShowHistoryBinding.inflate(layoutInflater)

        alertBuilder.setPositiveButton(ctx.getText(R.string.ok), null)      //ничего не делаем, просто закрываем диалог

        alertBuilder.setView(alertBinding.root).create().show()
    }

}
