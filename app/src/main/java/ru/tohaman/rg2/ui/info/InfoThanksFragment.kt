package ru.tohaman.rg2.ui.info

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

import ru.tohaman.rg2.R
import ru.tohaman.rg2.databinding.DialogEditCommentBinding
import ru.tohaman.rg2.databinding.DialogOpenUrlBinding
import ru.tohaman.rg2.databinding.FragmentInfoAboutBinding
import ru.tohaman.rg2.databinding.FragmentInfoThanksBinding

/**
 * A simple [Fragment] subclass.
 */
class InfoThanksFragment : Fragment() {
    private val infoViewModel by sharedViewModel<InfoViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentInfoThanksBinding.inflate(inflater, container, false)
            .apply {
                viewModel = infoViewModel
                reklamLogo.setOnClickListener {
                    showOpenUrlDialog(it)
                }
            }
        return binding.root    }


    //Создаем простенькое диалоговое окно со статичным текстом и двумя кнопками
    private fun showOpenUrlDialog(view: View) {
        val ctx = view.context
        val alertBuilder = MaterialAlertDialogBuilder(ctx)
        val alertBinding = DialogOpenUrlBinding.inflate(layoutInflater)

        alertBuilder.setPositiveButton(ctx.getText(R.string.goto_market)) { _, _ ->
            browse("https://cubemarket.ru")
        }
        alertBuilder.setNegativeButton(ctx.getText(R.string.backText), null)      //По отмене ничего не делаем, просто закрываем диалог

        alertBuilder.setView(alertBinding.root).create().show()
    }


    private fun browse(url: String, newTask: Boolean = false): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            if (newTask) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            false
        }
    }
}
