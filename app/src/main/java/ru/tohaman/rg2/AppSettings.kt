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

    var theme by stringPref(default = "AppTheme")                             //Тема приложени
    var showFAB by booleanPref(default = false)                             //Отображать плавающую кнопку в основном меню (Boolean)
    var mainTextSize by intPref(default = 2)                                    //+ к изначально задуманному размеру текста в обучалках (Int)
    var startCount by intPref(default = 0)                                      //количество запусков (всего)
    var version by intPref(default = BuildConfig.VERSION_CODE)                  //последняя сохраненная версия программы
    var helpCount by intPref(default = 0)                                       //Сколько раз был показан миниХелп по программе при старте
    var onStartHelpEnabled by booleanPref(default = true)                   //true, если надо показывать окно миниХелпа при запуске
    var favorites by stringPref(default = "empty")                            //для извлечения Избранного из предыдущей версии программы, там хранилось в SP
    var payCoins by intPref(default = 0)                                        //если 0, значит пользователь не донатил.
    var dayEnterCount by intPref(default = 1)
    var lastCallReviewTimestamp by longPref(default = 0)
    var dayOfLastEnter by longPref(default = 0)

    //Другие настройки программы
    var isScreenAlwaysON by booleanPref(default = false)                    //Не выключать экран, если приложение запущено
    var isYouTubeDisplayAlwaysOn by booleanPref(default = false)            //Не выключать экран при просмотре YouTube в отдельном окне
    var isTextSelectable by booleanPref(default = false)                    //Возможность выделить/скопировать текст из обучалки
    var godMode by booleanPref(default = false)                             //Режим разработчика (Boolean)

    //Использование интернета
    var useAllInternet by booleanPref(default = true)                       //Используем любой доступный интернет (Boolean)
    var useOnlyWiFi by booleanPref(default = false)                         //Используем интернет только при наличии Wi-Fi (Boolean)
    var doNotUseInternet by booleanPref(default = false)                    //Не использовать интернет

    //Настройки для PLL тренировки
    var is2SideRecognition by booleanPref(default = false)                  //Определение ситуации по 2-м сторонам если true (Boolean), иначе по 3-м
    var pllRandomSide by booleanPref(default = false)                       //Случайная сторона для определения (Boolean)
    var pllTrainingTimer by booleanPref(default = true)                     //Игра на время (Boolean)
    var pllTrainingTimerTime by intPref(default = 6)                             //Время в сек. на ответ (Int)
    var allPllCount by booleanPref(default = true)                          //Показывать все 21 вариант PLL
    var pllAnswerVariants by intPref(default = 6)                                //Сколько вариантов для выбора

    //Настройки игры Угадай букву блайнда
    var trainingEdges by booleanPref(default = true)                        //Тренируем углы? (Boolean)
    var trainingCorners by booleanPref(default = true)                      //Тренируем ребра? (Boolean)
    var trainingTimer by booleanPref(default = true)                        //Игра на время (Boolean)
    var trainingTimerTime by intPref(default = 6)                                //Время в сек. на ответ (Int)

    //Основное меню
    var currentCubeType by intPref(default = 2)                                  //Текущая головоломка (пункт меню)

    //Настройки Генератора скрмблов
    var currentScramble by stringPref("R F L B U2 L B' R F' D B R L F D R' D L")          //Последний придуманный скрамбл (текущий) (String)
    var isBufferEdgeSet by booleanPref(default = true)                      //Переплавка буфера ребер (Boolean)
    var isBufferCornerSet by booleanPref(default = true)                    //Переплавка буфера углов (Boolean)
    var scrambleLength by intPref(default = 14)                                  //Длина скрамбла (Int)
    var showSolving by booleanPref(default = true)                          //Показывать решение для скрамбла (Boolean)

    //Настройки таймера
    var isTimerDelayed by booleanPref(default = true)              //Нужна задержка перед срабатыванием Готовности таймера? (Boolean)
    var isOneHanded by booleanPref(default = false)              //Однорукий тайиер? (True - однорукий, False - двурукий)
    var isMetronomEnabled by booleanPref(default = false)          //Включен ли метроном (Boolean)
    var needScramble by booleanPref(default = true)          //Включено ли отображение скрамбла в таймере (Boolean)
    var needBackButton by booleanPref(default = true)            //Отображнеи кнопки "назад" (Boolean)
    var metronomFrequency by intPref(default = 60)     //Частота метронома (раз в минуту) (Int)
    var scrambleTextSize by intPref(default = 6)       //Размер текста для отображения скрабла в таймере (Int)

    //Меню Информации
    var infoBookmark by intPref(default = 1)                                    //Текущая выбранная закладка на странице Info

}