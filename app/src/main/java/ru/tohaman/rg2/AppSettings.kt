package ru.tohaman.rg2

import com.chibatching.kotpref.KotprefModel

/**
 * Делаем доступ к SharedPreferences как к простым переменным. Например, чтобы получить значение:
 * val startCount = AppSettings.startCount
 *
 * или вместо прочитать/увеличить/записать, можно сделать так:
 *
 * AppSettings.startCount += 1
 *
 * подробнее https://github.com/chibatching/Kotpref
 * */

object AppSettings: KotprefModel() {
    //Задаем имя файла, где будут храниться настройки, для совместимости с предыдущими версиями
    //где используется PreferenceManager.getDefaultSharedPreferences, задаем "ru.tohaman.rg2_preferences"
    override val kotprefName: String = "${context.applicationInfo.packageName}_preferences"

    var theme by stringPref(default = "AppTheme")

    var startCount by intPref(default = 0)

    var dayEnterCount by intPref(default = 1)
    var lastCallReviewTimestamp by longPref(default = 0)
    var dayOfLastEnter by longPref(default = 0)

    var mainTextSize by intPref(default = 2)

    var isScreenAlwaysON by booleanPref(default = false)                    //Не выключать экран, если приложение запущено
    var isYouTubeDisplayAlwaysOn by booleanPref(default = false)            //Не выключать экран при просмотре YouTube в отдельном окне
    var isTextSelectable by booleanPref(default = false)                    //Возможность выделить/скопировать текст из обучалки

    //Использование интернета
    var useAllInternet by booleanPref(default = true)                       //Используем любой доступный интернет (Boolean)
    var useOnlyWiFi by booleanPref(default = false)                         //Используем интернет только при наличии Wi-Fi (Boolean)
    var doNotUseInternet by booleanPref(default = false)                    //Не использовать интернет
}