package ru.tohaman.testempty.ui.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.SliderAdapter
import ru.tohaman.testempty.dataSource.entitys.TipsItem
import ru.tohaman.testempty.databinding.DialogMiniHelpBinding
import ru.tohaman.testempty.databinding.FragmentInfoMiniHelpBinding

/**
 * A simple [Fragment] subclass.
 */
class InfoMiniHelpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentInfoMiniHelpBinding.inflate(inflater, container, false)
            .apply {
                val randomItem = galleryDrawables.shuffled()[0]
                imageView.setImageResource(randomItem.imageRes)
                imageComment.text = randomItem.imageComment
                imageView.setOnClickListener {
                    showTips(it)
                }

            }

        return binding.root
    }

    private fun showTips(view: View) {
        val ctx = view.context
        val alertBuilder = MaterialAlertDialogBuilder(ctx)
        val alertBinding = DialogMiniHelpBinding.inflate(layoutInflater)
        val adapter = SliderAdapter()
        val imageList = mutableListOf<Int>()
        val textList = mutableListOf<String>()
        galleryDrawables.shuffled().map {
            imageList.add(it.imageRes)
            textList.add(it.imageComment)
        }
        adapter.refreshItems(galleryDrawables.shuffled())
        alertBinding.imageSlider.sliderAdapter = adapter

        alertBuilder.setNegativeButton(ctx.getText(R.string.backText)) { _, _ ->

        }

        alertBuilder.setView(alertBinding.root).create().show()
    }

    private val galleryDrawables = listOf(
        TipsItem(R.drawable.frame_1, "Обязательно прочитайте этот раздел"),
        TipsItem(R.drawable.frame_2, "Добавляйте в избранное этапы или головоломки целиком"),
        TipsItem(R.drawable.frame_3, "Используйте подсказки по азбуке вращений"),
        TipsItem(R.drawable.frame_4, "Создвайте свои комментарии к этапам"),
        TipsItem(R.drawable.frame_5, "Щелкните тут и задйте свой скрамбл"),
        TipsItem(R.drawable.frame_6, "Щелкните тут чтобы сменить скрамбл"),
        TipsItem(R.drawable.frame_7, "Таймер можно поставить на паузу"),
        TipsItem(R.drawable.frame_8, "При сохранении результата можно сразу задать свой комментарий"),
        TipsItem(R.drawable.frame_9, "Эту кнопку можно отключить в настройках программы")
    )

}
