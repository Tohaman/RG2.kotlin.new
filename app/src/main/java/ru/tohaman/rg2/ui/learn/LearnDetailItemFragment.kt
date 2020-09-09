package ru.tohaman.rg2.ui.learn

import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.annotations.NotNull
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.Constants.CUR_ITEM_ID
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.databinding.FragmentLearnDetailItemBinding
import ru.tohaman.rg2.dbase.entitys.MainDBItem
import ru.tohaman.rg2.utils.ClickTextHolder
import ru.tohaman.rg2.utils.dp
import ru.tohaman.rg2.utils.shareText
import ru.tohaman.rg2.utils.toEditable
import timber.log.Timber


class LearnDetailItemFragment : Fragment() {
    private val detailViewModel by sharedViewModel<LearnDetailViewModel>()
    private lateinit var binding: FragmentLearnDetailItemBinding
    private var fragmentNum = 0
    private lateinit var item: MainDBItem

    //Поскольку для вызова этого фрагмента НЕ используется Navigation component,
    //т.к. это фрагмент (страница) внутри ViewPager,
    //то передача/прием данных осуществляются классически через Bundle putInt/getInt
    companion object {
        fun newInstance(fragmentId: Int) = LearnDetailItemFragment().apply {
            arguments = Bundle().apply {
                putInt(CUR_ITEM_ID, fragmentId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fragmentNum = it.getInt(CUR_ITEM_ID)
           // Timber.d("$TAG Фрагмент получаем номер фрагмента из Bundle = $fragmentNum")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = FragmentLearnDetailItemBinding.inflate(inflater, container, false)
            .apply {
                // Делаем ссылки кликабельными
                content.descriptionText.movementMethod = LinkMovementMethod.getInstance()

                detailViewModel.liveCurrentItems.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        //Timber.d("$TAG Фрагмент = $fragmentNum, $it.size")
                        setViewElementsProperties(it)
                    }
                })

                //Регистрируем контекстное меню для "Избранного" (будем вызывать при долгом нажатии)
                registerForContextMenu(favourites)
                setClickListeners(this)
            }
        return binding.root
    }

    private fun @NotNull FragmentLearnDetailItemBinding.setViewElementsProperties(itemsList: List<MainDBItem>) {
        if (fragmentNum < itemsList.size) {            //На всякий случай проверяем, что номер фрагмента не больше, чем размер List, иначе не сможем получить данные из List'a
            item = itemsList[fragmentNum]
            mainDBItem = item
            content.urlClick = clickableText()
            content.descriptionText.setTextIsSelectable(detailViewModel.isTextSelectable)
            content.titleText.setTextIsSelectable(detailViewModel.isTextSelectable)
            content.descriptionText.linksClickable = true

            //Если надо отображать плеер, то инициализируем его
            if (detailViewModel.isYouTubePlayerEnabled(fragmentNum)) {
                content.youtubeView.enabled = true
                val youTubePlayerView = content.youtubeView.youtubePlayerView
                lifecycle.addObserver(youTubePlayerView)
                youTubePlayerView.addYouTubePlayerListener(
                    detailViewModel.youTubePlayerListener(fragmentNum)
                )
            } else {
                content.youtubeView.enabled = false
            }
        }
    }

    //Обработчик сликов по ссылкам в тексте, который передаем в TextView, тут обрабатываем только клики по переходам к другому этапу
    //генератор скрамблов и видео открываем стандартным обработчиком в MakeLinksClickable.kt
    private fun clickableText(): ClickTextHolder {
        return object : ClickTextHolder {
            override fun onUrlClick(url: String): Boolean {
                return if (url.startsWith("rg2://pager", true)) {
                    findNavController().popBackStack()
                    findNavController().navigate(
                        LearnFragmentDirections.actionToLearnDetails(getIdFromUrl(url), getPhaseFromUrl(url))
                    )
                    Timber.d("$TAG ссылка на другой этап, переходим к ${getIdFromUrl(url)}, ${getPhaseFromUrl(url)}")
                    true
                } else {
                    false
                }
            }
        }
    }

    //парсим ссылку типа "rg2://pager?phase=BEGIN&item=1"
    fun getIdFromUrl(url: String): Int {
        return try {
            url.substringAfter("item=").toInt()
        } catch (e: Exception) {
            Timber.e("$TAG Ошибка преобразования id в $url. Ошибка: $e")
            return 0
        }
    }

    fun getPhaseFromUrl(url: String): String {
        return url.substringAfter("phase=")
            .substringBefore("&")
            .substringBefore("/")
    }


    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        activity?.menuInflater?.inflate(R.menu.favourite_context_menu, menu)
    }

    override fun onContextItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.show_favourite -> {
                Timber.d("$TAG Показать список избранного вызвано из контекстного меню $item")
                findNavController().navigate(R.id.dialog_favourites)
                //
                true
            }
            R.id.change_favourite -> {
                Timber.d("$TAG Сменить статус, вызвано из контекстного меню")
                changeCurrentFavouriteStatus()
                true
            }
            else -> super.onContextItemSelected(menuItem)
        }
    }

    private fun setClickListeners(binding: FragmentLearnDetailItemBinding) {

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(LearnDetailFragmentDirections.actionDestLearnDetailsToRecyclerViewDialog(item.phase))
        }

        binding.favourites.setOnClickListener {
            changeCurrentFavouriteStatus()
        }

        binding.share.setOnClickListener {
            val link = "https://play.google.com/store/apps/details?id=ru.tohaman.rg2"
            val textToShare = getString(R.string.textToShare1) + link + "\n " + getString(R.string.textToShare2)
            val shareTitle = getString(R.string.shareTitle)
            context?.shareText(shareTitle, textToShare)
        }

        //вызываем созданный в коде AlertDialog https://android--code.blogspot.com/2020/03/android-kotlin-alertdialog-edittext.html
        binding.writeComment.setOnClickListener {
            context?.let {
                val comment = binding.mainDBItem?.comment ?: ""
                val builder = MaterialAlertDialogBuilder(it)

                // dialog message view
                val constraintLayout = getEditTextLayout(it)
                builder.setView(constraintLayout)

                //val textInputLayout = constraintLayout.findViewWithTag<TextInputLayout>("textInputLayoutTag")
                val textInputEditText = constraintLayout.findViewWithTag<TextInputEditText>("textInputEditTextTag")

                textInputEditText.text = comment.toEditable()
                textInputEditText.hint = getString(R.string.editCommentHint)
                builder.setTitle(getString(R.string.editCommentTitle))
                    .setPositiveButton("OK") { _, _ ->
                        item.comment = textInputEditText.text.toString()
                        binding.mainDBItem = item
                        detailViewModel.updateComment(item)
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                val dialog = builder.create()
                dialog.show()

/**             Можно еще добавить слушатель на изменения вводимого текста

                textInputEditText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
*/
            }
        }

    }

    private fun changeCurrentFavouriteStatus() {
        detailViewModel.changeItemFavouriteStatus(item)
    }

    // get edit text layout
    private fun getEditTextLayout(context: Context): ConstraintLayout {
        val constraintLayout = ConstraintLayout(context)
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        constraintLayout.layoutParams = layoutParams
        constraintLayout.id = View.generateViewId()

        //Когда пользователь начинает вводить текст в текстовом поле, то подсказка, заданная в этом компоненте, всплывает над ним в специальном TextView
        //http://developer.alexanderklimov.ru/android/layout/textinputlayout.php
        val textInputLayout = TextInputLayout(context)
        textInputLayout.boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
        layoutParams.setMargins(
            16.dp(context),
            8.dp(context),
            16.dp(context),
            8.dp(context)
        )
        textInputLayout.layoutParams = layoutParams
        textInputLayout.hint = ""
        textInputLayout.id = View.generateViewId()
        textInputLayout.tag = "textInputLayoutTag"


        val textInputEditText = TextInputEditText(context)
        textInputEditText.id = View.generateViewId()
        textInputEditText.tag = "textInputEditTextTag"

        textInputLayout.addView(textInputEditText)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        constraintLayout.addView(textInputLayout)
        return constraintLayout
    }

}
