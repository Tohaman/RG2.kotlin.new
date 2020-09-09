package ru.tohaman.rg2.ui.settings

import android.os.Handler
import android.widget.SeekBar
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import ru.tohaman.rg2.AppSettings

class SettingsViewModel: ViewModel(), KoinComponent {

    enum class Themes (val themeName: String){ Dark("AppTheme"), Light("AppThemeLight")}

    private val theme = AppSettings.theme
    var isThemeDark = ObservableBoolean()

    private val _textSize = AppSettings.mainTextSize
    var textSize = ObservableInt(_textSize)

    init {
        if (theme == Themes.Dark.themeName) isThemeDark.set(true) else isThemeDark.set(false)
    }

    fun darkThemeSelect() {
        val theme = Themes.Dark.themeName
        AppSettings.theme = theme
    }

    fun lightThemeSelect() {
        val theme = Themes.Light.themeName
        AppSettings.theme = theme
    }

    fun needShowFabChange(value: Boolean) {
        AppSettings.showFAB = value
    }

    fun onTextSeek(): SeekBar.OnSeekBarChangeListener? {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textSize.set(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                AppSettings.mainTextSize = textSize.get()
            }
        }
    }

    /**Задаем параметры отлючения экрана*/
    //Общие
    private val _isScreenAlwaysOn = AppSettings.isScreenAlwaysON
    var isScreenAlwaysOn = ObservableBoolean(_isScreenAlwaysOn)

    fun isScreenAlwaysOnChange(value: Boolean) {
        AppSettings.isScreenAlwaysON = value
    }
    //Для просмотра YouTube
    private val _isYoutubeScreenAlwaysOn = AppSettings.isYouTubeDisplayAlwaysOn
    var isYoutubeScreenAlwaysOn = ObservableBoolean(_isYoutubeScreenAlwaysOn)

    fun isYoutubeScreenAlwaysOnChange(value: Boolean) {
        AppSettings.isYouTubeDisplayAlwaysOn = value
    }

    //Параметр выделения текста
    private val _isTextSelectable = AppSettings.isTextSelectable
    var isTextSelectable = ObservableBoolean(_isTextSelectable)

    fun isTextSelectableChange(value: Boolean) {
        AppSettings.isTextSelectable = value
    }

    //МиниХелп при старте программы
    private val _onStartMiniHelp = AppSettings.onStartHelpEnabled
    var onStartMiniHelp = ObservableBoolean(_onStartMiniHelp)

    private var _godMode = AppSettings.godMode
    var godMode = ObservableBoolean(_godMode)
    var godCount = 0

    fun miniHelpTextClick() {
        if (godCount == 6) {
            godCount += 1
            _godMode = !_godMode
            godMode.set(_godMode)
            AppSettings.godMode = _godMode
        } else {
            godCount += 1
            Handler().postDelayed({ godCount = 0}, 2500)
        }

    }

    fun isOnStartMiniHelpEnabled(value: Boolean) {
        AppSettings.onStartHelpEnabled = value
    }

    //Использование интернета
    private var _allInternet = AppSettings.useAllInternet
    var allInternet = ObservableBoolean(_allInternet)

    private var _onlyWiFi = AppSettings.useOnlyWiFi
    var onlyWiFi = ObservableBoolean(_onlyWiFi)

    private var _doNotUseInternet = AppSettings.doNotUseInternet
    var doNotUseInternet = ObservableBoolean(_doNotUseInternet)

    fun allInternetSelect() {
        _allInternet = true
        _onlyWiFi = false
        _doNotUseInternet = false
        saveInternetSettings()
    }

    fun onlyWiFiSelect() {
        _allInternet = false
        _onlyWiFi = true
        _doNotUseInternet = false
        saveInternetSettings()
    }

    fun doNotUseInternetSelect() {
        _allInternet = false
        _onlyWiFi = false
        _doNotUseInternet = true
        saveInternetSettings()
    }

    private fun saveInternetSettings() {
        allInternet.set(_allInternet)
        onlyWiFi.set(_onlyWiFi)
        doNotUseInternet.set(_doNotUseInternet)

        AppSettings.useAllInternet = _allInternet
        AppSettings.useOnlyWiFi = _onlyWiFi
        AppSettings.doNotUseInternet = _doNotUseInternet
    }
}