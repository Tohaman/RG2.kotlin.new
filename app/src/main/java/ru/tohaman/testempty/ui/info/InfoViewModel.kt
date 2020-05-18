package ru.tohaman.testempty.ui.info

import android.app.Application
import android.content.SharedPreferences
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import ru.tohaman.testempty.Constants.INFO_BOOKMARK
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.dataSource.ItemsRepository
import timber.log.Timber
import java.text.FieldPosition

class InfoViewModel(app: Application): AndroidViewModel(app), KoinComponent {
    private val repository : ItemsRepository by inject()
    private val sp = get<SharedPreferences>()
    private val ctx = app.baseContext

    //номер закладки открываемой по-умолчанию
    var bookmark = sp.getInt(INFO_BOOKMARK, 2)

    fun setBookMark(position: Int) {
        Timber.d("$TAG setBookmark $position")
        sp.edit().putInt(INFO_BOOKMARK, position).apply()
    }

    val textAbout = ObservableInt(R.string.about)

    val textThanks = ObservableInt(R.string.thanks)
}