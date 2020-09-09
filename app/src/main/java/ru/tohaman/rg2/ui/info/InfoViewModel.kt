package ru.tohaman.rg2.ui.info

import android.app.Application
import android.content.SharedPreferences
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import ru.tohaman.rg2.AppSettings
import ru.tohaman.rg2.BuildConfig
import ru.tohaman.rg2.Constants.INFO_BOOKMARK
import ru.tohaman.rg2.Constants.PAYED_COINS
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.dataSource.ItemsRepository
import ru.tohaman.rg2.utils.SingleLiveEvent
import timber.log.Timber

class InfoViewModel(app: Application): AndroidViewModel(app), KoinComponent {

    //номер закладки открываемой по-умолчанию
    private var bookmark = AppSettings.infoBookmark

    fun setBookMark(position: Int) {
        Timber.d("$TAG setBookmark $position")
        bookmark = position
        AppSettings.infoBookmark = position
    }

    fun getBookmark(): Int = bookmark

    val textAbout = ObservableInt(R.string.about)

    val textThanks = ObservableInt(
        if (AppSettings.payCoins == 0)
            R.string.please_donate
        else
            R.string.big_thanks
    )

    val currentVersion = "ver. ${BuildConfig.VERSION_NAME}"

}